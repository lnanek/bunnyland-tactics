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
	
	//BUG sometimes after a drag the dragged piece disappears for a brief instant then shows up
	//maybe an update is in progress during the drag, the drag finishes, then the old update is displayed or something?
	
	private GameScreenController gameScreenController;
	
	private GameDisplayInfo displayInfo;
	
	//FlexTable#getWidge(int row, int col) is a slow DOM operation, unfortunately, 
	//so cache the panel locations, which don't change in the game currently anyway.
	private TableCellPanel[][] panels;
	
	public GameScreenBoardController(GameScreenController gameScreenController, GameDisplayInfo displayInfo, TableCellPanel[][] panels) {
		this.gameScreenController = gameScreenController;
		this.displayInfo = displayInfo;
		this.panels = panels;
	}
	
	/**
	 * Changes board to display new positions.
	 * 
	 * @param newPositions
	 */
	public void setPositions(GameUpdateInfo info, DragController dragController, boolean[][] visibleSquares) {

		for( int row = 0 ; row < visibleSquares.length; row++ ) {
			for ( int col = 0; col < visibleSquares[row].length; col++ ) {
				
				TableCellPanel panel = panels[row][col];
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
						if ( displayInfo.game.isMap() || (!info.ended && marker.player == info.playingAs && marker.movementRange > 0 )) {
							dragController.makeDraggable(square);
							gameScreenController.draggables.add(square);
						}
					}	
				}
			}
		}	
	}

}
