package name.nanek.gdwprototype.client;

import name.nanek.gdwprototype.client.controller.DialogController;
import name.nanek.gdwprototype.client.controller.PageController;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;

/**
 * Sets up a dialog and error popup, then defers to application controller for all other setup.
 * GWT calls into this when a user first comes to a page in the game.
 * 
 * @author Lance Nanek
 */
public class GdwPrototype implements EntryPoint {

	public void onModuleLoad() {
		
		//TODO fix statuscodeexception dialog sometimes flashed when leaving app while an async request is in progress

		// Catch uncaught exceptions.
		class ShowInDialogExceptionHandler implements GWT.UncaughtExceptionHandler {
			public void onUncaughtException(Throwable throwable) {
				new DialogController().showError("Unexpected Error", 
						"Sorry, an unexpected error occured running the game.", 
						true, 
						throwable);
			}
		}
		GWT.setUncaughtExceptionHandler(new ShowInDialogExceptionHandler());

		// Continue using a deferred command, so that exception are caught.
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				onModuleLoadExceptionsCaught();
			}
		});
	}

	private void onModuleLoadExceptionsCaught() {
		new PageController();
	}

}
