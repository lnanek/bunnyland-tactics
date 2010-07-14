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
package name.nanek.gdwprototype.client.controller;

import name.nanek.gdwprototype.client.GdwPrototype;
import name.nanek.gdwprototype.client.service.GameDataServiceAsync;
import name.nanek.gdwprototype.client.view.GameSquare;
import name.nanek.gdwprototype.client.view.PaletteImage;
import name.nanek.gdwprototype.client.view.TableCellPanel;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;

/**
 * DropController which allows a widget to be dropped on a SimplePanel drop target when the drop
 * target does not yet have a child widget.
 */
public class MarkerDropController extends SimpleDropController {

	private final TableCellPanel dropTarget;
	
	private final GameDataServiceAsync gameDataService;
	
	private final GdwPrototype module;

	public MarkerDropController(TableCellPanel dropTarget, GameDataServiceAsync gameDataService, GdwPrototype module) {
		super(dropTarget);
		this.dropTarget = dropTarget;
		this.gameDataService = gameDataService;
		this.module = module;
	}

	@Override
	public void onDrop(DragContext context) {
		
		TableCellPanel source = (TableCellPanel) context.draggable.getParent();
		
		//Ignore drop on to palette, assume user is putting something back.
		if ( dropTarget.getWidget() instanceof PaletteImage ) {
			module.moveMarker(source.getRow(), source.getColumn(), null, null, null);
			
			context.draggable.removeFromParent();
			super.onDrop(context);
			return;
		}

		Image draggedImage = (Image) context.draggable;
		module.moveMarker(source.getRow(), source.getColumn(), 
				dropTarget.getRow(), dropTarget.getColumn(), draggedImage.getUrl());
		dropTarget.setWidget(context.draggable);
		super.onDrop(context);
	}
	
	public void onPreviewDrop(DragContext context) throws VetoDragException {
		//if ( ((PickupDragController) context.dragController).getBehaviorDragProxy() ) {

		boolean destinationIsPalette = dropTarget.getWidget() instanceof PaletteImage;
		boolean sourceIsPalette = context.draggable instanceof PaletteImage;
		
		if ( destinationIsPalette ) {
			//Rearranging the palette is not supported.
			if ( sourceIsPalette ) {
				throw new VetoDragException();
			}
			//Allow drops on the palette from elsewhere. Treated as a remove piece in onDrop.
			super.onPreviewDrop(context);
			return;
		}
		
		//Veto drops from the palette, so it isn't depleted, but copy the image from it.
		if ( sourceIsPalette ) {
			GameSquare draggedImage = (GameSquare) context.draggable;
			GameSquare newImage = new GameSquare(draggedImage.marker);
			dropTarget.setWidget(newImage);
			context.dragController.makeDraggable(newImage);
			
			//TODO throw/catch an exception and veto? stale game state or something?
			module.moveMarker(null, null, 
					dropTarget.getRow(), dropTarget.getColumn(), draggedImage.getUrl());
			
			throw new VetoDragException();
		}
		
		//Allow normal drops that move the draggable.
		super.onPreviewDrop(context);
	}

	/*
	@Override
	public void onPreviewDrop(DragContext context) throws VetoDragException {
	if (dropTarget.getWidget() != null) {
		throw new VetoDragException();
	}
	super.onPreviewDrop(context);
	}
	*/
}
