package name.nanek.gdwprototype.client.model;

import java.io.Serializable;

import name.nanek.gdwprototype.shared.model.Game;
import name.nanek.gdwprototype.shared.model.Marker;

/**
 * Contains information needed to display a game board.
 * 
 * @author Lance Nanek
 *
 */
public class GameDisplayInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public Marker[] markers;

	public Game game;
	
	public GameUpdateInfo playInfo;
		
	@SuppressWarnings("unused") //Used by GWT serialization.
	private GameDisplayInfo() {
	}

	public GameDisplayInfo(Marker[] markers, Game game, GameUpdateInfo playInfo) {
		this.markers = markers;
		this.playInfo = playInfo;
		this.game = game;
	}

}
