package name.nanek.gdwprototype.client.view;

import name.nanek.gdwprototype.client.controller.support.SoundPlayer;
import name.nanek.gdwprototype.shared.model.Markers;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class HowToPlayDialog {

	public final DialogBox dialogBox = new DialogBox();

	public final Button closeButton = new Button("Close");

	public HowToPlayDialog(SoundPlayer player) {
		dialogBox.setText("How to Play");
		//dialogBox.setAnimationEnabled(true);

		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		FlexTable table = new FlexTable();
		int row = 0;
		table.setWidget(row, 0, new Image("images/" + Markers.CARROT.source));
		table.setWidget(row++, 2, new HTML(
				"Find the carrots! Landing on a carrot earns you a new random bunny back home, <br />" + 
				"as long as there's an open space adjacent to your home."));

		table.setWidget(row, 0, new Image("images/" + Markers.PLAYER_TWO_WARREN.source));
		table.setWidget(row, 1, new Image("images/" + Markers.PLAYER_ONE_WARREN.source));
		table.setWidget(row++, 2, new HTML(
				"Stomp the enemy's home with a stomper bunny to win! <br />" + 
				"Protect your own home to survive."));
		
		table.setWidget(row, 0, new Image("images/" + Markers.PLAYER_TWO_STOMPER.source));
		table.setWidget(row, 1, new Image("images/" + Markers.PLAYER_ONE_STOMPER.source));
		table.setWidget(row++, 2, new Label("Stomper bunnies remove enemies when placed on them."));

		table.setWidget(row, 0, new Image("images/" + Markers.PLAYER_TWO_SCOUT.source));
		table.setWidget(row, 1, new Image("images/" + Markers.PLAYER_ONE_SCOUT.source));
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
