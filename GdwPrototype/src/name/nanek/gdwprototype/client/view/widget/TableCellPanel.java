package name.nanek.gdwprototype.client.view.widget;

import name.nanek.gdwprototype.shared.model.DefaultMarkers;
import name.nanek.gdwprototype.shared.model.Marker.Layer;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;

/**
 * A panel that knows where it is on the game board.
 * 
 * @author Lance Nanek
 *
 */
public class TableCellPanel extends AbsolutePanel {

	private Integer row;

	private Integer column;
	
	public GameSquare[] children = new GameSquare[Layer.values().length];

	public TableCellPanel(Widget widget, FlexTable table, int tableRow, int tableCol, Integer gameRow, Integer gameCol) {
		super();
		if (null != widget) {
			add(widget);
		}
		setPixelSize(DefaultMarkers.MARKER_WIDTH_PX, DefaultMarkers.MARKER_HEIGHT_PX);
		table.setWidget(tableRow, tableCol, this);
		this.row = gameRow;
		this.column = gameCol;
	}
	
	@Override
	public boolean remove(Widget w) {
		if ( null == w ) {
			return false;
		}
		if ( !( w instanceof GameSquare ) ) {
			throw new IllegalArgumentException("Only GameSquare widgets may be removed.");
		}
		GameSquare square = (GameSquare) w;
		int layer = square.previousMarker.getLayer().ordinal();
		children[layer] = null;
		return super.remove(w);
	}

	@Override
	public void add(Widget w) {
		if ( null == w ) {
			return;
		}
		if ( !( w instanceof GameSquare ) ) {
			throw new IllegalArgumentException("Only GameSquare widgets may be added.");
		}
		GameSquare square = (GameSquare) w;
		int layer = square.previousMarker.getLayer().ordinal();
		
		GameSquare previousSquare = children[layer];
		if ( null != previousSquare ) {
			super.remove(previousSquare);
		}
		children[layer] = square;
		//0, 0 so that all pieces overlap.
		super.add(w, 0, 0);
	}
	
	
	
	//XXX wrap the real panel and only expose own methods? there are other add/remove methods not covered right now

	@Override
	public void clear() {
		for ( GameSquare child : children ) {
			remove(child);
		}
	}

	public Integer getRow() {
		return row;
	}

	public Integer getColumn() {
		return column;
	}

}
