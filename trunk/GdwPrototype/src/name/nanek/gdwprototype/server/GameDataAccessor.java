/**
 * 
 */
package name.nanek.gdwprototype.server;

import java.util.Collection;
import java.util.HashSet;
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

	public Collection<Game> getMaps(EntityManager em) {
		//Games created as a map and published (ended).
		
		Query query = em.createQuery(
				"SELECT FROM " + Game.class.getName() + " g " + 
				"WHERE g.startingMap AND g.ended ");
		Collection<Game> games = query.getResultList();
		return games;
	}	

	public Collection<Game> getJoinableGames(EntityManager em, String userId) {
		//Joinable if created as a game and user is a player or there is no second player yet.
		
		Set<Game> result = new HashSet<Game>();
		{
			Query secondPlayerQuery = em.createQuery(
					"SELECT FROM " + Game.class.getName() + " g " + 
					"WHERE g.ended = false AND g.startingMap = false AND " + 
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
					"WHERE g.ended = false AND g.startingMap = false AND " + 
					"g.firstPlayerUserId = :username ");
			if ( null == userId ) {
				userId = "";
			}
			firstPlayerQuery.setParameter("username", userId);
			result.addAll(firstPlayerQuery.getResultList());
		}		
		return result;
	}	

	public Collection<Game> getObservableGames(EntityManager em) {
		//Allow observation of games that have a second player.
		
		//TODO what to do about players who open a second browser, where they aren't logged in, and observe to cheat?
		//is IP detection enough? or should observation only be allowed of replays after the game is over?
		
		Query query = em.createQuery(
				"SELECT FROM " + Game.class.getName() + " g " + 
				"WHERE g.secondPlayerUserId IS NOT NULL ");
		Collection<Game> games = query.getResultList();
		return games;
	}	
}
