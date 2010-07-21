package name.nanek.gdwprototype.client.model;

import name.nanek.gdwprototype.shared.model.Marker;
import name.nanek.gdwprototype.shared.model.Markers;

import com.google.gwt.user.client.Random;

/**
 * Generates random terrain.
 * 
 */
public class TerrainGenerator {
	
	public static Marker[][] RANDOM_TERRAIN;
	
	public static void generateRandomTerrain(int width, int height) {
		RANDOM_TERRAIN = new Marker[width][height];
		for(int i = 0; i< width; i++) {
			for(int j = 0; j < height; j++) {
				RANDOM_TERRAIN[i][j] = getRandomTerrainMarker();
			}
		}
	}

	public static Marker getRandomTerrainMarker() {
		//Pick grass half the time without considering anything else to keep board from looking too crowded.
		if ( Random.nextBoolean() ) {
			return Markers.GRASS;
		}
		//Pick randomly other than that.
		int index = Random.nextInt(Markers.TERRAIN_ONLY_MARKERS.length);
		return Markers.TERRAIN_ONLY_MARKERS[index];
	}
	
}
