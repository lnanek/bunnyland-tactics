package name.nanek.gdwprototype.server;

import name.nanek.gdwprototype.client.model.GameDisplayInfo;
import name.nanek.gdwprototype.client.model.GameUpdateInfo;
import name.nanek.gdwprototype.client.service.GameService;
import name.nanek.gdwprototype.shared.FieldVerifier;
import name.nanek.gdwprototype.shared.exceptions.GameException;
import name.nanek.gdwprototype.shared.exceptions.UserFriendlyMessageException;
import name.nanek.gdwprototype.shared.model.Game;
import name.nanek.gdwprototype.shared.model.Marker;
import name.nanek.gdwprototype.shared.model.Player;
import name.nanek.gdwprototype.shared.model.Position;

import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;

/**
 * Handles game service requests.
 * 
 * @author Lance Nanek
 */
@SuppressWarnings("serial")
public class GameServiceImpl extends RemoteServiceServlet implements GameService {
	
	protected GameEngine gameEngine = new GameEngine();
	
	private GameDataAccessor gameDataAccessor = new GameDataAccessor();
	
	@Override
	public GameUpdateInfo moveMarker(Long gameId, Integer sourceRow, Integer sourceColumn, Integer destRow,
			Integer destColumn, Long markerId) throws GameException {
		
		System.out.println("GameServiceImpl#moveMarker: " + gameId  + ", " + sourceRow + ", " + sourceColumn + ", " + destRow
				 + ", " + destColumn + ", " + markerId);
		
		User user = requireUser();
			
		Objectify em = DbUtil.beginTransaction();
		try {
			GameUpdateInfo update = gameEngine.moveMarker(gameId, sourceRow, sourceColumn, destRow, destColumn, markerId, user, em);
			em.getTxn().commit();
			return update;
		} finally {
			rollbackIfNeeded(em);
		}		
	}

	@Override
	public GameDisplayInfo getDisplayInfo(Long gameId) throws GameException {
		Objectify em = DbUtil.beginTransaction();
		try {
			Game game = requireGame(em, gameId);
			return gameEngine.createDisplayInfo(game, em);

			// Don't bother committing, this was read only anyway.
		} finally {
			rollbackIfNeeded(em);
		}		
	}
	
	@Override
	public GameUpdateInfo getPositionsByGameId(Long gameId) throws GameException {
		Objectify em = DbUtil.beginTransaction();
		try {
			Game game = requireGame(em, gameId);
			return gameEngine.createPlayInfo(game, em);

			// Don't bother committing, this was read only anyway.
		} finally {
			rollbackIfNeeded(em);
		}
	}

	@Override
	public void surrender(Long gameId, Player surrenderer) throws GameException {
		
		Objectify em = DbUtil.beginTransaction();
		try {
			Game game = requireGame(em, gameId);
			game.setEnded(true);
			game.setWinner(Player.other(surrenderer));
			em.put(game);
			
			em.getTxn().commit();
		} finally {
			rollbackIfNeeded(em);
		}
	}

	@Override
	public void publishMap(Long mapId) throws GameException {
		Objectify em = DbUtil.beginTransaction();
		try {
			Game map = requireMap(em, mapId);
			map.setEnded(true);
			em.put(map);
			
			em.getTxn().commit();
		} finally {
			rollbackIfNeeded(em);
		}
	}
	
	@Override
	public Game getGameListingById(Long gameId) throws GameException {
		
		Objectify em = DbUtil.beginTransaction();
		try {
			Game game = requireGame(em, gameId);
			return game;
		} finally {
			rollbackIfNeeded(em);
		}
	}
	
	@Override
	public Game attemptToJoinGame(final Long gameId) throws GameException {

		final User user = requireUser();
	        
		Objectify ofy = DbUtil.beginTransaction();
		try {
			Game game = requireGame(ofy, gameId);
			if ( user.getUserId().equals(game.getFirstPlayerUserId()) ) {
				//User is already player one.
				//TODO switch to hotseat mode?
			} else if ( null == game.getSecondPlayerUserId() || user.getUserId().equals(game.getSecondPlayerUserId()) ) {
				game.setSecondPlayerUserId(user.getUserId());
			}
			ofy.put(game);
			
			ofy.getTxn().commit();
			
			return game;
		} finally {
			rollbackIfNeeded(ofy);
		}
	}
	
	@Override
	public String getLoginUrlIfNeeded(String returnUrl) {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        if (user != null) {
        	return null;
        } else {
            return userService.createLoginURL(returnUrl);
        }
	}
	
	@Override
	public Game[] getMapNames() throws GameException {
		Objectify em = DbUtil.createObjectify();
		Game[] listings = gameEngine.getListings(gameDataAccessor.getMaps(em));
		return listings;
	}
	
	@Override
	public Game[] getJoinableGameNames() throws GameException {
        String username = getUserId();
        
		Objectify em = DbUtil.createObjectify();
		Game[] listings = gameEngine.getListings(gameDataAccessor.getJoinableGames(em, username));
		return listings;
	}

	@Override
	public Game[] getObservableGameNames() throws GameException {
        String username = getUserId();
        
		Objectify em = DbUtil.createObjectify();
		Game[] listings = gameEngine.getListings(gameDataAccessor.getObservableGames(em, username));
		return listings;
	}

	@Override
	public Game createGameOrMap(String name, Integer boardWidth, Integer boardHeight, Marker[] markers, Long mapId) throws GameException {
		FieldVerifier.validateGameName(name);

		User user = requireUser();
		Objectify createOfy = DbUtil.beginTransaction();
		try {
			
			Game game = new Game();
			game.setCreatorNickname(user.getNickname());
			game.setName(name);
			game.setFirstPlayerUserId(user.getUserId());
			game.setEnded(false);
			if ( null == mapId ) {
				game.setMap(true);
			}
			Key<Game> gameKey = createOfy.put(game);
			
			//If we're a map, we use the settings passed to us and have no positions.
			if ( null == mapId ) {
				for ( Marker marker : markers ) {
					marker.setGame(gameKey);
					createOfy.put(marker);
				}
			} else {
				//Otherwise we're creating a game and have a map to start from.
				//Copy settings and positions from map.
				//Each game/map is a separate entity group, so we need a second transaction for this.
				Objectify referenceOfy = DbUtil.beginTransaction();
				try {
					Game map = requireMap(referenceOfy, mapId);		
					for ( Marker mapMarker :  referenceOfy.query(Marker.class).ancestor(map) ) {
						Marker gameMarker = mapMarker.copy();
						gameMarker.setGame(gameKey);
						Key<Marker> gameMarkerKey = createOfy.put(gameMarker);
						
						for ( Position mapPosition :  referenceOfy.query(Position.class).ancestor(map).filter("marker", mapMarker) ) {
							Position gamePosition = mapPosition.copy();
							gamePosition.setGame(gameKey);
							gamePosition.setMarker(gameMarkerKey);
							createOfy.put(gamePosition);
						}
					}
				} finally {
					rollbackIfNeeded(referenceOfy);
				}
			}
			
			createOfy.getTxn().commit();			
			return game;
		} finally {
			rollbackIfNeeded(createOfy);
		}
	}
	
	private Game requireGame(Objectify ofy, Long gameId) {
		return requireGameOrMap(ofy, gameId, "Couldn't find requested game. It may have been deleted.");
	}
	
	private Game requireMap(Objectify ofy, Long gameId) {
		return requireGameOrMap(ofy, gameId, "Couldn't find requested map. It may have been deleted.");
	}
	
	private Game requireGameOrMap(Objectify ofy, Long gameOrMapId, String errorMessage) {
		if ( null == gameOrMapId ) {
			throw new UserFriendlyMessageException(errorMessage);
		}
		
		Game gameOrMap = ofy.get(Game.class, gameOrMapId);
		if (null == gameOrMap) {
			throw new UserFriendlyMessageException(errorMessage);
		}
		return gameOrMap;
	}
	
	private User requireUser() {
		final User user = getUser();
		if (user == null) {
			//TODO maybe have a general purpose "need to login" exception with login URL?
			//all RPC calls needing user information could detect and redirect users not logged in
			throw new UserFriendlyMessageException("You need to login to join a game.");
		}
		return user;
	}
	
	private void rollbackIfNeeded(Objectify ofy) {
		if ( null == ofy ) {
			return;
		}
		
		Transaction tx = ofy.getTxn();
		if ( null == tx ) {
			return;
		}
		
	    if (tx.isActive()) {
	    	tx.rollback();
	    }		
	}

	private User getUser() {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        return user;
	}

	private String getUserId() {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        return null != user ? user.getUserId() : null;
	}

}
