package name.nanek.gdwprototype.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import name.nanek.gdwprototype.client.model.GameListingInfo;
import name.nanek.gdwprototype.client.model.GamePlayInfo;
import name.nanek.gdwprototype.client.model.Player;
import name.nanek.gdwprototype.client.model.PositionInfo;
import name.nanek.gdwprototype.client.service.GameDataService;
import name.nanek.gdwprototype.client.view.screen.MenuScreen;
import name.nanek.gdwprototype.server.model.Game;
import name.nanek.gdwprototype.server.model.Position;
import name.nanek.gdwprototype.shared.FieldVerifier;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GameDataServiceImpl extends RemoteServiceServlet implements GameDataService {
	//TODO put transactions back in now that understand about default fetch groups

	public GamePlayInfo moveMarker(Long gameId, Integer sourceRow, Integer sourceColumn, Integer destRow,
			Integer destColumn, String newImageSource) throws IllegalArgumentException {

		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if (user == null) {
			throw new IllegalStateException("You need to login to make a move.");
		}
			
		EntityManager em = DbUtil.createEntityManager();

		// EntityTransaction tx = em.getTransaction();
		// tx.begin();

		try {
			Game game = em.find(Game.class, gameId);
			game.getPositions();

			if ( !game.isUsersTurn(user) ) {
				throw new IllegalStateException("It isn't your turn.");
			}
			
			boolean changedPositions = false;

			if (null != sourceColumn && null != sourceRow) {
				// Source so delete it.
				removePosition(sourceRow, sourceColumn, em, game);
				changedPositions = true;
			}

			if (null != destColumn && null != destRow && null != newImageSource) {
				// Destination so remove any old position and create new
				// position.
				removePosition(destRow, destColumn, em, game);

				Position position = new Position(destRow, destColumn, newImageSource);
				em.persist(position);
				game.getPositions().add(position);
				changedPositions = true;
			}
			
			if ( changedPositions ) {
				game.setNextUsersTurn();
			}
			
			return createGameInfo(game);
			
			// tx.commit();
			// tx = null;

			/*
			 * if ( changedPositions ) { //tx = em.getTransaction();
			 * //tx.begin(); int newMoveCount = game.incrementMoveCount();
			 * //tx.commit(); //tx = null; return newMoveCount; }
			 * 
			 * return game.getMoveCount();
			 */
		} finally {
			// if ( null != tx ) {
			// tx.rollback();
			// }
			em.close();
		}

//		return getPositionsByGameId(gameId);
	}

	private void removePosition(Integer sourceRow, Integer sourceColumn, EntityManager em, Game game) {
		for (Position position : game.getPositions()) {
			if (sourceRow.equals(position.getRow()) && sourceColumn.equals(position.getColumn())) {
				game.getPositions().remove(position);
				em.remove(position);
			}
		}
	}
	
	private GamePlayInfo createGameInfo(Game game) {
		if ( null == game ) return null;
		
		//Create the array of position info.
		Set<Position> positions = game.getPositions();
		// PositionInfo[] list = new PositionInfo[positions.size()];
		ArrayList<PositionInfo> list = new ArrayList<PositionInfo>();
		// int i = 0;
		for (Position position : positions) {
			// System.out.println("Found position with row, column: " +
			// position.getRow() + ", " + position.getColumn());
			PositionInfo info = position.getInfo();

			// System.out.println("Created info with row, column: " +
			// info.getRow() + ", " + info.getColumn());
			// list[i++] = info;
			list.add(info);
		}
		PositionInfo[] positionInfos = list.toArray(new PositionInfo[] {});

		boolean needsSecondPlayer = null == game.getSecondPlayerUserId();
		
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		boolean isUsersTurn = false;
		Player userPlayingAs = null;
		if ( null != user ) {
			String userId = user.getUserId();
			isUsersTurn = game.isUsersTurn(user);
			if ( isUsersTurn ) {
				userPlayingAs = game.getCurrentUsersTurn();
			} else {
				userPlayingAs = Player.other(game.getCurrentUsersTurn());
			}
		}
		
		GamePlayInfo info = new GamePlayInfo(positionInfos, isUsersTurn, userPlayingAs, needsSecondPlayer);
		return info;
	}

	public GamePlayInfo getPositionsByGameId(Long id) throws IllegalArgumentException {
		EntityManager em = DbUtil.createEntityManager();
		// em.getTransaction().begin();

		try {
			Game game = em.find(Game.class, id);
			return createGameInfo(game);
			
		} finally {
			// em.getTransaction().commit();
			em.close();
		}
	}

	public GameListingInfo getGameListingById(Long id) throws IllegalArgumentException {
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
			GameListingInfo listing = game.getListing();

			return listing;
		} finally {
			em.getTransaction().commit();
			em.close();
		}
	}

	public GameListingInfo createGame(String name) throws IllegalArgumentException {
		if (!FieldVerifier.isValidGameName(name)) {
			throw new IllegalArgumentException(FieldVerifier.VALID_GAME_NAME_ERROR_MESSAGE);
		}
		
		if (MenuScreen.DEFAULT_GAME_NAME.equals(name)) {
			name = "Nameless Game";
		}

		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();

		//TODO maybe have a general purpose "need to login" exception with login URL?
		//all RPC calls could detect and redirect user
		if (user == null) {
			throw new IllegalStateException("You need to login to create a game.");
		}
	        
		Game game = new Game();
		
		EntityManager em = DbUtil.createEntityManager();
		em.getTransaction().begin();
		try {	
			game.setName(name);
			game.setFirstPlayerUserId(user.getUserId());
			game.setEnded(false);
			em.persist(game);
		} finally {
			// System.out.println("Got this far 2.");
			em.getTransaction().commit();
			em.close();
		}
		
		return game.getListing();
	}
	
	public GameListingInfo attemptToJoinGame(Long id) throws IllegalArgumentException {

		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();

		if (user == null) {
			throw new IllegalStateException("You need to login to join a game.");
		}
	        
		EntityManager em = DbUtil.createEntityManager();
		em.getTransaction().begin();

		try {
			Game game = em.find(Game.class, id);

			if (null == game) {
				throw new IllegalArgumentException("Couldn't find requested game.");
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

	public GameListingInfo[] getGameNames(boolean mustBeJoinable) throws IllegalArgumentException {

		EntityManager em = DbUtil.createEntityManager();
		em.getTransaction().begin();

		try {
			Query query = em.createQuery("SELECT FROM " + Game.class.getName());
			List<Game> games = query.getResultList();

			ArrayList<GameListingInfo> list = new ArrayList<GameListingInfo>();
			for (Game game : games) {
				//TODO change query to do this when joinable only requested for better performance
				if ( !mustBeJoinable || (!game.isEnded() && null == game.getSecondPlayerUserId()) ) {
					list.add(game.getListing());
				}
			}

			GameListingInfo[] array = list.toArray(new GameListingInfo[] {});
			return array;
		} finally {
			em.getTransaction().commit();
			em.close();
		}
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
