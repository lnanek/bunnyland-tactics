package name.nanek.gdwprototype.client.view.screen;

import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

public class GameScreen {

	public VerticalPanel content = new VerticalPanel();
	public FlexTable markers = new FlexTable();
	public FlexTable gameBoard = new FlexTable();

	public HorizontalPanel fogOfWarPanel = new HorizontalPanel();	
	public RadioButton fogOfWarPlayerOneRadio = new RadioButton("playerRadio", "Player One");
	public RadioButton fogOfWarPlayerTwoRadio = new RadioButton("playerRadio", "Player Two");
	public RadioButton fogOfWarNoneRadio = new RadioButton("playerRadio", "None");

	public Label turnStatusLabel = new Label();
	
	public VerticalPanel playingPiecesPanel = new VerticalPanel();
	
	public GameScreen() {
		markers.addStyleName("grid");
		gameBoard.addStyleName("grid");
		
		content.add(turnStatusLabel);

		fogOfWarPanel.add(new Label("Fog of War:"));
		//(Random.nextBoolean() ? fogOfWarPlayerOneRadio : fogOfWarPlayerTwoRadio).setValue(true, true);
		fogOfWarNoneRadio.setValue(true, false);
		fogOfWarPanel.add(fogOfWarPlayerOneRadio);
		fogOfWarPanel.add(fogOfWarPlayerTwoRadio);
		fogOfWarPanel.add(fogOfWarNoneRadio);
		content.add(fogOfWarPanel);
		
		
		HTML markerHeader = new HTML();
		markerHeader.setHTML("<h3>Playing pieces:</h3>");
		playingPiecesPanel.add(markerHeader);
		playingPiecesPanel.add(markers);
		content.add(playingPiecesPanel);
		
		content.add(new HTML("<br />"));
		HTML boardHeader = new HTML();
		boardHeader.setHTML("<h3>Game board:</h3>");
		content.add(boardHeader);
		content.add(gameBoard);

	}
}
