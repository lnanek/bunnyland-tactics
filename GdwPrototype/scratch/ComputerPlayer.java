package name.nanek.gdwprototype.server;

import java.util.Set;

import name.nanek.gdwprototype.shared.model.Position;

public class ComputerPlayer {

	public static enum Advantage {
		STOMPS_ENEMY_HOME(Integer.MAX_VALUE),
		STOMPS_ENEMY_STOMPER(1000),
		STOMPS_ENEMY_SCOUT(900),
		GETS_CARROT(800),
		PROTECTS_ENDANGERED_PIECE(700),
		RETREATS_ENDANGERED_PIECE(600),
		PROTECTS_UNPROTECTED_PIECE(500),
		MOVES_TOWARD_REMEMBERED_CARROT(400),
		MOVES_STOMPER_TOWARD_REMEMBERED_ENEMY_HOME(300),
		EXPLORES_UNKNOWN_TERRITORY(200);
		
		int value;
		
		Advantage(int value) {
			this.value = value;
		}
	}
	
	public static class Move {
		
		Position source;
		
		Position dest;
		
		Set<Advantage> advantages;
		
	}
	
	Set<Position> rememberedCarrots;
	
	public void makeMove() {
		
		//Determine which player's turn it is.
		
		//Find all stompable areas.
		
		//Stomp enemies in priority order: home, stomper, scount.
		
		//Find all reachable carrots.
		
		//Take carrot.
		
		//Find all areas stompable by enemies.
		
		//Find all safe areas.
		
		//Retreat any units possible.
		
		//Find all remembered carrots.
		
		//Move toward a remembered carrot.
		
		//Find all safe moves.
		
		//Find all never seen squares.
		
		//Move toward a never seen square as long as it is safe.
		
		//Find all scouts not protected by a stomper.
		
		//Move stomper to protect scout.
		
		
	}
	
}
