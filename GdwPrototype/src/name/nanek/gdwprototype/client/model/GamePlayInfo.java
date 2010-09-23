package name.nanek.gdwprototype.client.model;

import java.io.Serializable;
import java.util.Map;

import name.nanek.gdwprototype.shared.model.Marker;
import name.nanek.gdwprototype.shared.model.Position;

/**
 * Contains information needed to update a game board for a certain turn of play.
 * 
 * @author Lance Nanek
 *
 */
public class GamePlayInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	public Map<Position, Marker> positions;
	
	public boolean isUsersTurn;
	
	public Player playingAs;
	
	public boolean needsSecondPlayer;
	
	public Player winner;
	
	public boolean ended;
	
	public int moveCount;
	
	public boolean unitDiedLastTurn;
	
	public boolean carrotEatenLastTurn;
	
	public Player currentPlayersTurn;
	
	private GamePlayInfo() {
	}

	public GamePlayInfo(Map<Position, Marker> positions, boolean isUsersTurn, Player playingAs, boolean needsSecondPlayer, 
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
