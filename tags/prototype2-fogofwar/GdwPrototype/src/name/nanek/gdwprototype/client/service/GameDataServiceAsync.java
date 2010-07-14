package name.nanek.gdwprototype.client.service;

import java.util.List;

import name.nanek.gdwprototype.client.model.GameListing;
import name.nanek.gdwprototype.client.model.PositionInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GameDataService</code>.
 */
public interface GameDataServiceAsync {
	void getGameNames(AsyncCallback<GameListing[]> callback) throws IllegalArgumentException;
	void createGame(String input, AsyncCallback<Boolean> callback) throws IllegalArgumentException;
	void getGameListingById(Long id, AsyncCallback<GameListing> callback) throws IllegalArgumentException;
	void getPositionsByGameId(Long id, AsyncCallback<PositionInfo[]> callback) throws IllegalArgumentException;
	void moveMarker(Long gameId, Integer sourceRow, Integer sourceColumn, 
			Integer destRow, Integer destColumn, String newImageSource,
			AsyncCallback<PositionInfo[]> callback) throws IllegalArgumentException;
}
