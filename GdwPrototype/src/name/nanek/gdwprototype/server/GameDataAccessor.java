/**
 * 
 */
package name.nanek.gdwprototype.server;

import name.nanek.gdwprototype.shared.model.Game;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

/**
 * Runs queries for game data.
 * 
 * @author Lance Nanek
 *
 */
public class GameDataAccessor {
	//TODO some of the displayed collections in the UI are shuffling order on update. use ordered collections here and make sure sorted by something, like id/age at least

	public Iterable<Game> getMaps(Objectify ofy) {
		//Games created as a map and published (ended).
		
		Query<Game> q = ofy.query(Game.class)
			.filter("map = ", true)
			.filter("ended = ", true);
		return q;
	}	

	public Iterable<Game> getJoinableGames(Objectify ofy, String userId) {
		//Joinable if created as a game and user is a player or there is no second player yet.
		
		Query<Game> q = ofy.query(Game.class)
			.filter("map = ", false)
			.filter("ended = ", false)
			.filter("firstPlayerUserId = ", userId)
			.filter("secondPlayerUserId = ", userId)
			.filter("secondPlayerUserId = ", null);
		return q;
	}	

	public Iterable<Game> getObservableGames(Objectify ofy, final String userId) {
		//Allow observation of games that have a second player.
		
		//TODO what to do about players who open a second browser, where they aren't logged in, and observe to cheat?
		//is IP detection enough? or should observation only be allowed of replays after the game is over?
		//what about players who logout, take a look, then log back in? maybe require login to view games?
		//then they need two accounts at least
		
		Query<Game> q = ofy.query(Game.class)
			.filter("map = ", false)
			.filter("ended = ", false)
			.filter("firstPlayerUserId != ", userId)
			.filter("secondPlayerUserId != ", userId)
			.filter("secondPlayerUserId != ", null);
		return q;
		
		/*
		//TODO maybe just store a list of player IDs? might work better given the data store's limitations re different properties		
		Predicate<Game> notSecondPlayerPredicate = new Predicate<Game>() {
			@Override
			public boolean apply(Game input) {
				return !input.getSecondPlayerUserId().equals(userId);
			}
		};
		return Iterators.filter(notFirstPlayer, notSecondPlayerPredicate);
		*/
	}	
}
