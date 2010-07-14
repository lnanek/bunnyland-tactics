package name.nanek.gdwprototype.client.model;

import java.io.Serializable;

public class GamePlayInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	public PositionInfo[] positions;
	
	public boolean isUsersTurn;
	
	public Player playingAs;
	
	public boolean needsSecondPlayer;
	
	private GamePlayInfo() {
	}

	public GamePlayInfo(PositionInfo[] positions, boolean isUsersTurn, Player playingAs, boolean needsSecondPlayer) {
		this.positions = positions;
		this.isUsersTurn = isUsersTurn;
		this.playingAs = playingAs;
		this.needsSecondPlayer = needsSecondPlayer;
	}

}
