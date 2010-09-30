package name.nanek.gdwprototype.client.view.widget;

import name.nanek.gdwprototype.shared.model.Marker;

/**
 * Holds a game marker that isn't on the board, only in the palette of possible units shown when map building.
 * 
 * @author Lance Nanek
 *
 */
public class PaletteImage extends GameSquare {

	public PaletteImage(Marker marker) {
		super(marker, null);
	}

}
