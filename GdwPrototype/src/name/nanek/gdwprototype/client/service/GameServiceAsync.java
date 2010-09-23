package name.nanek.gdwprototype.client.service;

import name.nanek.gdwprototype.client.model.GameDisplayInfo;
import name.nanek.gdwprototype.client.model.GameListing;
import name.nanek.gdwprototype.client.model.GamePlayInfo;
import name.nanek.gdwprototype.client.model.Player;
import name.nanek.gdwprototype.shared.exceptions.GameException;
import name.nanek.gdwprototype.shared.model.GameSettings;
import name.nanek.gdwprototype.shared.model.Marker;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Methods the client can call on the server and receive a response back later.
 * Asynchronous counterpart to <code>GameService</code>.
 */
public interface GameServiceAsync {
	
	void surrender(Long gameId, Player surrenderer, AsyncCallback<Void> callback) throws GameException;

	void publishMap(Long mapId, AsyncCallback<Void> callback) throws GameException;
	
	void getMapNames(AsyncCallback<GameListing[]> callback) throws GameException;

	void getJoinableGameNames(AsyncCallback<GameListing[]> callback) throws GameException;

	void getObservableGameNames(AsyncCallback<GameListing[]> callback) throws GameException;

	void createGameOrMap(String input, GameSettings settings, Marker[] markers, Long mapId, AsyncCallback<GameListing> callback) throws GameException;

	void getGameListingById(Long id, AsyncCallback<GameListing> callback) throws GameException;

	void getPositionsByGameId(Long id, AsyncCallback<GamePlayInfo> callback) throws GameException;

	void moveMarker(Long gameId, Integer sourceRow, Integer sourceColumn, Integer destRow, Integer destColumn,
			Long markerId, AsyncCallback<GamePlayInfo> callback) throws GameException;
	
	void getLoginUrlIfNeeded(String returnUrl, AsyncCallback<String> callback);
	
	void attemptToJoinGame(Long id, AsyncCallback<GameListing> callback) throws GameException;
	
	void getDisplayInfo(Long id, AsyncCallback<GameDisplayInfo> callback) throws GameException;
}
