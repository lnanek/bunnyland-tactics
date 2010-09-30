package name.nanek.gdwprototype.client.controller.screen;

import name.nanek.gdwprototype.client.controller.PageController;
import name.nanek.gdwprototype.client.view.Page.Background;
import name.nanek.gdwprototype.client.view.screen.StartObservationScreen;
import name.nanek.gdwprototype.client.view.widget.GameAnchor;
import name.nanek.gdwprototype.shared.model.Game;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Hyperlink;

/**
 * Controls screen where a game to observe is selected.
 * 
 * @author Lance Nanek
 *
 */
public class StartObservationScreenController extends ScreenController {

	private static final int GAME_LIST_REFRESH_INTERVAL_MS = 2000;

	private class RefreshObservableGameListCallback implements AsyncCallback<Game[]> {
		public void onFailure(Throwable throwable) {
			//Protect against spurious call after screen hidden.
			if ( null == pageController ) return;
			
			//Show error.
			pageController.getDialogController().showError("Error Getting Games", 
				"An error occurred getting the current games from the server.", 
				true, 
				throwable);
		}

		public void onSuccess(final Game[] gamesListing) {
			//Protect against spurious call after screen hidden.
			if ( null == pageController ) return;

			screen.observableGamesTable.clear();
			int i = 0;
			for (final Game gameListing : gamesListing) {
				String anchor = GameAnchor.generateAnchor(gameListing);
				Hyperlink link = new Hyperlink(gameListing.getDisplayName(true), anchor);
				pageController.getSoundPlayer().addMenuClick(link);
				screen.observableGamesTable.setWidget(i++, 0, link);
			}
			if ( 0 == i ) {
				screen.observableGamesTable.setText(0, 0, "No games in progress found.");
			}
		}
	}

	private StartObservationScreen screen = new StartObservationScreen();
	
	private Timer refreshObservableGameListTimer = new Timer() {
		@Override
		public void run() {
			requestUpdateObservableGameList();
		}
	};
	
	private PageController pageController;
	
	private void requestUpdateObservableGameList() {
		//Protect against spurious call after screen hidden.
		if ( null == pageController ) return;
		
		pageController.gameService.getObservableGameNames(new RefreshObservableGameListCallback());
	}

	@Override
	public void hideScreen() {
		refreshObservableGameListTimer.cancel();
		pageController = null;
	}

	@Override
	public void createScreen(final PageController pageController, Long modelId) {
		this.pageController = pageController;
		pageController.setBackground(Background.MENU);
		pageController.getSoundPlayer().playMenuScreenMusic();
	    pageController.setScreenTitle("Select a Game to Observe");
	    
		pageController.addScreen(screen.content);
		
		requestUpdateObservableGameList();
		
		refreshObservableGameListTimer.cancel();
		// TODO would scheduling each time we update work better?
		// updates take different amounts of time for different
		// computers/networks
		refreshObservableGameListTimer.scheduleRepeating(GAME_LIST_REFRESH_INTERVAL_MS);
	}
	
}
