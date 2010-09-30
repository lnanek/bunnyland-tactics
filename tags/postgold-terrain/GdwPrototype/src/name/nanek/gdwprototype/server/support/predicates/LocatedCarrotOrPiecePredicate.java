/**
 * 
 */
package name.nanek.gdwprototype.server.support.predicates;

import java.util.Map;
import java.util.Map.Entry;

import name.nanek.gdwprototype.shared.model.Marker;
import name.nanek.gdwprototype.shared.model.Position;

import com.google.common.base.Predicate;

/**
 * Detects a player piece or carrot in a specific location.
 * 
 * @author Lance Nanek
 *
 */
public class LocatedCarrotOrPiecePredicate implements Predicate<Map.Entry<Position, Marker>> {
	
	private int row;
	
	private int col;
			
	public LocatedCarrotOrPiecePredicate(int row, int col) {
		this.row = row;
		this.col = col;
	}


	@Override
	public boolean apply(Entry<Position, Marker> entry) {
		Position position = entry.getKey();
		Marker marker = entry.getValue();
		return position.getRow() == row && position.getColumn() == col && 
			( null != marker.player || marker.role == Marker.Role.CARROT );

	}
}