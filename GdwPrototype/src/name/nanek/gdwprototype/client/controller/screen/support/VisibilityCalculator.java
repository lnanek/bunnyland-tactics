package name.nanek.gdwprototype.client.controller.screen.support;

import java.util.Map;

import name.nanek.gdwprototype.client.model.Player;
import name.nanek.gdwprototype.shared.model.Marker;
import name.nanek.gdwprototype.shared.model.Position;

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
	
	public static boolean[][] calculateVisibility(Player fogOfWarAs, int boardWidth, int boardHeight, Map<Position, Marker> positions) {
		boolean[][] visibleSquares = new boolean[boardHeight][boardWidth];	
		if ( null == fogOfWarAs ) {
			//TODO cache an all true grid?
			setAllTrue(visibleSquares);
			return visibleSquares;
		}
		
		for (Map.Entry<Position, Marker> entry : positions.entrySet() ) {
			Position position = entry.getKey();
			Marker marker = entry.getValue();
			int markerRow = position.getRow();
			int markerCol = position.getColumn();
			
			if ( null == marker || null == marker.visionRange || marker.player != fogOfWarAs ) {
				continue;
			}

			int startRow = Math.max(0,	markerRow - marker.visionRange);
			int endRow = Math.min(boardHeight - 1,	markerRow + marker.visionRange);
			int startCol = Math.max(0,	markerCol - marker.visionRange);
			int endCol = Math.min(boardWidth - 1, markerCol + marker.visionRange);
			
			for (int row = startRow; row <= endRow; row++) {
				for (int col = startCol; col <= endCol; col++) {
					int rowDistance = Math.abs(markerRow - row);
					int colDistance = Math.abs(markerCol - col);
					int totalDistance = rowDistance + colDistance;
					if (totalDistance <= marker.visionRange) {
						visibleSquares[row][col] = true;
					}
				}
			}
		}
		return visibleSquares;
	}
}
