package name.nanek.gdwprototype.client.view.screen;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class StartScreen {
	
	public static final String DEFAULT_GAME_NAME = "Enter name of game";

	public VerticalPanel content = new VerticalPanel();

	public TextBox createGameNameField = new TextBox();
	public Button createGameButton = new Button("Create Game");

	public final FlexTable currentGamesTable = new FlexTable();

	public StartScreen() {
		HTML startGameLabel = new HTML("<h3>Create a new game:</h3>");
		startGameLabel.addStyleName("heavy");
		content.add(startGameLabel);

		HorizontalPanel startGameControl = new HorizontalPanel();
		createGameNameField.setText(DEFAULT_GAME_NAME);
		startGameControl.add(createGameNameField);
		startGameControl.add(createGameButton);
		createGameButton.addStyleName("sendButton");
		content.add(startGameControl);
		content.add(new HTML("<br />"));

		HTML joinGameLabel = new HTML("<h3>Join an existing game:</h3>");
		joinGameLabel.addStyleName("heavy");
		content.add(joinGameLabel);

		currentGamesTable.setText(0, 0, "Loading...");
		content.add(currentGamesTable);
	}
}
