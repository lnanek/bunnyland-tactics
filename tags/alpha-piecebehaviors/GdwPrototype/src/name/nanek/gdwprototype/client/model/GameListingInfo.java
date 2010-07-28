package name.nanek.gdwprototype.client.model;

import java.io.Serializable;

public class GameListingInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;

	private long id;

	private GameListingInfo() {
	}

	public GameListingInfo(String name, long id) {
		this.name = name;
		this.id = id;
	}

	public String getLinkName() {
		if (null == name) {
			return null;
		}
		return name.replace(' ', '_');
	}

	public String getName() {
		if ( null == name || "".equals(name.trim()) ) {
			return Long.toString(id);
		}
		return '"' + name + '"';
	}

	public long getId() {
		return id;
	}
}
