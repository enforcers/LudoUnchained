package com.appspot.ludounchained.model;

import java.util.ArrayList;
import java.util.List;

public class AIPlayer {
	private GameState gameState;
	private List<Turn> turns = new ArrayList<Turn>();
	
	public AIPlayer(GameState gameState) {
		this.gameState = gameState;
	}

	public void play() {
		int AIRoll = 0;
		
		do {
			Turn turn = new Turn(gameState);
			turns.add(turn);
			
			if (turn.getValidTurns().size() > 0) {
				AIRoll = turn.getDice().get(turn.getDice().size() - 1);
				
				turn.execute(turn.getValidTurns().get(0).getId().getId());
			}

		} while (AIRoll == 6);
	}
	
	public List<Turn> getTurns() {
		return turns;
	}
}
