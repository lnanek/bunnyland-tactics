package name.nanek.gdwprototype.shared;

import name.nanek.gdwprototype.shared.exceptions.UserFriendlyMessageException;

/**
 * Exception that occured during validation.
 * 
 * @author Lance Nanek
 *
 */
public class ValidationException extends UserFriendlyMessageException {

	private static final long serialVersionUID = 1L;

	public ValidationException() {
	}

	public ValidationException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ValidationException(String arg0) {
		super(arg0);
	}

	public ValidationException(Throwable arg0) {
		super(arg0);
	}

}
