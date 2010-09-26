package name.nanek.gdwprototype.client.view.screen;

import java.util.HashMap;
import java.util.Map;

import name.nanek.gdwprototype.client.controller.support.SoundPlayer;
import name.nanek.gdwprototype.client.view.screen.support.ScreenUtil;
import name.nanek.gdwprototype.shared.model.DefaultMarkers;
import name.nanek.gdwprototype.shared.model.Marker;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Holds widgets for starting or joining a game.
 * 
 * @author Lance Nanek
 *
 */
public class CreateMapScreen {
	
	public final VerticalPanel content = new VerticalPanel();

	public final TextBox createMapNameField = new TextBox();
	public final TextBox boardWidthField = new TextBox();
	public final TextBox boardHeightField = new TextBox();
	public final TextBox generateCarrotPeriodField = new TextBox();
	public final Map<Marker, TextBox> playingPieceToVisibilityField = new HashMap<Marker, TextBox>();
	public final Map<Marker, TextBox> playingPieceToMovementField = new HashMap<Marker, TextBox>();
	
	public final Button createMapButton = new Button("Create Map");

	public CreateMapScreen(SoundPlayer soundPlayer) {
	
		//TODO make all fields line up using a table or CSS?
		content.add(ScreenUtil.labelAndWrap("Map name: ", createMapNameField));
		
		boardWidthField.setText("8");
		content.add(ScreenUtil.labelAndWrap("Board Width: ", boardWidthField));
		
		boardHeightField.setText("8");
		content.add(ScreenUtil.labelAndWrap("Board Height: ", boardHeightField));

		generateCarrotPeriodField.setText("3");
		content.add(ScreenUtil.labelAndWrap("Turns Per New Carrot (0 for no new carrots): ", generateCarrotPeriodField));
		
		//TODO use a table and have columns for visibility and movement?
		for ( Marker marker : DefaultMarkers.PLAYING_PIECES ) {
			//Create visibility field for piece.
			{
				TextBox visibilityField = new TextBox();
				visibilityField.setText(Integer.toString(marker.visionRange));
				String label = marker.name + " Visibility: ";
				content.add(ScreenUtil.labelAndWrap(label, visibilityField));
				playingPieceToVisibilityField.put(marker, visibilityField);
			}
			//Create movement field for piece.
			{
				TextBox movementField = new TextBox();
				movementField.setText(Integer.toString(marker.movementRange));
				String label = marker.name + " Movement: ";
				content.add(ScreenUtil.labelAndWrap(label, movementField));
				playingPieceToMovementField.put(marker, movementField);
			}
		}	

		createMapButton.addStyleName("sendButton");
		content.add(createMapButton);
		soundPlayer.addMenuClick(createMapButton);
	}
}
