package name.nanek.gdwprototype.client.controller;

import name.nanek.gdwprototype.client.model.GameListingInfo;
import name.nanek.gdwprototype.client.view.screen.StartScreen;
import name.nanek.gdwprototype.client.view.widget.GameAnchor;
import name.nanek.gdwprototype.shared.FieldVerifier;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Hyperlink;

public class StartScreenController {

	private static final int GAME_LIST_REFRESH_INTERVAL = 5000; // ms
	
	final AppPageController pageController;
	
	StartScreen startGameScreen = new StartScreen();

	Timer refreshGamesTableTimer = new Timer() {
		@Override
		public void run() {
			updateGamesListing();
		}
	};
	
	Command enableCreateGame = new Command() {
		public void execute() {
			startGameScreen.createGameButton.setEnabled(true);
			//startGameScreen.createGameButton.setFocus(true);
		}
	};

	public StartScreenController(AppPageController pageControllerParam) {
		this.pageController = pageControllerParam;
		pageController.addScreen(startGameScreen.content);
		

		class CreateGameHandler implements ClickHandler, KeyDownHandler {
			/**
			 * Fired when the user clicks on the sendButton.
			 */
			public void onClick(ClickEvent event) {
				sendNameToServer();
			}

			/**
			 * Fired when the user types in the nameField.
			 */
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					sendNameToServer();
				}
			}

			/**
			 * Send the name from the nameField to the server and wait for a
			 * response.
			 */
			private void sendNameToServer() {
				// First, we validate the input.

				pageController.setErrorLabel("");
				final String textToServer = startGameScreen.createGameNameField.getText();
				if (!FieldVerifier.isValidGameName(textToServer)) {
					pageController.setErrorLabel(FieldVerifier.VALID_GAME_NAME_ERROR_MESSAGE);
					return;
				}

				// Then, we send the input to the server.
				startGameScreen.createGameButton.setEnabled(false);
				pageController.gameDataService.createGame(textToServer, new AsyncCallback<GameListingInfo>() {
					public void onFailure(Throwable caught) {
						pageController.dialogController.showRpcError(DialogController.GAME_ERROR_TITLE,
								"An error occurred contacting the server to create the game. "
										+ DialogController.POSSIBLE_NETWORK_ERROR, enableCreateGame);
					}

					public void onSuccess(GameListingInfo gameListing) {
						enableCreateGame.execute();
						//updateGamesListing();
						final String anchor = GameAnchor.generateAnchor(gameListing);
						History.newItem(anchor);
					}
				});
			}
		}

		// Add a handler to send the name to the server
		CreateGameHandler handler = new CreateGameHandler();
		startGameScreen.createGameButton.addClickHandler(handler);
		startGameScreen.createGameNameField.addKeyDownHandler(handler);
	}
	
	AsyncCallback<GameListingInfo> attemptToJoinGame = new AsyncCallback<GameListingInfo>() {
		@Override
		public void onFailure(Throwable caught) {
			pageController.dialogController.showRpcError(DialogController.GAME_ERROR_TITLE,
					"An error occurred joining the game. " + DialogController.POSSIBLE_NETWORK_ERROR,
					null);
		}

		@Override
		public void onSuccess(GameListingInfo result) {
			if (null == result) {
				pageController.dialogController.showRpcError(DialogController.GAME_ERROR_TITLE,
						"Sorry, someone else joined just before you!",
						null);
			} else {
				final String anchor = GameAnchor.generateAnchor(result);
				History.newItem(anchor);
			}
		}
		
	};
	
	private void updateGamesListing() {
		pageController.gameDataService.getGameNames(true, new AsyncCallback<GameListingInfo[]>() {
			public void onFailure(Throwable caught) {
				pageController.dialogController.showRpcError(DialogController.GAME_ERROR_TITLE,
						"An error occurred contacting the server to get the current games. "
								+ DialogController.POSSIBLE_NETWORK_ERROR, null);
			}

			public void onSuccess(final GameListingInfo[] gamesListing) {
				startGameScreen.currentGamesTable.clear();
				int i = 0;
				for (final GameListingInfo gameListing : gamesListing) {
					if ( null != gameListing ) {
						Anchor link = new Anchor(gameListing.getDisplayName());
						link.addClickHandler(new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
								pageController.gameDataService.attemptToJoinGame(gameListing.getId(), attemptToJoinGame);
							}
						});					
						startGameScreen.currentGamesTable.setWidget(i++, 0, link);
					}
				}
			}
		});
	}
	
	public void hideScreen() {
		startGameScreen.content.setVisible(false);
		refreshGamesTableTimer.cancel();
	}

	public String showScreen() {
		
		//TODO have user login in a popup frame instead, so they don't have to wait for the application to reload when they get back.
		String returnUrl = Window.Location.getHref();// + '#' + AppPageController.START_GAME_SCREEN_HISTORY_TOKEN;
		
		pageController.gameDataService.getLoginUrlIfNeeded(returnUrl, new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				pageController.dialogController.showRpcError(DialogController.GAME_ERROR_TITLE,
						"An error occurred checking if you are logged in. " + DialogController.POSSIBLE_NETWORK_ERROR,
						null);
				// TODO go back to main menu?
			}

			public void onSuccess(final String loginUrl) {
				if ( null != loginUrl ) {
					Window.open(loginUrl, "_self", ""); 
				}
			}
		});


		startGameScreen.content.setVisible(true);

		startGameScreen.createGameNameField.setFocus(true);
		startGameScreen.createGameNameField.selectAll();
		
		updateGamesListing();
		
		refreshGamesTableTimer.cancel();
		// TODO would scheduling each time we update work better?
		// updates take different amounts of time for different
		// computers/networks
		refreshGamesTableTimer.scheduleRepeating(GAME_LIST_REFRESH_INTERVAL);
		
		return "Start Game";
	}


}
