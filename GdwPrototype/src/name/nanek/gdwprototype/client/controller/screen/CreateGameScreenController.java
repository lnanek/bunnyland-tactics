package name.nanek.gdwprototype.client.controller.screen;

import name.nanek.gdwprototype.client.controller.PageController;
import name.nanek.gdwprototype.client.view.Page.Background;
import name.nanek.gdwprototype.client.view.screen.CreateGameScreen;
import name.nanek.gdwprototype.client.view.widget.GameAnchor;
import name.nanek.gdwprototype.shared.FieldVerifier;
import name.nanek.gdwprototype.shared.ValidationException;
import name.nanek.gdwprototype.shared.model.Game;

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

/**
 * Controls the screen for starting a game.
 * 
 * @author Lance Nanek
 *
 */
public class CreateGameScreenController extends ScreenController {

	private static final int MAP_LIST_REFRESH_INTERVAL_MS = 2000;

	private class RefreshMapListCallback implements AsyncCallback<Game[]> {
		public void onFailure(Throwable throwable) {
			//Protect against spurious call after screen hidden.
			if ( null == pageController ) return;
			
			//Show error.
			pageController.getDialogController().showError("Error Getting Maps", 
					"An error occurred getting the maps from the server.", 
					true, 
					throwable);
		}

		public void onSuccess(final Game[] maps) {
			//Protect against spurious call after screen hidden.
			if ( null == pageController ) return;

			//Get the previously selected value before the refresh.
			int previouslySelectedIndex = screen.createGameMaps.getSelectedIndex();
			String previouslySelectedValue = null;
			if ( -1 != previouslySelectedIndex ) {
				previouslySelectedValue = screen.createGameMaps.getValue(previouslySelectedIndex);
			}

			//Refresh list.
			screen.createGameMaps.clear();
			int itemIndex = 0;
			int newIndexForPreviouslySelectedValue = -1;
			for (final Game gameListing : maps) {
				if ( null != gameListing ) {
					String value = Long.toString(gameListing.getId());
					if ( value.equals(previouslySelectedValue) ) {
						newIndexForPreviouslySelectedValue = itemIndex;
					}
					screen.createGameMaps.addItem(gameListing.getDisplayName(true), value);
					itemIndex++;
				}
			}
			
			//We have maps.
			if ( itemIndex > 0 ) {
				//Nothing selected previously, or previous selection out of bounds, so select first.
				if ( -1 == newIndexForPreviouslySelectedValue 
						|| newIndexForPreviouslySelectedValue >= maps.length) {
					screen.createGameMaps.setItemSelected(0, true);
				//Select same position as previously.
				}else {
					screen.createGameMaps.setItemSelected(previouslySelectedIndex, true);
				}
				//Allow map selection and game creation.
				screen.createGameMaps.setEnabled(true);
				screen.createGameButton.setEnabled(true);
				return;
			}
			
			//No maps, so can't select map or create game.
			screen.createGameMaps.addItem("No maps found.");
			screen.createGameMaps.setEnabled(false);
			screen.createGameButton.setEnabled(false);
		}
	}

	private class CreateGameCallback implements AsyncCallback<Game> {
		public void onFailure(Throwable throwable) {
			//Protect against spurious call after screen hidden.
			if ( null == pageController ) return;
			
			//Show error and enable button so user can retry.
			pageController.getDialogController().showError("Error Creating Game", 
					"An error occurred requesting the server create the game.", 
					true, 
					throwable,
					enableCreateGameButton);
		}

		public void onSuccess(Game gameListing) {
			//Go to the created game.
			final String anchor = GameAnchor.generateAnchor(gameListing);
			History.newItem(anchor);
		}
	}

	//Trigger create game on clicks and enter key.
	private class CreateGameHandler implements ClickHandler, KeyDownHandler {
		public void onClick(ClickEvent event) {
			requestCreateGame();
		}
		//XXX This can fire when a control is hidden, but focused, and a key is pressed. Confusing for users.
		public void onKeyDown(KeyDownEvent event) {
			if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
				requestCreateGame();
			}
		}
	}
	
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
			screen.createGameMaps.setEnabled(false);
			screen.createGameNameField.setFocus(true);
			screen.createGameNameField.selectAll();

			requestRefreshMapList();
									
			refreshMapListTimer.cancel();
			// TODO would scheduling each time we update work better?
			// updates take different amounts of time for different
			// computers/networks
			refreshMapListTimer.scheduleRepeating(MAP_LIST_REFRESH_INTERVAL_MS);

			screen.content.setVisible(true);
		}
	}
	
	private PageController pageController;
	
	private CreateGameScreen screen;

	private Timer refreshMapListTimer = new Timer() {
		@Override
		public void run() {
			requestRefreshMapList();
		}
	};
	
	private Command enableCreateGameButton = new Command() {
		public void execute() {
			screen.createGameButton.setEnabled(true);
		}
	};

	private void requestCreateGame() {
		//Protect against spurious call after screen hidden.
		if ( null == pageController ) return;
		
		//TODO catch validationexception and change error label instead of showing dialog
		//TODO validate as the user types
		pageController.setErrorLabel("");	
		
		//Validate input.
		final String gameName = FieldVerifier.validateGameName(screen.createGameNameField.getText());
		
		Long mapId = null;
		int selectedMap = screen.createGameMaps.getSelectedIndex();
		if ( -1 == selectedMap ) {
			throw new ValidationException("Please select a map.");
		} else {
			mapId = new Long(screen.createGameMaps.getValue(selectedMap));
		}
		
		//Disable button so we don't get double submits.
		screen.createGameButton.setEnabled(false);
		
		//Send data to server.
		pageController.gameService.createGame(gameName, mapId, new CreateGameCallback());
	}

	private void requestRefreshMapList() {
		//Protect against spurious call after screen hidden.
		if ( null == pageController ) return;
		
		pageController.gameService.getMapNames(new RefreshMapListCallback());
	}
		
	@Override
	public void hideScreen() {
		refreshMapListTimer.cancel();
		pageController = null;
	}

	@Override
	public void createScreen(final PageController pageController, Long modelId) {
		this.pageController = pageController;
		pageController.setBackground(Background.MENU);
		pageController.setScreenTitle("Create Game");
		pageController.getSoundPlayer().playMenuScreenMusic();
		
		screen = new CreateGameScreen(pageController.getSoundPlayer());		
		screen.content.setVisible(false);
		pageController.addScreen(screen.content);

		//Button to create a game.
		CreateGameHandler handler = new CreateGameHandler();
		screen.createGameButton.addClickHandler(handler);
		screen.createGameNameField.addKeyDownHandler(handler);

		//TODO have user login in a popup frame instead, so they don't have to wait for the application to reload when they get back.
		String returnUrl = Window.Location.getHref();		
		pageController.gameService.getLoginUrlIfNeeded(returnUrl, new ShowLoginOrStartDisplayCallback());
	}

}
