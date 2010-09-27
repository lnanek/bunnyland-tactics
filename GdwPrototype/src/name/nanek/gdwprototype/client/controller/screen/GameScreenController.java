package name.nanek.gdwprototype.client.controller.screen;

import java.util.Arrays;
import java.util.HashSet;

import name.nanek.gdwprototype.client.controller.PageController;
import name.nanek.gdwprototype.client.controller.screen.support.GameScreenBoardController;
import name.nanek.gdwprototype.client.controller.screen.support.GameScreenDropController;
import name.nanek.gdwprototype.client.controller.screen.support.VisibilityCalculator;
import name.nanek.gdwprototype.client.controller.support.ScreenControllers;
import name.nanek.gdwprototype.client.controller.support.ScreenControllers.Screen;
import name.nanek.gdwprototype.client.model.GameDisplayInfo;
import name.nanek.gdwprototype.client.model.GameUpdateInfo;
import name.nanek.gdwprototype.client.view.Page.Background;
import name.nanek.gdwprototype.client.view.screen.GameScreen;
import name.nanek.gdwprototype.client.view.screen.GameScreen.FogOfWarChangeListener;
import name.nanek.gdwprototype.client.view.widget.GameSquare;
import name.nanek.gdwprototype.client.view.widget.PaletteImage;
import name.nanek.gdwprototype.client.view.widget.TableCellPanel;
import name.nanek.gdwprototype.shared.model.Marker;
import name.nanek.gdwprototype.shared.model.Player;

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

	private static final int GAME_BOARD_REFRESH_INTERVAL_MS = 1000;

	private class SetupBoardCallback implements AsyncCallback<GameDisplayInfo> {
		public void onFailure(Throwable caught) {
			if ( null == pageController ) return;
			
			//TODO any other errors where user has to manually refresh to retry where that could be mentioned in the error message?
			//TODO retry for the user
			//TODO alternate way to show errors than dialogs? "reconnecting" bar with throbber at top?
			pageController.getDialogController().showError(
					"Error Setting Up Board",								
					"An error occurred asking the server for the game/map settings. " + 
					"You can refresh your browser to retry.",
					true,
					caught);
		}

		public void onSuccess(final GameDisplayInfo info) {
			if ( null == pageController ) return;
			
			setupBoard(info);
		}
	}

	private class GameDragHandler implements DragHandler {
		@Override
		public void onDragEnd(DragEndEvent event) {
			if ( null == pageController ) return;
			
			dragInProgress = false;
			
			//If was dragging something in a game, not a map editor.
			if ( null != displayInfo && !displayInfo.game.isMap() ) {
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
			if ( null == pageController ) return;
			
			//GWT.log("onDragStart: draggable = " + event.getContext().draggable.getClass());
			//GWT.log("onDragStart: draggable.parent = " + event.getContext().draggable.getParent().getClass());
			
			dragSource = (TableCellPanel) event.getContext().draggable.getParent();
			
			dragInProgress = true;
			
			pageController.getSoundPlayer().playPickupPiceSound();

			//If dragging something in a game, not a map editor.
			if ( null != displayInfo && !displayInfo.game.isMap() ) {
				
				//Highlight where it can go.
				GameSquare gameSquare = (GameSquare) event.getContext().draggable;
				if ( gameSquare instanceof PaletteImage ) {
					return;
				}
				Marker marker = gameSquare.previousMarker;
				
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
			//This isn't useful for deciding which pieces the player can drag.
			//if we make everything draggable, but veto here, the cursor still changes
			//when the draggable piece is moused over. So it is better to only make movable
			//pieces draggable.
		}
	}
	
	private class RefreshGameBoardCallback implements AsyncCallback<GameUpdateInfo> {
		public void onFailure(Throwable caught) {
			if ( null == pageController ) return;
			
			pageController.getDialogController().showError(
					"Error Getting Positions",								
					"An error occurred asking the server for the current game piece positions.",
					true,
					caught);
		}

		public void onSuccess(final GameUpdateInfo info) {
			if ( null == pageController ) return;
			
			updateGameBoardWithInfo(info);
		}
	}

	private class MoveMarkerCallback implements AsyncCallback<GameUpdateInfo> {
		public void onFailure(Throwable caught) {
			if ( null == pageController ) return;
			
			pageController.getDialogController().showError(
					"Error Moving Piece",								
					"An error occurred asking the game server to move the requested piece.",
					true,
					caught);
			pageController.getSoundPlayer().playInGameErrorSound();
			refreshGameBoardNeeded = true;
			requestRefreshGameBoard();
		}

		public void onSuccess(GameUpdateInfo info) {
			if ( null == pageController ) return;
			
			//TODO players take turns, so nothing changed but what we dragged, so just update fog of war and turn status?
			//technically we don't even need a positions update and it could be removed to make moving things respond quicker
			updateGameBoardWithInfo(info);
		}
	}

	public GameScreen gameScreen;

	private Long gameId;

	private Player fogOfWarAs;

	private boolean dragInProgress;

	private PickupDragController dragController;

	private PageController pageController;
	
	public HashSet<GameSquare> draggables;
	
	private boolean refreshGameBoardNeeded = true;

	private boolean playedGameOverMusic;
	
	private GameDisplayInfo displayInfo;
	
	private Integer lastPlayedSoundForMoveCount;
	
	private TableCellPanel dragSource;
	
	private GameScreenBoardController boardController;
	
	//TODO don't refresh when window blurred
	//detecting refocus seems dicey, chrome isn't calling properly when switch back to tab from another, 
	//but can show pause dialog and have user click to unpause
	private Timer refreshGameBoardTimer = new Timer() {
		@Override
		public void run() {
			requestRefreshGameBoard();
		}
	};

	@Override
	public void onFogOfWarChange(Player fogOfWarAs) {
		this.fogOfWarAs = fogOfWarAs;
		refreshGameBoardNeeded = true;
		requestRefreshGameBoard();
	}
	
	public GameDisplayInfo getCurrentGamePlayInfo() {
		return displayInfo;
	}
	
	public TableCellPanel getDragSource() {
		return dragSource;
	}
	
	public void moveMarker(Integer sourceRow, Integer sourceColumn, Integer destRow, Integer destColumn,
			GameSquare draggedImage, final Marker replacedMarker) {

		if ( null == pageController ) return;
		
		//TODO clearing and restoring draggables isn't needed for map building
		clearDraggables();
		
		Long movedMarkerId = draggedImage.previousMarker.getId();
		pageController.gameService.moveMarker(gameId, sourceRow, sourceColumn, destRow, destColumn,
				movedMarkerId, new MoveMarkerCallback());
	}

	private void requestRefreshGameBoard() {
		
		if ( dragInProgress || !refreshGameBoardNeeded || null == pageController ) {
			return;
		}

		pageController.gameService.getPositionsByGameId(gameId, new RefreshGameBoardCallback());
	}
	
	private void clearDraggables() {
		for(GameSquare square : draggables) {
			dragController.makeNotDraggable(square);
		}
		draggables.clear();
	}
	
	private void updateGameBoardWithInfo(final GameUpdateInfo info) {
		if (dragInProgress || null == pageController) {
			return;
		}
		
		displayInfo.playInfo = info;
		
		//Update publish/surrender controls.
		//Hide if game is over or map is published.
		if ( info.ended ) {
			gameScreen.publishMapButton.setVisible(false);
			gameScreen.surrenderButton.setVisible(false);
		//Else the game is still running or the map unpublished.
		//Show publish map button if we're building a map and the user is the map creator.
		} else if ( displayInfo.game.isMap() ) {
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
		if ( null == info.playingAs || displayInfo.game.isMap() || info.ended ) {
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
		
		//Update background.
		if ( null == info.currentPlayersTurn ) {
			pageController.setBackground(Background.MENU);
		} else if ( Player.ONE == info.currentPlayersTurn ) {
			pageController.setBackground(Background.BLACKS_TURN);
		} else {
			pageController.setBackground(Background.REDS_TURN);
		}

		//Play a sound for an active game/map if this is the first time we got this move data.
		//TODO we currently make these sounds after the server has been asked if the move is legal
		//if would be nice to play them immediately. Maybe have a move validator that both the client
		//and server share
		if ( !info.ended && info.moveCount > 0 && (null == lastPlayedSoundForMoveCount || lastPlayedSoundForMoveCount < info.moveCount) ) {
			lastPlayedSoundForMoveCount = info.moveCount;
			
			//Always piece placement sound when placing units on a map.
			if ( displayInfo.game.isMap() ) {
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
			if (!displayInfo.game.isMap() && info.isUsersTurn ) {
				pageController.getSoundPlayer().playYourTurnSound();
			}
		}
		
		//Play game over music if needed.
		if ( !displayInfo.game.isMap() && !playedGameOverMusic ) {
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
		if ( displayInfo.game.isMap() ) {
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
			refreshGameBoardNeeded = true;
		} else if ( info.ended ) {
			if ( Player.ONE == info.winner ) {
				status = "Black won!";
			} else if ( Player.TWO == info.winner ) {
				status = "Red won!";
			} else {
				status = "Game over.";
			}
			refreshGameBoardNeeded = false;
		} else if ( null == info.playingAs ) {
			status = "You are observing this game and cannot make moves.";
			refreshGameBoardNeeded = true;
		} else if ( info.isUsersTurn ) {
			String pieceColor = info.playingAs == Player.ONE ? "black" : "red";
			status = "It's your turn. Drag a " + pieceColor + " piece to make your move!";
			refreshGameBoardNeeded = false;
		} else if ( info.needsSecondPlayer ) {
			status = "Waiting for a second player to join the game.";
			refreshGameBoardNeeded = true;
		} else {
			status = "Please wait while the other player moves a piece.";
			refreshGameBoardNeeded = true;
		}
		gameScreen.statusLabel.setText(status +" ");

		int boardHeight = displayInfo.game.getBoardHeight();
		int boardWidth = displayInfo.game.getBoardWidth();

		//TODO keep track of game move number and only redo board if new move number?

		//Clear draggables.
		// TODO more efficient algorithm? right now we make some things undraggable just to make them draggable again
		clearDraggables();

		// TODO update visibility immediately on move instead of waiting for server update?		
		boolean[][] visibleSquares = VisibilityCalculator.calculateVisibility(fogOfWarAs, boardWidth, boardHeight, info.positions);
		boardController.setPositions(info, dragController, visibleSquares);
				
		gameScreen.content.setVisible(true);
	}

	@Override
	public void hideScreen() {
		refreshGameBoardTimer.cancel();
		displayInfo = null;
		pageController = null;
	}
	
	class SurrenderCallback implements AsyncCallback<Void> {
		public void onFailure(Throwable caught) {
			if ( null == pageController ) return;
			
			pageController.getDialogController().showError(
					"Error Surrendering",								
					"An error occurred asking the game server to surrender.",
					true,
					caught);
			gameScreen.surrenderButton.setEnabled(true);
			//Force a board update so draggable pieces are draggable again.
			refreshGameBoardNeeded = true;
			requestRefreshGameBoard();
		}

		public void onSuccess(Void unused) {
			if ( null == pageController ) return;
			
			refreshGameBoardNeeded = true;
			requestRefreshGameBoard();
		}
	};
	class SurrenderClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			if ( null == pageController ) return;

			clearDraggables();
			gameScreen.surrenderButton.setEnabled(false);
			pageController.gameService.surrender(gameId, displayInfo.playInfo.playingAs, new SurrenderCallback());
		}
	}
	
	class PublishMapCallback implements AsyncCallback<Void> {
		public void onFailure(Throwable caught) {
			if ( null == pageController ) return;
			
			pageController.getDialogController().showError(
					"Error Publishing Map",								
					"An error occurred asking the game server to publish the map.",
					true,
					caught);
			gameScreen.publishMapButton.setEnabled(true);
		}

		public void onSuccess(Void unused) {
			if ( null == pageController ) return;

			//Go to create game screen.
			History.newItem(ScreenControllers.getHistoryToken(Screen.CREATE_GAME));
		}
	};
	class PublishMapClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			if ( null == pageController ) return;
			
			gameScreen.publishMapButton.setEnabled(false);
			pageController.gameService.publishMap(gameId, new PublishMapCallback());
		}
	}
	
	@Override
	public void createScreen(final PageController pageController, final Long gameId) {
		this.gameId = gameId;
		if ( null == gameId ) {
			History.newItem(ScreenControllers.getHistoryToken(Screen.MENU));
			return;
		}
		
		this.pageController = pageController;
		pageController.getSoundPlayer().playInGameMusic();
		
		gameScreen = new GameScreen(pageController.getSoundPlayer());
		pageController.addScreen(gameScreen.content);	
		gameScreen.setFogOfWarChangeListener(this);
		//TODO remember players who have seen this before and don't show?
		gameScreen.piecesDialog.show();

		draggables = new HashSet<GameSquare>();
		dragController = new PickupDragController(RootPanel.get(), false);
		dragController.setBehaviorDragProxy(false);
		dragController.setBehaviorMultipleSelection(false);
		dragController.addDragHandler(new GameDragHandler());
		
		gameScreen.surrenderButton.addClickHandler(new SurrenderClickHandler());

		gameScreen.publishMapButton.addClickHandler(new PublishMapClickHandler());

		//Setup screen with game/map specific information.
		pageController.gameService.getDisplayInfo(gameId, new SetupBoardCallback());
	}
	
	private void setupBoard(GameDisplayInfo info) {

		if ( null == pageController ) return;
		
		displayInfo = info;
		
		String type = info.game.isMap() ? "Creating Map " : "Playing Game ";
		pageController.setScreenTitle(type + info.game.getDisplayName(false));

		//Fill build palette if needed.
		//GWT.log("GameScreenController#initializeBoardIfNeeded: isBuildingMap = " + info.isBuildingMap);
		//GWT.log("GameScreenController#initializeBoardIfNeeded: isUsersTurn = " + info.isUsersTurn);
		if ( info.game.isMap() && info.playInfo.isUsersTurn ) {
			gameScreen.mapBuilderPalettePanel.setVisible(true);
			int col = 0;
			Arrays.sort(info.markers);
			for ( Marker marker : info.markers ) {
				Image image = new PaletteImage(marker);
				TableCellPanel panel = new TableCellPanel(image, gameScreen.markers, 0, col++, null, null);
				dragController.makeDraggable(image);
				GameScreenDropController simpleDropController = new GameScreenDropController(panel, this);
				dragController.registerDropController(simpleDropController);
			}
			//TODO random options: fill board with random terrain, random palette entry that places a random terrain, random brush that can be dragged across area to add random terrain, etc..
		} else {
			gameScreen.mapBuilderPalettePanel.setVisible(false);
		}
		
		//Fill board table for game size.
		int height = info.game.getBoardHeight();
		int width = info.game.getBoardWidth();
		TableCellPanel[][] panels = new TableCellPanel[height][width];
		boardController = new GameScreenBoardController(this, info, panels);
		for (int column = 0; column < width; column++) {
			for (int row = 0; row < height; row++) {
				TableCellPanel panel = new TableCellPanel(null, gameScreen.gameBoard, row, column, row, column);
				panels[row][column] = panel;
				GameScreenDropController simpleDropController = new GameScreenDropController(panel, this);
				dragController.registerDropController(simpleDropController);
				
				//TODO need to change where can drop based on dragged piece's movement, and highlight where can drop for user as well
				//TODO adjust what drop controllers are registered? or just veto wrong moves?
			}
		}	
		
		//Start regular board updates.
		updateGameBoardWithInfo(info.playInfo);
		//TODO only schedule a new screen update when the previous finishes? repeating might build up a queue if updates are slower than refresh interval
		refreshGameBoardTimer.scheduleRepeating(GAME_BOARD_REFRESH_INTERVAL_MS);
	}

	public void notifyUserBadDrop() {		
		if ( null == pageController ) return;

		pageController.getSoundPlayer().playInGameErrorSound();
	}

}
