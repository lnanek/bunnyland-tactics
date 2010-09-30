package name.nanek.gdwprototype.client.controller.screen;

import java.util.Map;

import name.nanek.gdwprototype.client.controller.PageController;
import name.nanek.gdwprototype.client.view.Page.Background;
import name.nanek.gdwprototype.client.view.screen.CreateMapScreen;
import name.nanek.gdwprototype.client.view.widget.GameAnchor;
import name.nanek.gdwprototype.shared.FieldVerifier;
import name.nanek.gdwprototype.shared.model.DefaultMarkers;
import name.nanek.gdwprototype.shared.model.Game;
import name.nanek.gdwprototype.shared.model.Marker;

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
	
	//Have the user login if needed, otherwise start displaying the screen contents.
	private class ShowLoginOrStartDisplayCallback implements AsyncCallback<String> {
		public void onFailure(Throwable throwable) {
			//Protect against spurious call after screen hidden.
			if ( null == pageController ) return;
			
			//Show error.
			pageController.getDialogController().showError("Error Logging In", 
					"An error occurred checking if you are logged in.", 
					true, 
					throwable);
		}

		public void onSuccess(final String loginUrl) {
			//Protect against spurious call after screen hidden.
			if ( null == pageController ) return;

			//User needs to login.
			if ( null != loginUrl ) {
				Window.open(loginUrl, "_self", ""); 
				return;
			}
			
			//User is logged in, update screen.
			screen.content.setVisible(true);
			screen.createMapNameField.setFocus(true);
			screen.createMapNameField.selectAll();
		}
	}

	private class CreateMapCallback implements AsyncCallback<Game> {
		public void onFailure(Throwable throwable) {
			//Protect against spurious call after screen hidden.
			if ( null == pageController ) return;

			//Show error and enable button so user can retry.
			pageController.getDialogController().showError("Error Creating Map", 
					"An error occurred requesting the server create the map.", 
					true, 
					throwable,
					enableCreateMapButton);
		}

		public void onSuccess(Game gameListing) {
			//Go to map editor.
			final String anchor = GameAnchor.generateAnchor(gameListing);
			History.newItem(anchor);
		}
	}

	//Trigger create map on clicks and enter key.
	private class CreateMapHandler implements ClickHandler, KeyDownHandler {
		public void onClick(ClickEvent event) {
			requestCreateMap();
		}
		//XXX This can fire when a control is hidden, but focused, and a key is pressed. Confusing for users.
		public void onKeyDown(KeyDownEvent event) {
			if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
				requestCreateMap();
			}
		}
	}
	
	private PageController pageController;
	
	private CreateMapScreen screen;
	
	private Command enableCreateMapButton = new Command() {
		public void execute() {
			screen.createMapButton.setEnabled(true);
		}
	};

	private void requestCreateMap() {
		//Protect against spurious call after screen hidden.
		if ( null == pageController ) return;

		//TODO catch validationexception and change error label instead of showing dialog
		//TODO validate as the user types
		pageController.setErrorLabel("");				
		
		//Validate input.
		final String gameName = FieldVerifier.validateGameName(screen.createMapNameField.getText());
		
		int height = FieldVerifier.validateAndParseInt("Height", screen.boardHeightField, 2, 100);
		int width = FieldVerifier.validateAndParseInt("Width", screen.boardWidthField, 2, 100);
		int generateCarrotPeriod = FieldVerifier.validateAndParseInt("Turns Per New Carrot", 
				screen.generateCarrotPeriodField, 0, 1000);

		for( Map.Entry<Marker, TextBox> markerVisionRangeEntry : screen.playingPieceToVisibilityField.entrySet() ) {
			Marker marker = markerVisionRangeEntry.getKey();
			int visionRange = FieldVerifier.validateAndParseInt("Vision range", markerVisionRangeEntry.getValue(), 0, 100);
			marker.visionRange = visionRange;
		}
		for( Map.Entry<Marker, TextBox> markerMovementRangeEntry : screen.playingPieceToMovementField.entrySet() ) {
			Marker marker = markerMovementRangeEntry.getKey();
			int movementRange = FieldVerifier.validateAndParseInt("Movement range", markerMovementRangeEntry.getValue(), 0, 100);
			marker.movementRange = movementRange;
		}
	
		//Disable button so we don't get double submits.
		screen.createMapButton.setEnabled(false);
		
		//Send data to server.
		pageController.gameService.createMap(gameName, generateCarrotPeriod, width, height,  
				DefaultMarkers.MAP_MAKING_PIECES, new CreateMapCallback());
	}
		
	@Override
	public void hideScreen() {
		pageController = null;
	}
	
	@Override
	public void createScreen(final PageController pageController, Long modelId) {
		this.pageController = pageController;
		pageController.setBackground(Background.MENU);
		pageController.setScreenTitle("Create Map");
		pageController.getSoundPlayer().playMenuScreenMusic();
		
		screen = new CreateMapScreen(pageController.getSoundPlayer());
		screen.content.setVisible(false);
		pageController.addScreen(screen.content);

		//Button to create a map.
		CreateMapHandler handler = new CreateMapHandler();
		screen.createMapButton.addClickHandler(handler);
		screen.createMapButton.addKeyDownHandler(handler);
				
		//TODO have user login in a popup frame instead, so they don't have to wait for the application to reload when they get back.
		String returnUrl = Window.Location.getHref();
		pageController.gameService.getLoginUrlIfNeeded(returnUrl, new ShowLoginOrStartDisplayCallback());
	}

}
