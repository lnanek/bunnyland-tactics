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
	//TODO this is very efficient at avoiding DOM calls, but it can get out of sync with the actual DOM
	//maybe query the TableCellPanel instances more instead of keeping a separate array between updates?
	//would also remove the need to call this class when something is dragged
	
	//TODO more efficient just to prepopulate the DOM with an image for every x, y, z and tweak those each update?
	//would have lots of transparent images each draw (maybe test if no src works as transparent?)
	
	private GameScreenController gameScreenController;
	
	private GameSquare[][][] squares;
	
	private GameDisplayInfo displayInfo;
	
	public GameScreenBoardController(GameScreenController gameScreenController, GameDisplayInfo displayInfo) {
		this.gameScreenController = gameScreenController;
		this.displayInfo = displayInfo;
		squares = new GameSquare[displayInfo.game.getBoardHeight()][displayInfo.game.getBoardWidth()][Marker.Layer.values().length];
	}
	
	/**
	 * Changes board to display new positions.
	 * 
	 * @param newPositions
	 */
	public void setPositions(GameUpdateInfo info, DragController dragController, boolean[][] visibleSquares) {

		for( int row = 0 ; row < visibleSquares.length; row++ ) {
			for ( int col = 0; col < visibleSquares[row].length; col++ ) {
				//Just show fog if the player can't see this square.
				if ( !visibleSquares[row][col] ) {
					int fogLayer = Marker.Layer.UI.ordinal();
					GameSquare previousFog = squares[row][col][fogLayer];
					
					if ( null == previousFog ) {
						TableCellPanel panel = (TableCellPanel) gameScreenController.gameScreen.gameBoard.getWidget(row, col);
						panel.clear();	
						for( int layer = 0; layer < Marker.Layer.values().length ; layer++ ) {
							squares[row][col][layer] = null;
						}
						
						GameSquare newSquare = new GameSquare(DefaultMarkers.FOG_OF_WAR, info.currentPlayersTurn);
						panel.add(newSquare);
						squares[row][col][fogLayer] = newSquare;	
					}
					continue;
				}
				
				for ( int layer = 0; layer < Marker.Layer.values().length; layer++ ) {

					Marker marker = info.positions[row][col][layer];
					GameSquare previousSquare = squares[row][col][layer];

					//No marker to show.
					if ( null == marker ) {
						//Remove previous marker if needed.
						if ( null != previousSquare ) {
							removeSquare(row, col, previousSquare);
							squares[row][col][layer] = null;
						}
						continue;
					//New marker, but no previous square, so create a square.
					} else if ( null == previousSquare ) {
						GameSquare newSquare = new GameSquare(marker, info.currentPlayersTurn);
						TableCellPanel panel = (TableCellPanel) gameScreenController.gameScreen.gameBoard.getWidget(row, col);
						panel.add(newSquare);
						squares[row][col][layer] = newSquare;
					//New marker and previous square, so update the square.
					} else {
						previousSquare.set(marker, info.currentPlayersTurn);
					//Previous square, but no new marker, so remove it.
					}

					if ( null != info.playingAs && info.isUsersTurn ) {
						if ( displayInfo.game.isMap() || (!displayInfo.playInfo.ended && marker.player == displayInfo.playInfo.playingAs && null != marker.movementRange && marker.movementRange > 0 )) {
							GameSquare draggableSquare = squares[row][col][layer];
							dragController.makeDraggable(draggableSquare);
							gameScreenController.draggables.add(draggableSquare);
						}
					}	
				}
			}
		}	
	}
	
	private void removeSquare(Integer row, Integer col, GameSquare removedSquare) {
		if ( null != removedSquare && null != row && null != col ) {
			TableCellPanel panel = (TableCellPanel) gameScreenController.gameScreen.gameBoard.getWidget(row, col);
			panel.remove(removedSquare);	
		}		
	}

	public void moveMarker(Integer sourceRow, Integer sourceCol, Integer destRow, Integer destColumn,
			GameSquare draggedImage) {

		int layer = draggedImage.previousMarker.getLayer().ordinal();

		if ( null != sourceRow && null != sourceCol ) {
			squares[sourceRow][sourceCol][layer] = null;
		}		
		
		if ( null != destRow && null != destColumn ) {
			squares[destRow][destColumn][layer] = draggedImage;
		}	
	}

}
