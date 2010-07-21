package name.nanek.gdwprototype.client.view.widget;

import name.nanek.gdwprototype.shared.model.Marker;

import com.google.gwt.user.client.ui.Image;

public class GameSquare extends Image {

	public Marker marker;

	//public int row;
	
	//public int col;
	
	public GameSquare(Marker marker) {
		super("images/" + marker.source);
		this.marker = marker;
	}

	public void setMarker(Marker marker2) {
		this.marker = marker2;
		setUrl("images/" + marker.source);
	}

}
