package name.nanek.gdwprototype.client.service;

import name.nanek.gdwprototype.client.model.GameListingInfo;
import name.nanek.gdwprototype.client.model.GamePlayInfo;
import name.nanek.gdwprototype.client.model.Player;
import name.nanek.gdwprototype.shared.exceptions.ServerException;
import name.nanek.gdwprototype.shared.model.GameSettings;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("gameData")
public interface GameDataService extends RemoteService {

	void surrender(Long gameId, Player surrenderer) throws ServerException;

	void publishMap(Long mapId) throws ServerException;

	GameListingInfo[] getMapNames() throws ServerException;

	GameListingInfo[] getJoinableGameNames() throws ServerException;

	GameListingInfo[] getObservableGameNames() throws ServerException;
	
	GameListingInfo getGameListingById(Long id) throws ServerException;

	GamePlayInfo getPositionsByGameId(Long id) throws ServerException;

	GameListingInfo createGame(String name, GameSettings settings, Long mapId) throws ServerException;

	GamePlayInfo moveMarker(Long gameId, Integer sourceRow, Integer sourceColumn, Integer destRow,
			Integer destColumn, String newImageSource) throws ServerException;
	
	String getLoginUrlIfNeeded(String returnUrl);
	
	GameListingInfo attemptToJoinGame(Long id) throws ServerException;

}
