package name.nanek.gdwprototype.client.service;

import name.nanek.gdwprototype.client.model.GameDisplayInfo;
import name.nanek.gdwprototype.client.model.GameUpdateInfo;
import name.nanek.gdwprototype.shared.exceptions.GameException;
import name.nanek.gdwprototype.shared.model.Game;
import name.nanek.gdwprototype.shared.model.Marker;
import name.nanek.gdwprototype.shared.model.Player;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Methods the client can call on the server and receive a response back later.
 * Asynchronous counterpart to <code>GameService</code>.
 */
public interface GameServiceAsync {
	
	void surrender(Long gameId, Player surrenderer, AsyncCallback<Void> callback) throws GameException;

	void publishMap(Long mapId, AsyncCallback<Void> callback) throws GameException;
	
	void getMapNames(AsyncCallback<Game[]> callback) throws GameException;

	void getJoinableGameNames(AsyncCallback<Game[]> callback) throws GameException;

	void getObservableGameNames(AsyncCallback<Game[]> callback) throws GameException;

	void getGameListingById(Long id, AsyncCallback<Game> callback) throws GameException;

	void getPositionsByGameId(Long id, AsyncCallback<GameUpdateInfo> callback) throws GameException;

	void moveMarker(Long gameId, Integer sourceRow, Integer sourceColumn, Integer destRow, Integer destColumn,
			Long markerId, AsyncCallback<GameUpdateInfo> callback) throws GameException;
	
	void getLoginUrlIfNeeded(String returnUrl, AsyncCallback<String> callback);
	
	void attemptToJoinGame(Long id, AsyncCallback<Game> callback) throws GameException;
	
	void getDisplayInfo(Long id, AsyncCallback<GameDisplayInfo> callback) throws GameException;
	
	void createMap(String name, Integer carrotGenerationPeriod, Integer boardWidth, Integer boardHeight,
			Marker[] markers, AsyncCallback<Game> callback) throws GameException;

	void createGame(String name, Long mapId, AsyncCallback<Game> callback) throws GameException;
}
