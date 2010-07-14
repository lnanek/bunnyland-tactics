package name.nanek.gdwprototype.client;

import name.nanek.gdwprototype.shared.FieldVerifier;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GdwPrototype implements EntryPoint {
	
	private static final int GAME_LIST_REFRESH_INTERVAL = 5000; // ms
	
	private static final int GAME_BOARD_REFRESH_INTERVAL = 1000; // ms
	
	private static final int BOARD_DIMENSION = 12;

	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	private final GameDataServiceAsync gameDataService = GWT.create(GameDataService.class);
	
	private Long currentGameId;
	
	private boolean dragInProgress = false;
	
	VerticalPanel allContent = new VerticalPanel();
	HTML pageTitle = new HTML();
	Label errorLabel = new Label();

	VerticalPanel currentGamesContent = new VerticalPanel();
	TextBox createGameNameField = new TextBox();
	Button createGameButton = new Button("Create Game");
	
    final FlexTable currentGamesTable = new FlexTable();
	Timer refreshGamesTableTimer = new Timer() {
      @Override
      public void run() {
    	  updateGamesListing(resultDialog, currentGamesTable);	
      }
    };
    
    VerticalPanel playGameContent = new VerticalPanel();
    FlexTable markers = new FlexTable();
    FlexTable gameBoard = new FlexTable();
    PickupDragController dragController;
    
    ResultDialog resultDialog = new ResultDialog(createGameButton);
	
    {
    	
		allContent.add(pageTitle);
		allContent.add(errorLabel);
		allContent.add(new HTML("<br />"));
		allContent.add(currentGamesContent);
		allContent.add(playGameContent);
		
		HTML startGameLabel = new HTML("<h3>Create a new game:</h3>");
		startGameLabel.addStyleName("heavy");
		currentGamesContent.add(startGameLabel);

		HorizontalPanel startGameControl = new HorizontalPanel();
		createGameNameField.setText("My Awesome Game");		
		startGameControl.add(createGameNameField);
		startGameControl.add(createGameButton);
		createGameButton.addStyleName("sendButton");
		currentGamesContent.add(startGameControl);		
		currentGamesContent.add(new HTML("<br />"));
		
		HTML joinGameLabel = new HTML("<h3>Join an existing game:</h3>");
		joinGameLabel.addStyleName("heavy");
		currentGamesContent.add(joinGameLabel);
		
        currentGamesTable.setText(0, 0, "Loading...");
        currentGamesContent.add(currentGamesTable);

        markers.addStyleName("grid");
        gameBoard.addStyleName("grid");
        playGameContent.add(markers);
        playGameContent.add(new HTML("<br />"));
        playGameContent.add(gameBoard);
        
        //RootPanel.get().setPixelSize(600, 600);
        //RootPanel.get().getElement().getStyle().setProperty("position" , "relative");
        dragController = new PickupDragController(RootPanel.get(), false);
        dragController.setBehaviorDragProxy(true);
        dragController.setBehaviorMultipleSelection(false);
        dragController.addDragHandler(new DragHandler() {

			@Override
			public void onDragEnd(DragEndEvent event) {
				dragInProgress = false;
			}

			@Override
			public void onDragStart(DragStartEvent event) {
				dragInProgress = true;
			}

			@Override
			public void onPreviewDragEnd(DragEndEvent event)
					throws VetoDragException {
			}

			@Override
			public void onPreviewDragStart(DragStartEvent event)
					throws VetoDragException {
			}        	
        });
        
        for(int i = 0; i < Markers.SOURCES.length; i++) {
        	Image image = new PaletteImage("images/" + Markers.SOURCES[i]);
			TableCellPanel panel = new TableCellPanel(image, markers, 0, i);      	
        	dragController.makeDraggable(image);
			MarkerDropController simpleDropController = new MarkerDropController(panel, gameDataService, this);
			dragController.registerDropController(simpleDropController);
        }
        
		for(int column = 0; column < BOARD_DIMENSION; column++) {
			for(int row = 0; row < BOARD_DIMENSION; row++) {
				TableCellPanel panel = new TableCellPanel(null, gameBoard, column, row);	
				MarkerDropController simpleDropController = new MarkerDropController(panel, gameDataService, this);
				dragController.registerDropController(simpleDropController);
			}
		}


	}

	Timer refreshGameBoardTimer = new Timer() {
      @Override
      public void run() {
    	  updateGameBoard(resultDialog, gameBoard);
      }
    };
    
    public void onModuleLoad() {
/*
    	// set uncaught exception handler
        GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
          public void onUncaughtException(Throwable throwable) {
            String text = "Uncaught exception: ";
            while (throwable != null) {
              StackTraceElement[] stackTraceElements = throwable.getStackTrace();
              text += throwable.toString() + "\n";
              for (int i = 0; i < stackTraceElements.length; i++) {
                text += "    at " + stackTraceElements[i] + "\n";
              }
              throwable = throwable.getCause();
              if (throwable != null) {
                text += "Caused by: ";
              }
            }
            DialogBox dialogBox = new DialogBox(true);
            DOM.setStyleAttribute(dialogBox.getElement(), "backgroundColor", "#ABCDEF");
            System.err.print(text);
            text = text.replaceAll(" ", "&nbsp;");
            dialogBox.setHTML("<pre>" + text + "</pre>");
            dialogBox.center();
          }
        });

        // use a deferred command so that the handler catches onModuleLoad2() exceptions
        DeferredCommand.addCommand(new Command() {
          public void execute() {
            onModuleLoad2();
          }
        });
      }

	private void onModuleLoad2() {
*/
		RootPanel.get("contentContainer").add(allContent);
		
		class CreateGameHandler implements ClickHandler, KeyDownHandler {
			/**
			 * Fired when the user clicks on the sendButton.
			 */
			public void onClick(ClickEvent event) {
				sendNameToServer();
			}

			/**
			 * Fired when the user types in the nameField.
			 */
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					sendNameToServer();
				}
			}

			/**
			 * Send the name from the nameField to the server and wait for a response.
			 */
			private void sendNameToServer() {
				// First, we validate the input.
				errorLabel.setText("");
				final String textToServer = createGameNameField.getText();
				if (!FieldVerifier.isValidGameName(textToServer)) {
					errorLabel.setText(FieldVerifier.VALID_GAME_NAME_ERROR_MESSAGE);
					return;
				}

				// Then, we send the input to the server.
				createGameButton.setEnabled(false);
				gameDataService.createGame(textToServer,
						new AsyncCallback<Boolean>() {
							public void onFailure(Throwable caught) {
								showRpcError(resultDialog, textToServer);
							}

							public void onSuccess(Boolean success) {
								//String resultMessage = success ? "Game created." : "Could not create game with that name.";

								//resultDialog.show("Remote Procedure Call", false, textToServer, resultMessage);
																
								if (success) {
									updateGamesListing(resultDialog, currentGamesTable);
								}
							}
						});
			}
		}

		// Add a handler to send the name to the server
		CreateGameHandler handler = new CreateGameHandler();
		createGameButton.addClickHandler(handler);		
		createGameNameField.addKeyDownHandler(handler);
				
	    History.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				showPage(event.getValue());
			}
	    });
	    History.fireCurrentHistoryState();
	}
	
	private void showPage(String historyToken) {
		currentGameId = GameAnchor.getIdFromAnchor(historyToken);
		if ( null == currentGameId ) {
			setPageTitle("Current Games");
			
			currentGamesContent.setVisible(true);
			playGameContent.setVisible(false);
			
			createGameNameField.setFocus(true);
			createGameNameField.selectAll();
			
			updateGamesListing(resultDialog, currentGamesTable);
			refreshGameBoardTimer.cancel();
			refreshGamesTableTimer.cancel();
			//TODO would scheduling each time we update work better? 
			//updates take different amounts of time for different computers/networks
		    refreshGamesTableTimer.scheduleRepeating(GAME_LIST_REFRESH_INTERVAL);
		} else {
			
			gameDataService.getGameListingById(currentGameId, new AsyncCallback<GameListing>() {
				public void onFailure(Throwable caught) {
					showRpcError(resultDialog, "");
				}
				public void onSuccess(final GameListing gameListing) {
					setPageTitle("Game \"" + gameListing.getDisplayName() + "\"");
				}			
			});
			
			currentGamesContent.setVisible(false);			
			updateGameBoard(resultDialog, gameBoard);
			
			playGameContent.setVisible(true);
			
			refreshGamesTableTimer.cancel();
			refreshGameBoardTimer.cancel();
			refreshGameBoardTimer.scheduleRepeating(GAME_BOARD_REFRESH_INTERVAL);
		}
		
	}

	private void setPageTitle(String pageTitleText) {
		Window.setTitle("Game Design Workshop, Group #2, Prototype 1 : " + pageTitleText);
		pageTitle.setHTML("<h2>" + pageTitleText + "</h2>");
	}
	
	public void moveMarker(Integer sourceRow, Integer sourceColumn, 
			Integer destRow, Integer destColumn, String newImageSource) {
		
		gameDataService.moveMarker(currentGameId, sourceRow, sourceColumn, 
				destRow, destColumn, newImageSource, new AsyncCallback<Integer>() {
			public void onFailure(Throwable caught) {
				showRpcError(resultDialog, "");
			}
			public void onSuccess(final Integer moveNumber) {
				//TODO if we skipped move numbers, update board immediately
				//may not be needed when switch to turn taking
			}
		});
	}
	
	private void updateGameBoard(final ResultDialog resultDialog,
			final FlexTable gameBoard) {
		
		if ( null == currentGameId ) {
			return;
		}
		
		gameDataService.getPositionsByGameId(currentGameId, new AsyncCallback<PositionInfo[]>() {
			public void onFailure(Throwable caught) {
				showRpcError(resultDialog, "");
			}
			public void onSuccess(final PositionInfo[] positions) {
				
				if ( dragInProgress ) {
					return;
				}
				
				for(int column = 0; column < BOARD_DIMENSION; column++) {
					for(int row = 0; row < BOARD_DIMENSION; row++) {
						boolean hasPosition = false;
						for( final PositionInfo position : positions) {
							if (position.getColumn() == column && position.getRow() == row) {
								hasPosition = true;
								break;
							}
						}
						if (!hasPosition) {
							TableCellPanel panel = (TableCellPanel) gameBoard.getWidget(row, column);
							Image currentImage = (Image) panel.getWidget();
							if ( null != currentImage ) {
								panel.remove(currentImage);
							}
						}
					}
				}
				
				
		        for( final PositionInfo position : positions) {
		        	TableCellPanel panel = (TableCellPanel) gameBoard.getWidget(position.getRow(), position.getColumn());
		        	
		        	//setPageTitle("Getting cell at: " + position.getRow() + ", " + position.getColumn());
		        	
		        	Image currentImage = (Image) panel.getWidget();
		        	if ( null == currentImage || !currentImage.getUrl().endsWith(position.getMarkerSource())) {	        	
			        	Image image = new Image(position.getMarkerSource());
			        	dragController.makeDraggable(image);
		        		panel.setWidget(image);
		        	}
		        }
			}
		});
	}

	private void updateGamesListing(final ResultDialog resultDialog,
			final FlexTable gamesListingTable) {
		
		gameDataService.getGameNames(new AsyncCallback<GameListing[]>() {
			public void onFailure(Throwable caught) {
				showRpcError(resultDialog, "");
			}
			public void onSuccess(final GameListing[] gamesListing) {
				gamesListingTable.clear();
		        int i = 0;
		        for( final GameListing gameListing : gamesListing) {
		        	String anchor = GameAnchor.generateAnchor(gameListing);
		        	Hyperlink link = new Hyperlink(gameListing.getDisplayName(), anchor);
		        	gamesListingTable.setWidget(i++, 0, link);
		        }
			}
		});
	}
	
	// Show the RPC error message to the user
	private void showRpcError(final ResultDialog resultDialog, String textToServer) {
		resultDialog.show("Remote Procedure Call - Failure", true, textToServer, SERVER_ERROR);
	}
}
