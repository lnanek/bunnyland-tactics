package name.nanek.gdwprototype.client.service;

import name.nanek.gdwprototype.client.model.GamePlayInfo;
import name.nanek.gdwprototype.client.model.GameListingInfo;
import name.nanek.gdwprototype.client.model.PositionInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GameDataService</code>.
 */
public interface GameDataServiceAsync {
	void getGameNames(boolean joinable, AsyncCallback<GameListingInfo[]> callback) throws IllegalArgumentException;

	void createGame(String input, AsyncCallback<GameListingInfo> callback) throws IllegalArgumentException;

	void getGameListingById(Long id, AsyncCallback<GameListingInfo> callback) throws IllegalArgumentException;

	void getPositionsByGameId(Long id, AsyncCallback<GamePlayInfo> callback) throws IllegalArgumentException;

	void moveMarker(Long gameId, Integer sourceRow, Integer sourceColumn, Integer destRow, Integer destColumn,
			String newImageSource, AsyncCallback<GamePlayInfo> callback) throws IllegalArgumentException;
	
	void getLoginUrlIfNeeded(String returnUrl, AsyncCallback<String> callback);
	
	void attemptToJoinGame(Long id, AsyncCallback<GameListingInfo> callback) throws IllegalArgumentException;
}
