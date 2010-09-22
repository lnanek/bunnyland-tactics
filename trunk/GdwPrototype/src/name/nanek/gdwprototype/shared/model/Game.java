package name.nanek.gdwprototype.shared.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import name.nanek.gdwprototype.client.model.GameListing;
import name.nanek.gdwprototype.client.model.Player;

import com.google.code.twig.annotation.Child;
import com.google.code.twig.annotation.Entity;
import com.google.code.twig.annotation.Id;
import com.google.code.twig.annotation.Key;

/**
 * A game or map.
 * 
 * @author Lance Nanek
 *
 */
//@Entity(allocateIdsBy=10)
public class Game implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id private Long keyId;

	@Child private Set<Position> positions = new HashSet<Position>();

	@Child private GameSettings settings;

	private String name;
	
	private boolean map;

	private int moveNumber = 0;

	private String firstPlayerUserId;
	
	private String creatorNickname;
	
	private String secondPlayerUserId;
	
	private Player currentUsersTurn = Player.ONE;
	
	private boolean ended;
	
	private Player winner;
	
	private boolean unitDiedLastTurn;
	
	private boolean carrotEatenLastTurn;
	
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

	public Player getCurrentUsersTurn() {
		return currentUsersTurn;
	}

	public void setCurrentUsersTurn(Player currentUsersTurn) {
		this.currentUsersTurn = currentUsersTurn;
	}

	public String getFirstPlayerUserId() {
		return firstPlayerUserId;
	}

	public GameSettings getSettings() {
		return settings;
	}

	public void setSettings(GameSettings settings) {
		this.settings = settings;
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

	public GameListing getListing() {
		return new GameListing(name, keyId, map, creatorNickname);
	}
	
	public void setKeyId(long keyId) {
		this.keyId = keyId;
	}

	public Set<Position> getPositions() {
		return positions;
	}

	public void setPositions(Set<Position> positions) {
		this.positions = positions;
	}

	public int incrementMoveCount() {
		return ++moveNumber;
	}

	public int getMoveCount() {
		return moveNumber;
	}

	public void setNextUsersTurn() {
		currentUsersTurn = Player.other(currentUsersTurn);
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
	
}
