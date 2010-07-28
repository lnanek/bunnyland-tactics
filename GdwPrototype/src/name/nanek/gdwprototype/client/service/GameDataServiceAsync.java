package name.nanek.gdwprototype.client.service;

import name.nanek.gdwprototype.client.model.GameListingInfo;
import name.nanek.gdwprototype.client.model.GamePlayInfo;
import name.nanek.gdwprototype.client.model.Player;
import name.nanek.gdwprototype.shared.exceptions.ServerException;
import name.nanek.gdwprototype.shared.model.GameSettings;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GameDataService</code>.
 */
public interface GameDataServiceAsync {
	
	void surrender(Long gameId, Player surrenderer, AsyncCallback<Void> callback) throws ServerException;

	void publishMap(Long mapId, AsyncCallback<Void> callback) throws ServerException;
	
	void getMapNames(AsyncCallback<GameListingInfo[]> callback) throws ServerException;

	void getJoinableGameNames(AsyncCallback<GameListingInfo[]> callback) throws ServerException;

	void getObservableGameNames(AsyncCallback<GameListingInfo[]> callback) throws ServerException;

	void createGame(String input, GameSettings settings, Long mapId, AsyncCallback<GameListingInfo> callback) throws ServerException;

	void getGameListingById(Long id, AsyncCallback<GameListingInfo> callback) throws ServerException;

	void getPositionsByGameId(Long id, AsyncCallback<GamePlayInfo> callback) throws ServerException;

	void moveMarker(Long gameId, Integer sourceRow, Integer sourceColumn, Integer destRow, Integer destColumn,
			String newImageSource, AsyncCallback<GamePlayInfo> callback) throws ServerException;
	
	void getLoginUrlIfNeeded(String returnUrl, AsyncCallback<String> callback);
	
	void attemptToJoinGame(Long id, AsyncCallback<GameListingInfo> callback) throws ServerException;
}
