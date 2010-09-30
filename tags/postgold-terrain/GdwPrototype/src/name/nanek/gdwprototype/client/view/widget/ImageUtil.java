package name.nanek.gdwprototype.client.view.widget;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;

/**
 * Utility methods for working with images.
 * 
 * @author Lance Nanek
 *
 */
public class ImageUtil {
	/**
	 * Calls prevent default on mouse down events.
	 */
	public static final MouseDownHandler NO_DEFAULT_DRAG_HANDLER = new MouseDownHandler() {
		@Override
		public void onMouseDown(MouseDownEvent event) {
		    event.preventDefault();
		}
	};

	/**
	 * Creates an image that isn't draggable to the Desktop to save it in Firefox.
	 * This makes it more clear what can be moved in the game and what can't.
	 * 
	 * @param url String source of the image
	 * @return image
	 */
/*	public static Image getNoDefaultDragImage(final String url) {
		Image image = new Image(url);
		image.addMouseDownHandler(NO_DEFAULT_DRAG_HANDLER);
		return image;
	}
	*/
}
