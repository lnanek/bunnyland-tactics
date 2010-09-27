package name.nanek.gdwprototype.client.view.screen;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Holds widgets that list the games that can be observed.
 * 
 * @author Lance Nanek
 *
 */
public class StrategyScreen {
	//TODO this screen is almost all html, look into way to just include a file in GWT 
	//don't want an actual separate page, would require reloading GWT when you get back from it

	//TODO link back to the game the user came from? back button and home page link should work for now at least
	
	public VerticalPanel content = new VerticalPanel();

	public StrategyScreen() {

		HTML htmlContent = new HTML("<h3>Object of Game</h3><p>Be the first to capture your opponent's hole.</p><h3>Game Elements</h3><p>Carrot - when eaten, causes a new bunny to be born at your hole.</p> <p>Scouts - sees far and moves far but can not capture.</p> <p> Stomper - moves far and captures opponent's pieces and hole.</p><h3>How to Play</h3><p>You and your opponent will alternate turns, moving one piece per turn to try to capture each other's pieces and hole.</p><h3>Introduction &amp; Strategy Hints</h3><p>To win you must launch bold attacks, defend yourself from enemy assaults and capture your opponent's hole with cleverness and guile.</p><p>In <i>Bunny Tactics</i> <b>keep these three strategies</b> in mind:</p><p>1.) Eat as many carrots as you can! There are a limited number of  carrots on the board and when they are eaten there are no more.</p><p>2.) Use Stompers and Scouts as a team. First, use your scout to reveal the position of your opponent's pieces, then use your use your Stomper to capture what you see.</p><p>3.) Don't leave your hole undefended!</p>");
		content.add(htmlContent);
	}

}
