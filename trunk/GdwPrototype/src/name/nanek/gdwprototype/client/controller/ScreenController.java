package name.nanek.gdwprototype.client.controller;

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
