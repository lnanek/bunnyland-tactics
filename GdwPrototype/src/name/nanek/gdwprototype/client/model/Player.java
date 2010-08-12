package name.nanek.gdwprototype.client.model;

import java.io.Serializable;

public enum Player implements Serializable {

	ONE, TWO;

	public static Player other(Player currentUsersTurn) {
		if ( null == currentUsersTurn ) {
			return null;
		}
		return currentUsersTurn == ONE ? TWO : ONE;
	}

}
