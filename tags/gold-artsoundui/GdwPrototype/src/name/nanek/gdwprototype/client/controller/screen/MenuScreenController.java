package name.nanek.gdwprototype.client.controller.screen;

import name.nanek.gdwprototype.client.controller.PageController;
import name.nanek.gdwprototype.client.view.Page.Background;
import name.nanek.gdwprototype.client.view.screen.MenuScreen;

/**
 * Controls screen where the main menu is shown.
 * 
 * @author Lance Nanek
 *
 */
public class MenuScreenController extends ScreenController {
		
	@Override
	public void createScreen(final PageController pageController, Long modelId) {
		pageController.setBackground(Background.MENU);
		pageController.getSoundPlayer().playMenuScreenMusic();	    
		pageController.setScreenTitle("Menu");

		MenuScreen screen = new MenuScreen(pageController.getSoundPlayer());
		pageController.addScreen(screen.content);
	}

}
