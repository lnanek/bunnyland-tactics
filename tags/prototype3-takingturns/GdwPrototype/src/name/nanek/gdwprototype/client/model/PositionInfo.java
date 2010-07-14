package name.nanek.gdwprototype.client.model;

import java.io.Serializable;

public class PositionInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private int row;

	private int column;

	private String markerSource;

	public PositionInfo() {
	}

	public PositionInfo(int row, int column, String markerSource) {
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

	public String getMarkerSourceFilename() {

		String fileName = markerSource;
		int lastSlash = fileName.lastIndexOf('/');
		if (-1 != lastSlash) {
			fileName = fileName.substring(lastSlash + 1);
		}
		return fileName;
	}

}
