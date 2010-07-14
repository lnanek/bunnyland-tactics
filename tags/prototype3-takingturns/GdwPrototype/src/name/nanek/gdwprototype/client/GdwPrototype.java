package name.nanek.gdwprototype.client;

import name.nanek.gdwprototype.client.controller.AppPageController;
import name.nanek.gdwprototype.client.controller.DialogController;
import name.nanek.gdwprototype.client.util.ExceptionUtil;

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

	private final DialogController dialogController = new DialogController();

	public void onModuleLoad() {

		// Catch uncaught exceptions.
		class ShowInDialogExceptionHandler implements GWT.UncaughtExceptionHandler {
			public void onUncaughtException(Throwable throwable) {
				String exceptionText = ExceptionUtil.exceptionToText(throwable);
				System.err.print(exceptionText);
				String exceptionHtml = ExceptionUtil.exceptionTextToHtml(exceptionText);
				dialogController.showClosableDialogWithHtml(DialogController.GAME_ERROR_TITLE, exceptionHtml);
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
		new AppPageController(dialogController);
	}

}
