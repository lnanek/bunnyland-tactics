package name.nanek.gdwprototype.client.model;

import java.util.HashMap;
import java.util.Map;

public class Marker {

	public static final Marker[] ALL_MARKERS = new Marker[] { new Marker("piece_player1_warren.jpg", Player.ONE, 1),
			new Marker("piece_player1_grazer.png", Player.ONE, 1),
			new Marker("piece_player1_scout.png", Player.ONE, 3),
			new Marker("piece_player1_warrior.png", Player.ONE, 1),
			new Marker("piece_player2_warren.jpg", Player.TWO, 1),
			new Marker("piece_player2_grazer.png", Player.TWO, 1),
			new Marker("piece_player2_scout.png", Player.TWO, 3),
			new Marker("piece_player2_warrior.png", Player.TWO, 1), new Marker("tile_flowers_1.jpg", null, null),
			new Marker("tile_flowers_2.jpg", null, null), new Marker("tile_grass.jpg", null, null),
			new Marker("tile_rocks.jpg", null, null), new Marker("tile_trees.jpg", null, null),
			new Marker("tile_fow.png", null, null), };

	public String source;

	public Player player;

	public Integer visionRange;

	public static Map<String, Marker> markerBySource = new HashMap<String, Marker>();
	static {
		for (int i = 0; i < ALL_MARKERS.length; i++) {
			markerBySource.put(ALL_MARKERS[i].source, ALL_MARKERS[i]);
		}
	}

	private Marker(String source, Player player, Integer visionRange) {
		this.source = source;
		this.player = player;
		this.visionRange = visionRange;
	}

	/*
	 * public static Marker[] getPalletteForPlayer(Player player) { return
	 * player == Player.ONE ? NON_P2_MARKERS : NON_P1_MARKERS; }
	 */
}
