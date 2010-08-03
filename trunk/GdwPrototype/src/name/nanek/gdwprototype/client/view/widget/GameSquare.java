package name.nanek.gdwprototype.client.view.widget;

import name.nanek.gdwprototype.shared.model.Marker;

import com.google.gwt.user.client.ui.Image;

public class GameSquare extends Image {

	public Marker marker;

	//public int row;
	
	//public int col;
	
	public GameSquare(Marker marker) {
		super("images/" + marker.source, 0, 0, 50, 50);
		this.marker = marker;
		addMouseDownHandler(ImageUtil.NO_DEFAULT_DRAG_HANDLER);
		setTitle(marker.name);
	}

	public void setMarker(Marker marker) {
		this.marker = marker;
		setUrl("images/" + marker.source);
	}

}
