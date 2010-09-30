package name.nanek.gdwprototype.client.controller.screen;

import name.nanek.gdwprototype.client.controller.PageController;
import name.nanek.gdwprototype.client.view.Page.Background;
import name.nanek.gdwprototype.client.view.screen.StartGameScreen;
import name.nanek.gdwprototype.client.view.widget.GameAnchor;
import name.nanek.gdwprototype.shared.model.Game;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;

/**
 * Controls the screen for playing a game.
 * 
 * @author Lance Nanek
 *
 */
public class StartGameScreenController extends ScreenController {
	
	private static final int GAME_LIST_REFRESH_INTERVAL_MS = 2000;
	
	private PageController pageController;
	
	private StartGameScreen screen;

	private Timer refreshJoinableGameListTimer = new Timer() {
		@Override
		public void run() {
			requestRefreshJoinableGameList();
		}
	};

	//Have the user login if needed, otherwise start displaying the screen contents.
	//TODO extract this class into a top level type and share with other controllers
	//take a command for what to do on successful login
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

			requestRefreshJoinableGameList();
									
			refreshJoinableGameListTimer.cancel();
			// TODO would scheduling each time we update work better?
			// updates take different amounts of time for different
			// computers/networks
			refreshJoinableGameListTimer.scheduleRepeating(GAME_LIST_REFRESH_INTERVAL_MS);
		}
	}
	
	private class RequestJoinGameHandler implements ClickHandler {
		
		private final long gameId;

		private RequestJoinGameHandler(long gameId) {
			this.gameId = gameId;
		}

		@Override
		public void onClick(ClickEvent event) {
			//Protect against spurious call after screen hidden.
			if ( null == pageController ) return;
			
			//TODO disable links and show some sort of loading indication while this is going on?
			//if server is slow user might click another link, etc.
			pageController.gameService.attemptToJoinGame(gameId, new JoinGameCallback());
		}
	}
	
	
	private class RefreshJoinableGameListCallback implements AsyncCallback<Game[]> {

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
			
			screen.joinableGamesTable.clear();
			int row = 0;
			for (final Game gameListing : gamesListing) {
				if ( null != gameListing ) {
					Anchor link = new Anchor(gameListing.getDisplayName(true));
					pageController.getSoundPlayer().addMenuClick(link);
					link.addClickHandler(new RequestJoinGameHandler(gameListing.getId()));					
					screen.joinableGamesTable.setWidget(row++, 0, link);
				}
			}
			if ( 0 == row ) {
				screen.joinableGamesTable.setText(0, 0, "No joinable games found.");
			}
		}
	}

	private class JoinGameCallback implements AsyncCallback<Game> {
		@Override
		public void onFailure(Throwable throwable) {
			//Protect against spurious call after screen hidden.
			if ( null == pageController ) return;
			
			//Show error.
			pageController.getDialogController().showError("Error Joining Game", 
					"An error occurred joining the game on the server.", 
					true, 
					throwable);
		}

		@Override
		public void onSuccess(Game result) {
			//Protect against spurious call after screen hidden.
			if ( null == pageController ) return;
			
			//Go to game screen if we successfully joined the game.
			if ( null != result ) {
				final String anchor = GameAnchor.generateAnchor(result);
				History.newItem(anchor);
				return;
			}
			
			//Otherwise tell user they didn't make it.
			pageController.getDialogController().showError("Error Joining Game", 
				"Sorry, someone else joined the game just before you! Updating the game list...", 
				false, 
				null);
			requestRefreshJoinableGameList();
		}
	};
	
	private void requestRefreshJoinableGameList() {
		//Protect against spurious call after screen hidden.
		if ( null == pageController ) return;
		
		pageController.gameService.getJoinableGameNames(new RefreshJoinableGameListCallback());
	}
	
	@Override
	public void hideScreen() {
		refreshJoinableGameListTimer.cancel();
		pageController = null;
	}

	@Override
	public void createScreen(final PageController pageController, Long modelId) {
		this.pageController = pageController;
		pageController.setBackground(Background.MENU);
		pageController.setScreenTitle("Start Game");
		pageController.getSoundPlayer().playMenuScreenMusic();
		
		screen = new StartGameScreen(pageController.getSoundPlayer());
		screen.content.setVisible(false);
		pageController.addScreen(screen.content);
		
		//TODO have user login in a popup frame instead, so they don't have to wait for the application to reload when they get back.
		String returnUrl = Window.Location.getHref();		
		pageController.gameService.getLoginUrlIfNeeded(returnUrl, new ShowLoginOrStartDisplayCallback());
	}
	
}
