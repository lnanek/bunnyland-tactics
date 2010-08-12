package name.nanek.gdwprototype.shared.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.datanucleus.jpa.annotations.Extension;

/**
 * A place a marker is located on the game board.
 * 
 * @author Lance Nanek
 *
 */
@Entity
public class Position implements Serializable {

	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Extension(vendorName="datanucleus", key="gae.encoded-pk", value="true")
    private String encodedKey;

	private int row;

	private int column;

	@OneToOne(cascade = CascadeType.ALL)
	private Marker marker;

	public Position() {
	}

	public Position(int row, int column, Marker marker) {
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
