package name.nanek.gdwprototype.client.controller;

import name.nanek.gdwprototype.client.model.GameListingInfo;
import name.nanek.gdwprototype.client.view.screen.MenuScreen;
import name.nanek.gdwprototype.client.view.widget.GameAnchor;
import name.nanek.gdwprototype.shared.FieldVerifier;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Hyperlink;

public class MenuScreenController {

	private static final int GAME_LIST_REFRESH_INTERVAL = 5000; // ms

	MenuScreen menuScreen = new MenuScreen();

	Timer refreshGamesTableTimer = new Timer() {
		@Override
		public void run() {
			updateGamesListing();
		}
	};
	
	final AppPageController pageController;

	public MenuScreenController(AppPageController pageControllerParam) {

		this.pageController = pageControllerParam;

		pageController.addScreen(menuScreen.content);

	}

	private void updateGamesListing() {
		pageController.gameDataService.getGameNames(false, new AsyncCallback<GameListingInfo[]>() {
			public void onFailure(Throwable caught) {
				pageController.dialogController.showRpcError(DialogController.GAME_ERROR_TITLE,
						"An error occurred contacting the server to get the current games. "
								+ DialogController.POSSIBLE_NETWORK_ERROR, null);
			}

			public void onSuccess(final GameListingInfo[] gamesListing) {
				menuScreen.currentGamesTable.clear();
				int i = 0;
				for (final GameListingInfo gameListing : gamesListing) {
					String anchor = GameAnchor.generateAnchor(gameListing);
					Hyperlink link = new Hyperlink(gameListing.getDisplayName(), anchor);
					menuScreen.currentGamesTable.setWidget(i++, 0, link);
				}
			}
		});
	}

	public String showScreen() {

		menuScreen.content.setVisible(true);

		updateGamesListing();
		
		refreshGamesTableTimer.cancel();
		// TODO would scheduling each time we update work better?
		// updates take different amounts of time for different
		// computers/networks
		refreshGamesTableTimer.scheduleRepeating(GAME_LIST_REFRESH_INTERVAL);

		return null;
	}

	public void hideScreen() {
		menuScreen.content.setVisible(false);
		refreshGamesTableTimer.cancel();
	}

}
