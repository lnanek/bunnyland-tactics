package name.nanek.gdwprototype.client.controller.screen;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import name.nanek.gdwprototype.client.controller.DialogController;
import name.nanek.gdwprototype.client.controller.PageController;
import name.nanek.gdwprototype.client.model.GameListing;
import name.nanek.gdwprototype.client.view.screen.CreateGameScreen;
import name.nanek.gdwprototype.client.view.screen.StartGameScreen;
import name.nanek.gdwprototype.client.view.widget.GameAnchor;
import name.nanek.gdwprototype.shared.FieldVerifier;
import name.nanek.gdwprototype.shared.ValidationException;
import name.nanek.gdwprototype.shared.model.GameSettings;
import name.nanek.gdwprototype.shared.model.Marker;
import name.nanek.gdwprototype.shared.model.Markers;

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
import com.google.gwt.user.client.ui.TextBox;

/**
 * Controls the screen for starting a game.
 * 
 * @author Lance Nanek
 *
 */
public class CreateGameScreenController extends ScreenController {
	
	private static final int GAME_LIST_REFRESH_INTERVAL = 5000; // ms
	
	private PageController pageController;
	
	CreateGameScreen startGameScreen = new CreateGameScreen();

	Timer refreshListsTimer = new Timer() {
		@Override
		public void run() {
			updateMapListing();
		}
	};
	
	Command enableCreateGame = new Command() {
		public void execute() {
			startGameScreen.createGameButton.setEnabled(true);
		}
	};

	public CreateGameScreenController() {
	}

	private void createGame() {

		//TODO catch validationexception and change error label instead of showing dialog
		//TODO validate as the user types
		pageController.setErrorLabel("");				
		
		//Validate input.
		final String gameName = FieldVerifier.validateGameName(startGameScreen.createGameNameField.getText());
		
		Long mapId = null;
		int selectedMap = startGameScreen.createGameMaps.getSelectedIndex();
		if ( -1 == selectedMap ) {
			throw new ValidationException("Please select a map.");
		} else {
			mapId = new Long(startGameScreen.createGameMaps.getValue(selectedMap));
		}
		
		// Then, we send the input to the server.
		startGameScreen.createGameButton.setEnabled(false);
		pageController.gameService.createGameOrMap(gameName, null, mapId, new AsyncCallback<GameListing>() {
			public void onFailure(Throwable throwable) {
				pageController.getDialogController().showError("Error Creating Game", 
						"An error occurred requesting the server create the game.", 
						true, 
						throwable,
						enableCreateGame);
			}

			public void onSuccess(GameListing gameListing) {
				enableCreateGame.execute();
				//updateGamesListing();
				final String anchor = GameAnchor.generateAnchor(gameListing);
				History.newItem(anchor);
			}
		});
	}

	private void updateMapListing() {
		pageController.gameService.getMapNames(new AsyncCallback<GameListing[]>() {
			public void onFailure(Throwable throwable) {
				pageController.getDialogController().showError("Error Getting Maps", 
						"An error occurred getting the maps from the server.", 
						true, 
						throwable);
			}

			public void onSuccess(final GameListing[] gamesListing) {
				int previouslySelected = startGameScreen.createGameMaps.getSelectedIndex();
				
				startGameScreen.createGameMaps.clear();
				boolean foundMap = false;
				for (final GameListing gameListing : gamesListing) {
					if ( null != gameListing ) {
						startGameScreen.createGameMaps.addItem(gameListing.getName(),Long.toString(gameListing.getId()));
						foundMap = true;
					}
				}
				if ( foundMap ) {
					if ( -1 == previouslySelected || previouslySelected >= gamesListing.length) {
						startGameScreen.createGameMaps.setItemSelected(0, true);
					}else {
						startGameScreen.createGameMaps.setItemSelected(previouslySelected, true);
					}
					startGameScreen.createGameMaps.setEnabled(true);
				} else {
					startGameScreen.createGameMaps.addItem("No maps found.");
					startGameScreen.createGameMaps.setEnabled(false);
				}
			}
		});
	}
		
	@Override
	public void hideScreen() {
		refreshListsTimer.cancel();
	}

	@Override
	public void createScreen(final PageController pageController, Long modelId) {
		this.pageController = pageController;

		startGameScreen.content.setVisible(false);
		pageController.addScreen(startGameScreen.content);

		//Button to create a game.
		class CreateGameHandler implements ClickHandler, KeyDownHandler {
			/**
			 * Fired when the user clicks on the sendButton.
			 */
			public void onClick(ClickEvent event) {
				createGame();
			}

			/**
			 * Fired when the user types in the nameField.
			 */
			//TODO this can fire when hidden sometimes, maybe disable it when screen hidden or make sure focus is moved?
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					createGame();
				}
			}
		}
		CreateGameHandler handler = new CreateGameHandler();
		startGameScreen.createGameButton.addClickHandler(handler);
		startGameScreen.createGameNameField.addKeyDownHandler(handler);
		
		
		//TODO have user login in a popup frame instead, so they don't have to wait for the application to reload when they get back.
		String returnUrl = Window.Location.getHref();
		
		pageController.gameService.getLoginUrlIfNeeded(returnUrl, new AsyncCallback<String>() {
			public void onFailure(Throwable throwable) {
				pageController.getDialogController().showError("Error Logging In", 
						"An error occurred checking if you are logged in.", 
						true, 
						throwable);
				// TODO go back to main menu?
			}

			public void onSuccess(final String loginUrl) {
				if ( null != loginUrl ) {
					Window.open(loginUrl, "_self", ""); 
				} else {
					startGameScreen.content.setVisible(true);
					startGameScreen.createGameMaps.setEnabled(false);
					startGameScreen.createGameNameField.setFocus(true);
					startGameScreen.createGameNameField.selectAll();

					updateMapListing();
										
					refreshListsTimer.cancel();
					// TODO would scheduling each time we update work better?
					// updates take different amounts of time for different
					// computers/networks
					refreshListsTimer.scheduleRepeating(GAME_LIST_REFRESH_INTERVAL);
				}
			}
		});

		pageController.setScreenTitle("Create Game");
		pageController.getSoundPlayer().playMenuBackgroundMusic();
	}

}
