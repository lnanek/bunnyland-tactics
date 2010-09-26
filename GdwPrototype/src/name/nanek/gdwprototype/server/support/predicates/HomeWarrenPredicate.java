/**
 * 
 */
package name.nanek.gdwprototype.server.support.predicates;

import java.util.Map;
import java.util.Map.Entry;

import name.nanek.gdwprototype.shared.model.Marker;
import name.nanek.gdwprototype.shared.model.Player;
import name.nanek.gdwprototype.shared.model.Position;

import com.google.common.base.Predicate;

/**
 * Detects a player's home warren.
 * 
 * @author Lance Nanek
 *
 */
public class HomeWarrenPredicate implements Predicate<Map.Entry<Position, Marker>> {

	private Player currentUsersTurn;
	
	public HomeWarrenPredicate(Player currentUsersTurn) {
		this.currentUsersTurn = currentUsersTurn;
	}
	
	@Override
	public boolean apply(Entry<Position, Marker> entry) {
		if ( null == currentUsersTurn ) {
			return false;
		}
		Marker marker = entry.getValue();
		return marker.role == Marker.Role.HOME && marker.player == currentUsersTurn;
	}
}