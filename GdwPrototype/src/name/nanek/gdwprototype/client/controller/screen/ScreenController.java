package name.nanek.gdwprototype.client.controller.screen;

import name.nanek.gdwprototype.client.controller.PageController;

/**
 * Controls a UI screen, a section of content shown to the user to allow performing some task.
 * This includes things like playing the game, or navigating menus.
 * 
 * @author Lance Nanek
 *
 */
public abstract class ScreenController {
	
	boolean isCreated;
	
	public abstract void hideScreen();
	
	public String showScreen(PageController pageController, Long modelId) {
		if ( !isCreated ) {
			createScreen(pageController);
			isCreated = true;
		}
		return null;
	}
	
	public abstract void createScreen(PageController pageController);

}
