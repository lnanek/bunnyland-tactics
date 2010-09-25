package name.nanek.gdwprototype.server;

import name.nanek.gdwprototype.shared.exceptions.UserFriendlyMessageException;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class AppEngineUtil {

	static User requireUser() {
		final User user = AppEngineUtil.getUser();
		if (user == null) {
			//TODO maybe have a general purpose "need to login" exception with login URL?
			//all RPC calls needing user information could detect and redirect users not logged in
			throw new UserFriendlyMessageException("You need to login to join a game.");
		}
		return user;
	}

	static User getUser() {
	    UserService userService = UserServiceFactory.getUserService();
	    User user = userService.getCurrentUser();
	    return user;
	}

	static String getUserId() {
	    UserService userService = UserServiceFactory.getUserService();
	    User user = userService.getCurrentUser();
	    return null != user ? user.getUserId() : null;
	}

}
