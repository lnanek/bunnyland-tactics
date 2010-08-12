package name.nanek.gdwprototype.shared.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import name.nanek.gdwprototype.client.model.Player;

import org.datanucleus.jpa.annotations.Extension;

/**
 * A marker moved on the game board.
 * 
 * @author Lance Nanek
 *
 */
@Entity
public class Marker implements Serializable {
	
	public enum Role {
		HOME, STOMPER, CARROT, SCOUT, 
	}
	
	private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Extension(vendorName="datanucleus", key="gae.encoded-pk", value="true")
    private String encodedKey;

    @Extension(vendorName="datanucleus", key="gae.pk-id", value="true")
    private Long keyId;
    
	public String source;

	public Player player;

	public Integer visionRange;

	public Integer movementRange;

	public String name;
	
	public boolean terrain;
	
	public Role role;
	
	private Marker() {
	}
	
	public Marker(String name, String source, Player player, Integer visionRange, Integer movementRange, Role role, boolean terrain) {
		this.name = name;
		this.source = source;
		this.player = player;
		this.visionRange = visionRange;
		this.movementRange = movementRange;
		this.role = role;
		this.terrain = terrain;
	}
	
	public static Marker makeTerrain(String name, String source) {
		return new Marker(name, source, null, null, null, null, true);
	}

	public Marker copy() {
		Marker copy = new Marker(name, source, player, visionRange, movementRange, role, terrain);
		return copy;
	}

	public Long getKeyId() {
		return keyId;
	}
}
