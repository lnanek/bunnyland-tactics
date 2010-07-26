package name.nanek.gdwprototype.client.model;

import java.io.Serializable;

public class GameListing implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;

	private long id;
	
	private boolean startingMap;

	private GameListing() {
	}

	public GameListing(String name, long id, boolean startingMap) {
		this.name = name;
		this.id = id;
		this.startingMap = startingMap;
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

	public boolean isStartingMap() {
		return startingMap;
	}
}
