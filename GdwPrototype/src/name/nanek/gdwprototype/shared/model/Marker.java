package name.nanek.gdwprototype.shared.model;

import java.io.Serializable;

import javax.persistence.Id;

import com.vercer.engine.persist.annotation.Key;

import name.nanek.gdwprototype.client.model.Player;
import name.nanek.gdwprototype.shared.model.support.CompareToBuilder;

/**
 * A marker moved on the game board.
 * 
 * @author Lance Nanek
 *
 */
public class Marker implements Serializable, Comparable<Marker> {
	
	public enum Role {
		HOME, STOMPER, SCOUT, CARROT,  
	}
	
	public enum Layer {
		GROUND, SURFACE, UI
	}
	
	private static final long serialVersionUID = 1L;
	
	//@Key private Long keyId;
    
	public String source;
    
	public String activeSource;

	public Player player;

	public Integer visionRange;

	public Integer movementRange;

	public String name;
	
	public boolean terrain;
	
	public Role role;
	
	private Marker() {
	}
	
	public Marker(String name, String source, String activeSource, Player player, Integer visionRange, Integer movementRange, Role role, boolean terrain) {
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
	
	public static Marker makeTerrain(String name, String source) {
		return new Marker(name, source, null, null, null, null, null, true);
	}

	public Marker copy() {
		Marker copy = new Marker(name, source, activeSource, player, visionRange, movementRange, role, terrain);
		return copy;
	}
	
	public String getSourceForPlayersTurn(Player currentPlayersTurn) {
		if ( null == currentPlayersTurn || null == player || null == activeSource ) return source;
		
		return player == currentPlayersTurn ? activeSource : source;
	}

	public Long getKeyId() {
		return 0L;
		//return keyId;
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
}
