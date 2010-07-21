package name.nanek.gdwprototype.client.controller;

import name.nanek.gdwprototype.client.model.GameListingInfo;
import name.nanek.gdwprototype.client.view.screen.MenuScreen;
import name.nanek.gdwprototype.client.view.widget.GameAnchor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Hyperlink;

public class MenuScreenController extends ScreenController {

	private static final int GAME_LIST_REFRESH_INTERVAL = 5000; // ms

	MenuScreen menuScreen = new MenuScreen();

	Timer refreshGamesTableTimer = new Timer() {
		@Override
		public void run() {
			updateGamesListing();
		}
	};
	
	private PageController pageController;

	public MenuScreenController() {
	}
	
	@Override
	public void createScreen(PageController pageController) {

		this.pageController = pageController;

		pageController.addScreen(menuScreen.content);

	}

	private void updateGamesListing() {
		//TODO don't show games that need a second player here
		pageController.gameDataService.getObservableGameNames(new AsyncCallback<GameListingInfo[]>() {
			public void onFailure(Throwable throwable) {
				pageController.dialogController.showError("Error Getting Games", 
					"An error occurred getting the current games from the server.", 
					true, 
					throwable);
			}

			public void onSuccess(final GameListingInfo[] gamesListing) {
				GWT.log("MenuScreenController got list of observable games.");
				menuScreen.currentGamesTable.clear();
				int i = 0;
				for (final GameListingInfo gameListing : gamesListing) {
					String anchor = GameAnchor.generateAnchor(gameListing);
					Hyperlink link = new Hyperlink(gameListing.getName(), anchor);
					menuScreen.currentGamesTable.setWidget(i++, 0, link);
				}
				if ( 0 == i ) {
					menuScreen.currentGamesTable.setText(0, 0, "No games in progress found.");
				}
			}
		});
	}

	@Override
	public String showScreen(final PageController pageController, Long modelId) {
		super.showScreen(pageController, modelId);

		menuScreen.content.setVisible(true);

		updateGamesListing();
		
		refreshGamesTableTimer.cancel();
		// TODO would scheduling each time we update work better?
		// updates take different amounts of time for different
		// computers/networks
		refreshGamesTableTimer.scheduleRepeating(GAME_LIST_REFRESH_INTERVAL);

		return null;
	}

	@Override
	public void hideScreen() {
		menuScreen.content.setVisible(false);
		refreshGamesTableTimer.cancel();
	}

}
