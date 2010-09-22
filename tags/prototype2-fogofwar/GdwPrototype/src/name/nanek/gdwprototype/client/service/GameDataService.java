package name.nanek.gdwprototype.client.service;

import name.nanek.gdwprototype.client.model.GameListing;
import name.nanek.gdwprototype.client.model.PositionInfo;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("gameData")
public interface GameDataService extends RemoteService {
	GameListing[] getGameNames() throws IllegalArgumentException;
	GameListing getGameListingById(Long id) throws IllegalArgumentException;
	PositionInfo[] getPositionsByGameId(Long id) throws IllegalArgumentException;
	boolean createGame(String name) throws IllegalArgumentException;

	PositionInfo[] moveMarker(Long gameId, Integer sourceRow, Integer sourceColumn, 
			Integer destRow, Integer destColumn, String newImageSource) throws IllegalArgumentException;
}