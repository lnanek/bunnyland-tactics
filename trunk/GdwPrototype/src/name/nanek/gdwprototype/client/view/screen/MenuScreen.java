package name.nanek.gdwprototype.client.view.screen;

import name.nanek.gdwprototype.client.controller.support.ScreenControllers;
import name.nanek.gdwprototype.client.controller.support.SoundPlayer;
import name.nanek.gdwprototype.client.controller.support.ScreenControllers.Screen;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Holds widgets for the menu screen.
 * 
 * @author Lance Nanek
 *
 */
public class MenuScreen {
	
	public VerticalPanel content = new VerticalPanel();

	public MenuScreen(SoundPlayer soundPlayer) {
		Hyperlink playLink = new Hyperlink("Play", 
				ScreenControllers.getHistoryToken(Screen.START_GAME));
		soundPlayer.addMenuClick(playLink);
		content.add(playLink);
		content.add(new HTML("<br />"));

		Hyperlink optionsLink = new Hyperlink("Options", 
				ScreenControllers.getHistoryToken(Screen.OPTIONS));
		soundPlayer.addMenuClick(optionsLink);
		content.add(optionsLink);
		content.add(new HTML("<br />"));

		Hyperlink creditsLink = new Hyperlink("Credits", 
				ScreenControllers.getHistoryToken(Screen.CREDITS));
		soundPlayer.addMenuClick(creditsLink);
		content.add(creditsLink);
	}

}
