package name.nanek.gdwprototype.client.model;

public enum Player {

	ONE, TWO;

	public static Player other(Player currentUsersTurn) {
		if ( null == currentUsersTurn ) {
			return null;
		}
		return currentUsersTurn == ONE ? TWO : ONE;
	}

}
