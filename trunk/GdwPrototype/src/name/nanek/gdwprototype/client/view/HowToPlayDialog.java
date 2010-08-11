package name.nanek.gdwprototype.client.view;

import name.nanek.gdwprototype.client.controller.support.SoundPlayer;
import name.nanek.gdwprototype.shared.model.Markers;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Dialog explaining how to play.
 * 
 * @author Lance Nanek
 *
 */
public class HowToPlayDialog {

	public final DialogBox dialogBox = new DialogBox();

	public final Button closeButton = new Button("Close");

	public HowToPlayDialog(SoundPlayer player) {
		dialogBox.setText("How to Play");
		//dialogBox.setAnimationEnabled(true);

		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		FlexTable table = new FlexTable();
		
		//XXX CSS seems to be overriding this. Make CSS more specific.
		table.setCellPadding(10);
		
		int row = 0;
		table.setWidget(row, 0, new Image("images/" + Markers.CARROT.source, 0, 0, 50, 50));
		table.getFlexCellFormatter().setColSpan(row, 0, 2);
		table.getFlexCellFormatter().setAlignment(row, 0, 
				HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
		table.setWidget(row++, 1, new HTML(
				"Find the carrots! Landing on a carrot earns you a new random bunny back home, <br />" + 
				"as long as there's an open space adjacent to your home."));

		table.setWidget(row, 0, new Image("images/" + Markers.PLAYER_TWO_WARREN.source, 0, 0, 50, 50));
		table.setWidget(row, 1, new Image("images/" + Markers.PLAYER_ONE_WARREN.source, 0, 0, 50, 50));
		table.setWidget(row++, 2, new HTML(
				"Stomp the enemy's home with a stomper bunny to win! <br />" + 
				"Protect your own home to survive."));
		
		table.setWidget(row, 0, new Image("images/" + Markers.PLAYER_TWO_STOMPER.source, 0, 0, 50, 50));
		table.setWidget(row, 1, new Image("images/" + Markers.PLAYER_ONE_STOMPER.source, 0, 0, 50, 50));
		table.setWidget(row++, 2, new Label("Stomper bunnies remove enemies when placed on them."));

		table.setWidget(row, 0, new Image("images/" + Markers.PLAYER_TWO_SCOUT.source, 0, 0, 50, 50));
		table.setWidget(row, 1, new Image("images/" + Markers.PLAYER_ONE_SCOUT.source, 0, 0, 50, 50));
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

	public void show() {
		dialogBox.center();
		closeButton.setFocus(true);
	}

}
