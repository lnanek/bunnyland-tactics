package name.nanek.gdwprototype.server.support;

import name.nanek.gdwprototype.shared.model.Game;
import name.nanek.gdwprototype.shared.model.Marker;
import name.nanek.gdwprototype.shared.model.Position;

import com.google.appengine.api.datastore.Transaction;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

/**
 * Creates Objectify-AppEngine ORM library data stores.
 * 
 * @author Lance Nanek
 *
 */
public final class DbUtil {
	
	static {
		ObjectifyService.register(Game.class);
		ObjectifyService.register(Marker.class);
		ObjectifyService.register(Position.class);
	}
	
	public static Objectify createObjectify() {
		return ObjectifyService.begin();
	}
	
	public static Objectify beginTransaction() {
		return ObjectifyService.beginTransaction();
	}

	public static void rollbackIfNeeded(Objectify ofy) {
		if ( null == ofy ) {
			return;
		}
		
		Transaction tx = ofy.getTxn();
		if ( null == tx ) {
			return;
		}
		
	    if (tx.isActive()) {
	    	tx.rollback();
	    }		
	}
	
}