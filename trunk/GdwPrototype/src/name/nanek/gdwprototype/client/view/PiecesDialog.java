package name.nanek.gdwprototype.client.view;

import name.nanek.gdwprototype.client.controller.support.SoundPlayer;
import name.nanek.gdwprototype.shared.model.DefaultMarkers;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Dialog explaining how to play.
 * 
 * @author Lance Nanek
 *
 */
public class PiecesDialog {
	//TODO these help screens are getting hideously long and we were told the best help is just in time help in class
	//maybe detect clicks as well as drags, clicks would show short piece specific information and help
	//then all the user would have to be told at the start is to click on a piece to learn about it
	
	//BUG on small screens the dialog is bigger than the screen and you can't close it or scroll. other dialogs probably need fix too

	public final DialogBox dialogBox = new DialogBox();

	public final Button closeButton = new Button("Close");

	public PiecesDialog(SoundPlayer player) {
		dialogBox.setText("How to Play : Pieces");
		//dialogBox.setAnimationEnabled(true);

		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		FlexTable table = new FlexTable();
		
		//XXX CSS seems to be overriding this. Make CSS more specific.
		table.setCellPadding(10);
		
		int row = 0;

		addHeading("Home", table, row++);
		table.setWidget(row, 0, new Image("images/" + DefaultMarkers.PLAYER_TWO_WARREN.source, 0, 0, DefaultMarkers.MARKER_WIDTH_PX, DefaultMarkers.MARKER_HEIGHT_PX));
		table.setWidget(row, 1, new Image("images/" + DefaultMarkers.PLAYER_ONE_WARREN.source, 0, 0, DefaultMarkers.MARKER_WIDTH_PX, DefaultMarkers.MARKER_HEIGHT_PX));
		table.setWidget(row++, 2, new HTML(
				"Stomp the enemy's home with a stomper bunny to win! <br />" + 
				"Protect your own home and bunnies to survive."));

		addHeading("Stomper", table, row++);
		table.setWidget(row, 0, new Image("images/" + DefaultMarkers.PLAYER_TWO_STOMPER.source, 0, 0, DefaultMarkers.MARKER_WIDTH_PX, DefaultMarkers.MARKER_HEIGHT_PX));
		table.setWidget(row, 1, new Image("images/" + DefaultMarkers.PLAYER_ONE_STOMPER.source, 0, 0, DefaultMarkers.MARKER_WIDTH_PX, DefaultMarkers.MARKER_HEIGHT_PX));
		table.setWidget(row++, 2, new Label("Stomper bunnies remove enemies when placed on them."));

		addHeading("Scout", table, row++);
		table.setWidget(row, 0, new Image("images/" + DefaultMarkers.PLAYER_TWO_SCOUT.source, 0, 0, DefaultMarkers.MARKER_WIDTH_PX, DefaultMarkers.MARKER_HEIGHT_PX));
		table.setWidget(row, 1, new Image("images/" + DefaultMarkers.PLAYER_ONE_SCOUT.source, 0, 0, DefaultMarkers.MARKER_WIDTH_PX, DefaultMarkers.MARKER_HEIGHT_PX));
		table.setWidget(row++, 2, new Label("Scout bunnies can see far, but can't remove enemies."));

		dialogVPanel.add(table);
		
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
			}
		});
		dialogVPanel.add(closeButton);
		dialogBox.setWidget(dialogVPanel);
		
		player.addMenuClick(closeButton);
	}

	private void addHeading(String text, FlexTable table, int row) {
		Label label = new Label(text);
		label.addStyleName("heavy");
		table.setWidget(row, 0, label);
		table.getFlexCellFormatter().setColSpan(row, 0, 3);
	}

	public void show() {
		dialogBox.center();
		closeButton.setFocus(true);
	}

}
