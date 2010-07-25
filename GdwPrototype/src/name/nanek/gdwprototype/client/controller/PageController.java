package name.nanek.gdwprototype.client.controller;

import name.nanek.gdwprototype.client.controller.screen.ScreenController;
import name.nanek.gdwprototype.client.controller.support.ScreenControllers;
import name.nanek.gdwprototype.client.service.GameDataService;
import name.nanek.gdwprototype.client.service.GameDataServiceAsync;
import name.nanek.gdwprototype.client.view.Page;
import name.nanek.gdwprototype.client.view.widget.GameAnchor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Widget;

/**
 * Controls permanent parts of web page shown in the browser like the title and heading.
 * Defers to other controllers for the current content of the page, like a menu screen, 
 * or the play screen.
 * 
 * @author Lance Nanek
 *
 */
public class PageController {
	//TODO work on long load time when first come to game
	//break everything up into separate pieces using runAsync? have splash screen?

	// Services
	public final GameDataServiceAsync gameDataService = GWT.create(GameDataService.class);

	// View
	private final Page page = new Page();

	// Controller
	private ScreenController currentController;

	public PageController() {
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
		if ( null != currentController ) {
			currentController.hideScreen();
		}
		page.allContent.clear();
		currentController = controllerToShow;
		return controllerToShow.showScreen(this, modelId);
	}
	
	public void addScreen(Widget screen) {
		page.allContent.add(screen);
	}

	private void showPage(String historyToken) {
		GWT.log("AppPageController#showPage: historyToken = " + historyToken);

		
		
		Long currentGameId = GameAnchor.getIdFromAnchor(historyToken);
		
		ScreenController controller = ScreenControllers.getController(historyToken);
		String screenTitle = showScreenAndGetTitle(controller, currentGameId);
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
