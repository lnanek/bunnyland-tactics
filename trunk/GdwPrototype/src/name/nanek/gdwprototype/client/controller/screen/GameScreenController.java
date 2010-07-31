package name.nanek.gdwprototype.client.controller.screen;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import name.nanek.gdwprototype.client.controller.PageController;
import name.nanek.gdwprototype.client.controller.screen.support.GameScreenDropController;
import name.nanek.gdwprototype.client.controller.support.ScreenControllers;
import name.nanek.gdwprototype.client.controller.support.ScreenControllers.Screen;
import name.nanek.gdwprototype.client.model.GameListing;
import name.nanek.gdwprototype.client.model.GamePlayInfo;
import name.nanek.gdwprototype.client.model.Player;
import name.nanek.gdwprototype.client.view.screen.GameScreen;
import name.nanek.gdwprototype.client.view.screen.GameScreen.FogOfWarChangeListener;
import name.nanek.gdwprototype.client.view.widget.GameSquare;
import name.nanek.gdwprototype.client.view.widget.PaletteImage;
import name.nanek.gdwprototype.client.view.widget.TableCellPanel;
import name.nanek.gdwprototype.shared.model.Marker;
import name.nanek.gdwprototype.shared.model.Markers;
import name.nanek.gdwprototype.shared.model.Position;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;

/**
 * Controls screen where the actual game is played as opposed to showing menus.
 * 
 * @author Lance Nanek
 *
 */
public class GameScreenController extends ScreenController implements FogOfWarChangeListener {
	//TODO hotseat mode: move -> black screen with next player button -> switches player
	
	private static final int GAME_BOARD_REFRESH_INTERVAL = 1000; // ms

	GameScreen gameScreen;

	private Long gameId;

	private Player fogOfWarAs;

	private boolean dragInProgress;

	public PickupDragController dragController;

	public PageController pageController;
	
	private HashSet<GameSquare> draggables;
	
	private List<GameScreenDropController> boardDropControllers;
	
	private boolean updatesRequired = true;

	private boolean playedGameOverMusic;
	
	public GamePlayInfo info;
	
	Integer lastPlayedSoundForMoveCount;
	
	//TODO don't refresh when window blurred
	//detecting refocus seems dicey, chrome isn't calling properly when switch back to tab from another, 
	//but can show pause dialog and have user click to unpause
	Timer refreshGameBoardTimer = new Timer() {
		@Override
		public void run() {
			updateGameBoard();
		}
	};

	public GameScreenController() {
	}
	
	@Override
	public void onFogOfWarChange(Player newFogOfWarAs) {
		fogOfWarAs = newFogOfWarAs;
		updatesRequired = true;
		updateGameBoard();
	}
	
	public void moveMarker(Integer sourceRow, Integer sourceColumn, Integer destRow, Integer destColumn,
			String newImageSource, final Marker replacedMarker) {
		
		//TODO clearing and restoring draggables isn't needed for map building
		clearDraggables();

		pageController.gameService.moveMarker(gameId, sourceRow, sourceColumn, destRow, destColumn,
				newImageSource, new AsyncCallback<GamePlayInfo>() {
					public void onFailure(Throwable caught) {
						pageController.getDialogController().showError(
								"Error Moving Piece",								
								"An error occurred asking the game server to move the requested piece.",
								true,
								caught);
						pageController.getSoundPlayer().playInGameErrorSound();
						updatesRequired = true;
						updateGameBoard();
					}

					public void onSuccess(GamePlayInfo info) {
						/*
						if ( null == replacedMarker ) {
							pageController.getSoundPlayer().playPiecePlacementSound();
						} else if ( null != replacedMarker.player ) {
							pageController.getSoundPlayer().playDyingSound();
						} else if (replacedMarker.source.contains("carrot")) {
							pageController.getSoundPlayer().playCarrotSound();
						} else {
							pageController.getSoundPlayer().playPiecePlacementSound();
						}
						*/
						
						//TODO players take turns, so nothing changed but what we dragged, so just update fog of war and turn status?
						//technically we don't even need a positions update and it could be removed to make moving things respond quicker
						updateGameBoardWithInfo(info);
					}
				});
	}

	private void updateGameBoard() {
		
		if ( dragInProgress || !updatesRequired ) {
			return;
		}

		pageController.gameService.getPositionsByGameId(gameId, new AsyncCallback<GamePlayInfo>() {
			public void onFailure(Throwable caught) {
				pageController.getDialogController().showError(
						"Error Getting Positions",								
						"An error occurred asking the server for the current game piece positions.",
						true,
						caught);
			}

			public void onSuccess(final GamePlayInfo info) {
				updateGameBoardWithInfo(info);
			}
		});
	}
	
	private void setVisibileSquares(int markerRow, int markerCol, Marker marker, boolean[][] visibleSquares, int boardWidth, int boardHeight) {
		if (null == marker || null == marker.player || null == marker.visionRange || null == fogOfWarAs)
			return;

		if (marker.player != fogOfWarAs)
			return;

		int startRow = Math.max(0,	markerRow - marker.visionRange);
		int endRow = Math.min(boardHeight - 1,	markerRow + marker.visionRange);
		int startCol = Math.max(0,	markerCol - marker.visionRange);
		int endCol = Math.min(boardWidth - 1, markerCol + marker.visionRange);
		
		for (int row = startRow; row <= endRow; row++) {
			for (int column = startCol; column <= endCol; column++) {
				int rowDistance = Math.abs(markerRow - row);
				int colDistance = Math.abs(markerCol - column);
				int totalDistance = rowDistance + colDistance;
				if (totalDistance <= marker.visionRange) {
					visibleSquares[row][column] = true;
				}
			}
		}
	}
	
	private void clearDraggables() {
		for(GameSquare square : draggables) {
			dragController.makeNotDraggable(square);
		}
		draggables.clear();
	}
	
	private void updateGameBoardWithInfo(final GamePlayInfo info) {
		if (dragInProgress) {
			return;
		}
		
		this.info = info;
		
		//Update publish/surrender controls.
		//Hide if game is over or map is published.
		if ( info.ended ) {
			gameScreen.publishMapButton.setVisible(false);
			gameScreen.surrenderButton.setVisible(false);
		//Else the game is still running or the map unpublished.
		//Show publish map button if we're building a map and the user is the map creator.
		} else if ( info.isBuildingMap ) {
			gameScreen.publishMapButton.setVisible(info.isUsersTurn);
			gameScreen.surrenderButton.setVisible(false);
		//Show surrender button if we're playing a game and its the user's turn.
		} else if ( null != info.playingAs ) {
			gameScreen.publishMapButton.setVisible(false);
			//TODO allow surrender when not their turn would be nice, 
			//but currently the other client isn't polling for changes at that time
			gameScreen.surrenderButton.setVisible(info.isUsersTurn);
		//Observers don't see publish or surrender buttons.
		} else {
			gameScreen.publishMapButton.setVisible(false);
			gameScreen.surrenderButton.setVisible(false);
		}
		
		//Update fog of war controls.
		//TODO turn off fog of war/allow players to control fog of war after game ends?
		if ( null == info.playingAs || info.isBuildingMap || info.ended ) {
			gameScreen.fogOfWarPanel.setVisible(true);
		} else {
			gameScreen.fogOfWarPanel.setVisible(false);
			fogOfWarAs = info.playingAs;			
			if ( Player.ONE == info.playingAs ) {
				gameScreen.fogOfWarPlayerOneRadio.setValue(true, false);
			} else if ( Player.TWO == info.playingAs ) {
				gameScreen.fogOfWarPlayerTwoRadio.setValue(true, false);
			}
		}

		//Play a sound for an active game/map if this is the first time we got this move data.
		//TODO we currently make these sounds after the server has been asked if the move is legal
		//if would be nice to play them immediately. Maybe have a move validator that both the client
		//and server share
		if ( !info.ended && info.moveCount > 0 && (null == lastPlayedSoundForMoveCount || lastPlayedSoundForMoveCount < info.moveCount) ) {
			lastPlayedSoundForMoveCount = info.moveCount;
			
			//Always piece placement sound when placing units on a map.
			if ( info.isBuildingMap ) {
				pageController.getSoundPlayer().playPiecePlacementSound();

			//Both players always hear a unit dying, since they must have both had a unit involved.
			} else if ( info.unitDiedLastTurn ) {
				pageController.getSoundPlayer().playDyingSound();

			//We're viewing the screen of someone who just finished a move so can hear eating carrots or placing pieces.
			//TODO technically we should be able to hear the enemy doing it as well if the unit that did it is in sight
			} else if ( null == fogOfWarAs || fogOfWarAs != info.currentPlayersTurn ) {
				if ( info.carrotEatenLastTurn ) {
					pageController.getSoundPlayer().playCarrotSound();
				} else {
					pageController.getSoundPlayer().playPiecePlacementSound();
				}
			}
			
			//XXX sometimes we'll hear both a gong and a scream. 
			//see how that sounds. maybe we should play one after the other?
			if (!info.isBuildingMap && info.isUsersTurn ) {
				pageController.getSoundPlayer().playYourTurnSound();
			}
		}
		
		//Play game over music if needed.
		if ( !info.isBuildingMap && !playedGameOverMusic ) {
			if ( info.ended ) {
				if ( info.playingAs == info.winner ) {
					pageController.getSoundPlayer().playWinGameMusic();
				} else if ( null != info.playingAs ) {
					pageController.getSoundPlayer().playLoseGameMusic();
				}
				playedGameOverMusic = true;
			}
		}

		//Update status.
		//TODO bold the action verbs? bold and color the piece colors?
		String status = "";
		if ( info.isBuildingMap ) {
			if ( null != info.playingAs ) {
				status = "Drag pieces to build a map that other players start their games from.";
				if ( !info.ended ) {
					status += " Click publish to make the map visible on the start game screen.";
				}
			} else {
				status = "You are viewing a game map that players can start their games from.";
				if ( !info.ended ) {
					status += " The author has not yet published it for use.";
				}
			}
			updatesRequired = true;
		} else if ( info.ended ) {
			if ( Player.ONE == info.winner ) {
				status = "Black won!";
			} else if ( Player.TWO == info.winner ) {
				status = "Red won!";
			} else {
				status = "Game over.";
			}
			updatesRequired = false;
		} else if ( null == info.playingAs ) {
			status = "You are observing this game and cannot make moves.";
			updatesRequired = true;
		} else if ( info.isUsersTurn ) {
			String pieceColor = info.playingAs == Player.ONE ? "black" : "red";
			status = "It's your turn. Drag a " + pieceColor + " piece to make your move!";
			updatesRequired = false;
		} else if ( info.needsSecondPlayer ) {
			status = "Waiting for a second player to join the game.";
			updatesRequired = true;
		} else {
			status = "Please wait while the other player moves a piece.";
			updatesRequired = true;
		}
		gameScreen.statusLabel.setText(status +" ");

		//GWT.log("positions: " + info.positions.length);		
		HashSet<Position> positions = removeTerrainUnderUnits(info.positions);	
		//GWT.log("filtered positions: " + positions.length);
		int boardHeight = info.boardHeight;
		int boardWidth = info.boardWidth;
		
		// TODO update visibility immediately on move instead of waiting for server update?
		
		Marker[][] markerPositions = new Marker[boardHeight][boardWidth];
		boolean[][] visibleSquares = null;
		if ( null != fogOfWarAs ) {
			visibleSquares = new boolean[boardHeight][boardWidth];	
		}
		for (final Position position : positions) {
			//Translate the server sent positions into markers.
			//TODO just send markers over directly
			//TODO stop storing full URLs in DB
			Marker marker = Markers.markerBySource.get(position.getMarkerSourceFilename());
			int row = position.getRow();
			int col = position.getColumn();
			//GWT.log("Position " + row + ", " + col + " using marker: " + marker);
			if ( row < boardHeight && col < boardWidth ) {
				markerPositions[row][col] = marker;
			}
			
			//Determine what is visible.
			if ( null != fogOfWarAs ) {
				setVisibileSquares(row, col, marker, visibleSquares, boardWidth, boardHeight);			
			}
		}
		
		// TODO more efficient algorithm? right now we make some things undraggable just to make them draggable again
		clearDraggables();

		//Determine a marker for each square of game board.
		for (int row = 0; row < boardHeight; row++) {
			for (int col = 0; col < boardWidth; col++) {	
				//Use fog of war instead of server sent marker if not visible.
				Marker marker = null;
				if ( null != fogOfWarAs && !visibleSquares[row][col] ) {
					marker = Markers.FOG_OF_WAR;
				} else {
					marker = markerPositions[row][col];
				}
				
				TableCellPanel panel = (TableCellPanel) gameScreen.gameBoard.getWidget(row, col);
				Image currentImage = (Image) panel.getWidget();
				if ( null == marker ) {
					if ( null != currentImage ) {
						currentImage.removeFromParent();
					}
				} else if (null == currentImage || !currentImage.getUrl().endsWith(marker.source)) {
					Image image = new GameSquare(marker);
					panel.setWidget(image);
				}				
				if ( null != info.playingAs && info.isUsersTurn ) {
					makeContentsDraggableIfNeeded(panel);
				}
			}
		}
		
		gameScreen.content.setVisible(true);
	}
	
	private HashSet<Position> removeTerrainUnderUnits(Position[] positions) {
		//XXX The engine can't currently show a tile on top of another,
		//so there's an ugly hack here to filter out terrain that units are on top of.
		//TODO show tiles on top of one another, units on top of terrain
		//maybe have separate position collections for terrain and units
		HashSet<Position> filteredPositions = new HashSet<Position>();
		filteredPositions.addAll(Arrays.asList(positions));
		
		for( Position first : positions ) {
			if ( !first.getMarkerSource().contains("tile_") ) {
				continue;
			}
			
			for ( Position second : positions ) {
				if ( first != second && first.getColumn() == second.getColumn() &&
						first.getRow() == second.getRow() ) {

					filteredPositions.remove(first);
				}
			}
		}
		//ArrayList<Position> filteredPositionsList = new ArrayList<Position>(filteredPositions);
		//return filteredPositionsList.toArray(new Position[] {});
		return filteredPositions;
	}

	private void restoreDraggables() {
		if ( null == info ) {
			return;
		}
		
		// Make appropriate pieces draggable. Done each update to support login and hotseat play later.
		if ( null != info.playingAs && info.isUsersTurn ) {
			int rowCount = gameScreen.gameBoard.getRowCount();
			for (int row = 0; row < rowCount; row++) {
				int cellCount = gameScreen.gameBoard.getCellCount(row);
				for (int column = 0; column < cellCount; column++) {
					TableCellPanel panel = (TableCellPanel) gameScreen.gameBoard.getWidget(row, column);
					makeContentsDraggableIfNeeded(panel);
				}
			}				
		}
	}

	private void makeContentsDraggableIfNeeded(TableCellPanel panel) {
		if ( null == info ) {
			return;
		}
		
		GameSquare square = (GameSquare) panel.getWidget();
		if ( null == square || null == square.marker ) {
			return;
		}

		Marker marker = square.marker;
		//TODO do we have to check if the current user is the map builder?
		if ( info.isBuildingMap || (!info.ended && marker.player == info.playingAs && null != marker.movementRange && marker.movementRange > 0 )) {
			dragController.makeDraggable(square);
			//TODO Slay has a nice way of showing a piece can be interacted with.
			//Units hop up and down and buildings ready to build have a waving flag.
			//Something like that would help the player a lot.
			draggables.add(square);
		}
	}

	@Override
	public void hideScreen() {
		for( GameScreenDropController simpleDropController : boardDropControllers) {
			dragController.unregisterDropController(simpleDropController);
		}
		pageController = null;
		refreshGameBoardTimer.cancel();
	}
	
	@Override
	public void createScreen(final PageController pageController, final Long gameId) {
		
		gameScreen = new GameScreen(pageController.getSoundPlayer());

		this.gameId = gameId;
		if ( null == gameId ) {
			History.newItem(ScreenControllers.getHistoryToken(Screen.MENU));
			return;
		}
		
		//TODO remember players who have seen this before and don't show?
		gameScreen.howToPlayDialog.show();

		draggables = new HashSet<GameSquare>();
		boardDropControllers = new LinkedList<GameScreenDropController>();
		this.pageController = pageController;
		pageController.getSoundPlayer().playInGameMusic();
		pageController.addScreen(gameScreen.content);

		gameScreen.setFogOfWarChangeListener(this);

		dragController = new PickupDragController(RootPanel.get(), false);
		dragController.setBehaviorDragProxy(true);
		dragController.setBehaviorMultipleSelection(false);
		dragController.addDragHandler(new DragHandler() {

			@Override
			public void onDragEnd(DragEndEvent event) {
				dragInProgress = false;
				
				//If was dragging something in a game, not a map editor.
				if ( null != info && !info.isBuildingMap ) {
					//Remove highlights for where it can go.	
					
					CellFormatter formatter = gameScreen.gameBoard.getCellFormatter();
					
					int rows = gameScreen.gameBoard.getRowCount();
					for (int row = 0; row < rows; row++) {
						int cols = gameScreen.gameBoard.getCellCount(row);
						for (int col = 0; col < cols; col++) {	
							//TableCellPanel destPanel = (TableCellPanel) gameScreen.gameBoard.getWidget(row, col);
							//destPanel.removeStyleName("validDropTarget");
							
							formatter.removeStyleName(row, col, "validDropTarget");
						}
					}
				}
			}

			@Override
			public void onDragStart(DragStartEvent event) {
				dragInProgress = true;
				
				pageController.getSoundPlayer().playPickupPiceSound();

				//If dragging something in a game, not a map editor.
				if ( null != info && !info.isBuildingMap ) {
					
					//Highlight where it can go.
					GameSquare gameSquare = (GameSquare) event.getContext().draggable;
					if ( gameSquare instanceof PaletteImage ) {
						return;
					}
					Marker marker = gameSquare.marker;
					
					TableCellPanel sourcePanel = (TableCellPanel) gameSquare.getParent();

					int startRow = Math.max(0,	sourcePanel.getRow() - marker.movementRange);
					int endRow = Math.min(gameScreen.gameBoard.getRowCount() - 1, sourcePanel.getRow() + marker.movementRange);
						
					CellFormatter formatter = gameScreen.gameBoard.getCellFormatter();
					
					for (int row = startRow; row <= endRow; row++) {
						
						int startCol = Math.max(0,	sourcePanel.getColumn() - marker.movementRange);
						int endCol = Math.min(gameScreen.gameBoard.getCellCount(row) - 1, sourcePanel.getColumn() + marker.movementRange);
						
						for (int col = startCol; col <= endCol; col++) {	
							int rowDistance = Math.abs(sourcePanel.getRow() - row);
							int colDistance = Math.abs(sourcePanel.getColumn() - col);
							int totalDistance = rowDistance + colDistance;
							if (totalDistance <= marker.movementRange) {
								//TableCellPanel destPanel = (TableCellPanel) gameScreen.gameBoard.getWidget(row, col);
								//destPanel.addStyleName("validDropTarget");
								
								formatter.addStyleName(row, col, "validDropTarget");
							}
						}
					}
				}
			}

			@Override
			public void onPreviewDragEnd(DragEndEvent event) throws VetoDragException {
			}

			@Override
			public void onPreviewDragStart(DragStartEvent event) throws VetoDragException {
				//TODO veto here if not players marker instead of adjusting draggables?
			}
		});
		
		//Setup surrender button.
		class SurrenderCallback implements AsyncCallback<Void> {
			public void onFailure(Throwable caught) {
				pageController.getDialogController().showError(
						"Error Surrendering",								
						"An error occurred asking the game server to surrender.",
						true,
						caught);
				restoreDraggables();
				gameScreen.surrenderButton.setEnabled(true);
			}

			public void onSuccess(Void unused) {
				updatesRequired = true;
				updateGameBoard();
			}
		};
		class SurrenderClickHandler implements ClickHandler {
			@Override
			public void onClick(ClickEvent event) {
				clearDraggables();
				gameScreen.surrenderButton.setEnabled(false);
				pageController.gameService.surrender(gameId, info.playingAs, new SurrenderCallback());
			}
		}
		gameScreen.surrenderButton.addClickHandler(new SurrenderClickHandler());

		//Setup publish map button.
		class PublishMapCallback implements AsyncCallback<Void> {
			public void onFailure(Throwable caught) {
				pageController.getDialogController().showError(
						"Error Publishing Map",								
						"An error occurred asking the game server to publish the map.",
						true,
						caught);
				gameScreen.publishMapButton.setEnabled(true);
			}

			public void onSuccess(Void unused) {
				//updateGameBoard();
				History.newItem(ScreenControllers.getHistoryToken(Screen.CREATE_GAME));
			}
		};
		class PublishMapClickHandler implements ClickHandler {
			@Override
			public void onClick(ClickEvent event) {
				gameScreen.publishMapButton.setEnabled(false);
				pageController.gameService.publishMap(gameId, new PublishMapCallback());
			}
		}
		gameScreen.publishMapButton.addClickHandler(new PublishMapClickHandler());

		//Load screen title.
		pageController.gameService.getGameListingById(gameId, new AsyncCallback<GameListing>() {
			public void onFailure(Throwable caught) {
				pageController.getDialogController().showError(
						"Error Finding Game Or Map",								
						"An error occurred finding the requested game or map.",
						true,
						caught);
				// TODO go back to main menu? offer retry/cancel?
			}

			public void onSuccess(final GameListing gameListing) {
				if ( null == pageController ) {
					return;
				}
				String type = gameListing.isStartingMap() ? "Creating Map " : "Playing Game ";
				pageController.setScreenTitle(type + gameListing.getName());
			}
		});

		//Setup board.
		//TODO have a separate DTO with the setup information neeeded, make regular position callback quicker
		pageController.gameService.getPositionsByGameId(gameId, new AsyncCallback<GamePlayInfo>() {
			public void onFailure(Throwable caught) {
				pageController.getDialogController().showError(
						"Error Setting Up Board",								
						"An error occurred asking the server for the game/map settings.",
						true,
						caught);
			}

			public void onSuccess(final GamePlayInfo info) {
				setupBoard(info);
			}
		});
	}
	
	private void setupBoard(GamePlayInfo info) {

		//Load piece settings.
		//TODO just use markers from settings, don't have a static copy on client?
		for( Marker settingsMarker : info.markers ) {
			Marker marker = Markers.markerBySource.get(settingsMarker.source);
			marker.movementRange = settingsMarker.movementRange;
			marker.visionRange = settingsMarker.visionRange;
		}
		
		//Fill build palette if needed.
		//GWT.log("GameScreenController#initializeBoardIfNeeded: isBuildingMap = " + info.isBuildingMap);
		//GWT.log("GameScreenController#initializeBoardIfNeeded: isUsersTurn = " + info.isUsersTurn);
		if ( info.isBuildingMap && info.isUsersTurn ) {
			gameScreen.mapBuilderPalettePanel.setVisible(true);
			for (int i = 0; i < Markers.MAP_MAKING_PIECES.length; i++) {
				Marker marker = Markers.MAP_MAKING_PIECES[i];
				Image image = new PaletteImage(marker);
				TableCellPanel panel = new TableCellPanel(image, gameScreen.markers, 0, i);
				dragController.makeDraggable(image);
				GameScreenDropController simpleDropController = new GameScreenDropController(panel, this);
				dragController.registerDropController(simpleDropController);
				//boardDropControllers.add(simpleDropController);
			}
			//TODO random options: fill board with random terrain, random palette entry that places a random terrain, random brush that can be dragged across area to add random terrain, etc..
		} else {
			gameScreen.mapBuilderPalettePanel.setVisible(false);
		}
		
		//Fill board table for game size.
		int height = info.boardHeight;
		int width = info.boardWidth;
		for (int column = 0; column < width; column++) {
			for (int row = 0; row < height; row++) {
				TableCellPanel panel = new TableCellPanel(null, gameScreen.gameBoard, row, column);
				GameScreenDropController simpleDropController = new GameScreenDropController(panel, this);
				dragController.registerDropController(simpleDropController);
				boardDropControllers.add(simpleDropController);
				
				//TODO need to change where can drop based on dragged piece's movement, and highlight where can drop for user as well
				//TODO adjust what drop controllers are registered? or just veto wrong moves?
			}
		}	
		
		//Start regular board updates.
		updateGameBoardWithInfo(info);
		//TODO only schedule a new screen update when the previous finishes? repeating might build up a queue if updates are slower than refresh interval
		refreshGameBoardTimer.scheduleRepeating(GAME_BOARD_REFRESH_INTERVAL);
	}

}
