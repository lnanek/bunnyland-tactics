package name.nanek.gdwprototype.client.controller.screen.support;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import name.nanek.gdwprototype.client.controller.screen.GameScreenController;
import name.nanek.gdwprototype.shared.model.Position;
import name.nanek.gdwprototype.shared.model.support.CompareToBuilder;

/**
 * Controls the game board on the game screen.
 * 
 * @author Lance Nanek
 *
 */
public class GameScreenBoardController {
	//TODO break up some code from GameScreenController into this class, it's getting too long
	
	private GameScreenController gameScreenController;

	private List<Position> currentPositions = new LinkedList<Position>();
	
	public GameScreenBoardController(GameScreenController gameScreenController) {
		this.gameScreenController = gameScreenController;
	}
	
	/**
	 * Changes board to display new positions.
	 * 
	 * @param newPositions
	 */
	public void setPositions(List<Position> newPositions) {
		//TODO assert both position lists are sorted by x, y, layer
		
		//TODO just use hashset instead of all this sorting?

		//Complex because we want to avoid clearing the whole board and resetting everything.
		
		Iterator<Position> currentIterator = currentPositions.iterator();
		Position current = currentIterator.hasNext() ? currentIterator.next() : null;
		
		Iterator<Position> newIterator = newPositions.iterator();
		Position newPosition = newIterator.hasNext() ? newIterator.next() : null;
		
		while( true ) {
			if ( null == newPosition ) {
				//No more new positions to ensure displayed.
				//Any remaining previous positions should be removed.
				while( null != current ) {
					removeFromTable(current);
					current = currentIterator.hasNext() ? currentIterator.next() : null;
				}
				break;
			}
			

			if ( null == current ) {
				//Nothing previously placed in this location.
				addToTable(newPosition);
				newPosition = newIterator.hasNext() ? newIterator.next() : null;
				continue;
			}
			
			int comparison = compareLocationAndLayer(current, newPosition);
			if ( 0 == comparison ) {
				//Something previously placed in this location.
				String newMarker = newPosition.getMarker().source;
				String oldMarker = current.getMarker().source;
				if ( !newMarker.equals(oldMarker) ) {
					removeFromTable(current);
					addToTable(newPosition);
				}
				newPosition = newIterator.hasNext() ? newIterator.next() : null;
				current = currentIterator.hasNext() ? currentIterator.next() : null;
			} else if ( comparison < 0 ) {
				//No new position for something placed previously, remove it.
				removeFromTable(current);
				current = currentIterator.hasNext() ? currentIterator.next() : null;
			} else {
				//Nothing previously placed in this location.
				addToTable(newPosition);
				newPosition = newIterator.hasNext() ? newIterator.next() : null;
			}
		}
		
		currentPositions = newPositions;
	}
	
	public void replace(Position oldPosition, Position newPosition) {
		currentPositions.remove(oldPosition);

		//Find the right place and add it in the list
		boolean inserted = false;
		ListIterator<Position> iterator = currentPositions.listIterator();
		while( iterator.hasNext() ) {
			Position current = iterator.next();
			int comparison = compareLocationAndLayer(current, newPosition);
			if ( 0 == comparison ) {
				//assert false
			} else if ( comparison < 0 ) {
				continue;
			} else {
				iterator.previous();
				iterator.add(newPosition);
				inserted = true;
			}
		}
		if ( !inserted ) {
			currentPositions.add(newPosition);
		}
		
		removeFromTable(oldPosition);
		addToTable(newPosition);
	}
	
	private void removeFromTable(Position oldPosition) {
		//TODO
	}
	
	private void addToTable(Position newPosition) {
		//TODO
	}
	
	private int compareLocationAndLayer(Position a, Position b) {
		return new CompareToBuilder()
			.append(a.getColumn(), b.getColumn())
			.append(a.getRow(), b.getRow())
			.append(a.getMarker().getLayer(), b.getMarker().getLayer())
			.toComparison();
	}
}
