package name.nanek.gdwprototyperedirect;

import java.io.IOException;
import javax.servlet.http.*;

/**
 * Redirects browsers to the new subdomain for the game.
 * 
 * @author Lance Nanek
 *
 */
@SuppressWarnings("serial")
public class GdwPrototypeRedirectServlet extends HttpServlet {
	
	private static final String NEW_LOCATION = 
		"http://bunnyland-tactics.appspot.com/";
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.sendRedirect(NEW_LOCATION);
	}
}
