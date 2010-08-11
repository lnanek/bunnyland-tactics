package name.nanek.gdwprototype.client.model;

import java.io.Serializable;

import name.nanek.gdwprototype.shared.model.Marker;

public class GameDisplayInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public Marker[] markers;

	public int boardWidth;
	
	public int boardHeight;
	
	public boolean map;

	public GameListing listing;
	
	public GamePlayInfo playInfo;
		
	private GameDisplayInfo() {
	}

	public GameDisplayInfo(Marker[] markers, int boardHeight, int boardWidth, boolean map,
			GameListing listing, GamePlayInfo playInfo) {
		this.markers = markers;
		this.boardWidth = boardWidth;
		this.boardHeight = boardHeight;
		this.map = map;
		this.playInfo = playInfo;
		this.listing = listing;
	}

}
