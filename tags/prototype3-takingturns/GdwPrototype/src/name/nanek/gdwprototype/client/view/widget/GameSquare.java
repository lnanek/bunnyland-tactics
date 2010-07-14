package name.nanek.gdwprototype.client.view.widget;

import name.nanek.gdwprototype.client.model.Marker;

import com.google.gwt.user.client.ui.Image;

public class GameSquare extends Image {

	public boolean isVisible;

	public Marker marker;

	public GameSquare(Marker marker) {
		super("images/" + marker.source);
		this.marker = marker;
	}

	public void setMarker(Marker marker2) {
		this.marker = marker2;
		setUrl("images/" + marker.source);
	}

}
