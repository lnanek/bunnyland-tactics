package name.nanek.gdwprototype.client.service;

import name.nanek.gdwprototype.client.model.GameListing;
import name.nanek.gdwprototype.client.model.GamePlayInfo;
import name.nanek.gdwprototype.client.model.Player;
import name.nanek.gdwprototype.shared.exceptions.ServerException;
import name.nanek.gdwprototype.shared.model.GameSettings;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GameService</code>.
 */
public interface GameServiceAsync {
	
	void surrender(Long gameId, Player surrenderer, AsyncCallback<Void> callback) throws ServerException;

	void publishMap(Long mapId, AsyncCallback<Void> callback) throws ServerException;
	
	void getMapNames(AsyncCallback<GameListing[]> callback) throws ServerException;

	void getJoinableGameNames(AsyncCallback<GameListing[]> callback) throws ServerException;

	void getObservableGameNames(AsyncCallback<GameListing[]> callback) throws ServerException;

	void createGame(String input, GameSettings settings, Long mapId, AsyncCallback<GameListing> callback) throws ServerException;

	void getGameListingById(Long id, AsyncCallback<GameListing> callback) throws ServerException;

	void getPositionsByGameId(Long id, AsyncCallback<GamePlayInfo> callback) throws ServerException;

	void moveMarker(Long gameId, Integer sourceRow, Integer sourceColumn, Integer destRow, Integer destColumn,
			String newImageSource, AsyncCallback<GamePlayInfo> callback) throws ServerException;
	
	void getLoginUrlIfNeeded(String returnUrl, AsyncCallback<String> callback);
	
	void attemptToJoinGame(Long id, AsyncCallback<GameListing> callback) throws ServerException;
}
