package name.nanek.gdwprototype.client.controller;

import name.nanek.gdwprototype.client.model.GameListingInfo;
import name.nanek.gdwprototype.client.model.GamePlayInfo;
import name.nanek.gdwprototype.client.model.Marker;
import name.nanek.gdwprototype.client.model.Player;
import name.nanek.gdwprototype.client.model.PositionInfo;
import name.nanek.gdwprototype.client.view.screen.GameScreen;
import name.nanek.gdwprototype.client.view.widget.GameSquare;
import name.nanek.gdwprototype.client.view.widget.PaletteImage;
import name.nanek.gdwprototype.client.view.widget.TableCellPanel;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

public class GameScreenController {
	//TODO hotseat mode: move -> black screen with next player button -> switches player

	//TODO simple win condition of some sort? whoever runs out of pieces after placing the first one?
	
	private static final int GAME_BOARD_REFRESH_INTERVAL = 1000; // ms

	private static final int BOARD_DIMENSION = 12;

	GameScreen gameScreen = new GameScreen();

	private Long currentGameId;

	private Player playingAs;

	private Player fogOfWarAs;

	private boolean dragInProgress = false;

	public PickupDragController dragController;

	final AppPageController pageController;

	//TODO don't refresh when window blurred
	//detecting refocus seems dicey, chrome isn't calling properly when switch back to tab from another, 
	//but can show pause dialog and have user click to unpause
	Timer refreshGameBoardTimer = new Timer() {
		@Override
		public void run() {
			updateGameBoard();
		}
	};

	public GameScreenController(AppPageController pageController) {

		this.pageController = pageController;

		pageController.addScreen(gameScreen.content);

		gameScreen.fogOfWarPlayerOneRadio.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if ( event.getValue() ) {
					fogOfWarAs = Player.ONE;
					updateGameBoard();
				}
			}
		});
		gameScreen.fogOfWarPlayerTwoRadio.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if ( event.getValue() ) {
					fogOfWarAs = Player.TWO;
					updateGameBoard();
				}
			}
		});
		gameScreen.fogOfWarNoneRadio.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if ( event.getValue() ) {
					fogOfWarAs = null;
					updateGameBoard();
				}
			}
		});


		// RootPanel.get().setPixelSize(600, 600);
		// RootPanel.get().getElement().getStyle().setProperty("position" ,
		// "relative");
		dragController = new PickupDragController(RootPanel.get(), false);
		dragController.setBehaviorDragProxy(true);
		dragController.setBehaviorMultipleSelection(false);
		dragController.addDragHandler(new DragHandler() {

			@Override
			public void onDragEnd(DragEndEvent event) {
				dragInProgress = false;
			}

			@Override
			public void onDragStart(DragStartEvent event) {
				dragInProgress = true;
			}

			@Override
			public void onPreviewDragEnd(DragEndEvent event) throws VetoDragException {
			}

			@Override
			public void onPreviewDragStart(DragStartEvent event) throws VetoDragException {
			}
		});

		for (int i = 0; i < Marker.ALL_MARKERS.length - 1; i++) {
			Marker marker = Marker.ALL_MARKERS[i];
			// if ( marker.player != null && marker.player != playingAs )
			// continue;
			Image image = new PaletteImage(marker);
			TableCellPanel panel = new TableCellPanel(image, gameScreen.markers, 0, i);
			dragController.makeDraggable(image);
			GameScreenDropController simpleDropController = new GameScreenDropController(panel, this);
			dragController.registerDropController(simpleDropController);
		}

		for (int column = 0; column < BOARD_DIMENSION; column++) {
			for (int row = 0; row < BOARD_DIMENSION; row++) {
				TableCellPanel panel = new TableCellPanel(null, gameScreen.gameBoard, column, row);
				GameScreenDropController simpleDropController = new GameScreenDropController(panel, this);
				dragController.registerDropController(simpleDropController);
			}
		}
	}

	public void moveMarker(Integer sourceRow, Integer sourceColumn, Integer destRow, Integer destColumn,
			String newImageSource) {

		pageController.gameDataService.moveMarker(currentGameId, sourceRow, sourceColumn, destRow, destColumn,
				newImageSource, new AsyncCallback<GamePlayInfo>() {
					public void onFailure(Throwable caught) {
						pageController.dialogController.showRpcError(DialogController.GAME_ERROR_TITLE,
								"An error occurred contacting the server to move the game piece. "
										+ DialogController.POSSIBLE_NETWORK_ERROR, null);
					}

					public void onSuccess(GamePlayInfo info) {
						// TODO if we skipped move numbers, update board
						// immediately
						// may not be needed when switch to turn taking

						updateGameBoardWithInfo(info);
					}
				});
	}

	// private PositionInfo[] lastPositionsUpdate;

	private void updateGameBoard() {
		
		//TODO check which players turn it is and enable controls if that player, show waiting for other player otherwise

		if (null == currentGameId || dragInProgress || !updatesRequired) {
			return;
		}

		pageController.gameDataService.getPositionsByGameId(currentGameId, new AsyncCallback<GamePlayInfo>() {
			public void onFailure(Throwable caught) {
				pageController.dialogController.showRpcError(DialogController.GAME_ERROR_TITLE,
						"An error occurred contacting the server for the current game piece positions. "
								+ DialogController.POSSIBLE_NETWORK_ERROR, null);
			}

			public void onSuccess(final GamePlayInfo info) {

				updateGameBoardWithInfo(info);
			}
		});
	}

	private void setVisibileSquares(int markerRow, int markerCol, Marker marker) {
		if (null == marker || marker.player == null || marker.visionRange == null)
			return;

		if (marker.player != fogOfWarAs)
			return;

		//TODO pick start and end positions in these two loops based on vision range at least, no need to iterate every square
		for (int column = 0; column < BOARD_DIMENSION; column++) {
			for (int row = 0; row < BOARD_DIMENSION; row++) {
				int rowDistance = Math.abs(markerRow - row);
				int colDistance = Math.abs(markerCol - column);
				int totalDistance = rowDistance + colDistance;
				if (totalDistance <= marker.visionRange) {
					TableCellPanel panel = (TableCellPanel) gameScreen.gameBoard.getWidget(row, column);
					GameSquare gameSquare = (GameSquare) panel.getWidget();
					if (null != gameSquare) {
						gameSquare.isVisible = true;
					}
				}
			}
		}
	}

	private boolean updatesRequired = true;
	
	private void updateGameBoardWithInfo(final GamePlayInfo info) {
		if (dragInProgress) {
			return;
		}

		//TODO have separate values for playingAs and for fogOfWarAs? that way observer can switch sides to watch
		
		playingAs = info.playingAs;
		//TODO bold and color this?
		String pieceColor = playingAs == Player.ONE ? "black" : "red";
		
		if ( null == playingAs ) {
			gameScreen.turnStatusLabel.setText("You are observing this game and cannot make moves.");
			updatesRequired = true;
		} else if ( info.isUsersTurn ) {
			gameScreen.turnStatusLabel.setText("It's your turn. Drag a " + pieceColor + " piece to make your move!");
			updatesRequired = false;
		//TODO disable controls as well, right now they can still drag for these other cases, although it will show an error message and undo it.
		} else if ( info.needsSecondPlayer ) {
			gameScreen.turnStatusLabel.setText("Waiting for a second player to join the game.");
			updatesRequired = true;
		} else {
			gameScreen.turnStatusLabel.setText("Please wait while the other player moves a piece.");
			updatesRequired = true;
		}
	
		PositionInfo[] positions = info.positions;
		
		// Remove any pieces without a position, they've been moved.
		for (int column = 0; column < BOARD_DIMENSION; column++) {
			for (int row = 0; row < BOARD_DIMENSION; row++) {
				boolean hasPosition = false;
				for (final PositionInfo position : positions) {
					if (position.getColumn() == column && position.getRow() == row) {
						hasPosition = true;
						break;
					}
				}
				if (!hasPosition) {
					TableCellPanel panel = (TableCellPanel) gameScreen.gameBoard.getWidget(row, column);

					// XXX do we need to unregister any previous image with the
					// drag controller?
					/*
					 * Image currentImage = (Image) panel.getWidget(); if ( null
					 * != currentImage ) { panel.remove(currentImage); }
					 */
					// Grass.
					Image image = new GameSquare(Marker.ALL_MARKERS[10]);
					panel.setWidget(image);
				}
			}
		}

		// Update the image for any position if needed.
		for (final PositionInfo position : positions) {
			TableCellPanel panel = (TableCellPanel) gameScreen.gameBoard.getWidget(position.getRow(), position
					.getColumn());

			// setPageTitle("Getting cell at: " + position.getRow() + ", " +
			// position.getColumn());

			Image currentImage = (Image) panel.getWidget();
			if (null == currentImage || !currentImage.getUrl().endsWith(position.getMarkerSource())) {
				Marker marker = Marker.markerBySource.get(position.getMarkerSourceFilename());
				Image image = new GameSquare(marker);
				dragController.makeDraggable(image);
				panel.setWidget(image);
			}
		}

		if ( null == playingAs ) {
			gameScreen.fogOfWarPanel.setVisible(true);
			gameScreen.playingPiecesPanel.setVisible(false);
		} else {
			gameScreen.fogOfWarPanel.setVisible(false);
			gameScreen.playingPiecesPanel.setVisible(true);
			fogOfWarAs = playingAs;
		}
		
		// TODO update visibility immediately instead of waiting for next server update?
		if ( null != fogOfWarAs ) {
			// Clear visibility.
			for (int column = 0; column < BOARD_DIMENSION; column++) {
				for (int row = 0; row < BOARD_DIMENSION; row++) {
					TableCellPanel panel = (TableCellPanel) gameScreen.gameBoard.getWidget(row, column);
					GameSquare gameSquare = (GameSquare) panel.getWidget();
					gameSquare.isVisible = false;
				}
			}
	
			// Determine what is visible.
			for (int column = 0; column < BOARD_DIMENSION; column++) {
				for (int row = 0; row < BOARD_DIMENSION; row++) {
					TableCellPanel panel = (TableCellPanel) gameScreen.gameBoard.getWidget(row, column);
					GameSquare gameSquare = (GameSquare) panel.getWidget();
					setVisibileSquares(row, column, gameSquare.marker);
					/*
					 * if ( null != gameSquare ) { Marker marker =
					 * gameSquare.marker; if ( null != marker && marker.player ==
					 * playingAs ) { gameSquare.isVisible = true;
					 * 
					 * } }
					 */
				}
			}
	
			// Color non-visible squares black.
			for (int column = 0; column < BOARD_DIMENSION; column++) {
				for (int row = 0; row < BOARD_DIMENSION; row++) {
					TableCellPanel panel = (TableCellPanel) gameScreen.gameBoard.getWidget(row, column);
					GameSquare gameSquare = (GameSquare) panel.getWidget();
					if (null != gameSquare && !gameSquare.isVisible) {
						gameSquare.setMarker(Marker.ALL_MARKERS[13]);
					}
				}
			}
		}
	}

	public void hideScreen() {
		gameScreen.content.setVisible(false);
		refreshGameBoardTimer.cancel();
	}

	public String showScreen(Long showGameId) {
		
		//TODO check if user one of the two players, enable controls if so, disable otherwise
		
		currentGameId = showGameId;

		pageController.gameDataService.getGameListingById(currentGameId, new AsyncCallback<GameListingInfo>() {
			public void onFailure(Throwable caught) {
				pageController.dialogController.showRpcError(DialogController.GAME_ERROR_TITLE,
						"An error occurred looking up the requested game. " + DialogController.POSSIBLE_NETWORK_ERROR,
						null);
				// TODO go back to main menu?
			}

			public void onSuccess(final GameListingInfo gameListing) {
				pageController.setScreenTitle("Game \"" + gameListing.getDisplayName() + "\"");
			}
		});

		updateGameBoard();

		gameScreen.content.setVisible(true);

		refreshGameBoardTimer.cancel();
		refreshGameBoardTimer.scheduleRepeating(GAME_BOARD_REFRESH_INTERVAL);

		return "Game";
	}

}
