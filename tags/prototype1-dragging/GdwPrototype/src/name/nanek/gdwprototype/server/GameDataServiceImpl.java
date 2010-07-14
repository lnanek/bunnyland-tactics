package name.nanek.gdwprototype.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import name.nanek.gdwprototype.client.GameDataService;
import name.nanek.gdwprototype.client.GameListing;
import name.nanek.gdwprototype.client.PositionInfo;
import name.nanek.gdwprototype.shared.FieldVerifier;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GameDataServiceImpl extends RemoteServiceServlet implements
		GameDataService {
	
	public int moveMarker(Long gameId, Integer sourceRow, Integer sourceColumn, 
			Integer destRow, Integer destColumn, String newImageSource) throws IllegalArgumentException {
		
		EntityManager em = DbUtil.createEntityManager();

		//EntityTransaction tx = em.getTransaction();
		//tx.begin();
		
	    try {
	    	Game game = em.find(Game.class, gameId);	

	        boolean changedPositions = false;
	        
	        if ( null != sourceColumn && null != sourceRow ) {  
	        	//Source so delete it.
	        	removePosition(sourceRow, sourceColumn, em, game);
	        	changedPositions = true;
	        }
	        
	        
	        if ( null != destColumn && null != destRow && null != newImageSource ) {  
	        	//Destination so remove any old position and create new position.
	        	removePosition(destRow, destColumn, em, game);
	        	
	        	Position position = new Position(destRow, destColumn, newImageSource);
	        	em.persist(position);
	        	game.getPositions().add(position);
	        	changedPositions = true;
	        }
	        //tx.commit();
	        //tx = null;
	        
	        if ( changedPositions ) {
	    		//tx = em.getTransaction();
	    		//tx.begin();
	        	int newMoveCount = game.incrementMoveCount();
		        //tx.commit();
		        //tx = null;
	        	return newMoveCount;
	        }
	        
	        return game.getMoveCount();
	    } finally {
	    	//if ( null != tx ) {
	    	//	tx.rollback();
	    	//}
	    	em.close();
	    }	
	}

	private void removePosition(Integer sourceRow, Integer sourceColumn,
			EntityManager em, Game game) {
		for( Position position : game.getPositions() ) {
			if (sourceRow.equals(position.getRow()) 
					&& sourceColumn.equals(position.getColumn())) {
				game.getPositions().remove(position);
				em.remove(position);
			}
		}
	}
	
	public PositionInfo[] getPositionsByGameId(Long id) throws IllegalArgumentException {
		EntityManager em = DbUtil.createEntityManager();
		//em.getTransaction().begin();
		
	    try {
	    	Game game = em.find(Game.class, id);
	        
	        if ( null == game ) {
	        	return null;
	        }
	        Set<Position> positions = game.getPositions();
	        
	        
	        //PositionInfo[] list = new PositionInfo[positions.size()];
	        ArrayList<PositionInfo> list = new ArrayList<PositionInfo>();
	        //int i = 0;
	        for( Position position : positions) {
	        	//System.out.println("Found position with row, column: " + position.getRow() + ", " + position.getColumn());
	        	PositionInfo info = position.getInfo();

	        	//System.out.println("Created info with row, column: " + info.getRow() + ", " + info.getColumn());
	        	//list[i++] = info;
	        	list.add(info);
	        }

	        return list.toArray(new PositionInfo[] {});
	        
	    } finally {
	        //em.getTransaction().commit();
	    	em.close();
	    }	
	}

	public GameListing getGameListingById(Long id) throws IllegalArgumentException {
		EntityManager em = DbUtil.createEntityManager();
		em.getTransaction().begin();
		
	    try {
	        //Query query = em.createQuery("SELECT g FROM " + Game.class.getName() + " AS g WHERE g.id = " + id);
	        //Game game = (Game) query.getSingleResult();      
	        
	        Game game = em.find(Game.class, id);	        
	        
	        if ( null == game ) {
	        	return null;
	        }
	        GameListing listing = game.getListing();

	        return listing;
	    } finally {
	        em.getTransaction().commit();
	    	em.close();
	    }		
	}
	
	public boolean createGame(String name) throws IllegalArgumentException {	
		if ( !FieldVerifier.isValidGameName(name) ) {
			//throw new IllegalArgumentException(FieldVerifier.VALID_GAME_NAME_ERROR_MESSAGE);
			return false;
		}
		
		EntityManager em = DbUtil.createEntityManager();
		em.getTransaction().begin();
	    try {
	    	Game game = new Game();
	    	game.setName(name);
	    	em.persist(game);
	        return true;
	    } finally {
			//System.out.println("Got this far 2.");
	        em.getTransaction().commit();
			em.close();
	    }				
	}

	
	public GameListing[] getGameNames() throws IllegalArgumentException {

		EntityManager em = DbUtil.createEntityManager();
		em.getTransaction().begin();
		
	    try {
	        Query query = em.createQuery("SELECT FROM " + Game.class.getName());
	        List<Game> games = query.getResultList();      
	        
	        GameListing[] list = new GameListing[games.size()];
	        int i = 0;
	        for( Game game : games) {
	        	list[i++] = game.getListing();
	        }
	        return list;
	    } finally {
	        em.getTransaction().commit();
	    	em.close();
	    }
	}

	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html the html string to escape
	 * @return the escaped string
	 */
	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}
}
