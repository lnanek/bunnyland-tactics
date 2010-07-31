package name.nanek.gdwprototype.client.view.screen;

import name.nanek.gdwprototype.client.controller.support.SoundPlayer;
import name.nanek.gdwprototype.client.view.screen.support.ScreenUtil;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Holds widgets for starting or joining a game.
 * 
 * @author Lance Nanek
 *
 */
public class CreateGameScreen {
	
	public final VerticalPanel content = new VerticalPanel();

	public final TextBox createGameNameField = new TextBox();
	public final ListBox createGameMaps = new ListBox();
	public final Button createGameButton = new Button("Create Game");
	
	public CreateGameScreen(SoundPlayer soundPlayer) {

		//TODO make all fields line up using a table or CSS?
		content.add(ScreenUtil.labelAndWrap("Game name: ", createGameNameField));
		
		HorizontalPanel mapPanel = new HorizontalPanel();
		mapPanel.add(new Label("Map to play on: "));
		mapPanel.add(createGameMaps);
		createGameMaps.addItem("Loading maps...");
		createGameMaps.setVisibleItemCount(1);
		content.add(mapPanel);
		
		createGameButton.addStyleName("sendButton");
		createGameButton.setEnabled(false);
		content.add(createGameButton);
		soundPlayer.addMenuClick(createGameButton, createGameMaps);
	}
}
