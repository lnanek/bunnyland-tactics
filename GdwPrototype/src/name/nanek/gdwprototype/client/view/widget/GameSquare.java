package name.nanek.gdwprototype.client.view.widget;

import name.nanek.gdwprototype.shared.model.Marker;
import name.nanek.gdwprototype.shared.model.Player;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Image;

/**
 * Holds a game marker.
 * 
 * @author Lance Nanek
 *
 */
public class GameSquare extends Image {

	public Marker marker;

	public GameSquare(Marker marker, Player currentUsersTurn) {
		addMouseDownHandler(ImageUtil.NO_DEFAULT_DRAG_HANDLER);
		set(marker, currentUsersTurn);
		GWT.log("GameSquare created.");
	}

	public void set(Marker newMarker, Player currentUsersTurn) {
		if ( null == marker || !marker.source.equals(marker.source) ) {
			setTitle(newMarker.name);
			if ( null != marker ) {
				removeStyleName(marker.getCssLayerStyle());
			}
			addStyleName(newMarker.getCssLayerStyle());
		}
		
		String newUrl = "images/" + newMarker.getSourceForPlayersTurn(currentUsersTurn);
		if ( !newUrl.equals(getUrl())) {
			setUrl(newUrl);
		}
		
		this.marker = newMarker;

		GWT.log("GameSquare set.");
	}
	
}
