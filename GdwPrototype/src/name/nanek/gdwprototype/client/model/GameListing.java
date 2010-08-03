package name.nanek.gdwprototype.client.model;

import java.io.Serializable;

public class GameListing implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;

	private long id;
	
	private boolean startingMap;
	
	private String creatorNickname;

	private GameListing() {
	}

	public GameListing(String name, long id, boolean startingMap, String creatorNickname) {
		this.name = name;
		this.id = id;
		this.startingMap = startingMap;
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
				gameName += " (by " + creatorNickname + ")";
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

	public boolean isStartingMap() {
		return startingMap;
	}
}
