package name.nanek.gdwprototype.client.controller.screen.support;

import name.nanek.gdwprototype.shared.model.Marker;
import name.nanek.gdwprototype.shared.model.Player;

/**
 * Calculates what parts of the game board a player can see.
 * 
 * @author Lance Nanek
 *
 */
public class VisibilityCalculator {
	
	private static void setAllTrue(boolean[][] grid) {
		for (int rowIndex = 0; rowIndex < grid.length; rowIndex++) {
			boolean[] row = grid[rowIndex];
			for (int colIndex = 0; colIndex < row.length; colIndex++) {
				row[colIndex] = true;
			}
		}
	}
	
	public static boolean[][] calculateVisibility(Player fogOfWarAs, int boardWidth, int boardHeight, 
			Marker[][][] positions) {
		
		boolean[][] visibleSquares = new boolean[boardHeight][boardWidth];	
		if ( null == fogOfWarAs ) {
			//TODO cache an all true grid?
			setAllTrue(visibleSquares);
			return visibleSquares;
		}
		
		for ( int markerRow = 0; markerRow < boardHeight; markerRow++ ) {
			for ( int markerCol = 0; markerCol < boardWidth; markerCol++ ) {
				int visionRange = 0;
				boolean belongsToPlayer = false;
				for ( int layer = 0; layer < Marker.Layer.values().length; layer++ ) {
					Marker marker = positions[markerRow][markerCol][layer];
					if ( null == marker ) {
						continue;
					}
					
					visionRange += marker.visionRange;
					if ( marker.player == fogOfWarAs ) {
						belongsToPlayer = true;
					}
				}
				if ( belongsToPlayer && visionRange > 0 ) {
					int startRow = Math.max(0,	markerRow - visionRange);
					int endRow = Math.min(boardHeight - 1,	markerRow + visionRange);
					int startCol = Math.max(0,	markerCol - visionRange);
					int endCol = Math.min(boardWidth - 1, markerCol + visionRange);
					
					for (int row = startRow; row <= endRow; row++) {
						for (int col = startCol; col <= endCol; col++) {
							int rowDistance = Math.abs(markerRow - row);
							int colDistance = Math.abs(markerCol - col);
							int totalDistance = rowDistance + colDistance;
							if ( totalDistance <= visionRange ) {
								visibleSquares[row][col] = true;
							}
						}
					}
				}
			}
		}
		return visibleSquares;
	}
}
