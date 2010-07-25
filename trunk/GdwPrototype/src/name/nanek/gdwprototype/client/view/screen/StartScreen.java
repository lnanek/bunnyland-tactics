package name.nanek.gdwprototype.client.view.screen;

import java.util.HashMap;
import java.util.Map;

import name.nanek.gdwprototype.shared.model.Marker;
import name.nanek.gdwprototype.shared.model.Markers;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Holds widgets for starting or joining a game.
 * 
 * @author Lance Nanek
 *
 */
public class StartScreen {
	
	public final VerticalPanel content = new VerticalPanel();

	public final TextBox createGameNameField = new TextBox();
	public final TextBox boardWidthField = new TextBox();
	public final TextBox boardHeightField = new TextBox();
	public final Map<Marker, TextBox> playingPieceToVisibilityField = new HashMap<Marker, TextBox>();
	public final Map<Marker, TextBox> playingPieceToMovementField = new HashMap<Marker, TextBox>();
	
	public final ListBox createGameMaps = new ListBox();//TODO fill with available maps and send to server
	public final Button createGameButton = new Button("Create Game");
	public final Button createMapButton = new Button("Create Map");//TODO create map instead of game if clicked

	public final FlexTable joinableGamesTable = new FlexTable();
	
	private Widget labelAndWrap(String label, Widget control) {
		//TODO use html label element somehow so clicking focuses the control? or write own click handler?
		HorizontalPanel panel = new HorizontalPanel();
		panel.add(new Label(label));
		panel.add(control);
		return panel;
	}

	public StartScreen() {

		
		HTML joinGameLabel = new HTML("<h3>Games needing a second player:</h3>");
		joinGameLabel.addStyleName("heavy");
		content.add(joinGameLabel);

		joinableGamesTable.setText(0, 0, "Loading...");
		content.add(joinableGamesTable);
		
		content.add(new HTML("<br />"));
		
		HTML startGameLabel = new HTML("<h3>Create a new game:</h3>");
		startGameLabel.addStyleName("heavy");
		content.add(startGameLabel);

		//TODO indent sections under titles?
		VerticalPanel startGameControl = new VerticalPanel();
		
		//TODO make all fields line up using a table or CSS?
		startGameControl.add(labelAndWrap("Game name: ", createGameNameField));
		
		HorizontalPanel mapPanel = new HorizontalPanel();
		mapPanel.add(new Label("Map to play on: "));
		mapPanel.add(createGameMaps);
		createGameMaps.addItem("Loading maps...");
		createGameMaps.setVisibleItemCount(1);

		createMapButton.addStyleName("sendButton");
		mapPanel.add(createMapButton);
		startGameControl.add(mapPanel);
		
		boardWidthField.setText("12");
		startGameControl.add(labelAndWrap("Board Width: ", boardWidthField));
		
		boardHeightField.setText("12");
		startGameControl.add(labelAndWrap("Board Height: ", boardHeightField));

		//TODO use a table and have columns for visibility and movement?
		for ( Marker marker : Markers.PLAYING_PIECES ) {
			//Create visibility field for piece.
			{
				TextBox visibilityField = new TextBox();
				visibilityField.setText(Integer.toString(marker.visionRange));
				String label = marker.name + " Visibility: ";
				startGameControl.add(labelAndWrap(label, visibilityField));
				playingPieceToVisibilityField.put(marker, visibilityField);
			}
			//Create movement field for piece.
			{
				TextBox movementField = new TextBox();
				movementField.setText(Integer.toString(marker.movementRange));
				String label = marker.name + " Movement: ";
				startGameControl.add(labelAndWrap(label, movementField));
				playingPieceToMovementField.put(marker, movementField);
			}
		}	

		createGameButton.addStyleName("sendButton");
		startGameControl.add(createGameButton);
		
		content.add(startGameControl);
	}
}
