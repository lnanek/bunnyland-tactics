package name.nanek.gdwprototype.client.service;

import name.nanek.gdwprototype.client.model.GameDisplayInfo;
import name.nanek.gdwprototype.client.model.GameUpdateInfo;
import name.nanek.gdwprototype.shared.exceptions.GameException;
import name.nanek.gdwprototype.shared.model.Game;
import name.nanek.gdwprototype.shared.model.Marker;
import name.nanek.gdwprototype.shared.model.Player;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Service the remote server implements for clients to play games.
 * 
 * @author Lance Nanek
 *
 */
@RemoteServiceRelativePath("game")
public interface GameService extends RemoteService {

	void surrender(Long gameId, Player surrenderer) throws GameException;

	void publishMap(Long mapId) throws GameException;

	Game[] getMapNames() throws GameException;

	Game[] getJoinableGameNames() throws GameException;

	Game[] getObservableGameNames() throws GameException;
	
	Game getGameListingById(Long id) throws GameException;

	GameUpdateInfo getPositionsByGameId(Long id) throws GameException;

	GameDisplayInfo getDisplayInfo(Long id) throws GameException;

	GameUpdateInfo moveMarker(Long gameId, Integer sourceRow, Integer sourceColumn, Integer destRow,
			Integer destColumn, Long markerId) throws GameException;
	
	String getLoginUrlIfNeeded(String returnUrl) throws GameException;
	
	Game attemptToJoinGame(Long id) throws GameException;

	Game createMap(String name, Integer carrotGenerationPeriod, Integer boardWidth, Integer boardHeight,
			Marker[] markers) throws GameException;

	Game createGame(String name, Long mapId) throws GameException;

}
