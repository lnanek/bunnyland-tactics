package name.nanek.gdwprototype.client.controller.screen;

import com.google.gwt.core.client.GWT;

import name.nanek.gdwprototype.client.controller.PageController;
import name.nanek.gdwprototype.client.view.Page.Background;
import name.nanek.gdwprototype.client.view.screen.OptionsScreen;

/**
 * Controls screen where the options menu is shown.
 * 
 * @author Lance Nanek
 *
 */
public class OptionsScreenController extends ScreenController {

	private OptionsScreen screen;

	public OptionsScreenController() {
		GWT.log("OptionsScreenController()");
	}
	
	@Override
	public void createScreen(final PageController pageController, Long modelId) {
		GWT.log("OptionsScreenController#createScreen");
		
		pageController.setBackground(Background.MENU);
		screen = new OptionsScreen(pageController.getSoundPlayer());
		
		pageController.addScreen(screen.content);
		pageController.getSoundPlayer().playMenuScreenMusic();	    
		pageController.setScreenTitle("Options");
	}

}
