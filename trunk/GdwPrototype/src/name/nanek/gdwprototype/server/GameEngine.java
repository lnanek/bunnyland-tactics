/**
 * 
 */
package name.nanek.gdwprototype.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.Set;

import javax.persistence.EntityManager;

import name.nanek.gdwprototype.client.model.GameDisplayInfo;
import name.nanek.gdwprototype.client.model.GameListing;
import name.nanek.gdwprototype.client.model.GamePlayInfo;
import name.nanek.gdwprototype.client.model.Player;
import name.nanek.gdwprototype.shared.exceptions.UserFriendlyMessageException;
import name.nanek.gdwprototype.shared.model.Game;
import name.nanek.gdwprototype.shared.model.Marker;
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

	public GameDisplayInfo createDisplayInfo(Game game) {
		if ( null == game ) return null;
		
		//Create the array of marker info.
		ArrayList<Marker> markerList = new ArrayList<Marker>();
		markerList.addAll(game.getSettings().getMarkers());
		Marker[] markers = markerList.toArray(new Marker[] {});

		//TODO can GameDisplayInfo and GameSettings be merged and used for the same things?
		
		GameDisplayInfo info = new GameDisplayInfo(markers, 
				game.getSettings().getBoardHeight(), game.getSettings().getBoardWidth(),
				game.isMap(), game.getListing(), createGameInfo(game));
		return info;
	}
	
	public GamePlayInfo createGameInfo(Game game) {
		if ( null == game ) return null;
		
		//Create the array of position info.
		ArrayList<Position> positionList = new ArrayList<Position>();
		positionList.addAll(game.getPositions());
		Position[] positions = positionList.toArray(new Position[] {});

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
		
		//TODO make GamePlayInfo a child of game, store these things in it directly, and return it directly instead of making a DTO?
		GamePlayInfo info = new GamePlayInfo(positions, isUsersTurn, userPlayingAs, needsSecondPlayer, 
				game.getWinner(), 
				game.isEnded(), game.getMoveCount(), game.isUnitDiedLastTurn(),
				game.isCarrotEatenLastTurn(), game.getCurrentUsersTurn());
		return info;
	}
	
	public Game moveMarker(Long gameId, Integer sourceRow, Integer sourceColumn, Integer destRow, Integer destColumn,
			Long markerId, User user, EntityManager em) {
		
		//TODO this method needs massive cleanup. replace string checking with marker types, for example

		Game game;
		game = em.find(Game.class, gameId);
		game.getSettings();
		game.getPositions();
		Marker movedMarker = em.find(Marker.class, markerId);

		if ( !isUsersTurn(game, user) ) {
			throw new UserFriendlyMessageException("It isn't your turn to move.");
		}
		
		//TODO make sure the piece moved belongs to the player as well
		
		boolean changedPositions = false;
		boolean unitDiedThisTurn = false;
		boolean carrotEatenThisTurn = false;
		boolean enemyLostHome = false;
		if (null != destColumn && null != destRow && null != markerId) {
			// Destination so remove any old position and create new  position.
			
			//TODO do checks on client side too for better ux?
			
			//Always remove the destination when map building.
			if ( game.isMap() ) {
				removeAnyPosition(destRow, destColumn, em, game);

			//Otherwise it depends on what we are removing.
			} else {
				boolean removeDestination = false;
				Position removeCandidatePosition = findCarrotOrPiecePosition(destRow, destColumn, game.getPositions());
				if ( null != removeCandidatePosition ) {

					Marker removeCandidateMarker = removeCandidatePosition.getMarker();
					boolean isCarrotDestination = removeCandidateMarker.role == Marker.Role.CARROT;
					boolean isTerrainDestination = removeCandidateMarker.terrain;
					
					//Anything can remove a carrot.
					if ( isCarrotDestination ) {
						removeDestination = true;
						carrotEatenThisTurn = true;						
					//Other terrain is ignored, units just sit on top of it for now.
					//So continue checking if not terrain.
					} else if (!isTerrainDestination ) {
						
						//Must be a stomper to remove non-terrain during game.
						if ( movedMarker.role != Marker.Role.STOMPER ) {
							throw new UserFriendlyMessageException("Only stompers may land on enemies to remove them.");
						}
						
						if ( removeCandidateMarker.role == Marker.Role.HOME ) {
							if ( removeCandidateMarker.player == game.getCurrentUsersTurn() ) {
								//Can't go on your own warren for now.
								//TODO prevent landing on any of your own pieces?
								throw new UserFriendlyMessageException("You can't stomp your own warren! Think of the children!");
							} else {
								enemyLostHome = true;						
							}
						}
						unitDiedThisTurn = true;
						removeDestination = true;
					}
				}
				if ( removeDestination ) {
					removeCarrotOrPiecePosition(destRow, destColumn, em, game);
				}
			}


			
			Position position = new Position(destRow, destColumn, movedMarker);
			//persist(position);
			game.getPositions().add(position);
			//System.out.println("Added position.");
			changedPositions = true;
		}

		if (null != sourceColumn && null != sourceRow) {
			// Source so delete it.
			removeCarrotOrPiecePosition(sourceRow, sourceColumn, em, game);
			changedPositions = true;
		}

		//Now that move is complete, if a carrot was eaten, generate a new unit if a spot is available.
		if ( carrotEatenThisTurn ) {
			Point homeWarrenLocation = findHomeWarren(game.getCurrentUsersTurn(), game.getPositions());
			if ( null != homeWarrenLocation ) {
				Point newUnitLocation = findNearbyOpenSpot(homeWarrenLocation, 
						game.getPositions(), game.getSettings().getBoardHeight(), 
						game.getSettings().getBoardWidth());
				
				if ( null != newUnitLocation ) {
					Marker marker = getNewPlayerPiece(game.getSettings().getMarkers(), game.getCurrentUsersTurn());
					Position position = new Position(newUnitLocation.row, newUnitLocation.column, marker);
					//em.persist(position);
					game.getPositions().add(position);
				}
			}	
		}
		
		if ( enemyLostHome ) {
			Player enemyPlayer = Player.other(game.getCurrentUsersTurn());
			int enemyHomes = countMarkersWith(game.getPositions(), Marker.Role.HOME, enemyPlayer);
			if ( 0 == enemyHomes ) {
				winGame(game);
			}
		}
		
		if ( changedPositions ) {
			game.incrementMoveCount();
			if ( !game.isMap() ) {
				game.setUnitDiedLastTurn(unitDiedThisTurn);
				game.setCarrotEatenLastTurn(carrotEatenThisTurn);
				game.setNextUsersTurn();
			}
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
	
	private int countMarkersWith(Set<Position> positions, Marker.Role role, Player player) {
		int count = 0;
		for( Position position : positions ) {
			Marker marker = position.getMarker();
			if ( marker.role == role && marker.player == player ) {
				count++;
			}
		}
		return count;
	}
	
	private Marker getMarker(Set<Marker> Markers, Marker.Role role, Player player) {
		for( Marker marker : Markers ) {
			if ( marker.role == role && marker.player == player ) {
				return marker;
			}
		}
		return null;
	}
	
	private void winGame(Game game){
		game.setWinner(game.getCurrentUsersTurn());
		game.setCurrentUsersTurn(null);
		game.setEnded(true);	
	}
	
	private Marker getNewPlayerPiece(Set<Marker> markers, Player currentUsersTurn) {
		
		Marker.Role role = random.nextBoolean() ? Marker.Role.SCOUT : Marker.Role.STOMPER;
		return getMarker(markers, role, currentUsersTurn);
	}

	private Point findHomeWarren(Player currentUsersTurn, Set<Position> positions) {
		if ( null == currentUsersTurn ) {
			return null;
		}
		
		for( Position position : positions ) {
			Marker marker = position.getMarker();
			if ( marker.role == Marker.Role.HOME && marker.player == currentUsersTurn ) {
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
		
		Position existingPosition = findCarrotOrPiecePosition(location.row, location.column, positions);
		if ( null != existingPosition ) {
			return false;
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

	private Position findCarrotOrPiecePosition(Integer sourceRow, Integer sourceColumn, Set<Position> positions) {
		for (Position position : positions) {
			if (sourceRow.equals(position.getRow()) 
					&& sourceColumn.equals(position.getColumn())
					&& (position.getMarker().player != null
					|| position.getMarker().role == Marker.Role.CARROT) ) {
				return position;
			}
		}
		return null;
	}

	private Position findAnyPosition(Integer sourceRow, Integer sourceColumn, Set<Position> positions) {
		for (Position position : positions) {
			if (sourceRow.equals(position.getRow()) 
					&& sourceColumn.equals(position.getColumn()) ) {
				return position;
			}
		}
		return null;
	}
	


	private Position removeAnyPosition(Integer sourceRow, Integer sourceColumn, EntityManager em, Game game) {
		Position position = findAnyPosition(sourceRow, sourceColumn, game.getPositions());
		if ( null != position ) {
			game.getPositions().remove(position);
			em.remove(position);
			return position;
		}
		return null;
	}	

	private Position removeCarrotOrPiecePosition(Integer sourceRow, Integer sourceColumn, EntityManager em, Game game) {
		Position position = findCarrotOrPiecePosition(sourceRow, sourceColumn, game.getPositions());
		if ( null != position ) {
			game.getPositions().remove(position);
			em.remove(position);
			return position;
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
