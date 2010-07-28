package name.nanek.gdwprototype.client.controller;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import name.nanek.gdwprototype.client.model.GameListingInfo;
import name.nanek.gdwprototype.client.model.GamePlayInfo;
import name.nanek.gdwprototype.client.model.Player;
import name.nanek.gdwprototype.client.model.TerrainGenerator;
import name.nanek.gdwprototype.client.view.screen.GameScreen;
import name.nanek.gdwprototype.client.view.screen.GameScreen.FogOfWarChangeListener;
import name.nanek.gdwprototype.client.view.widget.GameSquare;
import name.nanek.gdwprototype.client.view.widget.PaletteImage;
import name.nanek.gdwprototype.client.view.widget.TableCellPanel;
import name.nanek.gdwprototype.shared.FieldVerifier;
import name.nanek.gdwprototype.shared.model.Marker;
import name.nanek.gdwprototype.shared.model.Markers;
import name.nanek.gdwprototype.shared.model.Position;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;

public class GameScreenController extends ScreenController implements FogOfWarChangeListener {
	//TODO hotseat mode: move -> black screen with next player button -> switches player
	
	private static final int GAME_BOARD_REFRESH_INTERVAL = 1000; // ms

	GameScreen gameScreen = new GameScreen();

	private Long currentGameId;

	private Player playingAs;

	private Player fogOfWarAs;

	private boolean dragInProgress;

	public PickupDragController dragController;

	private PageController pageController;
	
	private HashSet<GameSquare> draggables;
	
	private List<GameScreenDropController> boardDropControllers;
	
	private GameScreenBoardController boardController = new GameScreenBoardController(this);
	
	private boolean boardInitialized;

	private boolean updatesRequired = true;

	GamePlayInfo lastInfo;
	
	//TODO don't refresh when window blurred
	//detecting refocus seems dicey, chrome isn't calling properly when switch back to tab from another, 
	//but can show pause dialog and have user click to unpause
	Timer refreshGameBoardTimer = new Timer() {
		@Override
		public void run() {
			updateGameBoard();
		}
	};

	private boolean markersInitialized;

	public GameScreenController() {
	}
	
	@Override
	public void onFogOfWarChange(Player newFogOfWarAs) {
		fogOfWarAs = newFogOfWarAs;
		updatesRequired = true;
		updateGameBoard();
	}
	
	@Override
	public void createScreen(final PageController pageController) {

		draggables = new HashSet<GameSquare>();
		boardDropControllers = new LinkedList<GameScreenDropController>();
		this.pageController = pageController;

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
				if ( null != lastInfo && !lastInfo.isBuildingMap ) {
					//Remove highlights for where it can go.	
					
					CellFormatter formatter = gameScreen.gameBoard.getCellFormatter();
					
					int rows = gameScreen.gameBoard.getRowCount();
					for (int row = 0; row < rows; row++) {
						int cols = gameScreen.gameBoard.getRowCount();
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

				//If dragging something in a game, not a map editor.
				if ( null != lastInfo && !lastInfo.isBuildingMap ) {
					
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
				pageController.dialogController.showError(
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
				pageController.gameDataService.surrender(currentGameId, lastInfo.playingAs, new SurrenderCallback());
			}
		}
		gameScreen.surrenderButton.addClickHandler(new SurrenderClickHandler());

		//Setup publish map button.
		class PublishMapCallback implements AsyncCallback<Void> {
			public void onFailure(Throwable caught) {
				pageController.dialogController.showError(
						"Error Publishing Map",								
						"An error occurred asking the game server to publish the map.",
						true,
						caught);
				gameScreen.publishMapButton.setEnabled(true);
			}

			public void onSuccess(Void unused) {
				updateGameBoard();
			}
		};
		class PublishMapClickHandler implements ClickHandler {
			@Override
			public void onClick(ClickEvent event) {
				gameScreen.publishMapButton.setEnabled(false);
				pageController.gameDataService.publishMap(currentGameId, new PublishMapCallback());
			}
		}
		gameScreen.publishMapButton.addClickHandler(new PublishMapClickHandler());
	}
	
	private void initializeBoardIfNeeded(GamePlayInfo info) {
		if (boardInitialized) {
			return;
		}
		
		//Load piece settings.
		//TODO just use pieces from settings, don't have a static copy on client?
		for( Marker settingsMarker : info.markers ) {
			Marker marker = Markers.markerBySource.get(settingsMarker.source);
			marker.movementRange = settingsMarker.movementRange;
			marker.visionRange = settingsMarker.visionRange;
		}
		
		//Generate random terrain.
		int height = info.boardHeight;
		int width = info.boardWidth;
		TerrainGenerator.generateRandomTerrain(width, height);
		
		//Clear any previous board table and build palette.
		for( GameScreenDropController simpleDropController : boardDropControllers) {
			dragController.unregisterDropController(simpleDropController);
		}
		boardDropControllers.clear();
		gameScreen.gameBoard.clear();
		//gameScreen.markers.clear();
		
		//Fill build palette if needed.
		GWT.log("GameScreenController#initializeBoardIfNeeded: isBuildingMap = " + info.isBuildingMap);
		GWT.log("GameScreenController#initializeBoardIfNeeded: isUsersTurn = " + info.isUsersTurn);
		if ( info.isBuildingMap && info.isUsersTurn ) {
			gameScreen.mapBuilderPalettePanel.setVisible(true);
			if ( !markersInitialized ) {
				for (int i = 0; i < Markers.PLAYING_PIECES.length; i++) {
					Marker marker = Markers.PLAYING_PIECES[i];
					// if ( marker.player != null && marker.player != playingAs )
					// continue;
					Image image = new PaletteImage(marker);
					TableCellPanel panel = new TableCellPanel(image, gameScreen.markers, 0, i);
					dragController.makeDraggable(image);
					GameScreenDropController simpleDropController = new GameScreenDropController(panel, this);
					dragController.registerDropController(simpleDropController);
					//boardDropControllers.add(simpleDropController);
				}
				markersInitialized = true;
			}
		} else {
			gameScreen.mapBuilderPalettePanel.setVisible(false);
		}
		
		//Fill board table for game size.
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
		
		boardInitialized = true;
	}

	public void moveMarker(Integer sourceRow, Integer sourceColumn, Integer destRow, Integer destColumn,
			String newImageSource) {
		
		//TODO clearing and restoring draggables isn't needed for map building
		clearDraggables();

		pageController.gameDataService.moveMarker(currentGameId, sourceRow, sourceColumn, destRow, destColumn,
				newImageSource, new AsyncCallback<GamePlayInfo>() {
					public void onFailure(Throwable caught) {
						pageController.dialogController.showError(
								"Error Moving Piece",								
								"An error occurred asking the game server to move the requested piece.",
								true,
								caught);
						updatesRequired = true;
						updateGameBoard();
					}

					public void onSuccess(GamePlayInfo info) {
						//TODO players take turns, so nothing changed but what we dragged, so just update fog of war and turn status?
						//technically we don't even need a positions update and it could be removed to make moving things respond quicker
						updateGameBoardWithInfo(info);
					}
				});
	}

	private void updateGameBoard() {
		
		if (null == currentGameId || dragInProgress || !updatesRequired) {
			return;
		}

		pageController.gameDataService.getPositionsByGameId(currentGameId, new AsyncCallback<GamePlayInfo>() {
			public void onFailure(Throwable caught) {
				pageController.dialogController.showError(
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
		
		initializeBoardIfNeeded(info);
		lastInfo = info;
		playingAs = info.playingAs;

		//Update publish/surrender controls.
		//Hide if game is over or map is published.
		if ( info.ended ) {
			gameScreen.publishMapButton.setVisible(false);
			gameScreen.surrenderButton.setVisible(false);
		//Else the game is still running or the map unpublished.
		//Show publish map button if we're building a map and the user is the map creator.
		} else if ( info.isBuildingMap && info.isUsersTurn ) {
			gameScreen.publishMapButton.setVisible(true);
		//Show surrender button if we're playing a game and the user is a player.
		} else if ( null != playingAs ) {
			if ( info.isUsersTurn ) {
				gameScreen.surrenderButton.setVisible(true);
			} else {
				gameScreen.surrenderButton.setVisible(false);				
			}
		}
		
		//Update fog of war controls.
		if ( null == playingAs || info.isBuildingMap ) {
			gameScreen.fogOfWarPanel.setVisible(true);
			//gameScreen.mapBuilderPalettePanel.setVisible(false);
		} else {
			gameScreen.fogOfWarPanel.setVisible(false);
			//gameScreen.mapBuilderPalettePanel.setVisible(true);
			fogOfWarAs = playingAs;
		}

		//Update status.
		//TODO bold the action verbs? bold and color the piece colors?
		String status = "";
		if ( info.isBuildingMap ) {
			if ( null != playingAs ) {
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
		} else if ( null == playingAs ) {
			status = "You are observing this game and cannot make moves.";
			updatesRequired = true;
		} else if ( info.isUsersTurn ) {
			String pieceColor = playingAs == Player.ONE ? "black" : "red";
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

		
		Position[] positions = info.positions;	
		GWT.log("positions: " + positions.length);
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
			Marker marker = Markers.markerBySource.get(position.getMarkerSourceFilename());
			int row = position.getRow();
			int col = position.getColumn();
			GWT.log("Position " + row + ", " + col + " using marker: " + marker);
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
				//Use random terrain if not fog of war or server sent marker.
				if ( null == marker ) {
					marker = TerrainGenerator.RANDOM_TERRAIN[row][col];
				}
				
				TableCellPanel panel = (TableCellPanel) gameScreen.gameBoard.getWidget(row, col);
				Image currentImage = (Image) panel.getWidget();
				if (null == currentImage || !currentImage.getUrl().endsWith(marker.source)) {
					Image image = new GameSquare(marker);
					panel.setWidget(image);
				}				
				if ( null != playingAs && lastInfo.isUsersTurn ) {
					makeContentsDraggableIfNeeded(panel);
				}
			}
		}
	}
	
	private void restoreDraggables() {
		if ( null == lastInfo ) {
			return;
		}
		
		// Make appropriate pieces draggable. Done each update to support login and hotseat play later.
		if ( null != playingAs && lastInfo.isUsersTurn ) {
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
		if ( null == lastInfo ) {
			return;
		}
		
		GameSquare square = (GameSquare) panel.getWidget();
		if ( null == square || null == square.marker ) {
			return;
		}

		Marker marker = square.marker;
		if ( lastInfo.isBuildingMap || (marker.player == playingAs && null != marker.movementRange && marker.movementRange > 0 )) {
			dragController.makeDraggable(square);
			//TODO Slay has a nice way of showing a piece can be interacted with.
			//Units hop up and down and buildings ready to build have a waving flag.
			//Something like that would help the player a lot.
			draggables.add(square);
		}
	}

	@Override
	public void hideScreen() {
		gameScreen.content.setVisible(false);
		refreshGameBoardTimer.cancel();
		lastInfo = null;
	}

	@Override
	public String showScreen(final PageController pageController, Long showGameId) {
		super.showScreen(pageController, showGameId);
	
		boardInitialized = false;
		lastInfo = null;
		gameScreen.publishMapButton.setVisible(false);
		gameScreen.surrenderButton.setVisible(false);

				
		currentGameId = showGameId;

		pageController.gameDataService.getGameListingById(currentGameId, new AsyncCallback<GameListingInfo>() {
			public void onFailure(Throwable caught) {
				pageController.dialogController.showError(
						"Error Finding Game",								
						"An error occurred finding the requested game.",
						true,
						caught);
				// TODO go back to main menu? offer retry/cancel?
			}

			public void onSuccess(final GameListingInfo gameListing) {
				pageController.setScreenTitle("Game " + gameListing.getName());
			}
		});

		updateGameBoard();

		gameScreen.content.setVisible(true);

		refreshGameBoardTimer.cancel();
		//TODO only schedule a new screen update when the previous finishes? repeating might build up a queue if updates are slower than refresh interval
		refreshGameBoardTimer.scheduleRepeating(GAME_BOARD_REFRESH_INTERVAL);

		return "Game";
	}

}
