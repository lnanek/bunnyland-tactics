/**
 * 
 */
package name.nanek.gdwprototype.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import name.nanek.gdwprototype.client.model.GameDisplayInfo;
import name.nanek.gdwprototype.client.model.GameListing;
import name.nanek.gdwprototype.client.model.GamePlayInfo;
import name.nanek.gdwprototype.client.model.Player;
import name.nanek.gdwprototype.shared.exceptions.UserFriendlyMessageException;
import name.nanek.gdwprototype.shared.model.Game;
import name.nanek.gdwprototype.shared.model.GameSettings;
import name.nanek.gdwprototype.shared.model.Marker;
import name.nanek.gdwprototype.shared.model.Position;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;

/**
 * Performs game logic related actions.
 * 
 * @author Lance Nanek
 *
 */
public class GameEngine {

	private static final Random random = new Random();

	public GameDisplayInfo createDisplayInfo(Game game, Objectify em) {
		if ( null == game ) return null;
		
		//Create the array of marker info.	
		GameSettings gameSettings = em.query(GameSettings.class).filter("game", game).get();
		List<Marker> markerList =  em.query(Marker.class).filter("settings", gameSettings).list();
		Marker[] markers = markerList.toArray(new Marker[] {});

		GameDisplayInfo info = new GameDisplayInfo(markers, 
				gameSettings.getBoardHeight(), gameSettings.getBoardWidth(),
				game.isMap(), game.getListing(), createPlayInfo(game, em));
		return info;
	}
	
	public GamePlayInfo createPlayInfo(Game game, Objectify em) {
		if ( null == game ) return null;
		
		//Create the array of position info.
		//List<Position> positionList =  em.query(Position.class).filter("game", game).list();
		//Position[] positions = positionList.toArray(new Position[] {});
		
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
		GamePlayInfo info = new GamePlayInfo(getPositionsMap(em, game), isUsersTurn, userPlayingAs, needsSecondPlayer, 
				game.getWinner(), 
				game.isEnded(), game.getMoveCount(), game.isUnitDiedLastTurn(),
				game.isCarrotEatenLastTurn(), game.getCurrentUsersTurn());
		return info;
	}
	
	public Game moveMarker(Long gameId, Integer sourceRow, Integer sourceColumn, Integer destRow, Integer destColumn,
			Long markerId, User user, Objectify em) {
		
		//BUG moving terrain in map building mode is duplicating the terrain
		
		Game game = em.get(Game.class, gameId);
		Key<GameSettings> gameSettingsKey = em.query(GameSettings.class).filter("game", game).getKey();
		Key<Marker> movedMarkerKey = new Key<Marker>(gameSettingsKey, Marker.class, markerId);
		Marker movedMarker = em.get(movedMarkerKey);
		if ( null == movedMarker ) {
			throw new IllegalArgumentException("Could not find marker with specified ID.");
		}

		if ( !isUsersTurn(game, user) ) {
			throw new UserFriendlyMessageException("It isn't your turn to move.");
		}

		GameSettings gameSettings = em.get(gameSettingsKey);
		Map<Position, Marker> positions = getPositionsMap(em, game);
		
		//TODO make sure the piece moved belongs to the player as well
		
		boolean changedPositions = false;
		boolean unitDiedThisTurn = false;
		boolean carrotEatenThisTurn = false;
		boolean enemyLostHome = false;
		if (null != destColumn && null != destRow && null != markerId) {
			// Destination so remove any old position and create new  position.
			
			//TODO do checks on client side too for better ux?
						
			Position removeCandidatePosition = findPosition(destRow, destColumn, movedMarker.getLayer(), positions);
			if ( null != removeCandidatePosition ) {
				
				//When map building, ground layer markers replace ground layer markers and 
				//surface markers replace surface markers without any further logic. 
				//This lets you swap terrain under a placed unit.
				if ( !game.isMap() ) {
					Marker removeCandidateMarker = positions.get(removeCandidatePosition);

					//The rest of the time moves are only done on the surface layer. Moving on to a carrot is always allowed.
					if ( removeCandidateMarker.role == Marker.Role.CARROT ) {
						carrotEatenThisTurn = true;						
					//Replacing anything else on the surface layer when not map building requires a stomper.
					} else if ( movedMarker.role != Marker.Role.STOMPER ) {
							throw new UserFriendlyMessageException("Only stompers may land on other units to remove them.");
					} else {
						
						if ( removeCandidateMarker.role == Marker.Role.HOME ) {
							if ( removeCandidateMarker.player == game.getCurrentUsersTurn() ) {
								//Can't go on your own warren.
								throw new UserFriendlyMessageException("You can't stomp your own warren! Think of the children!");
							} else {
								enemyLostHome = true;						
							}
						}
						unitDiedThisTurn = true;
					}
				}
				removePosition(removeCandidatePosition, em, game);
			}
			
			Position position = new Position(destRow, destColumn, movedMarkerKey);
			em.put(position);
			changedPositions = true;
		}

		if (null != sourceColumn && null != sourceRow) {
			// Source so delete it.
			removeCarrotOrPiecePosition(sourceRow, sourceColumn, em, game, positions);
			changedPositions = true;
		}

		//Now that move is complete, if a carrot was eaten, generate a new unit if a spot is available.
		if ( carrotEatenThisTurn ) {
			positions = getPositionsMap(em, game);
			Point homeWarrenLocation = findHomeWarren(game.getCurrentUsersTurn(), positions);
			if ( null != homeWarrenLocation ) {
				Point newUnitLocation = findNearbyOpenSpot(homeWarrenLocation, 
						positions, gameSettings.getBoardHeight(), 
						gameSettings.getBoardWidth());
				
				if ( null != newUnitLocation ) {
					List<Marker> markers = em.query(Marker.class).filter("settings", gameSettings).list();
					Marker marker = getNewPlayerPiece(markers, game.getCurrentUsersTurn());
					Key<Marker> markerKey = new Key<Marker>(gameSettingsKey, Marker.class, marker.getKeyId());
					Position position = new Position(newUnitLocation.row, newUnitLocation.column, markerKey);
					em.put(position);
				}
			}	
		}
		
		if ( enemyLostHome ) {
			Player enemyPlayer = Player.other(game.getCurrentUsersTurn());
			int enemyHomes = countMarkersWith(positions, Marker.Role.HOME, enemyPlayer);
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
		
		em.put(game);
		/*
		 * if ( changedPositions ) { //tx = em.getTransaction();
		 * //tx.begin(); int newMoveCount = game.incrementMoveCount();
		 * //tx.commit(); //tx = null; return newMoveCount; }
		 * 
		 * return game.getMoveCount();
		 */
		return game;
	}

	private Map<Position, Marker> getPositionsMap(Objectify em, Game game) {
		Map<Position, Marker> positions = new HashMap<Position, Marker>();
		for ( Position position : em.query(Position.class).filter("game", game) ) {
			Marker marker = em.get(position.getMarkerKey());
			positions.put(position, marker);
		}
		return positions;
	}
	
	private int countMarkersWith(Map<Position, Marker> positions, Marker.Role role, Player player) {
		int count = 0;
		for (Map.Entry<Position, Marker> position : positions.entrySet() ) {
			Marker marker = position.getValue();
			if ( marker.role == role && marker.player == player ) {
				count++;
			}
		}
		return count;
	}
	
	private Marker getMarker(List<Marker> Markers, Marker.Role role, Player player) {
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
	
	private Marker getNewPlayerPiece(List<Marker> markers, Player currentUsersTurn) {
		
		Marker.Role role = random.nextBoolean() ? Marker.Role.SCOUT : Marker.Role.STOMPER;
		return getMarker(markers, role, currentUsersTurn);
	}

	private Point findHomeWarren(Player currentUsersTurn, Map<Position, Marker> positions) {
		if ( null == currentUsersTurn ) {
			return null;
		}
		
		for (Map.Entry<Position, Marker> position : positions.entrySet() ) {
			Marker marker = position.getValue();
			if ( marker.role == Marker.Role.HOME && marker.player == currentUsersTurn ) {
				return new Point(position.getKey().getRow(), position.getKey().getColumn());
			}
		}
		
		return null;
	}
	
	//TODO must be a cleaner way then iterating through so many positions
	//maybe hash them by a hash code based on their x and y?
	private boolean isOpen(Point location, Map<Position, Marker> positions, int boardHeight, int boardWidth) {
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

	private Point findNearbyOpenSpot(Point location, Map<Position, Marker> positions, int boardHeight, int boardWidth) {
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

	private Position findPosition(Integer sourceRow, Integer sourceColumn, Marker.Layer layer, Map<Position, Marker> positions) {
		if ( null == positions ) {
			return null;
		}
		for (Map.Entry<Position, Marker> position : positions.entrySet() ) {
			if (sourceRow.equals(position.getKey().getRow()) 
					&& sourceColumn.equals(position.getKey().getColumn())
					&& position.getValue().getLayer() == layer ) {
				return position.getKey();
			}
		}
		return null;
	}

	private Position findCarrotOrPiecePosition(Integer sourceRow, Integer sourceColumn, Map<Position, Marker> positions) {
		if ( null == positions ) {
			return null;
		}
		for (Map.Entry<Position, Marker> position : positions.entrySet() ) {
			if (sourceRow.equals(position.getKey().getRow()) 
					&& sourceColumn.equals(position.getKey().getColumn())
					&& (position.getValue().player != null
					|| position.getValue().role == Marker.Role.CARROT) ) {
				return position.getKey();
			}
		}
		return null;
	}

	private Position findAnyPosition(Integer sourceRow, Integer sourceColumn, Map<Position, Marker> positions) {
		if ( null == positions ) {
			return null;
		}
		for (Map.Entry<Position, Marker> position : positions.entrySet() ) {
			if (sourceRow.equals(position.getKey().getRow()) 
					&& sourceColumn.equals(position.getKey().getColumn()) ) {
				return position.getKey();
			}
		}
		return null;
	}
	


	private Position removeAnyPosition(Integer sourceRow, Integer sourceColumn, Objectify em, Game game, Map<Position, Marker> positions) {
		Position position = findAnyPosition(sourceRow, sourceColumn, positions);
		return removePosition(position, em, game);
	}	

	private Position removePosition(Position position, Objectify em, Game game) {
		if ( null != position ) {
			em.delete(position);
			return position;
		}
		return null;
	}	

	private Position removeCarrotOrPiecePosition(Integer sourceRow, Integer sourceColumn, Objectify em, Game game, Map<Position, Marker> positions) {
		Position position = findCarrotOrPiecePosition(sourceRow, sourceColumn, positions);
		return removePosition(position, em, game);
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

	GameListing[] getListings(Iterable<Game> games) {
		ArrayList<GameListing> list = new ArrayList<GameListing>();
		for( Game game : games ) {
			list.add(game.getListing());
		}
		GameListing[] array = list.toArray(new GameListing[] {});
		return array;		
	}
}
