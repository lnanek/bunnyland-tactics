package name.nanek.gdwprototype.client.controller;

import name.nanek.gdwprototype.client.service.GameDataService;
import name.nanek.gdwprototype.client.service.GameDataServiceAsync;
import name.nanek.gdwprototype.client.view.Page;
import name.nanek.gdwprototype.client.view.widget.GameAnchor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Widget;

public class PageController {

	public static final String START_GAME_SCREEN_HISTORY_TOKEN = "start_game";
	
	// Services
	public final GameDataServiceAsync gameDataService = GWT.create(GameDataService.class);

	// View
	private final Page page = new Page();

	// Controllers
	private final GameScreenController gameScreenController = new GameScreenController();
	private final MenuScreenController menuScreenController = new MenuScreenController();
	private final StartScreenController startScreenController = new StartScreenController();
	public final DialogController dialogController;

	private ScreenController activeScreenController;

	public PageController(DialogController dialogController) {

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

	public String showScreenAndGetTitle(ScreenController controllerToShow, Long modelId) {
		if ( null != activeScreenController ) {
			activeScreenController.hideScreen();
		}
		activeScreenController = controllerToShow;
		return controllerToShow.showScreen(this, modelId);
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
			screenTitle = showScreenAndGetTitle(startScreenController, null);
			
		//Show main menu.	
		} else if (null == currentGameId) {
			GWT.log("AppPageController#showPage: showing menu screen.");
			screenTitle = showScreenAndGetTitle(menuScreenController, null);

		//Show game screen.
		} else {
			GWT.log("AppPageController#showPage: showing game screen.");
			screenTitle = showScreenAndGetTitle(gameScreenController, currentGameId);

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
