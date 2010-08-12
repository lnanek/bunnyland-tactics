package name.nanek.gdwprototype.client.view.screen;

import name.nanek.gdwprototype.client.controller.support.ScreenControllers;
import name.nanek.gdwprototype.client.controller.support.SoundPlayer;
import name.nanek.gdwprototype.client.controller.support.ScreenControllers.Screen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Holds widgets for the options screen.
 * 
 * @author Lance Nanek
 *
 */
public class OptionsScreen {
	
	public VerticalPanel content = new VerticalPanel();

	public OptionsScreen(SoundPlayer soundPlayer) {
		GWT.log("OptionsScreen(SoundPlayer)");
		
		Hyperlink observeLink = new Hyperlink("Observe a Game", 
				ScreenControllers.getHistoryToken(Screen.START_OBSERVATION));
		soundPlayer.addMenuClick(observeLink);
		content.add(observeLink);
		
		content.add(new HTML("<br />"));

		Hyperlink createMapLink = new Hyperlink("Create a Map", 
				ScreenControllers.getHistoryToken(Screen.CREATE_MAP));
		soundPlayer.addMenuClick(createMapLink);
		content.add(createMapLink);
	}

}
