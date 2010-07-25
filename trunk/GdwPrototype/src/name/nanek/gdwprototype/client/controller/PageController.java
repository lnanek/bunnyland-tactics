package name.nanek.gdwprototype.client.controller;

import name.nanek.gdwprototype.client.controller.screen.ScreenController;
import name.nanek.gdwprototype.client.controller.support.ScreenControllers;
import name.nanek.gdwprototype.client.service.GameService;
import name.nanek.gdwprototype.client.service.GameServiceAsync;
import name.nanek.gdwprototype.client.view.Page;
import name.nanek.gdwprototype.client.view.widget.GameAnchor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.RootPanel;
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

	// Services
	public final GameServiceAsync gameService = GWT.create(GameService.class);

	// View
	private final Page page = new Page();

	// Controllers
	private final SoundPlayer soundPlayer = new SoundPlayer();
	private final DialogController dialogController;
	private ScreenController currentController;

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
	
	private void showPage(String historyToken) {
		GWT.log("AppPageController#showPage: historyToken = " + historyToken);

		
		
		Long currentGameId = GameAnchor.getIdFromAnchor(historyToken);
		
		ScreenController controller = ScreenControllers.getController(historyToken);
		if ( null != currentController ) {
			currentController.hideScreen();
		}

		page.allContent.clear();
		setErrorLabel("");
		setScreenTitle(null);
		
		currentController = controller;
		controller.createScreen(this, currentGameId);
	}

	public void addScreen(Widget screen) {
		page.allContent.add(screen);
	}

	public void setScreenTitle(String screenTitle) {
		page.setScreenTitle(screenTitle);
	}

	public void setErrorLabel(String string) {
		page.errorLabel.setText(string);
	}
	
	public void setLinkHeadingToHome(boolean enabled) {
		RootPanel heading = RootPanel.get("siteHeading");
		if ( enabled ) {
			heading.clear();
			heading.getElement().setInnerHTML("");
			//Would be nice to reset everything, but doesn't work in dev mode
			//where a code server has to be specified.
			//heading.add(new Anchor("Bunnyland Tactics", "/"));
			
			heading.add(new Hyperlink("Bunnyland Tactics", ""));
		} else {
			heading.clear();
			heading.getElement().setInnerHTML("Bunnyland Tactics");
		}
	}

	/**
	 * Gets a shared dialog controller. Helps prevent having multiple stacked dialogs.
	 * @return dialog controller
	 */
	public DialogController getDialogController() {
		return dialogController;
	}

	/**
	 * Gets a shared sound player. Helps keep music playing across screens when needed.
	 * @return sound player
	 */
	public SoundPlayer getSoundPlayer() {
		return soundPlayer;
	}
}
