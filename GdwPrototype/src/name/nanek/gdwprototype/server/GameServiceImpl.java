package name.nanek.gdwprototype.server;

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
import name.nanek.gdwprototype.shared.model.Marker;
import name.nanek.gdwprototype.shared.model.Position;

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
			
		Game game = null;
		Objectify em = DbUtil.beginTransaction();
		try {
			game = gameEngine.moveMarker(gameId, sourceRow, sourceColumn, destRow, destColumn, markerId, user, em);
			
			em.getTxn().commit();
		} finally {
			rollbackIfNeeded(em);
		}

		return gameEngine.createPlayInfo(game, em);
	}

	@Override
	public GameDisplayInfo getDisplayInfo(Long gameId) throws GameException {

		Objectify em = DbUtil.createObjectify();

		Game game = em.get(Game.class, gameId);
		return gameEngine.createDisplayInfo(game, em);
	}

	@Override
	public GamePlayInfo getPositionsByGameId(Long gameId) throws GameException {

		Objectify em = DbUtil.beginTransaction();
		try {
			Game game = em.get(Game.class, gameId);
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
			Game game = em.get(Game.class, gameId);
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
			Game game = em.get(Game.class, mapId);
			game.setEnded(true);
			em.put(game);
			
			em.getTxn().commit();
		} finally {
			rollbackIfNeeded(em);
		}
	}
	
	@Override
	public GameListing getGameListingById(Long id) throws GameException {
		
		Objectify em = DbUtil.createObjectify();
		Game game = em.get(Game.class, id);

		if (null == game) {
			return null;
		}
		GameListing listing = game.getListing();

		return listing;
	}

	@Override
	public GameListing createGameOrMap(String name, GameSettings settings, Marker[] markers, Long mapId) throws GameException {
		FieldVerifier.validateGameName(name);

		User user = requireUser();
		Objectify em = DbUtil.beginTransaction();
		try {
			
			Game game = new Game();
			game.setCreatorNickname(user.getNickname());
			game.setName(name);
			game.setFirstPlayerUserId(user.getUserId());
			game.setEnded(false);
			if ( null == mapId ) {
				game.setMap(true);
			}
			Key<Game> gameKey = em.put(game);
			
			//If we're a map, we use the settings passed to us and have no positions.
			if ( null == mapId ) {
				settings.setGame(gameKey);
				Key<GameSettings> gameSettingsKey = em.put(settings);
				for ( Marker marker : markers ) {
					marker.setSettingsKey(gameSettingsKey);
					em.put(marker);
				}
				return game.getListing();
			} else {
				//Otherwise we're creating a game and have a map to start from.
				//Copy settings and positions from map.
				Game map = em.get(Game.class, mapId);				
				GameSettings mapSettings = em.query(GameSettings.class).filter("game", map).get();
				GameSettings gameSettings = mapSettings.copy();
				gameSettings.setGame(gameKey);
				Key<GameSettings> gameSettingsKey = em.put(gameSettings);							
				for ( Marker mapMarker :  em.query(Marker.class).filter("settings", mapSettings) ) {
					Marker gameMarker = mapMarker.copy();
					gameMarker.setSettingsKey(gameSettingsKey);
					Key<Marker> gameMarkerKey = em.put(gameMarker);
					
					for ( Position mapPosition :  em.query(Position.class).filter("marker", gameMarker) ) {
						Position gamePosition = mapPosition.copy();
						gamePosition.setGame(gameKey);
						gamePosition.setMarkerKey(gameMarkerKey);
						em.put(gamePosition);
					}
				}
			}
			
			em.getTxn().commit();
			
			return game.getListing();
		} finally {
			rollbackIfNeeded(em);
		}
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
	
	@Override
	public GameListing attemptToJoinGame(final Long id) throws GameException {

		final User user = requireUser();
	        
		Objectify ofy = DbUtil.beginTransaction();
		try {
			Game game = ofy.get(Game.class, id);
			
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
			ofy.put(game);
			
			ofy.getTxn().commit();
			
			return listing;
		} finally {
			rollbackIfNeeded(ofy);
		}
	}
	
	private void rollbackIfNeeded(Objectify ofy) {
	    if (ofy.getTxn().isActive()) {
	    	ofy.getTxn().rollback();
	    }		
	}

	private User getUser() {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        return user;
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
	public GameListing[] getMapNames() throws GameException {
		Objectify em = DbUtil.createObjectify();
		GameListing[] listings = gameEngine.getListings(gameDataAccessor.getMaps(em));
		return listings;
	}

	private String getUserId() {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        return null != user ? user.getUserId() : null;
	}
	
	@Override
	public GameListing[] getJoinableGameNames() throws GameException {
        String username = getUserId();
        
		Objectify em = DbUtil.createObjectify();
		GameListing[] listings = gameEngine.getListings(gameDataAccessor.getJoinableGames(em, username));
		return listings;
	}

	@Override
	public GameListing[] getObservableGameNames() throws GameException {
        String username = getUserId();
        
		Objectify em = DbUtil.createObjectify();
		GameListing[] listings = gameEngine.getListings(gameDataAccessor.getObservableGames(em, username));
		return listings;
	}

}
