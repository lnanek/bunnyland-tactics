package name.nanek.gdwprototype.shared.model;

import java.io.Serializable;

import javax.persistence.Id;

import name.nanek.gdwprototype.shared.model.support.StringUtil;

import com.google.appengine.api.users.User;

/**
 * A game or map.
 * 
 * @author Lance Nanek
 *
 */
public class Game implements Serializable, Comparable<Game> {
	private static final long serialVersionUID = 1L;

	@Id private Long id;

	private String name;
	
	private boolean map;

	private int moveNumber = 0;

	private String firstPlayerUserId;
	
	private String creatorNickname;
	
	private String secondPlayerUserId;
	
	private Player currentPlayersTurn = Player.ONE;
	
	private boolean ended;
	
	private Player winner;
	
	private boolean unitDiedLastTurn;
	
	private boolean carrotEatenLastTurn;
	
	private int boardWidth = 8;

	private int boardHeight = 8;

	public int carrotGenerationPeriod = 3;

	public int turnsUntilNextCarrotGenerated = carrotGenerationPeriod;	
	
	public Game() {
	}
	
	public Game(String name, User user) {
		setCreatorNickname(user.getNickname());
		setName(name);
		setFirstPlayerUserId(user.getUserId());
	}
	
	public int getBoardWidth() {
		return boardWidth;
	}

	public void setBoardWidth(int boardWidth) {
		this.boardWidth = boardWidth;
	}

	public int getBoardHeight() {
		return boardHeight;
	}

	public void setBoardHeight(int boardHeight) {
		this.boardHeight = boardHeight;
	}
	
	public boolean isEnded() {
		return ended;
	}

	public boolean isMap() {
		return map;
	}

	public void setMap(boolean map) {
		this.map = map;
	}

	public Player getWinner() {
		return winner;
	}

	public void setWinner(Player winner) {
		this.winner = winner;
	}

	public void setEnded(boolean ended) {
		this.ended = ended;
	}

	public Player getCurrentPlayersTurn() {
		return currentPlayersTurn;
	}

	public void setCurrentPlayersTurn(Player currentUsersTurn) {
		this.currentPlayersTurn = currentUsersTurn;
	}

	public String getFirstPlayerUserId() {
		return firstPlayerUserId;
	}

	public void setFirstPlayerUserId(String firstPlayerUserId) {
		this.firstPlayerUserId = firstPlayerUserId;
	}

	public String getSecondPlayerUserId() {
		return secondPlayerUserId;
	}

	public void setSecondPlayerUserId(String secondPlayerUserId) {
		this.secondPlayerUserId = secondPlayerUserId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int incrementMoveCount() {
		return ++moveNumber;
	}

	public int getMoveCount() {
		return moveNumber;
	}

	public void setNextUsersTurn() {
		currentPlayersTurn = Player.other(currentPlayersTurn);
	}

	public boolean isUnitDiedLastTurn() {
		return unitDiedLastTurn;
	}

	public void setUnitDiedLastTurn(boolean unitDiedLastTurn) {
		this.unitDiedLastTurn = unitDiedLastTurn;
	}

	public boolean isCarrotEatenLastTurn() {
		return carrotEatenLastTurn;
	}

	public void setCarrotEatenLastTurn(boolean carrotEatenLastTurn) {
		this.carrotEatenLastTurn = carrotEatenLastTurn;
	}

	public String getCreatorNickname() {
		return creatorNickname;
	}

	public void setCreatorNickname(String creatorNickname) {
		this.creatorNickname = creatorNickname;
	}
	
	public Long getId() {
		return id;
	}

	public String getLinkName() {
		if (null == name) {
			return null;
		}
		return name.replace(' ', '_');
	}

	public String getDisplayName(boolean includeAuthor) {
		String gameName;
		if ( StringUtil.nullOrEmpty(name) ) {
			gameName = Long.toString(id);
		} else {
			gameName = '"' + name + '"';
		}
		
		if ( includeAuthor ) {
			if ( !StringUtil.nullOrEmpty(creatorNickname) ) {
				gameName += " (by " + StringUtil.escapeHtml(creatorNickname) + ")";
			}
		}
		return gameName;
	}

	@Override
	public int compareTo(Game o) {
		if ( null == id || null == o.id ) {
			throw new IllegalStateException("Comparison needs ID set.");
		}
		
		return id.compareTo(o.id);
	}
	
}
