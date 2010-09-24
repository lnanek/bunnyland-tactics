package name.nanek.gdwprototype.client.view.widget;

import name.nanek.gdwprototype.shared.model.DefaultMarkers;

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
	public void add(Widget w) {
		//All pieces should overlap.
		super.add(w, 0, 0);
	}



	public Integer getRow() {
		return row;
	}

	public Integer getColumn() {
		return column;
	}

}
