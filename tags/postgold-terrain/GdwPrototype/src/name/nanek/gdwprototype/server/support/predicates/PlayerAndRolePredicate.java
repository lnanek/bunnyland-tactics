/**
 * 
 */
package name.nanek.gdwprototype.server.support.predicates;

import name.nanek.gdwprototype.shared.model.Marker;
import name.nanek.gdwprototype.shared.model.Player;
import name.nanek.gdwprototype.shared.model.Marker.Role;

import com.google.common.base.Predicate;

public class PlayerAndRolePredicate implements Predicate<Marker> {
	
	private Marker.Role role;
	
	private Player player;

	public PlayerAndRolePredicate(Role role, Player player) {
		this.role = role;
		this.player = player;
	}

	@Override
	public boolean apply(Marker marker) {
		return null != marker && marker.role == role && marker.player == player;
	}
	
}