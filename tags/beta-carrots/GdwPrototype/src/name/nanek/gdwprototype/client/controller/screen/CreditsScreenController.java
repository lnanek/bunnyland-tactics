package name.nanek.gdwprototype.client.controller.screen;

import name.nanek.gdwprototype.client.controller.DialogController;
import name.nanek.gdwprototype.client.controller.PageController;
import name.nanek.gdwprototype.client.controller.SoundPlayer;
import name.nanek.gdwprototype.client.model.GameListing;
import name.nanek.gdwprototype.client.view.screen.CreditsScreen;
import name.nanek.gdwprototype.client.view.screen.MenuScreen;
import name.nanek.gdwprototype.client.view.widget.GameAnchor;

import com.allen_sauer.gwt.voices.client.Sound;
import com.allen_sauer.gwt.voices.client.SoundController;
import com.allen_sauer.gwt.voices.client.handler.PlaybackCompleteEvent;
import com.allen_sauer.gwt.voices.client.handler.SoundHandler;
import com.allen_sauer.gwt.voices.client.handler.SoundLoadStateChangeEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Hyperlink;

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
		pageController.getSoundPlayer().playMenuBackgroundMusic();	    
		pageController.setScreenTitle("Credits");
	}
}