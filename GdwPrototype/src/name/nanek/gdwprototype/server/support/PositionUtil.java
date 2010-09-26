package name.nanek.gdwprototype.server.support;

import java.util.Map;

import name.nanek.gdwprototype.server.support.predicates.LocatedCarrotOrPiecePredicate;
import name.nanek.gdwprototype.shared.model.Game;
import name.nanek.gdwprototype.shared.model.Marker;
import name.nanek.gdwprototype.shared.model.Position;

import com.google.common.base.Predicate;
import com.googlecode.objectify.Objectify;

/**
 * Utility methods for working with positions.
 * 
 * @author Lance Nanek
 *
 */
public class PositionUtil {

	private static final Position toPosition(Map.Entry<Position, Marker> entry) {
		if ( null == entry ) {
			return null;
		}
		return entry.getKey();
	}
	
	public static Position find(Map<Position, Marker> positions, Predicate<Map.Entry<Position, Marker>> predicate) {
		Map.Entry<Position, Marker> entry = IteratorUtil.findOrNull(positions.entrySet().iterator(), predicate);
		return toPosition(entry);		
	}

	public static Position removePosition(Position position, Objectify em, Game game, Map<Position, Marker> positions) {
		if ( null != position ) {
			em.delete(position);
			positions.remove(position);
			return position;
		}
		return null;
	}

	public static boolean isOpen(Position location, Map<Position, Marker> positions, int boardHeight, int boardWidth) {
		if ( null == location || null == positions ) {
			return false;
		}
		
		if ( location.getRow() < 0 || location.getRow() >= boardHeight) {
			return false;
		}
		if ( location.getColumn() < 0 || location.getColumn() >= boardWidth) {
			return false;
		}

		//TODO must be a cleaner way then iterating through so many positions
		//maybe hash them by a hash code based on their x and y?
		Position existingPosition = find(positions, new LocatedCarrotOrPiecePredicate(location.getRow(), location.getColumn()));
		if ( null != existingPosition ) {
			return false;
		}
		
		return true;
	}

	public static Position createNearbyOpenSpot(Position location, Map<Position, Marker> positions, int boardHeight, int boardWidth) {
		if ( null == location ) {
			return location;
		}
		
		int[][] offsets = new int[][]{
				{0, -1}, {0, +1}, {-1, 0}, {+1, 0}
		};
		for ( int[] offset : offsets ) {
			Position offsetPosition = new Position(location.getRow() + offset[0], location.getColumn()  + offset[1]);
			if ( isOpen(offsetPosition, positions, boardHeight, boardWidth)) {
				return offsetPosition;
			}
		}
		
		return null;
	}

}
