/**
 * 
 */
package name.nanek.gdwprototype.server;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import name.nanek.gdwprototype.shared.exceptions.UserFriendlyMessageException;
import name.nanek.gdwprototype.shared.model.Game;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

/**
 * Runs queries for game data.
 * 
 * @author Lance Nanek
 *
 */
public class DataAccessor {

	public Iterable<Game> getMaps(Objectify ofy) {
		//Games created as a map and published (ended).
		
		Query<Game> q = ofy.query(Game.class)
			.filter("map = ", true)
			.filter("ended = ", true);
		return q;
	}	

	public Iterator<Game> getJoinableGames(Objectify ofy, String userId) {
		//Joinable if created as a game and user is a player or there is no second player yet.
		
		Set<Game> sortedAndDuplicatesRemoved = new TreeSet<Game>();

		{
			Query<Game> playingAsFirstPlayer = ofy.query(Game.class)
				.filter("map = ", false)
				.filter("ended = ", false)
				.filter("firstPlayerUserId = ", userId);
			sortedAndDuplicatesRemoved.addAll(playingAsFirstPlayer.list());
		}
		
		{
			Query<Game> playingAsSecondPlayer = ofy.query(Game.class)
				.filter("map = ", false)
				.filter("ended = ", false)
				.filter("secondPlayerUserId = ", userId);
			sortedAndDuplicatesRemoved.addAll(playingAsSecondPlayer.list());
		}
		
		{
			Query<Game> needsSecondPlayer = ofy.query(Game.class)
				.filter("map = ", false)
				.filter("ended = ", false)
				.filter("secondPlayerUserId = ", null);
			sortedAndDuplicatesRemoved.addAll(needsSecondPlayer.list());
		}
		
		return sortedAndDuplicatesRemoved.iterator();
	}	

	public Iterator<Game> getObservableGames(Objectify ofy, final String userId) {
		//Allow observation of games that have a second player.
		
		//TODO what to do about players who open a second browser, where they aren't logged in, and observe to cheat?
		//is IP detection enough? or should observation only be allowed of replays after the game is over?
		//what about players who logout, take a look, then log back in? maybe require login to view games?
		//then they need two accounts at least
		
		Query<Game> q = ofy.query(Game.class)
			.filter("map = ", false)
			.filter("ended = ", false)
			.filter("secondPlayerUserId != ", userId)
			.filter("secondPlayerUserId != ", null);	
		
		//TODO maybe just store a list of player IDs? might work better given the data store's limitations re different properties		
		Predicate<Game> notFirstPlayerPredicate = new Predicate<Game>() {
			@Override
			public boolean apply(Game input) {
				return !input.getFirstPlayerUserId().equals(userId);
			}
		};
		return Iterators.filter(q.iterator(), notFirstPlayerPredicate);
		
	}

	Game requireGame(ServiceImpl serviceImpl, Objectify ofy, Long gameId) {
		return requireGameOrMap(ofy, gameId, "Couldn't find requested game. It may have been deleted.");
	}

	Game requireMap(ServiceImpl serviceImpl, Objectify ofy, Long gameId) {
		return requireGameOrMap(ofy, gameId, "Couldn't find requested map. It may have been deleted.");
	}

	Game requireGameOrMap(Objectify ofy, Long gameOrMapId, String errorMessage) {
		if ( null == gameOrMapId ) {
			throw new UserFriendlyMessageException(errorMessage);
		}
		
		Game gameOrMap = ofy.get(Game.class, gameOrMapId);
		if (null == gameOrMap) {
			throw new UserFriendlyMessageException(errorMessage);
		}
		return gameOrMap;
	}	
}
