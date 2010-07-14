package name.nanek.gdwprototype.client;

import java.io.Serializable;

public class PositionInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
    
    private int row;
	
    private int column;
    
    private String markerSource;

	public PositionInfo() {
	}

	public PositionInfo(int row, int column,String markerSource) {
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

}
