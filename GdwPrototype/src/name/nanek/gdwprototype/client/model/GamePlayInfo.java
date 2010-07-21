package name.nanek.gdwprototype.client.model;

import java.io.Serializable;

import name.nanek.gdwprototype.shared.model.GameSettings;
import name.nanek.gdwprototype.shared.model.Marker;
import name.nanek.gdwprototype.shared.model.Position;

public class GamePlayInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	public Position[] positions;
	
	public boolean isUsersTurn;
	
	public Player playingAs;
	
	public boolean needsSecondPlayer;
	
	public Player winner;
	
	public boolean ended;
	
	public int boardWidth;
	
	public int boardHeight;
	
	public Marker[] markers;

	public boolean isBuildingMap;
	
	private GamePlayInfo() {
	}

	public GamePlayInfo(Position[] positions, boolean isUsersTurn, Player playingAs, boolean needsSecondPlayer, 
			int boardHeight, int boardWidth, Marker[] markers, Player winner, boolean isBuildingMap, boolean ended) {
		this.positions = positions;
		this.isUsersTurn = isUsersTurn;
		this.playingAs = playingAs;
		this.needsSecondPlayer = needsSecondPlayer;
		this.boardWidth = boardWidth;
		this.boardHeight = boardHeight;
		this.markers = markers;
		this.winner = winner;
		this.isBuildingMap = isBuildingMap;
		this.ended = ended;
	}

}
