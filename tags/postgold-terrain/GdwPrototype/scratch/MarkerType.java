package name.nanek.gdwprototype.shared.model;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Parent;

import name.nanek.gdwprototype.client.model.Player;

public class MarkerType extends Marker {

	private static final long serialVersionUID = 1L;

	@Parent private Key<GameSettings> settings;

	private MarkerType() {
		super();
	}
	
	public MarkerType(String name, String source, String activeSource, Player player, Integer visionRange,
			Integer movementRange, Role role, boolean terrain) {
		super(name, source, activeSource, player, visionRange, movementRange, role, terrain);
	}

	public void setSettingsKey(Key<GameSettings> gameSettingsKey) {
		this.settings = gameSettingsKey;		
	}

	public MarkerType copy() {
		MarkerType copy = new MarkerType(name, source, activeSource, player, visionRange, movementRange, role, terrain);
		return copy;
	}
	
	public static MarkerType makeTerrain(String name, String source) {
		return new MarkerType(name, source, null, null, null, null, null, true);
	}
	
}
