package name.nanek.gdwprototype.client.view;

import name.nanek.gdwprototype.client.model.Marker;

import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ImageResource;
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
