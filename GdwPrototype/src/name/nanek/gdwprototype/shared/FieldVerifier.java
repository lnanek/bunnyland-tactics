package name.nanek.gdwprototype.shared;

import com.google.gwt.user.client.ui.TextBox;

/**
 * <p>
 * FieldVerifier validates that the name the user enters is valid.
 * </p>
 * <p>
 * This class is in the <code>shared</code> packing because we use it in both
 * the client code and on the server. On the client, we verify that the name is
 * valid before sending an RPC request so the user doesn't have to wait for a
 * network round trip to get feedback. On the server, we verify that the name is
 * correct to ensure that the input is correct regardless of where the RPC
 * originates.
 * </p>
 * <p>
 * When creating a class that is used on both the client and the server, be sure
 * that all code is translatable and does not use native JavaScript. Code that
 * is note translatable (such as code that interacts with a database or the file
 * system) cannot be compiled into client side JavaScript. Code that uses native
 * JavaScript (such as Widgets) cannot be run on the server.
 * </p>
 */
public class FieldVerifier {

	public static void throwValidationError(String name, int minInclusive, int maxInclusive) {
		String errorMessage = name + " must be a whole number between " + minInclusive + " and " + maxInclusive + ".";
		throw new ValidationException(errorMessage);
	}
	
	public static int validateAndParseInt(String name, TextBox field, int minInclusive, int maxInclusive) {
		int value = 0;
		try {
			value = new Integer(field.getText());
			if ( value < minInclusive || value > maxInclusive) {
				throwValidationError(name, minInclusive, maxInclusive);
			}
		} catch(NumberFormatException e) {
			throwValidationError(name, minInclusive, maxInclusive);
		}				
		return value;
	}

	public static String validateGameName(TextBox name) {
		return validateGameName(name.getText());
	}

	public static String validateGameName(String name) {
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			if (!Character.isLetterOrDigit(c) && c != ' ') {
				throw new ValidationException("Please use only letters, numbers, and spaces in the game name.");
			}
		}
		return name;
	}
}
