package name.nanek.gdwprototype.shared.model;

import java.io.Serializable;

import javax.persistence.Id;

import com.vercer.engine.persist.annotation.Child;
import com.vercer.engine.persist.annotation.Key;

/**
 * A place a marker is located on the game board.
 * 
 * @author Lance Nanek
 *
 */
public class Position implements Serializable {

	public static enum Layer { TERRAIN, ITEM, PLAYER_PIECE }
	
	private static final long serialVersionUID = 1L;

	@Key private Long keyId;

	@Child private Marker marker;

	private int row;

	private int column;
	
	private Layer layer;

	private Position() {
	}

	public Position(int row, int column, Marker marker) {
		if ( null == marker ) {
			throw new IllegalArgumentException("Position cannot have a null marker.");
		}
		
		this.row = row;
		this.column = column;
		this.marker = marker;
	}

	public int getColumn() {
		return column;
	}

	public int getRow() {
		return row;
	}
	
	public Marker getMarker() {
		return marker;
	}

	public void setKeyId(long id) {
		this.keyId = id;
	}

	/*
	public String getMarkerSource() {
		return markerSource;
	}

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
