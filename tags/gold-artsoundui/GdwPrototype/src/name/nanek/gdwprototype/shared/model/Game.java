package name.nanek.gdwprototype.shared.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import name.nanek.gdwprototype.client.model.GameListing;
import name.nanek.gdwprototype.client.model.Player;

import org.datanucleus.jpa.annotations.Extension;

@Entity
public class Game implements Serializable {
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Extension(vendorName="datanucleus", key="gae.encoded-pk", value="true")
    private String encodedKey;

    @Extension(vendorName="datanucleus", key="gae.pk-id", value="true")
    private Long keyId;

	private String name;

	// @OneToMany(mappedBy="game", fetch=FetchType.EAGER)
	// @OneToMany(mappedBy="game")
	@OneToMany(cascade = CascadeType.ALL)   
	private Set<Position> positions;
	
	private boolean startingMap;

	@Column(nullable = false)
	private int moveNumber = 0;

	private String firstPlayerUserId;
	
	private String creatorNickname;
	
	private String secondPlayerUserId;
	
	private Player currentUsersTurn = Player.ONE;
	
	private boolean ended;
	
	private Player winner;
	
	private boolean unitDiedLastTurn;
	
	private boolean carrotEatenLastTurn;

	//@OneToOne (mappedBy = "game", fetch = FetchType.EAGER, cascade = 
    //{CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
	@OneToOne(cascade = CascadeType.ALL)
	private GameSettings settings;
	
	public boolean isEnded() {
		return ended;
	}

	public boolean isStartingMap() {
		return startingMap;
	}

	public void setStartingMap(boolean startingMap) {
		this.startingMap = startingMap;
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
		return new GameListing(name, keyId, startingMap, creatorNickname);
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
