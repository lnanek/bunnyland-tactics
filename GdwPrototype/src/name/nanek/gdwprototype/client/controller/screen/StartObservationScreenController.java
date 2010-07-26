package name.nanek.gdwprototype.client.controller.screen;

import name.nanek.gdwprototype.client.controller.DialogController;
import name.nanek.gdwprototype.client.controller.PageController;
import name.nanek.gdwprototype.client.controller.SoundPlayer;
import name.nanek.gdwprototype.client.model.GameListing;
import name.nanek.gdwprototype.client.view.screen.MenuScreen;
import name.nanek.gdwprototype.client.view.screen.StartObservationScreen;
import name.nanek.gdwprototype.client.view.widget.GameAnchor;

import com.allen_sauer.gwt.voices.client.Sound;
import com.allen_sauer.gwt.voices.client.SoundController;
import com.allen_sauer.gwt.voices.client.handler.PlaybackCompleteEvent;
import com.allen_sauer.gwt.voices.client.handler.SoundHandler;
import com.allen_sauer.gwt.voices.client.handler.SoundLoadStateChangeEvent;
import com.google.gwt.core.client.GWT;
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

	private static final int GAME_LIST_REFRESH_INTERVAL = 5000; // ms

	StartObservationScreen screen = new StartObservationScreen();
	
	Timer refreshGamesTableTimer = new Timer() {
		@Override
		public void run() {
			updateGamesListing();
		}
	};
	
	private PageController pageController;

	public StartObservationScreenController() {
	}
	
	private void updateGamesListing() {
		//TODO don't show games that need a second player here
		pageController.gameService.getObservableGameNames(new AsyncCallback<GameListing[]>() {
			public void onFailure(Throwable throwable) {
				pageController.getDialogController().showError("Error Getting Games", 
					"An error occurred getting the current games from the server.", 
					true, 
					throwable);
			}

			public void onSuccess(final GameListing[] gamesListing) {
				GWT.log("got list of observable games.");
				screen.observableGamesTable.clear();
				int i = 0;
				for (final GameListing gameListing : gamesListing) {
					String anchor = GameAnchor.generateAnchor(gameListing);
					Hyperlink link = new Hyperlink(gameListing.getName(), anchor);
					screen.observableGamesTable.setWidget(i++, 0, link);
				}
				if ( 0 == i ) {
					screen.observableGamesTable.setText(0, 0, "No games in progress found.");
				}
			}
		});
	}

	@Override
	public void createScreen(final PageController pageController, Long modelId) {
		this.pageController = pageController;

		pageController.addScreen(screen.content);
		
		updateGamesListing();
		
		refreshGamesTableTimer.cancel();
		// TODO would scheduling each time we update work better?
		// updates take different amounts of time for different
		// computers/networks
		refreshGamesTableTimer.scheduleRepeating(GAME_LIST_REFRESH_INTERVAL);

		pageController.getSoundPlayer().playMenuBackgroundMusic();
	    pageController.setScreenTitle("Select a Game to Observe");
	}

	@Override
	public void hideScreen() {
		refreshGamesTableTimer.cancel();
	}

}
