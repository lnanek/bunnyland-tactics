package name.nanek.gdwprototype.server;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import name.nanek.gdwprototype.client.PositionInfo;

import com.google.appengine.api.datastore.Key;

@Entity
public class Position {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Key key;
    
    //@ManyToOne
    //private Game game;
    
    private int row;
    
    private int column;
    
    private String markerSource;

	public Position() {
	}

	public Position(int row, int column, String markerSource) {
		this.row = row;
		this.column = column;
		this.markerSource = markerSource;
	}

	public int getColumn() {
		return column;
	}

	public int getRow() {
		return row;
	}

	public String getMarkerSource() {
		return markerSource;
	}

	public PositionInfo getInfo() {
		return new PositionInfo(row, column, markerSource);
	}
    
}
