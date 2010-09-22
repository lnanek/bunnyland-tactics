/**
 * 
 */
package name.nanek.gdwprototype.server;

import java.util.Iterator;

import name.nanek.gdwprototype.shared.model.Game;

import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.vercer.engine.persist.ObjectDatastore;
import com.vercer.engine.persist.FindCommand.RootFindCommand;

/**
 * Runs queries for game data.
 * 
 * @author Lance Nanek
 *
 */
public class GameDataAccessor {
	//TODO some of the displayed collections in the UI are shuffling order on update. use ordered collections here and make sure sorted by something, like id/age at least

	public Iterator<Game> getMaps(ObjectDatastore em) {
		//Games created as a map and published (ended).
		
		return em.find().type(Game.class)
			.addFilter("map", FilterOperator.EQUAL, true)
			.addFilter("ended", FilterOperator.EQUAL, true)
			.returnResultsNow();
	}	

	public Iterator<Game> getJoinableGames(ObjectDatastore em, String userId) {
		//Joinable if created as a game and user is a player or there is no second player yet.
		
		RootFindCommand<Game> find = em.find().type(Game.class)
			.addFilter("map", FilterOperator.EQUAL, false)
			.addFilter("ended", FilterOperator.EQUAL, false);
		
		find.addChildQuery()
			.addFilter("firstPlayerUserId", FilterOperator.EQUAL, userId)
			.addChildQuery()
			.addFilter("secondPlayerUserId", FilterOperator.EQUAL, userId)
			.addFilter("secondPlayerUserId", FilterOperator.EQUAL, null);
		return find.returnResultsNow();
	}	

	public Iterator<Game> getObservableGames(ObjectDatastore em, final String userId) {
		//Allow observation of games that have a second player.
		
		//TODO what to do about players who open a second browser, where they aren't logged in, and observe to cheat?
		//is IP detection enough? or should observation only be allowed of replays after the game is over?
		//what about players who logout, take a look, then log back in? maybe require login to view games?
		
		/*Didn't work. Twig-persist 2 alpha may not support AND yet...
		RootFindCommand<Game> find = em.find().type(Game.class);

		find.branch(MergeOperator.AND).addChildCommand()
			//XXX NOT_EQUAL threw unsupported operation exception, it just gets translated to this anyway.
			.addFilter("firstPlayerUserId", FilterOperator.LESS_THAN, userId)
			.addFilter("firstPlayerUserId", FilterOperator.GREATER_THAN, userId)
			.branch(MergeOperator.AND).addChildCommand()
			.addFilter("secondPlayerUserId", FilterOperator.LESS_THAN, userId)
			.addFilter("secondPlayerUserId", FilterOperator.GREATER_THAN, userId);
			//.addFilter("secondPlayerUserId", FilterOperator.NOT_EQUAL, null);
		return find.now();
		*/
		
		Iterator<Game> notFirstPlayer = em.find().type(Game.class)
			.addFilter("firstPlayerUserId", FilterOperator.NOT_EQUAL, userId)
			.returnResultsNow();		
		
		//TODO maybe just store a list of player IDs? might work better given the data store's limitations re different properties		
		Predicate<Game> notSecondPlayerPredicate = new Predicate<Game>() {
			@Override
			public boolean apply(Game input) {
				return !input.getSecondPlayerUserId().equals(userId);
			}
		};
		return Iterators.filter(notFirstPlayer, notSecondPlayerPredicate);
	}	
}
