package name.nanek.gdwprototype.shared.exceptions;

/**
 * Exception with a message understandable by the typical user.
 * For example, telling a user it isn't their turn.
 * 
 * @author Lance Nanek
 *
 */
public class UserFriendlyMessageException extends GameException {

	private static final long serialVersionUID = 1L;

	public UserFriendlyMessageException() {
		super();
	}

	public UserFriendlyMessageException(String message, Throwable cause) {
		super(message, cause);
	}

	public UserFriendlyMessageException(String message) {
		super(message);
	}

	public UserFriendlyMessageException(Throwable cause) {
		super(cause);
	}

}
