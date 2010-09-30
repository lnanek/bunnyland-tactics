/**
 * 
 */
package name.nanek.gdwprototype.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import name.nanek.gdwprototype.client.model.GameDisplayInfo;
import name.nanek.gdwprototype.client.model.GameUpdateInfo;
import name.nanek.gdwprototype.server.support.MarkerUtil;
import name.nanek.gdwprototype.server.support.PositionUtil;
import name.nanek.gdwprototype.server.support.predicates.HomeWarrenPredicate;
import name.nanek.gdwprototype.server.support.predicates.LocationPredicate;
import name.nanek.gdwprototype.shared.exceptions.UserFriendlyMessageException;
import name.nanek.gdwprototype.shared.model.Game;
import name.nanek.gdwprototype.shared.model.Marker;
import name.nanek.gdwprototype.shared.model.Player;
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
public class Engine {

	public static final Random random = new Random();

	public GameDisplayInfo createDisplayInfo(Game game, Objectify em) {
		if ( null == game ) return null;
		
		//Create the array of marker info.	

		List<Marker> markerList =  em.query(Marker.class).ancestor(game).list();
		Marker[] markers = markerList.toArray(new Marker[] {});

		GameDisplayInfo info = new GameDisplayInfo(markers, game, createPlayInfo(game, em));
		return info;
	}
	
	public GameUpdateInfo createPlayInfo(Game game, Objectify em) {
		if ( null == game ) return null;
		
		return createPlayInfo(game, getPositionsMap(em, game));
	}
	
	public GameUpdateInfo createPlayInfo(Game game, Map<Position, Marker> positionsMap) {
		if ( null == game ) return null;
		
		//Determine player/turn info.
		//TODO just send the whole game object?
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
		
		Marker[][][] positions = new Marker[game.getBoardHeight()][game.getBoardWidth()][Marker.Layer.values().length];
		for (Map.Entry<Position, Marker> entry : positionsMap.entrySet() ) {
			Marker marker = entry.getValue();
			Position position = entry.getKey();
			positions[position.getRow()][position.getColumn()][marker.getLayer().ordinal()] = marker;
		}
		
		GameUpdateInfo info = new GameUpdateInfo(positions, isUsersTurn, userPlayingAs, needsSecondPlayer, 
				game.getWinner(), 
				game.isEnded(), game.getMoveCount(), game.isUnitDiedLastTurn(),
				game.isCarrotEatenLastTurn(), game.getCurrentPlayersTurn());
		return info;
	}
	
	public GameUpdateInfo moveMarker(Long gameId, Integer sourceRow, Integer sourceColumn, Integer destRow, Integer destColumn,
			Long markerId, User user, Objectify em) {
		
		Key<Game> gameKey = new Key<Game>(Game.class, gameId);
		Game game = em.get(gameKey);
		
		Key<Marker> movedMarkerKey = new Key<Marker>(gameKey, Marker.class, markerId);
		Marker movedMarker = em.get(movedMarkerKey);
		if ( null == movedMarker ) {
			throw new IllegalArgumentException("Could not find marker with specified ID.");
		}

		if ( !isUsersTurn(game, user) ) {
			throw new UserFriendlyMessageException("It isn't your turn to move.");
		}

		Map<Position, Marker> positions = getPositionsMap(em, game);
		
		//TODO make sure the piece moved belongs to the player when non-map making mode as well
		
		boolean changedPositions = false;
		boolean unitDiedThisTurn = false;
		boolean carrotEatenThisTurn = false;
		boolean enemyLostHome = false;
		if (null != destColumn && null != destRow && null != markerId) {
			// Destination so remove any old position and create new  position.
			
			//TODO do checks on client side too for better ux?
						
			Position removeCandidatePosition = 
				PositionUtil.find(positions, new LocationPredicate(destRow, destColumn, movedMarker.getLayer()));
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
							if ( removeCandidateMarker.player == game.getCurrentPlayersTurn() ) {
								//Can't go on your own warren.
								throw new UserFriendlyMessageException("You can't stomp your own warren! Think of the children!");
							} else {
								enemyLostHome = true;						
							}
						}
						unitDiedThisTurn = true;
					}
				}
				PositionUtil.removePosition(removeCandidatePosition, em, game, positions);
			}
			
			Position position = new Position(destRow, destColumn, movedMarkerKey, gameKey);
			em.put(position);
			
			//Keep in memory positions map up to date. With datastore a query during a transaction returns the data
			//at the start of the transaction, so we can't query for the updated state later in this transaction.
			positions.put(position, movedMarker);
			
			changedPositions = true;
		}

		if (null != sourceColumn && null != sourceRow) {
			// Source so delete it.
			Position position = PositionUtil.find(positions, new LocationPredicate(sourceRow, sourceColumn, movedMarker.getLayer()));
			PositionUtil.removePosition(position, em, game, positions);
			changedPositions = true;
		}

		if ( !game.isMap() ) {

			//Now that move is complete, if a carrot was eaten, generate a new unit if a spot is available.
			if ( carrotEatenThisTurn ) {			
				Position homeWarrenLocation = PositionUtil.find(positions, new HomeWarrenPredicate(game.getCurrentPlayersTurn()));
				if ( null != homeWarrenLocation ) {
					Position newUnitLocation = PositionUtil.createNearbyOpenSpot(homeWarrenLocation, 
							positions, game.getBoardHeight(), 
							game.getBoardWidth());
					
					if ( null != newUnitLocation ) {
						List<Marker> markers = em.query(Marker.class).ancestor(game).list();
						Marker marker = MarkerUtil.getNewPlayerPiece(markers, game.getCurrentPlayersTurn());
						addPosition(em, gameKey, positions, newUnitLocation, marker);
					}
				}	
			}
			
			Player enemyPlayer = Player.other(game.getCurrentPlayersTurn());
			if ( enemyLostHome ) {
				int enemyHomes = MarkerUtil.count(positions, Marker.Role.HOME, enemyPlayer);
				if ( 0 == enemyHomes ) {
					winGame(game);	
				}
			} else if ( unitDiedThisTurn ) {
				int enemyMovablePieces = MarkerUtil.countMovable(positions, enemyPlayer);
				if ( 0 == enemyMovablePieces ) {
					winGame(game);	
				}
			}
			
			//Generate a carrot if needed.
			if ( game.carrotGenerationPeriod > 0 ) {
				game.turnsUntilNextCarrotGenerated--;
				if ( game.turnsUntilNextCarrotGenerated <= 0 ) {
					game.turnsUntilNextCarrotGenerated = game.carrotGenerationPeriod;
					Position newCarrotPosition = PositionUtil.createOnRandomUnoccupiedGrass(positions);
					if ( null != newCarrotPosition ) {
						List<Marker> markers = em.query(Marker.class).ancestor(game).list();
						Marker carrot = MarkerUtil.getMarker(markers, Marker.Role.CARROT, null);
						addPosition(em, gameKey, positions, newCarrotPosition, carrot);
					}	
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
		}
		
		em.put(game);
		return createPlayInfo(game, positions);
	}

	private void addPosition(Objectify em, Key<Game> gameKey, Map<Position, Marker> positions, Position position,
			Marker marker) {
		Key<Marker> markerKey = new Key<Marker>(gameKey, Marker.class, marker.getId());
		
		position.setGame(gameKey);
		position.setMarker(markerKey);					
		em.put(position);
		positions.put(position, marker);
	}

	private void winGame(Game game) {
		game.setWinner(game.getCurrentPlayersTurn());
		game.setCurrentPlayersTurn(null);
		game.setEnded(true);
	}
	
	private static Map<Position, Marker> getPositionsMap(Objectify em, Game game) {
		Map<Position, Marker> positions = new HashMap<Position, Marker>();
		for ( Position position : em.query(Position.class).ancestor(game) ) {
			Marker marker = em.get(position.getMarker());
			positions.put(position, marker);
		}
		return positions;
	}
	
	public static boolean isUsersTurn(Game game, User user) {
		if ( null == user || null == game.getCurrentPlayersTurn() ) {
			return false;
		}
		
		String userId = user.getUserId();
		switch ( game.getCurrentPlayersTurn() ) {
			case ONE :
				return userId.equals(game.getFirstPlayerUserId());
			case TWO :
				return userId.equals(game.getSecondPlayerUserId());
		}
		
		throw new IllegalStateException("Can't determine the current user.");
	}

	Game[] getListings(Iterable<Game> games) {
		return getListings(games.iterator());		
	}

	Game[] getListings(Iterator<Game> games) {
		ArrayList<Game> list = new ArrayList<Game>();
		while ( games.hasNext() ) {
			Game game = games.next();
			list.add(game);
		}
		Game[] array = list.toArray(new Game[] {});
		return array;		
	}
}
