package name.nanek.gdwprototype.shared.exceptions;

/**
 * An exception specifically sent from the server. 
 * A useful distinction to make so that we know if we should ask the user to check their network connection or not.
 * Many errors are simply due to losing network connection during the game.
 * 
 * @author Lance Nanek
 *
 */
public class ServerException extends RuntimeException {

	public ServerException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ServerException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public ServerException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public ServerException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

}
