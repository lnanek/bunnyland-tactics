package name.nanek.gdwprototype.shared.model;

import java.util.ArrayList;


/**
 * Holds all available markers.
 * 
 * @author Lance Nanek
 *
 */
public class DefaultMarkers {
	//TODO the 1-bit transparency on the animated GIFs looks really bad
	//maybe have separate PNG frames and switch between them via timers?
	//alternatively, have images for every combination of marker and tile
	
	//TODO have images for different directions? if a unit moves right, show facing right, etc.
	
	public static final int MARKER_HEIGHT_PX = 64;
	
	public static final int MARKER_WIDTH_PX = 64;

	public static final Marker CARROT = 
		new Marker("Carrot", "tile_carrot.png", null, null, 0, 0, Marker.Role.CARROT, true);

	public static final Marker GRASS = 
		new Marker("Grass", "tile_grass.png", null, null, 0, 0, Marker.Role.GRASS, true);

	public static final Marker HILL = Marker.makeTerrain("Hill", "tile_hill.png", 1);

	public static final Marker TREE = Marker.makeTerrain("Tree", "tile_tree.png", -1);

	public static final Marker PLAYER_ONE_STOMPER = 
		new Marker("Player 1 Stomper", "piece_player1_warrior.png", "piece_player1_warrior_jumping.gif", Player.ONE, 2, 2, Marker.Role.STOMPER, false);

	public static final Marker PLAYER_ONE_SCOUT = 
		new Marker("Player 1 Scout", "piece_player1_scout.png", "piece_player1_scout_looking_around.gif", Player.ONE, 3, 3, Marker.Role.SCOUT, false);

	public static final Marker PLAYER_ONE_WARREN = 
		new Marker("Player 1 Home", "piece_player1_home.png", null,  Player.ONE, 1, 0, Marker.Role.HOME, false);

	public static final Marker PLAYER_TWO_STOMPER = 
		new Marker("Player 2 Stomper", "piece_player2_warrior.png", "piece_player2_warrior_jumping.gif", Player.TWO, 2, 2, Marker.Role.STOMPER, false);

	public static final Marker PLAYER_TWO_SCOUT = 
		new Marker("Player 2 Scout", "piece_player2_scout.png", "piece_player2_scout_looking_around.gif", Player.TWO, 3, 3, Marker.Role.SCOUT, false);

	public static final Marker PLAYER_TWO_WARREN = 
		new Marker("Player 2 Home", "piece_player2_home.png", null, Player.TWO, 1, 0, Marker.Role.HOME, false);
	
	public static final Marker FOG_OF_WAR = 
		new Marker("You can't see this far.", "tile_fog_of_war.png", null, null, 0, 0, null, false);
	
	public static final Marker[] ALL_MARKERS = new Marker[] { 
		PLAYER_ONE_WARREN,
		PLAYER_ONE_SCOUT,
		PLAYER_ONE_STOMPER,
		
		PLAYER_TWO_WARREN, 
		PLAYER_TWO_SCOUT,
		PLAYER_TWO_STOMPER, 

		CARROT,
		GRASS, 
		TREE, 
		HILL, 
		
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
