package name.nanek.gdwprototype.server;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import name.nanek.gdwprototype.client.GameListing;

import com.google.appengine.api.datastore.Key;

@Entity
public class Game {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Key key;
    
    @Column(nullable=false) 
    private String name;
    
    //@OneToMany(mappedBy="game", fetch=FetchType.EAGER)
    //@OneToMany(mappedBy="game")
    private Set<Position> positions;
    
    @Column(nullable=false) 
    private int moveNumber = 0;
    
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public GameListing getListing() {
		return new GameListing(name, key.getId());
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
}
