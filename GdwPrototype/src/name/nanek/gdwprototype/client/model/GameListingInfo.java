package name.nanek.gdwprototype.client.model;

import java.io.Serializable;

public class GameListingInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String displayName;

	private long id;

	private GameListingInfo() {
	}

	public GameListingInfo(String name, long id) {
		this.displayName = name;
		this.id = id;
	}

	public String getLinkName() {
		if (null == displayName) {
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
