/*
 * Copyright 2009 Fred Sauer
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package name.nanek.gdwprototype.client.controller.screen.support;

import name.nanek.gdwprototype.client.controller.screen.GameScreenController;
import name.nanek.gdwprototype.client.model.GameDisplayInfo;
import name.nanek.gdwprototype.client.view.widget.GameSquare;
import name.nanek.gdwprototype.client.view.widget.PaletteImage;
import name.nanek.gdwprototype.client.view.widget.TableCellPanel;
import name.nanek.gdwprototype.shared.model.Marker;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

/**
 * Handles drops on to game squares.
 */
public class GameScreenDropController extends SimpleDropController {

	private final TableCellPanel dropTarget;

	private final GameScreenController gameScreenController;

	private final boolean isDropTargetPalette;
	
	public GameScreenDropController(TableCellPanel dropTarget, GameScreenController gameScreenController) {
		super(dropTarget);
		this.dropTarget = dropTarget;
		this.gameScreenController = gameScreenController;
		isDropTargetPalette = null == dropTarget.getColumn()&& null == dropTarget.getRow();
	}

	@Override
	public void onDrop(DragContext context) {
		
		GameDisplayInfo info = gameScreenController.getCurrentGamePlayInfo();
		if ( null == info ) {
			return;
		}

		TableCellPanel source = gameScreenController.getDragSource();

		// Ignore drop on to palette, assume user is putting something back.
		if ( isDropTargetPalette ) {
			gameScreenController.moveMarker(source.getRow(), source.getColumn(), null, null, null, null);

			context.draggable.removeFromParent();
			super.onDrop(context);
			return;
		}

		GameSquare draggedImage = (GameSquare) context.draggable;
		
		GameSquare removedSquare = setMarker(draggedImage);
		Marker removedMarker = null != removedSquare ? removedSquare.marker : null;

		gameScreenController.moveMarker(source.getRow(), source.getColumn(), dropTarget.getRow(), dropTarget.getColumn(),
				draggedImage.marker.getKeyId(), removedMarker);
		super.onDrop(context);
	}
	
	private GameSquare setMarker(GameSquare newSquare) {
		GameSquare removedSquare = null;
		Marker newMarker = newSquare.marker;
		for( Widget check : dropTarget ) {
			GameSquare checkSquare = (GameSquare) check;
			if ( checkSquare.marker.getLayer() == newMarker.getLayer() ){
				removedSquare = checkSquare;
				dropTarget.remove(checkSquare);
			}
		}
		dropTarget.add(newSquare);
		return removedSquare;
	}
	
	public static boolean contains(HasWidgets widgets, Widget widget) {
		for( Widget check : widgets ) {
			if ( check == widget ){
				return true;
			}
		}
		return false;
	}

	public void onPreviewDrop(DragContext context) throws VetoDragException {

		GameDisplayInfo info = gameScreenController.getCurrentGamePlayInfo();
		if ( null == info ) {
			throw new VetoDragException();
		}
		
		// Veto drops to source, so picking up and dropping doesn't count as move.
		if ( contains(dropTarget, context.draggable) ) {
			throw new VetoDragException();
		}
		
		//Check unit can move this far.
		//TODO check on server as well
		if ( !info.map ) {
			TableCellPanel source = gameScreenController.getDragSource();
			int sourceCol = source.getColumn();
			int sourceRow = source.getRow();
			
			int destCol = dropTarget.getColumn();
			int destRow = dropTarget.getRow();
			
			GameSquare draggedImage = (GameSquare) context.draggable;
			int movementRange = draggedImage.marker.movementRange;
			int rowDistance = Math.abs(sourceRow - destRow);
			int colDistance = Math.abs(sourceCol - destCol);
			int totalDistance = rowDistance + colDistance;
			if (totalDistance > movementRange) {
				vetoDropAndNotifyUser();
			}
		}

		boolean sourceIsPalette = context.draggable instanceof PaletteImage;

		if (isDropTargetPalette) {
			// Rearranging the palette is not supported.
			if (sourceIsPalette) {
				vetoDropAndNotifyUser();
			}
			// Allow drops on the palette from elsewhere. Treated as a remove
			// piece in onDrop.
			super.onPreviewDrop(context);
			return;
		}

		// Veto drops from the palette, so it isn't depleted, but copy the image
		// from it.
		if (sourceIsPalette) {
			GameSquare draggedImage = (GameSquare) context.draggable;
			GameSquare newImage = new GameSquare(draggedImage.marker, null);			
			GameSquare removedSquare = setMarker(newImage);
			Marker removedMarker = null != removedSquare ? removedSquare.marker : null;
			context.dragController.makeDraggable(newImage);

			// TODO throw/catch an exception and veto? stale game state or
			// something?
			gameScreenController.moveMarker(null, null, dropTarget.getRow(), dropTarget.getColumn(), draggedImage.marker.getKeyId(), removedMarker);

			throw new VetoDragException();
		}

		// Allow normal drops that move the draggable.
		super.onPreviewDrop(context);
	}
	
	private void vetoDropAndNotifyUser() throws VetoDragException {
		gameScreenController.notifyUserBadDrop();
		throw new VetoDragException();
	}

	/*
	 * @Override public void onPreviewDrop(DragContext context) throws
	 * VetoDragException { if (dropTarget.getWidget() != null) { throw new
	 * VetoDragException(); } super.onPreviewDrop(context); }
	 */
}