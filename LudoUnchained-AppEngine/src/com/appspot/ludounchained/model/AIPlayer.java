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
			
			Meeple meeple = null;

			// Pick of first meeple in queue, simple AI
			if (turn.getValidTurns().size() > 0)
				meeple = turn.getValidTurns().get(0);

			AIRoll = turn.getRoll();
			turn.execute(meeple);

			turns.add(turn);
		} while (AIRoll == 6);
	}
	
	public List<Turn> getTurns() {
		return turns;
	}
}
