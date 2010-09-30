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

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused") //Used by ORM.
	@Id private Long id;
	
	@SuppressWarnings("unused") //Used by ORM.
	//Transient for performance, not needed on the client. Objectify ignores, but GWT doesn't.
	@Parent private transient Key<Game> game;
	
	//Transient for performance, not needed on the client. Objectify ignores, but GWT doesn't.
	//TODO send this instead of a map of position to marker? can lookup marker on client from gamedisplaydata marker ids
	private transient Key<Marker> marker;

	private int row;

	private int column;

	@SuppressWarnings("unused") //Used by ORM.
	private Position() {
	}

	public Position(int row, int column) {
		this(row, column, null, null);
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

	public void setGame(Key<Game> game) {
		this.game = game;
	}

	public Position copy() {
		return new Position(row, column, null, null);
	}

	public void setMarker(Key<Marker> marker) {
		this.marker = marker;
	}
	
	public Key<Marker> getMarker() {
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
