package name.nanek.gdwprototype.shared.model;

import java.io.Serializable;

import javax.persistence.Id;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Parent;

/**
 * Settings for a game.
 * 
 * @author Lance Nanek
 *
 */
public class GameSettings implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id private Long keyId;

	@Parent Key<Game> game;

	private int boardWidth = 8;

	private int boardHeight = 8;
	
	public void setGame(Key<Game> game) {
		this.game = game;
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

	public GameSettings copy() {
		GameSettings copy = new GameSettings();
		copy.setBoardHeight(boardHeight);
		copy.setBoardWidth(boardWidth);
		return copy;
	}

	public void setKeyId(Long id) {
		this.keyId = id;
	}	
}
