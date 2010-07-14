package name.nanek.gdwprototype.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ResultDialog {
	final DialogBox dialogBox = new DialogBox();
	final Button closeButton = new Button("Close");
	final HTML serverResponseLabel = new HTML();
	final Label textToServerLabel = new Label();

	public ResultDialog(final Button sendButton) {
		// Create the popup dialog box
		dialogBox.setText("Remote Procedure Call");
		dialogBox.setAnimationEnabled(true);
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(new HTML("<b>Sending name to the server:</b>"));
		dialogVPanel.add(textToServerLabel);
		dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
		dialogVPanel.add(serverResponseLabel);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(closeButton);
		dialogBox.setWidget(dialogVPanel);

		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
				sendButton.setEnabled(true);
				sendButton.setFocus(true);
			}
		});
	}
	
	public void show(String title, boolean isErrorResponse, String textToServer, String serverResponseHtml) {
		dialogBox.setText(title);
		textToServerLabel.setText(textToServer);
		if (isErrorResponse) {
			serverResponseLabel.addStyleName("serverResponseLabelError");
		} else {
			serverResponseLabel.removeStyleName("serverResponseLabelError");
		}
		serverResponseLabel.setHTML(serverResponseHtml);
		dialogBox.center();
		closeButton.setFocus(true);
	}
	
	
}
