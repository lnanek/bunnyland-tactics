package name.nanek.gdwprototype.shared.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.datanucleus.jpa.annotations.Extension;

@Entity
public class GameSettings implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Extension(vendorName="datanucleus", key="gae.encoded-pk", value="true")
    private String encodedKey;

    @Extension(vendorName="datanucleus", key="gae.pk-id", value="true")
    private Long keyId;

    @OneToMany(fetch = FetchType.EAGER, cascade = 
    {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
	private Set<Marker> markers;
    
    //private Game game;

	private int boardWidth = 8;

	private int boardHeight = 8;

	public int getBoardWidth() {
		return boardWidth;
	}

	public void setBoardWidth(int boardWidth) {
		this.boardWidth = boardWidth;
	}

	public int getBoardHeight() {
		return boardHeight;
	}

	public void setBoardHeight(int boardHeight) {
		this.boardHeight = boardHeight;
	}

	public Set<Marker> getMarkers() {
		return markers;
	}

	public void setMarkers(Set<Marker> markers) {
		this.markers = markers;
	}

	public GameSettings copy() {
		GameSettings copy = new GameSettings();
		copy.setBoardHeight(boardHeight);
		copy.setBoardWidth(boardWidth);
		Set<Marker> copyMarkers = new HashSet<Marker>();
		for( Marker marker : markers ) {
			copyMarkers.add(marker.copy());
		}
		copy.setMarkers(copyMarkers);
		return copy;
	}	
}
