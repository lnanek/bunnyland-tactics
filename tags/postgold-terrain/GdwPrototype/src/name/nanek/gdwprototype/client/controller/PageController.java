package name.nanek.gdwprototype.client.controller;

import name.nanek.gdwprototype.client.controller.screen.ScreenController;
import name.nanek.gdwprototype.client.controller.support.ScreenControllers;
import name.nanek.gdwprototype.client.controller.support.SoundPlayer;
import name.nanek.gdwprototype.client.service.GameService;
import name.nanek.gdwprototype.client.service.GameServiceAsync;
import name.nanek.gdwprototype.client.view.Page;
import name.nanek.gdwprototype.client.view.Page.Background;
import name.nanek.gdwprototype.client.view.widget.GameAnchor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
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
		dialogController.setSoundPlayer(soundPlayer);
		
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
		//GWT.log("PageController#showPage: historyToken = " + historyToken);

		Long currentGameId = GameAnchor.getIdFromAnchor(historyToken);
		
		ScreenController controller = ScreenControllers.getController(historyToken);
		if ( null != currentController ) {
			currentController.hideScreen();
		}
		//GWT.log("PageController#showPage: controller = " + controller);

		//When there's no history token, we're at the first page/main menu, so don't link the title.
		RootPanel heading = RootPanel.get("siteHeading");
		heading.clear();
		if ( "".equals(historyToken) ) {
			heading.getElement().setInnerHTML("Bunnyland Tactics");
		} else {
			heading.getElement().setInnerHTML("");	
			Hyperlink home = new Hyperlink("Bunnyland Tactics", "");
			soundPlayer.addMenuClick(home);
			heading.add(home);
		}
		
		page.screenContent.clear();
		setErrorLabel("");
		setScreenTitle(null);
		
		currentController = controller;
		controller.createScreen(this, currentGameId);
	}

	public void addScreen(Widget screen) {
		page.screenContent.add(screen);
	}

	public void setScreenTitle(String screenTitle) {
		page.setScreenTitle(screenTitle);
	}
	


	public void setErrorLabel(String string) {
		page.errorLabel.setText(string);
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
	
	public void setBackground(Background bg) {
		page.setBackground(bg);
	}
}
