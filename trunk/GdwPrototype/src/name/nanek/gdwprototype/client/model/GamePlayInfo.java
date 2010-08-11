package name.nanek.gdwprototype.client.model;

import java.io.Serializable;

import name.nanek.gdwprototype.shared.model.Position;

public class GamePlayInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	public Position[] positions;
	
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

	public GamePlayInfo(Position[] positions, boolean isUsersTurn, Player playingAs, boolean needsSecondPlayer, 
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
