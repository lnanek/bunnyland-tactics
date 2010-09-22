package name.nanek.gdwprototype.server;

import java.util.HashSet;
import java.util.Set;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyRange;
import com.google.appengine.api.datastore.Transaction;

import name.nanek.gdwprototype.client.model.GameDisplayInfo;
import name.nanek.gdwprototype.client.model.GameListing;
import name.nanek.gdwprototype.client.model.GamePlayInfo;
import name.nanek.gdwprototype.client.model.Player;
import name.nanek.gdwprototype.client.service.GameService;
import name.nanek.gdwprototype.shared.FieldVerifier;
import name.nanek.gdwprototype.shared.exceptions.GameException;
import name.nanek.gdwprototype.shared.exceptions.UserFriendlyMessageException;
import name.nanek.gdwprototype.shared.model.Game;
import name.nanek.gdwprototype.shared.model.GameSettings;
import name.nanek.gdwprototype.shared.model.Position;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.code.twig.ObjectDatastore;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Handles game service requests.
 * 
 * @author Lance Nanek
 */
@SuppressWarnings("serial")
public class GameServiceImpl extends RemoteServiceServlet implements GameService {
	//TODO put transactions back in now that understand about default fetch groups
	
	protected GameEngine gameEngine = new GameEngine();
	
	private GameDataAccessor gameDataAccessor = new GameDataAccessor();
	
	@Override
	public GamePlayInfo moveMarker(Long gameId, Integer sourceRow, Integer sourceColumn, Integer destRow,
			Integer destColumn, Long markerId) throws GameException {
		
		//System.out.println("GameServiceImpl#moveMarker: " + gameId  + ", " + sourceRow + ", " + sourceColumn + ", " + destRow
		//		 + ", " + destColumn + ", " + newImageSource);
		 
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if (user == null) {
			throw new UserFriendlyMessageException("You need to login to make a move.");
		}
			
		ObjectDatastore em = DbUtil.createObjectDatastore();

		Game game = null;
		Transaction tx = em.beginTransaction();
		try {
			game = gameEngine.moveMarker(gameId, sourceRow, sourceColumn, destRow, destColumn, markerId, user, em);
			tx.commit();
		} finally {
			if ( null != tx && tx.isActive() ) {
				tx.rollback();
			}
			
		}

		return gameEngine.createPlayInfo(game);
	}

	@Override
	public GameDisplayInfo getDisplayInfo(Long gameId) throws GameException {

		ObjectDatastore em = DbUtil.createObjectDatastore();
		Transaction tx = em.beginTransaction();
		try {
			Game game = em.load(Game.class, gameId);
			return gameEngine.createDisplayInfo(game);

			// Don't bother committing, this was read only anyway.
		} finally {
			if ( null != tx && tx.isActive() ) {
				tx.rollback();
			}
		}
	}

	@Override
	public GamePlayInfo getPositionsByGameId(Long gameId) throws GameException {

		ObjectDatastore em = DbUtil.createObjectDatastore();
		Transaction tx = em.beginTransaction();
		try {
			Game game = em.load(Game.class, gameId);
			return gameEngine.createPlayInfo(game);

			// Don't bother committing, this was read only anyway.
		} finally {
			if ( null != tx && tx.isActive() ) {
				tx.rollback();
			}
		}
	}

	@Override
	public void surrender(Long gameId, Player surrenderer) throws GameException {
		
		ObjectDatastore em = DbUtil.createObjectDatastore();
		Transaction tx = em.beginTransaction();
		try {
			Game game = em.load(Game.class, gameId);
			game.setEnded(true);
			game.setWinner(Player.other(surrenderer));
			em.update(game);
			
			tx.commit();
		} finally {
			if ( null != tx && tx.isActive() ) {
				tx.rollback();
			}
		}
	}

	@Override
	public void publishMap(Long mapId) throws GameException {
		ObjectDatastore em = DbUtil.createObjectDatastore();
		Transaction tx = em.beginTransaction();
		try {
			Game game = em.load(Game.class, mapId);
			game.setEnded(true);
			em.update(game);
			
			tx.commit();
		} finally {
			if ( null != tx && tx.isActive() ) {
				tx.rollback();
			}
		}
	}
	
	@Override
	public GameListing getGameListingById(Long id) throws GameException {
		
		ObjectDatastore em = DbUtil.createObjectDatastore();
		Transaction tx = em.beginTransaction();
		try {
			Game game = em.load(Game.class, id);

			if (null == game) {
				return null;
			}
			GameListing listing = game.getListing();

			return listing;
			
			//Don't bother committing, this was read only anyway.
		} finally {
			if ( null != tx && tx.isActive() ) {
				tx.rollback();
			}
			
		}
	}

	@Override
	public GameListing createGameOrMap(String name, GameSettings settings, Long mapId) throws GameException {
		FieldVerifier.validateGameName(name);

		//System.out.println("GameDataServiceImpl#createGame: settings are: " + settings);
		//System.out.println("GameDataServiceImpl#createGame: markers are: " + settings.getMarkers());

		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		//TODO maybe have a general purpose "need to login" exception with login URL?
		//all RPC calls needing user information could detect and redirect users not logged in
		if (user == null) {
			throw new UserFriendlyMessageException("You need to login to create a game.");
		}
		
		ObjectDatastore em = DbUtil.createObjectDatastore();
		Transaction tx = em.beginTransaction();
		try {	
			
			Game game = new Game();
			game.setCreatorNickname(user.getNickname());
			Set<Position> mapPositions = null;
			GameSettings mapSettings = null;
			if ( null != mapId ) {
	
				//System.out.println("GameDataServiceImpl#createGame: starting game from map: " + mapId);
				Game map = em.load(Game.class, mapId);
				mapSettings = map.getSettings();
				mapPositions = map.getPositions();
				
				GameSettings gameSettings = mapSettings.copy();
				game.setSettings(gameSettings);
			} else {
				//System.out.println("GameDataServiceImpl#createGame: starting new map");
				game.setSettings(settings);
				game.setMap(true);
			}
			game.setName(name);
			game.setFirstPlayerUserId(user.getUserId());
			game.setEnded(false);

			
			//em.persist(game.getSettings());
			if ( null != mapPositions ) {
				Set<Position> gamePositions = new HashSet<Position>();
				for ( Position mapPosition : mapPositions ) {
					Position newPosition = new Position(mapPosition.getRow(), mapPosition.getColumn(), 
							mapPosition.getMarker().copy());
					//em.persist(newPosition);
					gamePositions.add(newPosition);
				}
				game.setPositions(gamePositions);
			}
			
            KeyRange range = em.getService().allocateIds("Game", 1);
            Key key = range.getStart();
            game.setKeyId(key.getId()); 
            
            range = em.getService().allocateIds("GameSettings", 1);
            key = range.getStart();
            game.getSettings().setKeyId(key.getId()); 
			
			em.store(game);
			//em.store().instance(game).batch().now();
			
			tx.commit();
			
			return game.getListing();
		} finally {
			if ( null != tx && tx.isActive() ) {
				tx.rollback();
			}
			
		}
	}
	
	@Override
	public GameListing attemptToJoinGame(Long id) throws GameException {

		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();

		if (user == null) {
			throw new UserFriendlyMessageException("You need to login to join a game.");
		}
	        
		ObjectDatastore em = DbUtil.createObjectDatastore();
		Transaction tx = em.beginTransaction();
		try {
			Game game = em.load(Game.class, id);
			
			if (null == game) {
				throw new UserFriendlyMessageException("Couldn't find requested game.");
			}
			
			GameListing listing = null;
			if ( user.getUserId().equals(game.getFirstPlayerUserId()) ) {
				//User is already player one.
				//TODO switch to hotseat mode?
				listing = game.getListing();
			} else if ( null == game.getSecondPlayerUserId() || user.getUserId().equals(game.getSecondPlayerUserId()) ) {
				game.setSecondPlayerUserId(user.getUserId());
				listing = game.getListing();
			}
			em.update(game);
			
			tx.commit();
			
			return listing;
			
		} finally {
			if ( null != tx && tx.isActive() ) {
				tx.rollback();
			}
			
		}
	}
	
	@Override
	public String getLoginUrlIfNeeded(String returnUrl) {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();

        if (user != null) {
        	return null;
        } else {
        	//HttpServletRequest req = getThreadLocalRequest();
            return userService.createLoginURL(returnUrl);
        }
	}
	
	@Override
	public GameListing[] getMapNames() throws GameException {
		ObjectDatastore em = DbUtil.createObjectDatastore();
		//Transaction tx = em.beginTransaction();
		try {
			
			GameListing[] listings = gameEngine.getListings(gameDataAccessor.getMaps(em));
			return listings;

			//Don't bother committing, this was read only anyway.
		} finally {
		//	if ( null != tx && tx.isActive() ) {
		//		tx.rollback();
		//	}
			
		}
	}

	private String getUserId() {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        return null != user ? user.getUserId() : null;
	}
	
	@Override
	public GameListing[] getJoinableGameNames() throws GameException {
        String username = getUserId();
        
		ObjectDatastore em = DbUtil.createObjectDatastore();
		//Transaction tx = em.beginTransaction();
		try {
			
			GameListing[] listings = gameEngine.getListings(gameDataAccessor.getJoinableGames(em, username));
			return listings;

			//Don't bother committing, this was read only anyway.
		} finally {
		//	if ( null != tx && tx.isActive() ) {
		//		tx.rollback();
		//	}
			
		}
	}

	@Override
	public GameListing[] getObservableGameNames() throws GameException {
        String username = getUserId();
        
		ObjectDatastore em = DbUtil.createObjectDatastore();
		//Transaction tx = em.beginTransaction();
		try {
			
			GameListing[] listings = gameEngine.getListings(gameDataAccessor.getObservableGames(em, username));
			return listings;

			//Don't bother committing, this was read only anyway.
		} finally {
		//	if ( null != tx && tx.isActive() ) {
		//		tx.rollback();
		//	}
			
		}
	}

}
