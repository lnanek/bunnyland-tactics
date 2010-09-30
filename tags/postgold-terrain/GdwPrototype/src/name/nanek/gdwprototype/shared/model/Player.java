package name.nanek.gdwprototype.shared.model;

import java.io.Serializable;

/**
 * Enumerates possible player roles.
 * 
 * @author Lance Nanek
 *
 */
public enum Player implements Serializable {

	ONE, TWO;

	public static Player other(Player currentUsersTurn) {
		if ( null == currentUsersTurn ) {
			return null;
		}
		return currentUsersTurn == ONE ? TWO : ONE;
	}

}
