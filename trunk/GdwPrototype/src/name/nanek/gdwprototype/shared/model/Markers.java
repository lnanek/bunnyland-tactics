package name.nanek.gdwprototype.shared.model;

import java.util.ArrayList;

import name.nanek.gdwprototype.client.model.Player;

/**
 * Holds all available markers.
 * 
 * @author Lance Nanek
 *
 */
public class Markers {

	public static final Marker CARROT = 
		new Marker("Carrot", "tile_carrot.png", null, null, null, Marker.Role.CARROT, true);
	
	public static final Marker PLAYER_ONE_STOMPER = 
		new Marker("Player 1 Stomper", "piece_player1_warrior.png", Player.ONE, 2, 2, Marker.Role.STOMPER, false);

	public static final Marker PLAYER_ONE_SCOUT = 
		new Marker("Player 1 Scout", "piece_player1_scout.png", Player.ONE, 3, 3, Marker.Role.SCOUT, false);

	public static final Marker PLAYER_ONE_WARREN = 
		new Marker("Player 1 Home", "piece_player1_home.png", Player.ONE, 1, 0, Marker.Role.HOME, false);

	public static final Marker PLAYER_TWO_STOMPER = 
		new Marker("Player 2 Stomper", "piece_player2_warrior.png", Player.TWO, 2, 2, Marker.Role.STOMPER, false);

	public static final Marker PLAYER_TWO_SCOUT = 
		new Marker("Player 2 Scout", "piece_player2_scout.png", Player.TWO, 3, 3, Marker.Role.SCOUT, false);

	public static final Marker PLAYER_TWO_WARREN = 
		new Marker("Player 2 Home", "piece_player2_home.png", Player.TWO, 1, 0, Marker.Role.HOME, false);
	
	public static final Marker FOG_OF_WAR = 
		new Marker("You can't see this far.", "tile_fog_of_war.png", null, null, null, null, false);
	
	public static final Marker[] ALL_MARKERS = new Marker[] { 
		PLAYER_ONE_WARREN,
		PLAYER_ONE_SCOUT,
		PLAYER_ONE_STOMPER,
		
		PLAYER_TWO_WARREN, 
		PLAYER_TWO_SCOUT,
		PLAYER_TWO_STOMPER, 

		CARROT,
		Marker.makeTerrain("Tree", "tile_tree.png"), 
		Marker.makeTerrain("Grass", "tile_grass.png"), 
		Marker.makeTerrain("Hill", "tile_hill.png"), 
		
		FOG_OF_WAR,
	};
	
	public static final Marker[] PLAYING_PIECES;
	static {
		ArrayList<Marker> playingPieces = new ArrayList<Marker>();
		for( Marker marker : ALL_MARKERS ){
			if ( null != marker.player ) {
				playingPieces.add(marker);
			}
		}
		PLAYING_PIECES = playingPieces.toArray(new Marker[] {});
	}
	
	public static final Marker[] MAP_MAKING_PIECES;
	static {
		ArrayList<Marker> mapMakingPieces = new ArrayList<Marker>();
		for( Marker marker : ALL_MARKERS ){
			if ( null != marker.player || marker.terrain ) {
				mapMakingPieces.add(marker);
			}
		}
		MAP_MAKING_PIECES = mapMakingPieces.toArray(new Marker[] {});
	}

}
