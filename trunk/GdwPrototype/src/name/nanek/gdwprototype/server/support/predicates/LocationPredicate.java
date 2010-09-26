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
 * Detects a location and layer.
 * 
 * @author Lance Nanek
 *
 */
public class LocationPredicate implements Predicate<Map.Entry<Position, Marker>> {

	private int row;
	
	private int col;
	
	private Marker.Layer layer;
			
	public LocationPredicate(int row, int col, Marker.Layer layer) {
		this.row = row;
		this.col = col;
		this.layer = layer;
	}


	@Override
	public boolean apply(Entry<Position, Marker> entry) {
		Position position = entry.getKey();
		Marker marker = entry.getValue();
		return position.getRow() == row && position.getColumn() == col && marker.getLayer() == layer;

	}
}