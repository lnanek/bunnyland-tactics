package name.nanek.gdwprototype.client.controller;

import name.nanek.gdwprototype.client.service.GameDataService;
import name.nanek.gdwprototype.client.service.GameDataServiceAsync;
import name.nanek.gdwprototype.client.view.AppPage;
import name.nanek.gdwprototype.client.view.widget.GameAnchor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Widget;

public class AppPageController {

	public static final String START_GAME_SCREEN_HISTORY_TOKEN = "start_game";
	
	// Services
	public final GameDataServiceAsync gameDataService = GWT.create(GameDataService.class);

	// View
	private final AppPage page = new AppPage();

	// Controllers
	private final GameScreenController gameScreenController = new GameScreenController(this);
	private final MenuScreenController menuScreenController = new MenuScreenController(this);
	private final StartScreenController startScreenController = new StartScreenController(this);
	public final DialogController dialogController;

	public AppPageController(DialogController dialogController) {

		this.dialogController = dialogController;

		// Show requested page when history changes.
		History.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				showPage(event.getValue());
			}
		});

		// Show requested page when first started.
		History.fireCurrentHistoryState();
	}

	public void addScreen(Widget screen) {
		page.allContent.add(screen);
	}

	private void showPage(String historyToken) {
		GWT.log("AppPageController#showPage: historyToken = " + historyToken);
		
		Long currentGameId = GameAnchor.getIdFromAnchor(historyToken);
		String screenTitle;
		
		//Show start game screen.
		if ( START_GAME_SCREEN_HISTORY_TOKEN.equals(historyToken) ) {
			GWT.log("AppPageController#showPage: showing start game screen.");
			
			gameScreenController.hideScreen();
			menuScreenController.hideScreen();
			screenTitle = startScreenController.showScreen();
			
		//Show main menu.	
		} else if (null == currentGameId) {
			GWT.log("AppPageController#showPage: showing menu screen.");
			
			startScreenController.hideScreen();
			gameScreenController.hideScreen();
			screenTitle = menuScreenController.showScreen();

		//Show game screen.
		} else {
			GWT.log("AppPageController#showPage: showing game screen.");
			
			startScreenController.hideScreen();
			menuScreenController.hideScreen();
			screenTitle = gameScreenController.showScreen(currentGameId);

		}

		setErrorLabel("");
		setScreenTitle(screenTitle);
	}

	public void setScreenTitle(String screenTitle) {
		page.setScreenTitle(screenTitle);
	}

	public void setErrorLabel(String string) {
		page.errorLabel.setText(string);
	}

}
