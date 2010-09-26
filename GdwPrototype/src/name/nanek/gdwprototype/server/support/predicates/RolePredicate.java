/**
 * 
 */
package name.nanek.gdwprototype.server.support.predicates;

import java.util.Map;

import name.nanek.gdwprototype.shared.model.Marker;
import name.nanek.gdwprototype.shared.model.Position;
import name.nanek.gdwprototype.shared.model.Marker.Role;

import com.google.common.base.Predicate;

public class RolePredicate implements Predicate<Map.Entry<Position, Marker>> {
	
	private Role role;
	
	public RolePredicate(Role role) {
		this.role = role;
	}

	@Override
	public boolean apply(Map.Entry<Position, Marker> entry) {
		Marker marker = entry.getValue();
		return marker.role == role;
	}
	
}