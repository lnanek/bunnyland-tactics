package name.nanek.gdwprototype.client.view.widget;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A panel that knows where it is on the game board.
 * 
 * @author Lance Nanek
 *
 */
public class TableCellPanel extends SimplePanel {

	private int row;

	private int column;

	public TableCellPanel(Widget widget, FlexTable table, int row, int column) {
		super();
		if (null != widget) {
			add(widget);
		}
		setPixelSize(50, 50);
		table.setWidget(row, column, this);
		this.row = row;
		this.column = column;
	}

	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}

}