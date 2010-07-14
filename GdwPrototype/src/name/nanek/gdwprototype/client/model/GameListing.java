package name.nanek.gdwprototype.client.model;

import java.io.Serializable;

public class GameListing implements Serializable {

	private static final long serialVersionUID = 1L;

	private String displayName;
	
	private long id;

	public GameListing() {
	}

	public GameListing(String name, long id) {
		this.displayName = name;
		this.id = id;
	}
	
	public String getLinkName() {
		if ( null == displayName ) {
			return null;
		}
		
		return displayName.replace(' ', '_');
	}

	public String getDisplayName() {
		return displayName;
	}

	public long getId() {
		return id;
	}
}
