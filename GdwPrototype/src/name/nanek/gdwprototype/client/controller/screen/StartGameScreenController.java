package name.nanek.gdwprototype.client.controller.screen;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import name.nanek.gdwprototype.client.controller.DialogController;
import name.nanek.gdwprototype.client.controller.PageController;
import name.nanek.gdwprototype.client.model.GameListing;
import name.nanek.gdwprototype.client.view.screen.StartScreen;
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
 * Controls the screen for starting or joining a game.
 * 
 * @author Lance Nanek
 *
 */
public class StartGameScreenController extends ScreenController {
	
	//TODO split creategameormapscreen out of startgamescreen

	private static final int GAME_LIST_REFRESH_INTERVAL = 5000; // ms
	
	private PageController pageController;
	
	StartScreen startGameScreen = new StartScreen();

	Timer refreshListsTimer = new Timer() {
		@Override
		public void run() {
			updateGamesListing();
			updateMapListing();
		}
	};
	
	Command enableCreateGame = new Command() {
		public void execute() {
			startGameScreen.createGameButton.setEnabled(true);
			//startGameScreen.createGameButton.setFocus(true);
		}
	};

	public StartGameScreenController() {
	}

	private void createGame(boolean createMap) {

		//TODO catch validationexception and change error label instead of showing dialog
		//TODO validate as the user types
		pageController.setErrorLabel("");				
		
		//Validate input.
		final String gameName = FieldVerifier.validateGameName(startGameScreen.createGameNameField.getText());
		
		GameSettings settings = new GameSettings();				
		int height = FieldVerifier.validateAndParseInt("Height", startGameScreen.boardHeightField, 2, 100);
		settings.setBoardHeight(height);
		int width = FieldVerifier.validateAndParseInt("Width", startGameScreen.boardWidthField, 2, 100);
		settings.setBoardWidth(width);

		//Set<Marker> markers = new HashSet<Marker>();
		for( Map.Entry<Marker, TextBox> markerVisionRangeEntry : startGameScreen.playingPieceToVisibilityField.entrySet() ) {
			Marker marker = markerVisionRangeEntry.getKey();
			int visionRange = FieldVerifier.validateAndParseInt("Vision range", markerVisionRangeEntry.getValue(), 0, 100);
			marker.visionRange = visionRange;
		}
		for( Map.Entry<Marker, TextBox> markerMovementRangeEntry : startGameScreen.playingPieceToMovementField.entrySet() ) {
			Marker marker = markerMovementRangeEntry.getKey();
			int movementRange = FieldVerifier.validateAndParseInt("Movement range", markerMovementRangeEntry.getValue(), 0, 100);
			marker.movementRange = movementRange;
		}
		//TODO would an arraylist be cheaper to serialize?
		//an array doesn't work out well, not persisted on server side
		Set<Marker> markers = new HashSet<Marker>(Arrays.asList(Markers.PLAYING_PIECES));
		settings.setMarkers(markers);

		Long mapId = null;
		if ( !createMap ) {
			int selectedMap = startGameScreen.createGameMaps.getSelectedIndex();
			if ( -1 == selectedMap ) {
				throw new ValidationException("Please select a map.");
			} else {
				mapId = new Long(startGameScreen.createGameMaps.getValue(selectedMap));
			}
		}
		
		// Then, we send the input to the server.
		startGameScreen.createGameButton.setEnabled(false);
		pageController.gameDataService.createGame(gameName, settings, mapId, new AsyncCallback<GameListing>() {
			public void onFailure(Throwable throwable) {
				new DialogController().showError("Error Creating Game", 
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
	
	@Override
	public void createScreen(final PageController pageController) {
		this.pageController = pageController;
		
		pageController.addScreen(startGameScreen.content);

		//Button to create a game.
		class CreateGameHandler implements ClickHandler, KeyDownHandler {
			/**
			 * Fired when the user clicks on the sendButton.
			 */
			public void onClick(ClickEvent event) {
				createGame(false);
			}

			/**
			 * Fired when the user types in the nameField.
			 */
			//TODO this can fire when hidden sometimes, maybe disable it when screen hidden or make sure focus is moved?
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					createGame(false);
				}
			}
		}
		CreateGameHandler handler = new CreateGameHandler();
		startGameScreen.createGameButton.addClickHandler(handler);
		startGameScreen.createGameNameField.addKeyDownHandler(handler);
		
		//Button to create a map.
		class CreateMapHandler implements ClickHandler {
			public void onClick(ClickEvent event) {
				createGame(true);
			}
		}
		startGameScreen.createMapButton.addClickHandler(new CreateMapHandler());
	}
	
	AsyncCallback<GameListing> attemptToJoinGame = new AsyncCallback<GameListing>() {
		@Override
		public void onFailure(Throwable throwable) {
			new DialogController().showError("Error Joining Game", 
					"An error occurred joining the game on the server.", 
					true, 
					throwable);
		}

		@Override
		public void onSuccess(GameListing result) {
			if (null == result) {
				new DialogController().showError("Error Joining Game", 
						"Sorry, someone else joined the game just before you!", 
						false, 
						null);
			} else {
				final String anchor = GameAnchor.generateAnchor(result);
				History.newItem(anchor);
			}
		}
		
	};
	
	private void updateMapListing() {
		pageController.gameDataService.getMapNames(new AsyncCallback<GameListing[]>() {
			public void onFailure(Throwable throwable) {
				new DialogController().showError("Error Getting Maps", 
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
	
	private void updateGamesListing() {
		pageController.gameDataService.getJoinableGameNames(new AsyncCallback<GameListing[]>() {
			public void onFailure(Throwable throwable) {
				new DialogController().showError("Error Getting Games", 
						"An error occurred getting the current games from the server.", 
						true, 
						throwable);
			}

			public void onSuccess(final GameListing[] gamesListing) {
				startGameScreen.joinableGamesTable.clear();
				int i = 0;
				for (final GameListing gameListing : gamesListing) {
					if ( null != gameListing ) {
						Anchor link = new Anchor(gameListing.getName());
						link.addClickHandler(new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
								pageController.gameDataService.attemptToJoinGame(gameListing.getId(), attemptToJoinGame);
							}
						});					
						startGameScreen.joinableGamesTable.setWidget(i++, 0, link);
					}
				}
				if ( 0 == i ) {
					startGameScreen.joinableGamesTable.setText(0, 0, "No joinable games found.");
				}
			}
		});
	}
	
	@Override
	public void hideScreen() {
		startGameScreen.content.setVisible(false);
		refreshListsTimer.cancel();
	}

	@Override
	public String showScreen(final PageController pageController, Long modelId) {
		super.showScreen(pageController, modelId);
		
		//TODO have user login in a popup frame instead, so they don't have to wait for the application to reload when they get back.
		String returnUrl = Window.Location.getHref();
		
		pageController.gameDataService.getLoginUrlIfNeeded(returnUrl, new AsyncCallback<String>() {
			public void onFailure(Throwable throwable) {
				new DialogController().showError("Error Logging In", 
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

					updateGamesListing();
					updateMapListing();
										
					refreshListsTimer.cancel();
					// TODO would scheduling each time we update work better?
					// updates take different amounts of time for different
					// computers/networks
					refreshListsTimer.scheduleRepeating(GAME_LIST_REFRESH_INTERVAL);
				}
			}
		});

		return "Start Game";
	}


}
