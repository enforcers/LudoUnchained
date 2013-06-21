package com.appspot.ludounchained.model;

import java.util.ArrayList;
import java.util.List;

import com.appspot.ludounchained.util.PlayerColor;

public class AIPlayer {
	private GameState gameState;
	private List<Turn> turns = new ArrayList<Turn>();
	
	public AIPlayer(GameState gameState) {
		this.gameState = gameState;
	}

	public void play() {
		gameState.setTurnColor(PlayerColor.BLUE);
		
		int AIRoll = 0;
		
		do {
			Turn turn = new Turn(gameState);
			turns.add(turn);
	
			if (turn.getValidTurns().size() > 0) {
				AIRoll = turn.getDice().get(turn.getDice().size() - 1);
	
				for (Meeple m : gameState.getMeeples()) {
					if (m.getId().equals(turn.getValidTurns().get(0).getId())) {
						if (m.getPosition() == 0 && AIRoll == 6)
							m.moveFromHome();
						else
							m.moveBy(AIRoll);
						
						// Spieler schlagen
						for (Meeple mo : gameState.getMeeples()) {
							if (mo.getColor() != m.getColor()) {
								if (mo.getPosition() == m.getPosition()) {
									mo.moveToHome();
								}
							}
						}
					}
				}
				
			}
		} while (AIRoll == 6);

		gameState.setTurnColor(PlayerColor.RED);
	}
	
	public List<Turn> getTurns() {
		return turns;
	}
}
