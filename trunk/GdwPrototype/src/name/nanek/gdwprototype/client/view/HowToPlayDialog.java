package name.nanek.gdwprototype.client.view;

import name.nanek.gdwprototype.shared.model.Markers;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class HowToPlayDialog {

	public final DialogBox dialogBox = new DialogBox();

	public final Button closeButton = new Button("Close");

	public HowToPlayDialog() {
		dialogBox.setText("How to Play");
		dialogBox.setAnimationEnabled(true);

		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		FlexTable table = new FlexTable();
		table.setWidget(0, 0, new Image(Markers.PLAYER_TWO_WARREN.source));
		table.setWidget(0, 1, new Image(Markers.PLAYER_ONE_WARREN.source));
		table.setWidget(0, 2, new Label("Stomp the enemy's home to win! Protect your own to survive."));
		table.setWidget(1, 0, new Image(Markers.PLAYER_TWO_STOMPER.source));
		table.setWidget(1, 1, new Image(Markers.PLAYER_ONE_STOMPER.source));
		table.setWidget(1, 2, new Label("Stompers remove enemy pieces when placed on them."));
		table.setWidget(2, 0, new Image(Markers.PLAYER_TWO_SCOUT.source));
		table.setWidget(2, 1, new Image(Markers.PLAYER_ONE_SCOUT.source));
		table.setWidget(2, 2, new Label("Scouts can see far, but can't take enemy pieces."));

		dialogVPanel.add(table);
		
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
			}
		});
		dialogVPanel.add(closeButton);
		dialogBox.setWidget(dialogVPanel);
	}

	public void show() {
		dialogBox.center();
		closeButton.setFocus(true);
	}

}
