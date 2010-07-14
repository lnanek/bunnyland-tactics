package name.nanek.gdwprototype.client.controller;

import name.nanek.gdwprototype.client.view.widget.ResultDialog;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;

public class DialogController {
	//TODO include exception information in the error messages from rpc calls, particularly if it is user related, like must login
	
	//TODO don't show possible network error string if exception obviously from the server logic

	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network " + "connection and try again.";

	public static final String POSSIBLE_NETWORK_ERROR = "Please check your network connection and try again.";

	public static final String GAME_ERROR_TITLE = "Error Running Game";

	private final ResultDialog dialog = new ResultDialog();

	public void showServerError() {
		showRpcError("Remote Procedure Call - Failure", SERVER_ERROR, null);
	}

	// Show the RPC error message to the user
	void showRpcError(String titleText, String contentHtml, final Command customCloseHandler) {
		dialog.closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialog.dialogBox.hide();
				if (null != customCloseHandler) {
					customCloseHandler.execute();
				}
			}
		});
		dialog.show(titleText, true, contentHtml);
	}

	public void showClosableDialogWithHtml(String titleText, String exceptionHtml) {
		showRpcError(titleText, exceptionHtml, null);
	}

}
