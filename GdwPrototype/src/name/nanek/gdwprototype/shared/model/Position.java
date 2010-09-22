package name.nanek.gdwprototype.shared.model;

import java.io.Serializable;

import com.google.code.twig.annotation.Child;
import com.google.code.twig.annotation.Id;
import com.google.code.twig.annotation.Key;

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
