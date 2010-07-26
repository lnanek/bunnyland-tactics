package name.nanek.gdwprototype.client.view.screen;

import name.nanek.gdwprototype.client.controller.support.ScreenControllers;
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
	
	public StartGameScreen() {

		
		HTML joinGameLabel = new HTML("<h3>Games you can play in:</h3>");
		joinGameLabel.addStyleName("heavy");
		content.add(joinGameLabel);
		
		VerticalPanel indented = new VerticalPanel();
		indented.addStyleName("indented");
		content.add(indented);
		joinableGamesTable.setText(0, 0, "Loading...");
		indented.add(joinableGamesTable);
		content.add(new HTML("<br />"));

		Hyperlink createGameLink = new Hyperlink("Create a New Game", 
				ScreenControllers.getHistoryToken(Screen.CREATE_GAME));
		createGameLink.addStyleName("heavy");
		content.add(createGameLink);
		content.add(new HTML("<br />"));

		Hyperlink createMapLink = new Hyperlink("Create a New Map", 
				ScreenControllers.getHistoryToken(Screen.CREATE_MAP));
		createMapLink.addStyleName("heavy");
		content.add(createMapLink);
	}
}
