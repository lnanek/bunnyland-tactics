package name.nanek.gdwprototype.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

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
import name.nanek.gdwprototype.shared.model.Markers;
import name.nanek.gdwprototype.shared.model.Position;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.util.Random;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 * 
 * @author Lance Nanek
 */
@SuppressWarnings("serial")
public class GameServiceImpl extends RemoteServiceServlet implements GameService {
	//TODO put transactions back in now that understand about default fetch groups
	
	//TODO this class is too complex, split into separate classes for transaction handling and game logic and in/out?

	private static final Random random = new Random();
	
	private GameDataAccessor gameDataAccessor = new GameDataAccessor();
	
	public GamePlayInfo moveMarker(Long gameId, Integer sourceRow, Integer sourceColumn, Integer destRow,
			Integer destColumn, String newImageSource) throws GameException {
		//TODO this method needs massive cleanup. replace string checking with marker types, for example
		
		/*
		System.out.println("GameServiceImpl#moveMarker: " + gameId 
				+ ", " + sourceRow
				 + ", " + sourceColumn
				 + ", " + destRow
				 + ", " + destColumn
				 + ", " + newImageSource);
		 */
		
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if (user == null) {
			throw new UserFriendlyMessageException("You need to login to make a move.");
		}
			
		EntityManager em = DbUtil.createEntityManager();
		//EntityTransaction tx = em.getTransaction();
		//tx.begin();

		Game game = null;
		try {
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
									em.persist(position);
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
				em.persist(position);
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
			
			
			// tx.commit();
			// tx = null;

			/*
			 * if ( changedPositions ) { //tx = em.getTransaction();
			 * //tx.begin(); int newMoveCount = game.incrementMoveCount();
			 * //tx.commit(); //tx = null; return newMoveCount; }
			 * 
			 * return game.getMoveCount();
			 */
			//tx.commit();
		} finally {
			//if ( null != tx && tx.isActive() ) {
			//	tx.rollback();
			//}
			em.close();
		}

		return createGameInfo(game);

//		return getPositionsByGameId(gameId);
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
	
	private GamePlayInfo createGameInfo(Game game) {
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
		boolean isUsersTurn = false;
		Player userPlayingAs = null;
		if ( null != user ) {
			isUsersTurn = isUsersTurn(game, user);
			if ( isUsersTurn ) {
				userPlayingAs = game.getCurrentUsersTurn();
			} else {
				userPlayingAs = Player.other(game.getCurrentUsersTurn());
			}
		}
		
		//System.out.println("GameDataServiceImpl#createGameInfo: settings are: " + game.getSettings());
		//System.out.println("GameDataServiceImpl#createGameInfo: markers are: " + game.getSettings().getMarkers());
		//System.out.println("GameDataServiceImpl#createGameInfo: positionInfos are: " + positions.length );	
		
		GamePlayInfo info = new GamePlayInfo(positions, isUsersTurn, userPlayingAs, needsSecondPlayer, 
				game.getSettings().getBoardHeight(), game.getSettings().getBoardWidth(), markers, game.getWinner(), game.isStartingMap(), game.isEnded());
		return info;
	}

	public GamePlayInfo getPositionsByGameId(Long gameId) throws GameException {
				
			EntityManager em = DbUtil.createEntityManager();

			Game game = null;
			try {
				game = em.find(Game.class, gameId);
				game.getSettings();
				game.getPositions();
				return createGameInfo(game);
			} finally {
				em.close();
			}

			//return createGameInfo(game);
		}

	public void surrender(Long gameId, Player surrenderer) throws GameException {
		EntityManager em = DbUtil.createEntityManager();
		em.getTransaction().begin();
		try {
			Game game = em.find(Game.class, gameId);
			game.setEnded(true);
			game.setWinner(Player.other(surrenderer));
		} finally {
			em.getTransaction().commit();
			em.close();
		}
	}

	public void publishMap(Long mapId) throws GameException {
		EntityManager em = DbUtil.createEntityManager();
		em.getTransaction().begin();
		try {
			Game game = em.find(Game.class, mapId);
			game.setEnded(true);
		} finally {
			em.getTransaction().commit();
			em.close();
		}
	}
	
	public GameListing getGameListingById(Long id) throws GameException {
		EntityManager em = DbUtil.createEntityManager();
		em.getTransaction().begin();

		try {
			// Query query = em.createQuery("SELECT g FROM " +
			// Game.class.getName() + " AS g WHERE g.id = " + id);
			// Game game = (Game) query.getSingleResult();

			Game game = em.find(Game.class, id);

			if (null == game) {
				return null;
			}
			GameListing listing = game.getListing();

			return listing;
		} finally {
			em.getTransaction().commit();
			em.close();
		}
	}

	public GameListing createGameOrMap(String name, GameSettings settings, Long mapId) throws GameException {
		FieldVerifier.validateGameName(name);

		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();

		//TODO maybe have a general purpose "need to login" exception with login URL?
		//all RPC calls could detect and redirect user
		if (user == null) {
			throw new UserFriendlyMessageException("You need to login to create a game.");
		}
	        
		Game game = new Game();
		
		//System.out.println("GameDataServiceImpl#createGame: settings are: " + settings);
		//System.out.println("GameDataServiceImpl#createGame: markers are: " + settings.getMarkers());
		
		EntityManager em = DbUtil.createEntityManager();
		EntityTransaction tx = null;
		try {	
			
			Set<Position> mapPositions = null;
			GameSettings mapSettings = null;
			if ( null != mapId ) {
	
				//System.out.println("GameDataServiceImpl#createGame: starting game from map: " + mapId);
				Game map = em.find(Game.class, mapId);
				mapSettings = map.getSettings();
				mapPositions = map.getPositions();
				
				GameSettings gameSettings = mapSettings.copy();
				game.setSettings(gameSettings);
			} else {
				//System.out.println("GameDataServiceImpl#createGame: starting new map");
				game.setSettings(settings);
				game.setStartingMap(true);
			}
			game.setName(name);
			game.setFirstPlayerUserId(user.getUserId());
			game.setEnded(false);

			
			tx = em.getTransaction();
			tx.begin();

			em.persist(game);
			em.persist(game.getSettings());
			if ( null != mapPositions ) {
				Set<Position> gamePositions = new HashSet<Position>();
				for ( Position mapPosition : mapPositions ) {
					Position newPosition = new Position(mapPosition.getRow(), mapPosition.getColumn(), mapPosition.getMarkerSource());
					em.persist(newPosition);
					gamePositions.add(newPosition);
				}
				game.setPositions(gamePositions);
			}
			
			tx.commit();
		} finally {
			if ( null != tx && tx.isActive() ) {
				tx.rollback();
			}
			em.close();
		}
		
		return game.getListing();
	}
	
	public GameListing attemptToJoinGame(Long id) throws GameException {

		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();

		if (user == null) {
			throw new UserFriendlyMessageException("You need to login to join a game.");
		}
	        
		EntityManager em = DbUtil.createEntityManager();
		em.getTransaction().begin();

		try {
			Game game = em.find(Game.class, id);

			if (null == game) {
				throw new UserFriendlyMessageException("Couldn't find requested game.");
			}
			
			if ( user.getUserId().equals(game.getFirstPlayerUserId()) ) {
				//User is already player one.
				//TODO switch to hotseat mode?
				return game.getListing();
			}
			
			if ( null == game.getSecondPlayerUserId() || user.getUserId().equals(game.getSecondPlayerUserId()) ) {
				game.setSecondPlayerUserId(user.getUserId());
				return game.getListing();
			}
			
			return null;
		} finally {
			em.getTransaction().commit();
			em.close();
		}
	}
	
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
	
	public GameListing[] getMapNames() throws GameException {
		EntityManager em = DbUtil.createEntityManager();
		EntityTransaction tx = null;
		try {
			tx = em.getTransaction();
			tx.begin();
			
			GameListing[] listings = getListings(gameDataAccessor.getMaps(em));
			return listings;

			//Don't bother committing, this was read only anyway.
		} finally {
			if ( null != tx && tx.isActive() ) {
				tx.rollback();
			}
			em.close();
		}
	}

	public GameListing[] getJoinableGameNames() throws GameException {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        String username = null != user ? user.getUserId() : null;
        
		EntityManager em = DbUtil.createEntityManager();
		EntityTransaction tx = null;
		try {
			tx = em.getTransaction();
			tx.begin();
			
			GameListing[] listings = getListings(gameDataAccessor.getJoinableGames(em, username));
			return listings;

			//Don't bother committing, this was read only anyway.
		} finally {
			if ( null != tx && tx.isActive() ) {
				tx.rollback();
			}
			em.close();
		}
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
	
	public GameListing[] getObservableGameNames() throws GameException {
		EntityManager em = DbUtil.createEntityManager();
		EntityTransaction tx = null;
		try {
			tx = em.getTransaction();
			tx.begin();
			
			GameListing[] listings = getListings(gameDataAccessor.getObservableGames(em));
			return listings;

			//Don't bother committing, this was read only anyway.
		} finally {
			if ( null != tx && tx.isActive() ) {
				tx.rollback();
			}
			em.close();
		}
	}

	private GameListing[] getListings(Collection<Game> games) {
		ArrayList<GameListing> list = new ArrayList<GameListing>();
		for (Game game : games) {
			list.add(game.getListing());
		}
		GameListing[] array = list.toArray(new GameListing[] {});
		return array;		
	}
	
	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html
	 *            the html string to escape
	 * @return the escaped string
	 */
	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	}
}
