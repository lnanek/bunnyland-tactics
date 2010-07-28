package name.nanek.gdwprototype.client.view.screen.support;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ScreenUtil {

	public static Widget labelAndWrap(String label, Widget control) {
		//TODO use html label element somehow so clicking focuses the control? or write own click handler?
		HorizontalPanel panel = new HorizontalPanel();
		panel.add(new Label(label));
		panel.add(control);
		return panel;
	}
	
}
