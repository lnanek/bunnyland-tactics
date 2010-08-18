package name.nanek.gdwprototype.client.view.widget;

import name.nanek.gdwprototype.client.model.Player;
import name.nanek.gdwprototype.shared.model.Marker;
import name.nanek.gdwprototype.shared.model.Markers;

import com.google.gwt.user.client.ui.Image;

/**
 * Holds a game marker.
 * 
 * @author Lance Nanek
 *
 */
public class GameSquare extends Image {

	public Marker marker;

	//public int row;
	
	//public int col;
	
	public GameSquare(Marker marker, Player currentUsersTurn) {
		super("images/" + marker.getSourceForPlayersTurn(currentUsersTurn), 0, 0, 
				Markers.MARKER_WIDTH_PX, Markers.MARKER_HEIGHT_PX);
		this.marker = marker;
		addMouseDownHandler(ImageUtil.NO_DEFAULT_DRAG_HANDLER);
		setTitle(marker.name);
	}

	/*
	public void setMarker(Marker marker) {
		this.marker = marker;
		setUrl("images/" + marker.source);
	}
	 */
}
