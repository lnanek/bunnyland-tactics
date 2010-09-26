package name.nanek.gdwprototype.client.model;

import java.io.Serializable;

import name.nanek.gdwprototype.shared.model.Marker;
import name.nanek.gdwprototype.shared.model.Player;

/**
 * Contains information needed to update a game board for a certain turn of play.
 * 
 * @author Lance Nanek
 *
 */
public class GameUpdateInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	//TODO consider sending long IDs and looking up from markers sent to client with game display info to improve performance
	//TODO consider using a map keyed by x, y, layer instead of an array, might be quicker to send than a sparse array like this
	public Marker[][][] positions;
	
	//TODO a lot of the below data duplicates fields on Game
	//maybe send game itself instead, or make this a persisted child of game instead of having the needed properties on game itself
	public boolean isUsersTurn;
	
	public Player playingAs;
	
	public boolean needsSecondPlayer;
	
	public Player winner;
	
	public boolean ended;
	
	public int moveCount;
	
	public boolean unitDiedLastTurn;
	
	public boolean carrotEatenLastTurn;
	
	public Player currentPlayersTurn;
	
	@SuppressWarnings("unused") //Used by GWT serialization.
	private GameUpdateInfo() {
	}

	public GameUpdateInfo(Marker[][][] positions, boolean isUsersTurn, Player playingAs, boolean needsSecondPlayer, 
			Player winner, boolean ended,
			int moveCount, boolean unitDiedLastTurn, boolean carrotEatenLastTurn, Player currentPlayersTurn) {
		this.positions = positions;
		this.isUsersTurn = isUsersTurn;
		this.playingAs = playingAs;
		this.needsSecondPlayer = needsSecondPlayer;
		this.winner = winner;
		this.ended = ended;
		this.moveCount = moveCount;
		this.unitDiedLastTurn = unitDiedLastTurn;
		this.carrotEatenLastTurn = carrotEatenLastTurn;
		this.currentPlayersTurn = currentPlayersTurn;
	}

}
