package name.nanek.gdwprototype.client.util;

public class ExceptionUtil {

	public static String exceptionTextToHtml(String text) {
		text = "Sorry, an error occured running the game. " + 
			"Some helpful information you can include with bug reports is below.\n\n" + 
			text;
		text = text.replaceAll(" ", "&nbsp;");
		text = text.replaceAll("\n", "<br />");
		return text;
	}

	public static String exceptionToText(Throwable throwable) {
		String text = "Uncaught exception: ";
		while (throwable != null) {
			StackTraceElement[] stackTraceElements = throwable.getStackTrace();
			text += throwable.toString() + "\n";
			for (int i = 0; i < stackTraceElements.length; i++) {
				text += "    at " + stackTraceElements[i] + "\n";
			}
			throwable = throwable.getCause();
			if (throwable != null) {
				text += "Caused by: ";
			}
		}
		return text;
	}

}
