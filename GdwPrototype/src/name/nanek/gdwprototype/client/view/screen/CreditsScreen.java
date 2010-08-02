package name.nanek.gdwprototype.client.view.screen;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
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
	
	//TODO this screen is almost all html, look into way to just include a file in GWT 
	//don't want an actual separate page, would require reloading GWT when you get back from it
	
	public VerticalPanel content = new VerticalPanel();

	public CreditsScreen() {

		HTML joinGameLabel = new HTML("<h3>Development Team:</h3>");
		joinGameLabel.addStyleName("heavy");
		content.add(joinGameLabel);
		VerticalPanel indented = new VerticalPanel();
		indented.addStyleName("indented");
		indented.add(new Label("Adlee Fayyaz"));
		indented.add(new Label("Evan Rothstein"));
		
		HorizontalPanel linkAndText = new HorizontalPanel();
		linkAndText.add(new Anchor(
				"Foster Birch", 
				"http://smileonacloudyday.com/"));		
		linkAndText.add(new HTML("&nbsp;&nbsp;(original idea conceived by)"));
		indented.add(linkAndText);
		
		indented.add(new Anchor(
				"Lance Nanek", 
				"http://nanek.name/"));
		content.add(indented);

		HTML musicLabel = new HTML("<h3>Sound:</h3>");
		musicLabel.addStyleName("heavy");
		content.add(musicLabel);
		indented = new VerticalPanel();
		indented.addStyleName("indented");
		indented.add(new HTML("Menu Screen Music<br />Name: Bombs, Blades & Bunnyninjas 2<br />Author: Rajunen<br />URL: http://www.newgrounds.com/audio/listen/257918<br />Filename: 257918_Bunnykill_4_Soundtrack___2.mp3<br />License: http://creativecommons.org/licenses/by-nc-sa/3.0/legalcode<br /><br />In-Game Music<br />Name: Japanese Lead (preview)<br />Author: Matieus (DjMateius)<br />URL: http://www.newgrounds.com/audio/listen/124568<br />Filename: 124568_Japanese_Lead.mp3<br />License: http://creativecommons.org/licenses/by-nc-sa/3.0/legalcode<br /><br />Win Game Music<br />Name: Anthem 2007 Samples - Backing Piano<br />Author: dataset<br />Band: WaveSounds<br />URL: http://www.freesound.org/samplesViewSingle.php?id=44538<br />Filename: 44538__dataset__Backing_Piano.wav<br />License: http://creativecommons.org/licenses/sampling+/1.0/<br /><br />Lose Game Music<br />Name: Pain and Desire remix RD <br />Author: Matieus (DjMateius)<br />URL: http://www.newgrounds.com/audio/listen/132178<br />Filename: 132178_Desire_remix.mp3<br />License: http://creativecommons.org/licenses/by-nc-sa/3.0/legalcode<br />Notes: Cut to first 17 seconds to match win game music.<br /><br />In-Game Error Sound<br />Author: SoundJay.com<br />URL: http://www.soundjay.com/button/sounds/button-10.mp3<br />Filename: button-10.mp3<br />License: http://www.soundjay.com/tos.html<br /><br />Piece Placement Sound<br />Author: SoundJay.com<br />URL: http://www.soundjay.com/button/sounds/button-20.mp3<br />Filename: button-20.mp3<br />License: http://www.soundjay.com/tos.html<br /><br />Menu Screen Button Sound<br />Author: SoundJay.com<br />URL: http://www.soundjay.com/button/sounds/button-21.mp3<br />Filename: button-21.mp3<br />License: http://www.soundjay.com/tos.html<br /><br />Your Turn Sound<br />Author: Mike Koenig<br />URL: http://soundbible.com/1496-Japanese-Temple-Bell-Small.html<br />Filename: Japanese Temple Bell Small-SoundBible.com-113624364.mp3<br />License: http://creativecommons.org/licenses/by/3.0/legalcode<br /><br />Carrot Sound <br />Author: SoundJay.com<br />URL: http://www.soundjay.com/human/sounds/crunching-1.mp3<br />Filename: crunching-1.mp3<br />License: http://www.soundjay.com/tos.html<br /><br />Dying Sound<br />Author: HardPCM<br />URL: http://www.freesound.org/samplesViewSingle.php?id=27383<br />Filename: 27383__HardPCM__HardKick001.wav<br />License: http://creativecommons.org/licenses/sampling+/1.0/"));
		content.add(indented);
	}

}
