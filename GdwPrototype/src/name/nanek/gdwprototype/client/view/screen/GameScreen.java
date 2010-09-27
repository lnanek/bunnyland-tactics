package name.nanek.gdwprototype.client.view.screen;

import name.nanek.gdwprototype.client.controller.support.ScreenControllers;
import name.nanek.gdwprototype.client.controller.support.SoundPlayer;
import name.nanek.gdwprototype.client.controller.support.ScreenControllers.Screen;
import name.nanek.gdwprototype.client.view.PiecesDialog;
import name.nanek.gdwprototype.client.view.TerrainDialog;
import name.nanek.gdwprototype.shared.model.Player;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Holds widgets for the game screen.
 * 
 * @author Lance Nanek
 *
 */
public class GameScreen {

	public interface FogOfWarChangeListener {
		void onFogOfWarChange(Player newFogOfWarAs);
	}

	public VerticalPanel content = new VerticalPanel();

	public Label statusLabel = new Label();
	
	public final Button surrenderButton = new Button("Surrender");
	
	public final Button publishMapButton = new Button("Publish Map");

	public final Button piecesButton = new Button("Pieces");

	public final Button terrainButton = new Button("Terrain");

	public HorizontalPanel fogOfWarPanel = new HorizontalPanel();	

	public RadioButton fogOfWarPlayerOneRadio = new RadioButton("playerRadio", "Player One");

	public RadioButton fogOfWarPlayerTwoRadio = new RadioButton("playerRadio", "Player Two");
	
	public VerticalPanel mapBuilderPalettePanel = new VerticalPanel();
	
	public FlexTable markers = new FlexTable();
	
	public FlexTable gameBoard = new FlexTable();
	
	public PiecesDialog piecesDialog;
	
	private final class FogOfWarCheckboxValueChangeListener implements ValueChangeHandler<Boolean> {
		Player player;
		
		public FogOfWarCheckboxValueChangeListener(Player player) {
			this.player = player;
		}

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			if ( event.getValue() ) {
				callFogOfWarChangeListener(player);
			}
		}
	}
	
	private FogOfWarChangeListener fogOfWarChangeListener;
	
	private TerrainDialog terrainDialog;
		
	public GameScreen(SoundPlayer soundPlayer) {
		piecesDialog = new PiecesDialog(soundPlayer);
		terrainDialog = new TerrainDialog(soundPlayer);
		
		//Show game status and controls at top of screen.
		content.add(statusLabel);
		content.add(new HTML("<br />"));
		HorizontalPanel controlsPanel = new HorizontalPanel();
		controlsPanel.add(surrenderButton);
		controlsPanel.add(publishMapButton);
		content.add(controlsPanel);
		
		content.add(new HTML("<br />"));
		HorizontalPanel helpPanel = new HorizontalPanel();
		helpPanel.add(new Label("How to Play: "));
		helpPanel.add(new HTML("&nbsp;&nbsp;"));
		helpPanel.add(piecesButton);
		piecesButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				piecesDialog.show();
			}
		});
		//TODO space via CSS
		helpPanel.add(new HTML("&nbsp;&nbsp;"));
		helpPanel.add(terrainButton);
		terrainButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				terrainDialog.show();
			}
		});

		helpPanel.add(new HTML("&nbsp;&nbsp;"));
		Hyperlink strategyLink = new Hyperlink("Strategy", 
				ScreenControllers.getHistoryToken(Screen.STRATEGY));
		soundPlayer.addMenuClick(strategyLink);
		helpPanel.add(strategyLink);
		
		content.add(helpPanel);

		//Setup fog of war controls for observer and map builder.
		fogOfWarPanel.add(new Label("Fog of War:"));

		fogOfWarPanel.add(fogOfWarPlayerOneRadio);
		fogOfWarPlayerOneRadio.addValueChangeHandler(new FogOfWarCheckboxValueChangeListener(Player.ONE));
		fogOfWarPanel.add(fogOfWarPlayerTwoRadio);
		fogOfWarPlayerTwoRadio.addValueChangeHandler(new FogOfWarCheckboxValueChangeListener(Player.TWO));
		RadioButton fogOfWarNoneRadio = new RadioButton("playerRadio", "None");
		fogOfWarNoneRadio.setValue(true, false);
		fogOfWarPanel.add(fogOfWarNoneRadio);
		fogOfWarNoneRadio.addValueChangeHandler(new FogOfWarCheckboxValueChangeListener(null));

		content.add(fogOfWarPanel);
		
		//Setup palette for map builder.
		//TODO delete map button? way to find maps you previously made?
		HTML markerHeader = new HTML();
		markerHeader.setHTML("<h3>Playing pieces and terrain:</h3>");
		mapBuilderPalettePanel.add(markerHeader);
		markers.addStyleName("grid");
		mapBuilderPalettePanel.add(markers);
		content.add(mapBuilderPalettePanel);

		//Setup game board for players.
		HTML boardHeader = new HTML();
		boardHeader.setHTML("<h3>Game board:</h3>");
		content.add(boardHeader);
		gameBoard.addStyleName("grid");		
		content.add(gameBoard);		
		
		//Start invisible until data is loaded.
		content.setVisible(false);
		
		soundPlayer.addMenuClick(surrenderButton, publishMapButton, piecesButton, 
				fogOfWarPlayerOneRadio, fogOfWarPlayerTwoRadio, fogOfWarNoneRadio);
	}
	
	private void callFogOfWarChangeListener(Player player) {
		if ( null != fogOfWarChangeListener ){
			fogOfWarChangeListener.onFogOfWarChange(player);
		}
	}

	public void setFogOfWarChangeListener(FogOfWarChangeListener newFogOfWarChangeListener) {
		fogOfWarChangeListener = newFogOfWarChangeListener;
	}
}
