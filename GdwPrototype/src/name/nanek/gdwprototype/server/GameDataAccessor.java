/**
 * 
 */
package name.nanek.gdwprototype.server;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import name.nanek.gdwprototype.shared.model.Game;

/**
 * Runs queries for game data.
 * 
 * @author Lance Nanek
 *
 */
public class GameDataAccessor {
	//TODO some of the displayed collections in the UI are shuffling order on update. use ordered collections here and make sure sorted by something, like id/age at least

	public Collection<Game> getMaps(EntityManager em) {
		//Games created as a map and published (ended).
		
		Query query = em.createQuery(
				"SELECT FROM " + Game.class.getName() + " g " + 
				"WHERE g.map  = true AND g.ended = true ");
		Collection<Game> games = query.getResultList();
		return games;
	}	

	public Collection<Game> getJoinableGames(EntityManager em, String userId) {
		//Joinable if created as a game and user is a player or there is no second player yet.
		
		Set<Game> result = new LinkedHashSet<Game>();
		{
			Query secondPlayerQuery = em.createQuery(
					"SELECT FROM " + Game.class.getName() + " g " + 
					"WHERE g.ended = false AND g.map = false AND " + 
					"( g.secondPlayerUserId IS NULL OR " + 
					"g.secondPlayerUserId = :username )");
			if ( null == userId ) {
				userId = "";
			}
			secondPlayerQuery.setParameter("username", userId);
			result.addAll(secondPlayerQuery.getResultList());
		}
		//AppEngine datastore does not allow querying on both firstPlayerUserId and secondPlayerUserId in same query
		{
			Query firstPlayerQuery = em.createQuery(
					"SELECT FROM " + Game.class.getName() + " g " + 
					"WHERE g.ended = false AND g.map = false AND " + 
					"g.firstPlayerUserId = :username ");
			if ( null == userId ) {
				userId = "";
			}
			firstPlayerQuery.setParameter("username", userId);
			result.addAll(firstPlayerQuery.getResultList());
		}		
		return result;
	}	

	public Collection<Game> getObservableGames(EntityManager em, String userId) {
		//Allow observation of games that have a second player.
		
		//TODO what to do about players who open a second browser, where they aren't logged in, and observe to cheat?
		//is IP detection enough? or should observation only be allowed of replays after the game is over?
		//what about players who logout, take a look, then log back in? maybe require login to view games?
		
		Query query = em.createQuery(
				"SELECT FROM " + Game.class.getName() + " g " + 
				"WHERE g.secondPlayerUserId IS NOT NULL AND " +
				"g.secondPlayerUserId <> :username ");
		if ( null == userId ) {
			userId = "";
		}
		query.setParameter("username", userId);
		Collection<Game> games = query.getResultList();

		//Filter out games where the user is the first player. 
		//This can't be added to the above query, which is already querying the second player.
		LinkedList<Game> results = new LinkedList<Game>();
		for ( Game game : games ) {
			if ( !game.getFirstPlayerUserId().equals(userId) ) {
				results.add(game);
			}
		}		
		return results;
	}	
}
