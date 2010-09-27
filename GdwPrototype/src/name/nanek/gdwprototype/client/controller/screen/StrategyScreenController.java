package name.nanek.gdwprototype.client.controller.screen;

import name.nanek.gdwprototype.client.controller.PageController;
import name.nanek.gdwprototype.client.view.Page.Background;
import name.nanek.gdwprototype.client.view.screen.StrategyScreen;

/**
 * Controls screen where the credits is shown.
 * 
 * @author Lance Nanek
 *
 */
public class StrategyScreenController extends ScreenController {
	
	@Override
	public void createScreen(final PageController pageController, Long modelId) {
		pageController.setBackground(Background.MENU);
		pageController.addScreen(new StrategyScreen().content);
		pageController.getSoundPlayer().playMenuScreenMusic();	    
		pageController.setScreenTitle("Strategy");
	}
	
}
