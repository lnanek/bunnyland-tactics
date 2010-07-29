package name.nanek.gdwprototype.client.controller.screen;

import name.nanek.gdwprototype.client.controller.PageController;
import name.nanek.gdwprototype.client.view.screen.CreditsScreen;

/**
 * Controls screen where the credits is shown.
 * 
 * @author Lance Nanek
 *
 */
public class CreditsScreenController extends ScreenController {

	private CreditsScreen screen = new CreditsScreen();
	
	private PageController pageController;

	public CreditsScreenController() {
	}
	
	@Override
	public void createScreen(final PageController pageController, Long modelId) {
		this.pageController = pageController;

		pageController.addScreen(screen.content);
		pageController.getSoundPlayer().playMenuScreenMusic();	    
		pageController.setScreenTitle("Credits");
	}
}
