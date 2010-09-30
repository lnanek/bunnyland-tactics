package name.nanek.gdwprototype.client.controller.screen;

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
	
	@Override
	public void createScreen(final PageController pageController, Long modelId) {	
		pageController.setBackground(Background.MENU);
		pageController.getSoundPlayer().playMenuScreenMusic();	    
		pageController.setScreenTitle("Options");

		OptionsScreen screen = new OptionsScreen(pageController.getSoundPlayer());
		pageController.addScreen(screen.content);
	}

}
