package name.nanek.gdwprototype.client.view;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Browser page that all other content in the application is shown inside.
 * 
 * @author Lance Nanek
 */
public class Page {

	private static final String GAME_TITLE = "Bunnyland Tactics";

	public VerticalPanel allContent = new VerticalPanel();

	public HTML pageTitle = new HTML();

	public Label errorLabel = new Label();

	public Page() {
		allContent.add(pageTitle);
		errorLabel.addStyleName("serverResponseLabelError");
		allContent.add(errorLabel);
		RootPanel root = RootPanel.get("contentContainer");
		
		//Clear loading message in static HTML.
		root.getElement().setInnerText("");
		
		root.add(allContent);
	}

	public void setScreenTitle(String screenTitle) {
		if ( null == screenTitle ) {
			Window.setTitle(GAME_TITLE);
			pageTitle.setVisible(false);
			return;
		}
		
		Window.setTitle(GAME_TITLE + " : " + screenTitle);
		pageTitle.setHTML("<h2>" + screenTitle + "</h2>");
	}

}
