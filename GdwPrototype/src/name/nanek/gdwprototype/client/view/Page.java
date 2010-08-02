package name.nanek.gdwprototype.client.view;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
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

	public VerticalPanel screenContent = new VerticalPanel();

	public HTML pageTitle = new HTML();

	public Label errorLabel = new Label();
	
	private RootPanel backgroundRoot;
	
	private Background currentBackground = Background.MENU;

	public Page() {
		VerticalPanel allContent = new VerticalPanel();
		allContent.add(pageTitle);
		errorLabel.addStyleName("serverResponseLabelError");
		allContent.add(errorLabel);
		screenContent.addStyleName("indented");
		allContent.add(screenContent);

		RootPanel root = RootPanel.get("contentContainer");
		//Clear loading message in static HTML.
		root.getElement().setInnerText("");
		root.add(allContent);
		
		backgroundRoot = RootPanel.get("background");
		//backgroundImageElement = backgroundRoot.getElement();
	}

	public void setScreenTitle(String screenTitle) {
		if ( null == screenTitle ) {
			Window.setTitle(GAME_TITLE);
			pageTitle.setVisible(false);
			return;
		}

		Window.setTitle(GAME_TITLE + " : " + screenTitle);
		pageTitle.setHTML("<h2 class=\"tk-nagomi\">" + screenTitle + "</h2>");
		pageTitle.setVisible(true);
	}
	
	public enum Background {
		
		MENU("/images/background_menu.jpg"), 
		BLACKS_TURN("/images/background_blacks_turn.jpg"), 
		REDS_TURN("/images/background_reds_turn.jpg");
		
		private String imageSource;

		private Background(String imageSource) {
			this.imageSource = imageSource;
		}

		public String getImageSource() {
			return imageSource;
		}
	}
	
	public void setBackground(Background bg) {
		if ( bg == currentBackground ) {
			return;
		}
		backgroundRoot.clear();
		backgroundRoot.getElement().setInnerHTML("");
		backgroundRoot.add(new Image(bg.getImageSource()));
		currentBackground = bg;
	}

}
