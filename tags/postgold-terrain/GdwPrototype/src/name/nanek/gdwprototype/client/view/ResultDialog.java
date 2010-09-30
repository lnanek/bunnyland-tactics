package name.nanek.gdwprototype.client.view;

import name.nanek.gdwprototype.client.controller.support.SoundPlayer;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Dialog used to display the result of some operation.
 * 
 * @author Lance Nanek
 *
 */
public class ResultDialog {

	public final DialogBox dialogBox = new DialogBox();

	public final Button closeButton = new Button("Close");

	final HTML serverResponseLabel = new HTML();

	public ResultDialog() {
		dialogBox.setAnimationEnabled(true);

		// We can set the id of a widget by accessing its Element
		// closeButton.getElement().setId("closeButton");

		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		//dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);

		dialogVPanel.add(serverResponseLabel);
		dialogVPanel.add(closeButton);

		dialogBox.setWidget(dialogVPanel);
	}

	public void show(String title, boolean isErrorResponse, String htmlContent) {
		dialogBox.setText(title);
		serverResponseLabel.setHTML(htmlContent);
		if (isErrorResponse) {
			serverResponseLabel.addStyleName("serverResponseLabelError");
		} else {
			serverResponseLabel.removeStyleName("serverResponseLabelError");
		}
		dialogBox.center();
		closeButton.setFocus(true);
	}

	public void setSoundPlayer(SoundPlayer soundPlayer) {
		soundPlayer.addMenuClick(closeButton);
	}
}
