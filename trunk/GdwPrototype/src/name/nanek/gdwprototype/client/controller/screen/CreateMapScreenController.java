package name.nanek.gdwprototype.client.controller.screen;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import name.nanek.gdwprototype.client.controller.PageController;
import name.nanek.gdwprototype.client.model.GameListing;
import name.nanek.gdwprototype.client.view.screen.CreateMapScreen;
import name.nanek.gdwprototype.client.view.widget.GameAnchor;
import name.nanek.gdwprototype.shared.FieldVerifier;
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Controls the screen for creating a map.
 * 
 * @author Lance Nanek
 *
 */
public class CreateMapScreenController extends ScreenController {
	
	//TODO split creategameormapscreen out of startgamescreen

	private static final int GAME_LIST_REFRESH_INTERVAL = 5000; // ms
	
	private PageController pageController;
	
	CreateMapScreen startGameScreen;
	
	Command enableCreateMap = new Command() {
		public void execute() {
			startGameScreen.createMapButton.setEnabled(true);
		}
	};

	public CreateMapScreenController() {
	}

	private void createMap() {

		//TODO catch validationexception and change error label instead of showing dialog
		//TODO validate as the user types
		pageController.setErrorLabel("");				
		
		//Validate input.
		final String gameName = FieldVerifier.validateGameName(startGameScreen.createMapNameField.getText());
		
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
	
		// Then, we send the input to the server.
		startGameScreen.createMapButton.setEnabled(false);
		pageController.gameService.createGameOrMap(gameName, settings, null, new AsyncCallback<GameListing>() {
			public void onFailure(Throwable throwable) {
				pageController.getDialogController().showError("Error Creating Map", 
						"An error occurred requesting the server create the map.", 
						true, 
						throwable,
						enableCreateMap);
			}

			public void onSuccess(GameListing gameListing) {
				enableCreateMap.execute();
				final String anchor = GameAnchor.generateAnchor(gameListing);
				History.newItem(anchor);
			}
		});
	}
		
	@Override
	public void createScreen(final PageController pageController, Long modelId) {
		this.pageController = pageController;
		startGameScreen = new CreateMapScreen(pageController.getSoundPlayer());
		
		startGameScreen.content.setVisible(false);
		pageController.addScreen(startGameScreen.content);

		//Button to create a map.
		class CreateGameHandler implements ClickHandler, KeyDownHandler {
			/**
			 * Fired when the user clicks on the sendButton.
			 */
			public void onClick(ClickEvent event) {
				createMap();
			}

			/**
			 * Fired when the user types in the nameField.
			 */
			//TODO this can fire when hidden sometimes, maybe disable it when screen hidden or make sure focus is moved?
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					createMap();
				}
			}
		}
		CreateGameHandler handler = new CreateGameHandler();
		startGameScreen.createMapButton.addClickHandler(handler);
		startGameScreen.createMapButton.addKeyDownHandler(handler);
				
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
					startGameScreen.createMapNameField.setFocus(true);
					startGameScreen.createMapNameField.selectAll();
				}
			}
		});

		pageController.setScreenTitle("Create Map");
		pageController.getSoundPlayer().playMenuScreenMusic();
	}


}
