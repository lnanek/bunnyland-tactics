package name.nanek.gdwprototype.client.controller.screen;

import name.nanek.gdwprototype.client.controller.PageController;
import name.nanek.gdwprototype.client.model.GameListing;
import name.nanek.gdwprototype.client.view.screen.StartGameScreen;
import name.nanek.gdwprototype.client.view.widget.GameAnchor;

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
	
	private static final int GAME_LIST_REFRESH_INTERVAL = 5000; // ms
	
	private PageController pageController;
	
	StartGameScreen startGameScreen = new StartGameScreen();

	Timer refreshListsTimer = new Timer() {
		@Override
		public void run() {
			updateGamesListing();
		}
	};
	
	public StartGameScreenController() {
	}
	
	AsyncCallback<GameListing> attemptToJoinGame = new AsyncCallback<GameListing>() {
		@Override
		public void onFailure(Throwable throwable) {
			pageController.getDialogController().showError("Error Joining Game", 
					"An error occurred joining the game on the server.", 
					true, 
					throwable);
		}

		@Override
		public void onSuccess(GameListing result) {
			if (null == result) {
				pageController.getDialogController().showError("Error Joining Game", 
						"Sorry, someone else joined the game just before you!", 
						false, 
						null);
			} else {
				final String anchor = GameAnchor.generateAnchor(result);
				History.newItem(anchor);
			}
		}
		
	};
	
	private void updateGamesListing() {
		pageController.gameService.getJoinableGameNames(new AsyncCallback<GameListing[]>() {
			public void onFailure(Throwable throwable) {
				pageController.getDialogController().showError("Error Getting Games", 
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
								pageController.gameService.attemptToJoinGame(gameListing.getId(), attemptToJoinGame);
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
		refreshListsTimer.cancel();
	}

	@Override
	public void createScreen(final PageController pageController, Long modelId) {
		this.pageController = pageController;

		startGameScreen.content.setVisible(false);
		pageController.addScreen(startGameScreen.content);
		
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

					updateGamesListing();
										
					refreshListsTimer.cancel();
					// TODO would scheduling each time we update work better?
					// updates take different amounts of time for different
					// computers/networks
					refreshListsTimer.scheduleRepeating(GAME_LIST_REFRESH_INTERVAL);
				}
			}
		});

		pageController.setScreenTitle("Start Game");
		pageController.getSoundPlayer().playMenuScreenMusic();
	}
	
}
