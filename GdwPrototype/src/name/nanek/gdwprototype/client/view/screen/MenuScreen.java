package name.nanek.gdwprototype.client.view.screen;

import name.nanek.gdwprototype.client.controller.PageController;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MenuScreen {
	
	public static final String DEFAULT_GAME_NAME = "Enter name of game";

	public VerticalPanel content = new VerticalPanel();

	public final FlexTable currentGamesTable = new FlexTable();

	public MenuScreen() {
		Hyperlink link = new Hyperlink("Play a Game", PageController.START_GAME_SCREEN_HISTORY_TOKEN);
		content.add(link);
		
		content.add(new HTML("<br />"));

		HTML joinGameLabel = new HTML("<h3>Games in Progress:</h3>");
		joinGameLabel.addStyleName("heavy");
		content.add(joinGameLabel);

		currentGamesTable.setText(0, 0, "Loading...");
		content.add(currentGamesTable);
	}

}
