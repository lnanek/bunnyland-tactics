package name.nanek.gdwprototype.shared.model;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Parent;

import name.nanek.gdwprototype.client.model.Player;

public class PositionedMarker extends Marker {

	private static final long serialVersionUID = 1L;

	@Parent private Key<Position> position;

	private PositionedMarker() {
		super();
	}
	
	public PositionedMarker(String name, String source, String activeSource, Player player, Integer visionRange,
			Integer movementRange, Role role, boolean terrain) {
		super(name, source, activeSource, player, visionRange, movementRange, role, terrain);
	}

	public void setSettingsKey(Key<Position> position) {
		this.position = position;		
	}

	public PositionedMarker copy() {
		PositionedMarker copy = new PositionedMarker(name, source, activeSource, player, visionRange, movementRange, role, terrain);
		return copy;
	}
	
}
