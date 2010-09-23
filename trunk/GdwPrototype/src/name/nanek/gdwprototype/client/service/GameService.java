package name.nanek.gdwprototype.client.service;

import name.nanek.gdwprototype.client.model.GameDisplayInfo;
import name.nanek.gdwprototype.client.model.GameListing;
import name.nanek.gdwprototype.client.model.GamePlayInfo;
import name.nanek.gdwprototype.client.model.Player;
import name.nanek.gdwprototype.shared.exceptions.GameException;
import name.nanek.gdwprototype.shared.model.GameSettings;
import name.nanek.gdwprototype.shared.model.Marker;

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

	GameListing[] getMapNames() throws GameException;

	GameListing[] getJoinableGameNames() throws GameException;

	GameListing[] getObservableGameNames() throws GameException;
	
	GameListing getGameListingById(Long id) throws GameException;

	GamePlayInfo getPositionsByGameId(Long id) throws GameException;

	GameDisplayInfo getDisplayInfo(Long id) throws GameException;

	GameListing createGameOrMap(String name, GameSettings settings, Marker[] markers, Long mapId) throws GameException;

	GamePlayInfo moveMarker(Long gameId, Integer sourceRow, Integer sourceColumn, Integer destRow,
			Integer destColumn, Long markerId) throws GameException;
	
	String getLoginUrlIfNeeded(String returnUrl) throws GameException;
	
	GameListing attemptToJoinGame(Long id) throws GameException;

}
