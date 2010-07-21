package name.nanek.gdwprototype.shared.model;

import java.util.HashMap;
import java.util.Map;

import name.nanek.gdwprototype.client.model.Player;

public class Markers {

	private static final Marker PLAYER_ONE_WARREN = 
		new Marker("Player 1 Warren", "piece_player1_warren.jpg", Player.ONE, 1, 0);
	
	private static final Marker PLAYER_TWO_WARREN = 
		new Marker("Player 2 Warren", "piece_player2_warren.jpg", Player.TWO, 1, 0);
	
	public static final Marker FOG_OF_WAR = new Marker("tile_fow.png");
	
	public static final Marker GRASS = new Marker("tile_grass.jpg");
	
	public static final Marker[] PLAYING_PIECES = new Marker[] { 
		PLAYER_ONE_WARREN,
		new Marker("Player 1 Scout", "piece_player1_scout.png", Player.ONE, 3, 3),
		new Marker("Player 1 Stomper", "piece_player1_warrior.png", Player.ONE, 1, 3),
		
		PLAYER_TWO_WARREN, 
		new Marker("Player 2 Scout", "piece_player2_scout.png", Player.TWO, 3, 3),
		new Marker("Player 2 Stomper", "piece_player2_warrior.png", Player.TWO, 1, 3), 
	};
	
	public static final Marker[] TERRAIN_ONLY_MARKERS = new Marker[] { 
		GRASS,
		new Marker("tile_flowers_1.jpg"),
		new Marker("tile_flowers_2.jpg"), 
		new Marker("tile_rocks.jpg"), 
		new Marker("tile_tree.jpg"), 
		new Marker("tile_carrot.jpg"), 
	};
	
	public static final Marker[] ALL_MARKERS;
	static {
		ALL_MARKERS = new Marker[PLAYING_PIECES.length + TERRAIN_ONLY_MARKERS.length + 1];
		System.arraycopy(PLAYING_PIECES, 0, ALL_MARKERS, 0, PLAYING_PIECES.length);
		System.arraycopy(TERRAIN_ONLY_MARKERS, 0, ALL_MARKERS, PLAYING_PIECES.length, TERRAIN_ONLY_MARKERS.length);
		ALL_MARKERS[ALL_MARKERS.length - 1] = FOG_OF_WAR;
	};

	public static Marker getEnemyWarren(Player currentUsersTurn) {
		if ( null == currentUsersTurn ) {
			return null;
		}
		return currentUsersTurn == Player.ONE ? PLAYER_TWO_WARREN : PLAYER_ONE_WARREN;
	}

	public static Map<String, Marker> markerBySource = new HashMap<String, Marker>();
	static {
		for (int i = 0; i < ALL_MARKERS.length; i++) {
			markerBySource.put(ALL_MARKERS[i].source, ALL_MARKERS[i]);
		}
	}
}
