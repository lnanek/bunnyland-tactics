package name.nanek.gdwprototype.shared;


public class MoveVerifier {

	/*
	public void verifyMove(Game game, User user, Player player, Integer sourceRow, Integer sourceCol, Integer destRow,
			Integer destCol, Marker movedMarker, Set<Marker> destMarkers) {
		
		if ( null == movedMarker ) {
			throw new IllegalArgumentException("Could not find marker with specified ID.");
		}
	
		if (user == null) {
			throw new UserFriendlyMessageException("You need to login to make a move.");
		}
		
		if ( !GameEngine.isUsersTurn(game, user) ) {
			throw new UserFriendlyMessageException("It isn't your turn to move.");
		}
		
		if ( !game.isMap() ) {
			if ( player != movedMarker.player ) {
				throw new UserFriendlyMessageException("You may only move your own pieces.");
			}

			if ( null == sourceRow || null == sourceCol || null == destRow || null == destCol ) {
				throw new IllegalArgumentException("Cannot move pieces to/from the palette outside of map creation mode.");
			}
			
			final int rowDistance = Math.abs(sourceRow - destRow);
			final int colDistance = Math.abs(sourceCol - destCol);
			final int totalDistance = rowDistance + colDistance;
			if (totalDistance > movedMarker.movementRange) {
				throw new UserFriendlyMessageException("Your piece cannot move that far.");
			}
			
			
		
		}
	}
	*/
}
