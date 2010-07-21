package name.nanek.gdwprototype.shared.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import name.nanek.gdwprototype.client.model.Player;

import org.datanucleus.jpa.annotations.Extension;

@Entity
public class Marker implements Serializable {
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
	
	Marker() {
	}
	
	Marker(String source) {
		this(null, source, null, null, null);
	}
	
	Marker(String name, String source, Player player, Integer visionRange, Integer movementRange) {
		this.source = source;
		this.player = player;
		this.visionRange = visionRange;
		this.name = name;
		this.movementRange = movementRange;
	}

	/*
	 * public static Marker[] getPalletteForPlayer(Player player) { return
	 * player == Player.ONE ? NON_P2_MARKERS : NON_P1_MARKERS; }
	 */
}
