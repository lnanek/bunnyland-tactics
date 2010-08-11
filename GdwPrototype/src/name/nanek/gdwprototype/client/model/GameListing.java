package name.nanek.gdwprototype.client.model;

import java.io.Serializable;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

/**
 * Contains information needed to display a game or map in a list.
 * 
 * @author Lance Nanek
 *
 */
public class GameListing implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;

	private long id;
	
	private boolean map;
	
	private String creatorNickname;

	private GameListing() {
	}

	public GameListing(String name, long id, boolean map, String creatorNickname) {
		this.name = name;
		this.id = id;
		this.map = map;
		this.creatorNickname = creatorNickname;
	}

	public String getLinkName() {
		if (null == name) {
			return null;
		}
		return name.replace(' ', '_');
	}

	public String getDisplayName(boolean includeAuthor) {
		String gameName;
		if ( nullOrEmpty(name) ) {
			gameName = Long.toString(id);
		} else {
			gameName = '"' + name + '"';
		}
		
		if ( includeAuthor ) {
			if ( !nullOrEmpty(creatorNickname) ) {
				gameName += " (by " + escapeHtml(creatorNickname) + ")";
			}
		}
		return gameName;
	}
	
	private boolean nullOrEmpty(String testValue) {
		return null == testValue || "".equals(testValue.trim());
	}

	public long getId() {
		return id;
	}

	public boolean isMap() {
		return map;
	}

	private static String escapeHtml(String maybeHtml) {
		final Element div = DOM.createDiv();
		DOM.setInnerText(div, maybeHtml);
		return DOM.getInnerHTML(div);
	}

}
