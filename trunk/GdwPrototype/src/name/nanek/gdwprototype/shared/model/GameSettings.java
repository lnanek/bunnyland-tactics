package name.nanek.gdwprototype.shared.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Id;

import com.vercer.engine.persist.annotation.Child;
import com.vercer.engine.persist.annotation.Key;

/**
 * Settings for a game.
 * 
 * @author Lance Nanek
 *
 */
public class GameSettings implements Serializable {
	private static final long serialVersionUID = 1L;

	@Key private Long keyId;

    @Child private Set<Marker> markers;

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

	public void setKeyId(long id) {
		this.keyId = id;
	}	
}
