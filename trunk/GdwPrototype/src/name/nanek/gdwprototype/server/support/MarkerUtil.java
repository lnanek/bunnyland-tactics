package name.nanek.gdwprototype.server.support;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import name.nanek.gdwprototype.server.Engine;
import name.nanek.gdwprototype.server.support.predicates.PlayerAndRolePredicate;
import name.nanek.gdwprototype.shared.model.Marker;
import name.nanek.gdwprototype.shared.model.Player;
import name.nanek.gdwprototype.shared.model.Position;

import com.google.common.collect.Iterators;

/**
 * Holds utility methods for working with Markers.
 * 
 * @author Lance Nanek
 *
 */
public class MarkerUtil {
	
	public static int countMovable(Map<Position, Marker> positions, Player player) {		
		Iterator<Marker> scouts = Iterators.filter(positions.values().iterator(), 
				new PlayerAndRolePredicate(Marker.Role.SCOUT, player));	
		Iterator<Marker> stompers = Iterators.filter(positions.values().iterator(), 
				new PlayerAndRolePredicate(Marker.Role.STOMPER, player));
		return Iterators.size(scouts) + Iterators.size(stompers);
	}

	public static int count(Map<Position, Marker> positions, Marker.Role role, Player player) {		
		Iterator<Marker> matchingMarkers = Iterators.filter(positions.values().iterator(), new PlayerAndRolePredicate(role, player));
		return Iterators.size(matchingMarkers);
	}

	public static Marker getMarker(List<Marker> markers, Marker.Role role, Player player) {
		return IteratorUtil.findOrNull(markers.iterator(), new PlayerAndRolePredicate(role, player));
	}

	public static Marker getNewPlayerPiece(List<Marker> markers, Player currentUsersTurn) {
		Marker.Role role = Engine.random.nextBoolean() ? Marker.Role.SCOUT : Marker.Role.STOMPER;
		return getMarker(markers, role, currentUsersTurn);
	}

}
