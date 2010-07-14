package name.nanek.gdwprototype.server.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import name.nanek.gdwprototype.client.model.GameListingInfo;
import name.nanek.gdwprototype.client.model.Player;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.User;

@Entity
public class Game {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key key;

	@Column(nullable = false)
	private String name;

	// @OneToMany(mappedBy="game", fetch=FetchType.EAGER)
	// @OneToMany(mappedBy="game")
	private Set<Position> positions;

	@Column(nullable = false)
	private int moveNumber = 0;

	private String firstPlayerUserId;
	
	private String secondPlayerUserId;
	
	private Player currentUsersTurn = Player.ONE;
	
	private Boolean ended = true;
	
	public boolean isEnded() {
		return null == ended || ended;
	}

	public void setEnded(boolean ended) {
		this.ended = ended;
	}

	public boolean isUsersTurn(User user) {
		if ( null == user || null == currentUsersTurn ) {
			return false;
		}
		
		String userId = user.getUserId();
		switch ( currentUsersTurn ) {
			case ONE :
				return userId.equals(firstPlayerUserId);
			case TWO :
				return userId.equals(secondPlayerUserId);
		}
		
		throw new IllegalStateException("Can't determine the current user.");
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

	public GameListingInfo getListing() {
		return new GameListingInfo(name, key.getId());
	}

	public Set<Position> getPositions() {
		return positions;
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
}
