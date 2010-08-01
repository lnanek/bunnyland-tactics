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
	MenuScreen screen;
	
	private PageController pageController;

	public MenuScreenController() {
	}
	
	@Override
	public void createScreen(final PageController pageController, Long modelId) {
		this.pageController = pageController;
		pageController.setBackground(Background.MENU);
		screen = new MenuScreen(pageController.getSoundPlayer());
		
		pageController.addScreen(screen.content);
		pageController.getSoundPlayer().playMenuScreenMusic();	    
		pageController.setScreenTitle("Menu");
	}

}
