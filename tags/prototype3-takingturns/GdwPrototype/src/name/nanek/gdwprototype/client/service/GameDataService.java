package name.nanek.gdwprototype.client.service;

import name.nanek.gdwprototype.client.model.GamePlayInfo;
import name.nanek.gdwprototype.client.model.GameListingInfo;
import name.nanek.gdwprototype.client.model.PositionInfo;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("gameData")
public interface GameDataService extends RemoteService {
	GameListingInfo[] getGameNames(boolean joinable) throws IllegalArgumentException;

	GameListingInfo getGameListingById(Long id) throws IllegalArgumentException;

	GamePlayInfo getPositionsByGameId(Long id) throws IllegalArgumentException;

	GameListingInfo createGame(String name) throws IllegalArgumentException;

	GamePlayInfo moveMarker(Long gameId, Integer sourceRow, Integer sourceColumn, Integer destRow,
			Integer destColumn, String newImageSource) throws IllegalArgumentException;
	
	String getLoginUrlIfNeeded(String returnUrl);
	
	GameListingInfo attemptToJoinGame(Long id) throws IllegalArgumentException;

}
