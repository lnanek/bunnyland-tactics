package name.nanek.gdwprototype.shared.model;

import java.io.Serializable;

import javax.persistence.Id;

import name.nanek.gdwprototype.shared.model.support.CompareToBuilder;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Parent;

/**
 * A marker moved on the game board.
 * 
 * @author Lance Nanek
 *
 */
public class Marker implements Serializable, Comparable<Marker> {
	
	public enum Role {
		HOME, STOMPER, SCOUT, CARROT, GRASS, 
	}
	
	public enum Layer {
		GROUND, SURFACE, UI
	}
	
	private static final long serialVersionUID = 1L;
	
	@Id private Long id;
	
	@SuppressWarnings("unused") //Used by ORM.
	//Transient for performance, not needed on the client. Objectify ignores, but GWT doesn't.
	@Parent private transient Key<Game> game;
    
	public String source;
    
	public String activeSource;

	public Player player;

	public int visionRange;

	public int movementRange;

	public String name;
	
	public boolean terrain;
	
	public Role role;
	
	protected Marker() {
	}
	
	public Marker(String name, String source, String activeSource, Player player, int visionRange, int movementRange, Role role, boolean terrain) {
		this.name = name;
		this.source = source;
		this.activeSource = activeSource;
		this.player = player;
		this.visionRange = visionRange;
		this.movementRange = movementRange;
		this.role = role;
		this.terrain = terrain;
	}
	
	public Layer getLayer() {
		if ( role == Role.CARROT || null != player ) return Layer.SURFACE;
		
		if ( terrain ) return Layer.GROUND;
		
		return Layer.UI;		
	}
	
	public String getCssLayerStyle() {
		String titleCaseLayer = getLayer().toString();
		titleCaseLayer = titleCaseLayer.toLowerCase();
		titleCaseLayer = titleCaseLayer.substring(0, 1).toUpperCase() + titleCaseLayer.substring(1);
		return "gameBoardMarker" + titleCaseLayer;
	}
	
	public static Marker makeTerrain(String name, String source, int visibility) {
		return new Marker(name, source, null, null, visibility, 0, null, true);
	}

	public Marker copy() {
		Marker copy = new Marker(name, source, activeSource, player, visionRange, movementRange, role, terrain);
		return copy;
	}
	
	public String getSourceForPlayersTurn(Player currentPlayersTurn) {
		if ( null == currentPlayersTurn || null == player || null == activeSource ) return source;
		
		return player == currentPlayersTurn ? activeSource : source;
	}

	public Long getId() {
		return id;
	}

	@Override
	public int compareTo(Marker other) {
		if ( this.equals(other) ) {
			return 0;	
		}
		
		return new CompareToBuilder()
				.append(other.player, player) //Reversed so that nulls are last.
				.append(role, other.role)
				.toComparison();
	}

	public void setGame(Key<Game> game) {
		this.game = game;		
	}
}
