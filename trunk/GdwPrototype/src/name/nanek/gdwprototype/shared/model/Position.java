package name.nanek.gdwprototype.shared.model;

import java.io.Serializable;

import javax.persistence.Id;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Parent;

/**
 * A place a marker is located on the game board.
 * 
 * @author Lance Nanek
 *
 */
public class Position implements Serializable {

	public static enum Layer { TERRAIN, ITEM, PLAYER_PIECE }
	
	private static final long serialVersionUID = 1L;

	@Id private Long keyId;
	
	@Parent Key<Game> game;
	
	private Key<Marker> marker;

	private int row;

	private int column;

	private Position() {
	}

	public Position(int row, int column, Key<Marker> marker, Key<Game> game) {
		this.row = row;
		this.column = column;
		this.marker = marker;
		this.game = game;
	}

	public int getColumn() {
		return column;
	}

	public int getRow() {
		return row;
	}

	public void setKeyId(long id) {
		this.keyId = id;
	}

	public void setGame(Key<Game> gameKey) {
		this.game = gameKey;
	}

	public Position copy() {
		return new Position(row, column, null, null);
	}

	public void setMarkerKey(Key<Marker> gameMarkerKey) {
		this.marker = gameMarkerKey;
	}
	
	public Key<Marker> getMarkerKey() {
		return marker;
	}

	/*
	public String getMarkerSourceFilename() {

		String fileName = markerSource;
		int lastSlash = fileName.lastIndexOf('/');
		if (-1 != lastSlash) {
			fileName = fileName.substring(lastSlash + 1);
		}
		return fileName;
	}
	*/
}
