/**
 * 
 */
package name.nanek.gdwprototype.server.support.predicates;

import java.util.Map;

import name.nanek.gdwprototype.shared.model.Marker;
import name.nanek.gdwprototype.shared.model.Position;
import name.nanek.gdwprototype.shared.model.Marker.Layer;

import com.google.common.base.Predicate;

public class LayerPredicate implements Predicate<Map.Entry<Position, Marker>> {
	
	private Layer layer;
	
	public LayerPredicate(Layer role) {
		this.layer = role;
	}

	@Override
	public boolean apply(Map.Entry<Position, Marker> entry) {
		Marker marker = entry.getValue();
		return marker.getLayer() == layer;
	}
	
}