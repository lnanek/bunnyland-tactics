package name.nanek.gdwprototype.client.view.screen;

import name.nanek.gdwprototype.client.controller.support.ScreenControllers;
import name.nanek.gdwprototype.client.controller.support.SoundPlayer;
import name.nanek.gdwprototype.client.controller.support.ScreenControllers.Screen;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Holds widgets for starting or joining a game.
 * 
 * @author Lance Nanek
 *
 */
public class StartGameScreen {
	
	public final VerticalPanel content = new VerticalPanel();

	public final FlexTable joinableGamesTable = new FlexTable();
	
	public StartGameScreen(SoundPlayer soundPlayer) {

		
		HTML joinGameLabel = new HTML("<h3>Games you can play in:</h3>");
		joinGameLabel.addStyleName("heavy");
		content.add(joinGameLabel);
		
		VerticalPanel indented = new VerticalPanel();
		indented.addStyleName("indented");
		content.add(indented);
		joinableGamesTable.setText(0, 0, "Loading...");
		indented.add(joinableGamesTable);
		content.add(new HTML("<br />"));

		Hyperlink createGameLink = new Hyperlink("Create a Game", 
				ScreenControllers.getHistoryToken(Screen.CREATE_GAME));
		soundPlayer.addMenuClick(createGameLink);
		createGameLink.addStyleName("heavy");
		content.add(createGameLink);
	}
}
