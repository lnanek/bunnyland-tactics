package name.nanek.gdwprototype.client.view.widget;

import name.nanek.gdwprototype.shared.model.Marker;
import name.nanek.gdwprototype.shared.model.Player;

import com.google.gwt.user.client.ui.Image;

/**
 * Holds a game marker.
 * 
 * @author Lance Nanek
 *
 */
public class GameSquare extends Image {

	public Marker previousMarker;

	public Player previousPlayer;
	
	public GameSquare(Marker newMarker, Player newPlayer) {
		addMouseDownHandler(ImageUtil.NO_DEFAULT_DRAG_HANDLER);

		previousMarker = newMarker;
		previousPlayer = newPlayer;

		setTitle(newMarker.name);
		addStyleName(newMarker.getCssLayerStyle());
		setUrl("images/" + newMarker.getSourceForPlayersTurn(newPlayer));		
	}

	public void set(Marker newMarker, Player newPlayer) {
		//XXX Performance hack: assume previous not null.
	
		//XXX Performance hack: assume if source the same, marker is the same, which holds for now.
		//TODO Maybe even switch to instance equals, if stop sending new Markers over the wire each update.
		if ( previousMarker.source.equals(newMarker.source) ) {
			if ( previousPlayer == newPlayer ) {
				return;
			}
			
			setUrl("images/" + newMarker.getSourceForPlayersTurn(newPlayer));		
			previousPlayer = newPlayer;
			return;
		}
		
		setTitle(newMarker.name);
		removeStyleName(previousMarker.getCssLayerStyle());
		addStyleName(newMarker.getCssLayerStyle());
		setUrl("images/" + newMarker.getSourceForPlayersTurn(newPlayer));		
		previousMarker = newMarker;
		previousPlayer = newPlayer;
	}
	
}
