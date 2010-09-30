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
	//TODO just wrap real container somehow, so don't have to override the interface as much?
	//even with all this there's still the remove method on the children iterator which
	//might make things out of sync...

	private Integer row;

	private Integer column;
	
	public GameSquare[] children = new GameSquare[Layer.values().length];
	
	private boolean isChanging;

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
		try {
			isChanging = true;
			if ( super.remove(w) ) {
				children[layer] = null;
				return true;
			}
		} finally {
			isChanging = false;
			
		}
		return false;
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
			remove(previousSquare);
		}
		
		children[layer] = square;
		//0, 0 so that all pieces overlap.
		try {
			isChanging = true;
			super.add(w, 0, 0);
		} finally {
			isChanging = false;
			
		}
	}
		
	@Override
	public void clear() {
		try {
			isChanging = true;
			super.clear();
		} finally {
			isChanging = false;
		}
		children = new GameSquare[Layer.values().length];
	}

	@Override
	public void add(Widget w, int left, int top) {
		add(w);
	}

	@Override
	public void insert(Widget w, int left, int top, int beforeIndex) {
		if ( !isChanging ) {
			throw new UnsupportedOperationException();
		}
		super.insert(w, left, top, beforeIndex);
	}

	@Override
	public void insert(Widget w, int beforeIndex) {
		if ( !isChanging ) {
			throw new UnsupportedOperationException();
		}
		super.insert(w, beforeIndex);
	}

	@Override
	public boolean remove(int index) {
		if ( !isChanging ) {
			throw new UnsupportedOperationException();
		}
		return super.remove(index);
	}

	public Integer getRow() {
		return row;
	}

	public Integer getColumn() {
		return column;
	}

}
