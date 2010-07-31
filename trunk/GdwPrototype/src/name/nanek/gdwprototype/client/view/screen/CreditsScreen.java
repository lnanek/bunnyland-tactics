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
		indented.add(new HTML("Menu Screen Music<br />&nbsp;&nbsp;&nbsp;&nbsp;Name: Bombs, Blades & Bunnyninjas 2<br />&nbsp;&nbsp;&nbsp;&nbsp;Author: Rajunen<br />&nbsp;&nbsp;&nbsp;&nbsp;URL: http://www.newgrounds.com/audio/listen/257918<br />&nbsp;&nbsp;&nbsp;&nbsp;Filename: 257918_Bunnykill_4_Soundtrack___2.mp3<br />&nbsp;&nbsp;&nbsp;&nbsp;License: http://creativecommons.org/licenses/by-nc-sa/3.0/legalcode<br /><br />In-Game Music<br />&nbsp;&nbsp;&nbsp;&nbsp;Name: Japanese Lead (preview)<br />&nbsp;&nbsp;&nbsp;&nbsp;Author: Matieus (DjMateius)<br />&nbsp;&nbsp;&nbsp;&nbsp;URL: http://www.newgrounds.com/audio/listen/124568<br />&nbsp;&nbsp;&nbsp;&nbsp;Filename: 124568_Japanese_Lead.mp3<br />&nbsp;&nbsp;&nbsp;&nbsp;License: http://creativecommons.org/licenses/by-nc-sa/3.0/legalcode<br /><br />Win Game Music<br />&nbsp;&nbsp;&nbsp;&nbsp;Name: Anthem 2007 Samples - Backing Piano<br />&nbsp;&nbsp;&nbsp;&nbsp;Author: dataset<br />&nbsp;&nbsp;&nbsp;&nbsp;Band: WaveSounds<br />&nbsp;&nbsp;&nbsp;&nbsp;URL: http://www.freesound.org/samplesViewSingle.php?id=44538<br />&nbsp;&nbsp;&nbsp;&nbsp;Filename: 44538__dataset__Backing_Piano.wav<br />&nbsp;&nbsp;&nbsp;&nbsp;License: http://creativecommons.org/licenses/sampling+/1.0/<br /><br />Lose Game Music<br />&nbsp;&nbsp;&nbsp;&nbsp;Name: Pain and Desire remix RD <br />&nbsp;&nbsp;&nbsp;&nbsp;Author: Matieus (DjMateius)<br />&nbsp;&nbsp;&nbsp;&nbsp;URL: http://www.newgrounds.com/audio/listen/132178<br />&nbsp;&nbsp;&nbsp;&nbsp;Filename: 132178_Desire_remix.mp3<br />&nbsp;&nbsp;&nbsp;&nbsp;License: http://creativecommons.org/licenses/by-nc-sa/3.0/legalcode<br />&nbsp;&nbsp;&nbsp;&nbsp;Notes: Cut to first 17 seconds to match win game music.<br /><br />In-Game Error Sound<br />&nbsp;&nbsp;&nbsp;&nbsp;Author: SoundJay.com<br />&nbsp;&nbsp;&nbsp;&nbsp;URL: http://www.soundjay.com/button/sounds/button-10.mp3<br />&nbsp;&nbsp;&nbsp;&nbsp;Filename: button-10.mp3<br />&nbsp;&nbsp;&nbsp;&nbsp;License: http://www.soundjay.com/tos.html<br /><br />Piece Placement Sound<br />&nbsp;&nbsp;&nbsp;&nbsp;Author: SoundJay.com<br />&nbsp;&nbsp;&nbsp;&nbsp;URL: http://www.soundjay.com/button/sounds/button-20.mp3<br />&nbsp;&nbsp;&nbsp;&nbsp;Filename: button-20.mp3<br />&nbsp;&nbsp;&nbsp;&nbsp;License: http://www.soundjay.com/tos.html<br /><br />Menu Screen Button Sound<br />&nbsp;&nbsp;&nbsp;&nbsp;Author: SoundJay.com<br />&nbsp;&nbsp;&nbsp;&nbsp;URL: http://www.soundjay.com/button/sounds/button-21.mp3<br />&nbsp;&nbsp;&nbsp;&nbsp;Filename: button-21.mp3<br />&nbsp;&nbsp;&nbsp;&nbsp;License: http://www.soundjay.com/tos.html<br /><br />Your Turn Sound<br />&nbsp;&nbsp;&nbsp;&nbsp;Author: Mike Koenig<br />&nbsp;&nbsp;&nbsp;&nbsp;URL: http://soundbible.com/1496-Japanese-Temple-Bell-Small.html<br />&nbsp;&nbsp;&nbsp;&nbsp;Filename: Japanese Temple Bell Small-SoundBible.com-113624364.mp3<br />&nbsp;&nbsp;&nbsp;&nbsp;License: http://creativecommons.org/licenses/by/3.0/legalcode<br /><br />Carrot Sound <br />&nbsp;&nbsp;&nbsp;&nbsp;Author: SoundJay.com<br />&nbsp;&nbsp;&nbsp;&nbsp;URL: http://www.soundjay.com/human/sounds/crunching-1.mp3<br />&nbsp;&nbsp;&nbsp;&nbsp;Filename: crunching-1.mp3<br />&nbsp;&nbsp;&nbsp;&nbsp;License: http://www.soundjay.com/tos.html<br /><br />Dying Sound<br />&nbsp;&nbsp;&nbsp;&nbsp;Author: HardPCM<br />&nbsp;&nbsp;&nbsp;&nbsp;URL: http://www.freesound.org/samplesViewSingle.php?id=27383<br />&nbsp;&nbsp;&nbsp;&nbsp;Filename: 27383__HardPCM__HardKick001.wav<br />&nbsp;&nbsp;&nbsp;&nbsp;License: http://creativecommons.org/licenses/sampling+/1.0/"));
		content.add(indented);
	}

}
