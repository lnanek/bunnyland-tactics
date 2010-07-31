package name.nanek.gdwprototype.client.view.screen;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Holds widgets that list the games that can be observed.
 * 
 * @author Lance Nanek
 *
 */
public class StartObservationScreen {
	
	public VerticalPanel content = new VerticalPanel();

	public final FlexTable observableGamesTable = new FlexTable();

	public StartObservationScreen() {

		HTML joinGameLabel = new HTML("<h3>Games you can observe:</h3>");
		joinGameLabel.addStyleName("heavy");
		content.add(joinGameLabel);

		observableGamesTable.setText(0, 0, "Loading...");
		VerticalPanel indented = new VerticalPanel();
		indented.addStyleName("indented");
		indented.add(observableGamesTable);
		content.add(indented);
	}

}
