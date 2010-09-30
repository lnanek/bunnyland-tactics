package name.nanek.gdwprototype.shared;

import com.google.gwt.user.client.ui.TextBox;

/**
 * Validates game names and integers.
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
