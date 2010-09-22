/**
 * 
 */
package name.nanek.gdwprototype.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.Set;

import javax.persistence.EntityManager;

import name.nanek.gdwprototype.client.model.GameListing;
import name.nanek.gdwprototype.client.model.GamePlayInfo;
import name.nanek.gdwprototype.client.model.Player;
import name.nanek.gdwprototype.shared.exceptions.UserFriendlyMessageException;
import name.nanek.gdwprototype.shared.model.Game;
import name.nanek.gdwprototype.shared.model.Marker;
import name.nanek.gdwprototype.shared.model.Markers;
import name.nanek.gdwprototype.shared.model.Position;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * Performs game logic related actions.
 * 
 * @author Lance Nanek
 *
 */
public class GameEngine {

	private static final Random random = new Random();

	public GamePlayInfo createGameInfo(Game game) {
		if ( null == game ) return null;
		
		//Create the array of position info.
		ArrayList<Position> positionList = new ArrayList<Position>();
		positionList.addAll(game.getPositions());
		Position[] positions = positionList.toArray(new Position[] {});

		//Create the array of marker info.
		ArrayList<Marker> markerList = new ArrayList<Marker>();
		markerList.addAll(game.getSettings().getMarkers());
		Marker[] markers = markerList.toArray(new Marker[] {});

		//Determine player/turn info.
		boolean needsSecondPlayer = null == game.getSecondPlayerUserId();		
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		boolean isUsersTurn = isUsersTurn(game, user);
		Player userPlayingAs = null;
		
		if ( null != user ) {
			if ( user.getUserId().equals(game.getFirstPlayerUserId() )) {
				userPlayingAs = Player.ONE;
			} else if ( user.getUserId().equals(game.getSecondPlayerUserId() )) {
				userPlayingAs = Player.TWO;				
			}
		}	
		//System.out.println("GameDataServiceImpl#createGameInfo: settings are: " + game.getSettings());
		//System.out.println("GameDataServiceImpl#createGameInfo: markers are: " + game.getSettings().getMarkers());
		//System.out.println("GameDataServiceImpl#createGameInfo: positionInfos are: " + positions.length );	
		
		GamePlayInfo info = new GamePlayInfo(positions, isUsersTurn, userPlayingAs, needsSecondPlayer, 
				game.getSettings().getBoardHeight(), game.getSettings().getBoardWidth(), markers, game.getWinner(), game.isStartingMap(), game.isEnded());
		return info;
	}
	
	public Game moveMarker(Long gameId, Integer sourceRow, Integer sourceColumn, Integer destRow, Integer destColumn,
			String newImageSource, User user, EntityManager em) {
		
		//TODO this method needs massive cleanup. replace string checking with marker types, for example

		Game game;
		game = em.find(Game.class, gameId);
		game.getSettings();
		game.getPositions();

		if ( !isUsersTurn(game, user) ) {
			throw new UserFriendlyMessageException("It isn't your turn to move.");
		}
		
		//TODO make sure the piece moved belongs to the player as well
		
		boolean changedPositions = false;

		if (null != destColumn && null != destRow && null != newImageSource) {
			// Destination so remove any old position and create new  position.
			
			//TODO do checks on client side too for better ux?
			
			boolean removeDestination = false;
			//Always remove the destination when map building.
			if ( game.isStartingMap() ) {
				removeDestination = true;
			//Otherwise it depends on what we are removing.
			} else {
				String removeCandidate = findPosition(destRow, destColumn, em, game);
				if ( null != removeCandidate ) {

					boolean isCarrotDestination = removeCandidate.endsWith("carrot.png");
					boolean isTerrainDestination = removeCandidate.contains("tile_");
					
					//Anything can remove a carrot.
					if ( isCarrotDestination ) {
						removeDestination = true;
						
						Point homeWarrenLocation = findHomeWarren(game.getCurrentUsersTurn(), game.getPositions());
						if ( null != homeWarrenLocation ) {
							Point newUnitLocation = findNearbyOpenSpot(homeWarrenLocation, game.getPositions(), game.getSettings().getBoardHeight(), game.getSettings().getBoardWidth());
							
							if ( null != newUnitLocation ) {
								String markerSource = getNewPlayerPiece(game.getCurrentUsersTurn());
								
								Position position = new Position(newUnitLocation.row, newUnitLocation.column, 
										markerSource);
								//em.persist(position);
								game.getPositions().add(position);
							}
						}							
					//Other terrain is ignored, units just sit on top of it for now.
					//So continue checking if not terrain.
					} else if (!isTerrainDestination ) {
						
						//Must be a stomper to remove non-terrain during game.
						boolean isStomperSource = newImageSource.endsWith("warrior.png");
						if ( !isStomperSource ) {
							throw new UserFriendlyMessageException("Only stompers may land on units to remove them.");
						}
						
						if ( null != game.getCurrentUsersTurn() ) {
							//Check if got enemy warren.
							Marker enemyWarren = Markers.getEnemyWarren(game.getCurrentUsersTurn());
							if (removeCandidate.endsWith(enemyWarren.source)) {
								game.setWinner(game.getCurrentUsersTurn());
								game.setCurrentUsersTurn(null);
								game.setEnded(true);
							} else {	
								//Can't go on your own warren for now.
								//TODO prevent landing on any of your own pieces?
								Marker playerWarren = Markers.getPlayerWarren(game.getCurrentUsersTurn());
								if (removeCandidate.endsWith(playerWarren.source)) {
									throw new UserFriendlyMessageException("You can't stomp your own warren! Think of the children!");
								}
							}
						}
						
						removeDestination = true;
					}
				}
			}

			if ( removeDestination ) {
				removePosition(destRow, destColumn, em, game);
			}

			
			Position position = new Position(destRow, destColumn, newImageSource);
			//persist(position);
			game.getPositions().add(position);
			//System.out.println("Added position.");
			changedPositions = true;
		}

		if (null != sourceColumn && null != sourceRow) {
			// Source so delete it.
			removePosition(sourceRow, sourceColumn, em, game);
			changedPositions = true;
		}

		if ( changedPositions && !game.isStartingMap() ) {
			game.setNextUsersTurn();
		}
		
		/*
		 * if ( changedPositions ) { //tx = em.getTransaction();
		 * //tx.begin(); int newMoveCount = game.incrementMoveCount();
		 * //tx.commit(); //tx = null; return newMoveCount; }
		 * 
		 * return game.getMoveCount();
		 */
		return game;
	}
	

	
	private String getNewPlayerPiece(Player currentUsersTurn) {
		if ( currentUsersTurn == Player.ONE ) {
			return random.nextBoolean() ? 
					Markers.PLAYER_ONE_SCOUT.source : Markers.PLAYER_ONE_STOMPER.source;
		} else if ( currentUsersTurn == Player.TWO ) {
			return random.nextBoolean() ? 
					Markers.PLAYER_TWO_SCOUT.source : Markers.PLAYER_TWO_STOMPER.source;
		}
		return null;
	}

	private Point findHomeWarren(Player currentUsersTurn, Set<Position> positions) {
		if ( null == currentUsersTurn ) {
			return null;
		}
		
		Marker playerWarren = Markers.getPlayerWarren(currentUsersTurn);
		for( Position position : positions ) {
			if ( position.getMarkerSource().contains(playerWarren.source) ) {
				return new Point(position.getRow(), position.getColumn());
			}
		}
		
		return null;
	}
	
	//TODO must be a cleaner way then iterating through so many positions
	//maybe hash them by a hash code based on their x and y?
	private boolean isOpen(Point location, Set<Position> positions, int boardHeight, int boardWidth) {
		if ( null == location || null == positions ) {
			return false;
		}
		
		if ( location.row < 0 || location.row >= boardHeight) {
			return false;
		}
		if ( location.column < 0 || location.column >= boardWidth) {
			return false;
		}
		
		for( Position position : positions ) {
			if ( position.getColumn() == location.column && position.getRow() == location.row ) {
				return false;
			}
		}
		
		return true;
	}

	private Point findNearbyOpenSpot(Point location, Set<Position> positions, int boardHeight, int boardWidth) {
		if ( null == location ) {
			return location;
		}
		
		{
			Point left = new Point(location);
			left.column -= 1;
			if ( isOpen(left, positions, boardHeight, boardWidth)) {
				return left;
			}
		}

		{
			Point right = new Point(location);
			right.column += 1;
			if ( isOpen(right, positions, boardHeight, boardWidth)) {
				return right;
			}
		}
		
		{
			Point up = new Point(location);
			up.row -= 1;
			if ( isOpen(up, positions, boardHeight, boardWidth)) {
				return up;
			}
		}
		
		{
			Point down = new Point(location);
			down.row += 1;
			if ( isOpen(down, positions, boardHeight, boardWidth)) {
				return down;
			}
		}
		
		return null;
	}

	private String findPosition(Integer sourceRow, Integer sourceColumn, EntityManager em, Game game) {
		for (Position position : game.getPositions()) {
			if (sourceRow.equals(position.getRow()) && sourceColumn.equals(position.getColumn())) {
				return position.getMarkerSource();
			}
		}
		return null;
	}

	private String removePosition(Integer sourceRow, Integer sourceColumn, EntityManager em, Game game) {
		for (Position position : game.getPositions()) {
			if (sourceRow.equals(position.getRow()) && sourceColumn.equals(position.getColumn())) {
				game.getPositions().remove(position);
				em.remove(position);
				return position.getMarkerSource();
			}
		}
		return null;
	}	
	
	public static boolean isUsersTurn(Game game, User user) {
		if ( null == user || null == game.getCurrentUsersTurn() ) {
			return false;
		}
		
		String userId = user.getUserId();
		switch ( game.getCurrentUsersTurn() ) {
			case ONE :
				return userId.equals(game.getFirstPlayerUserId());
			case TWO :
				return userId.equals(game.getSecondPlayerUserId());
		}
		
		throw new IllegalStateException("Can't determine the current user.");
	}

	GameListing[] getListings(Collection<Game> games) {
		ArrayList<GameListing> list = new ArrayList<GameListing>();
		for (Game game : games) {
			list.add(game.getListing());
		}
		GameListing[] array = list.toArray(new GameListing[] {});
		return array;		
	}
}