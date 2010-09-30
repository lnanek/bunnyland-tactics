package name.nanek.gdwprototype.shared.exceptions;

/**
 * Indicates the user needs to login.
 * 
 * @author Lance Nanek
 *
 */
public class MustLoginException extends UserFriendlyMessageException {
	//TODO include the login URL
	
	private static final long serialVersionUID = 1L;

	public MustLoginException() {
	}

	public MustLoginException(String message, Throwable cause) {
		super(message, cause);
	}

	public MustLoginException(String message) {
		super(message);
	}

	public MustLoginException(Throwable cause) {
		super(cause);
	}

}
