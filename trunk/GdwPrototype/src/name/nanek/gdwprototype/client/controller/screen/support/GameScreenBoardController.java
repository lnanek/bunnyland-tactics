package name.nanek.gdwprototype.client.controller.screen.support;

import name.nanek.gdwprototype.client.controller.screen.GameScreenController;
import name.nanek.gdwprototype.client.model.GameDisplayInfo;
import name.nanek.gdwprototype.client.model.GameUpdateInfo;
import name.nanek.gdwprototype.client.view.widget.GameSquare;
import name.nanek.gdwprototype.client.view.widget.TableCellPanel;
import name.nanek.gdwprototype.shared.model.DefaultMarkers;
import name.nanek.gdwprototype.shared.model.Marker;

import com.allen_sauer.gwt.dnd.client.DragController;

/**
 * Controls the game board on the game screen.
 * 
 * @author Lance Nanek
 *
 */
public class GameScreenBoardController {
	//TODO more efficient just to prepopulate the DOM with an image for every x, y, z and tweak those each update?
	//would have lots of transparent images each draw (maybe test if no src works as transparent?)
	
	private GameScreenController gameScreenController;
	
	private GameDisplayInfo displayInfo;
	
	public GameScreenBoardController(GameScreenController gameScreenController, GameDisplayInfo displayInfo) {
		this.gameScreenController = gameScreenController;
		this.displayInfo = displayInfo;
	}
	
	/**
	 * Changes board to display new positions.
	 * 
	 * @param newPositions
	 */
	public void setPositions(GameUpdateInfo info, DragController dragController, boolean[][] visibleSquares) {

		for( int row = 0 ; row < visibleSquares.length; row++ ) {
			for ( int col = 0; col < visibleSquares[row].length; col++ ) {
				
				//TODO this is a slow DOM operation. cache them in an [][]? they don't move around like the squares
				TableCellPanel panel = (TableCellPanel) gameScreenController.gameScreen.gameBoard.getWidget(row, col);
				GameSquare[] squares = panel.children;
				
				//Just show fog if the player can't see this square.
				if ( !visibleSquares[row][col] ) {
					int fogLayer = Marker.Layer.UI.ordinal();
					GameSquare previousFog = squares[fogLayer];
					
					if ( null == previousFog ) {
						panel.clear();						
						GameSquare newSquare = new GameSquare(DefaultMarkers.FOG_OF_WAR, info.currentPlayersTurn);
						panel.add(newSquare);
					}
					continue;
				}
				
				for ( int layer = 0; layer < Marker.Layer.values().length; layer++ ) {

					Marker marker = info.positions[row][col][layer];
					GameSquare square = squares[layer];

					//No marker to show.
					if ( null == marker ) {
						//Remove previous marker if needed.
						if ( null != square ) {
							panel.remove(square);
						}
						continue;
					//New marker, but no previous square, so create a square.
					} else if ( null == square ) {
						square = new GameSquare(marker, info.currentPlayersTurn);
						panel.add(square);
					//New marker and previous square, so update the square.
					} else {
						square.set(marker, info.currentPlayersTurn);
					//Previous square, but no new marker, so remove it.
					}

					if ( null != info.playingAs && info.isUsersTurn ) {
						if ( displayInfo.game.isMap() || (!displayInfo.playInfo.ended && marker.player == displayInfo.playInfo.playingAs && marker.movementRange > 0 )) {
							dragController.makeDraggable(square);
							gameScreenController.draggables.add(square);
						}
					}	
				}
			}
		}	
	}

}
