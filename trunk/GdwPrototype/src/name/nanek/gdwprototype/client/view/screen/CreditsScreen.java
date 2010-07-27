package name.nanek.gdwprototype.client.view.screen;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Holds widgets that list the games that can be observed.
 * 
 * @author Lance Nanek
 *
 */
public class CreditsScreen {
	//has a lot of inlined strings, but they are only used once, not sure they are worth constants
	
	public VerticalPanel content = new VerticalPanel();

	public CreditsScreen() {

		HTML joinGameLabel = new HTML("<h3>Development Team:</h3>");
		joinGameLabel.addStyleName("heavy");
		content.add(joinGameLabel);
		VerticalPanel indented = new VerticalPanel();
		indented.addStyleName("indented");
		indented.add(new Label("Adlee Fayyaz"));
		indented.add(new Label("Evan Rothstein"));
		indented.add(new Anchor(
				"Foster Birch (original idea conceived by)", 
				"http://smileonacloudyday.com/"));		
		indented.add(new Anchor(
				"Lance Nanek", 
				"http://nanek.name/"));
		content.add(indented);

		HTML musicLabel = new HTML("<h3>Sound:</h3>");
		musicLabel.addStyleName("heavy");
		content.add(musicLabel);
		indented = new VerticalPanel();
		indented.addStyleName("indented");
		indented.add(new Anchor(
				"WaveSounds (Menu Background)", 
				"http://www.garageband.com/wavesounds/"));
		content.add(indented);
	}

}
