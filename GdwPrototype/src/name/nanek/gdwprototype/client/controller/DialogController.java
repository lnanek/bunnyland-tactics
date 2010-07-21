package name.nanek.gdwprototype.client.controller;

import name.nanek.gdwprototype.client.util.ExceptionUtil;
import name.nanek.gdwprototype.client.view.widget.ResultDialog;
import name.nanek.gdwprototype.shared.exceptions.ServerException;
import name.nanek.gdwprototype.shared.exceptions.UserFriendlyMessageException;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;

public class DialogController {

	private static final String POSSIBLE_NETWORK_ERROR = " Please check you have an internet connection and try again. ";

	private final ResultDialog dialog = new ResultDialog();
	
	private void showErrorDialog(String titleText, String contentHtml, final Command customCloseHandler) {
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

	public void showError(String titleText, String fallbackContentHtml, boolean checkIfNetwork, Throwable throwable) {
		showError(titleText, fallbackContentHtml, checkIfNetwork, throwable, null);
	}

	public void showError(String titleText, String fallbackContentHtml, boolean checkIfNetwork, Throwable throwable, Command customCloseHandler) {

		//The message to show the user.
		String message = fallbackContentHtml;		

		if ( null != throwable ) {
			//Output the stack trace to the log during development.
			String exceptionText = ExceptionUtil.exceptionToText(throwable);
			System.err.print(exceptionText);

			//For user friendly message exceptions, we show just that message to the user instead.
			if ( throwable instanceof UserFriendlyMessageException ) {
				UserFriendlyMessageException userMessageException = (UserFriendlyMessageException) throwable;
				message = userMessageException.getMessage();
			} else if ( throwable.getCause() instanceof UserFriendlyMessageException ) {
				UserFriendlyMessageException userMessageException = (UserFriendlyMessageException) throwable.getCause();
				message = userMessageException.getMessage();
			} else {	
				//Otherwise, if the message wasn't known to be from the server, 
				//we show the fallback message and mention it could be a network error.
				if ( !(throwable instanceof ServerException) && checkIfNetwork ) {
					message += POSSIBLE_NETWORK_ERROR;
				}

				//Then we append the stack trace, which includes the message, but that's often in technical terms meaningless to the user.
				//TODO this tends to bug users eyes out, maybe send stack track to a stack trace collector on the server
				message += ExceptionUtil.exceptionTextToHtml(exceptionText);
			}
		}

		showErrorDialog(titleText, message, customCloseHandler);
	}

}
