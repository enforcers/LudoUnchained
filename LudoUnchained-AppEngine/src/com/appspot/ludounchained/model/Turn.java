package com.appspot.ludounchained.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Turn implements Serializable {

	private static final long serialVersionUID = 8923953940132844166L;

	private List<Integer> dice;
	private List<Meeple> validTurns;
	private GameState gameState;
	public Turn() {
		super();
	}

	public Turn(GameState gameState) {
		this.gameState = gameState;

		dice = new ArrayList<Integer>();
		validTurns = new ArrayList<Meeple>();
		calculateValidTurns();
	}
	
	public List<Integer> getDice() {
		return dice;
	}
	
	public List<Meeple> getValidTurns() {
		return validTurns;
	}
	
	public GameState getGameState() {
		return gameState;
	}
	
	private int rollDice() {
		return new Random().nextInt(6) + 1;
	}
	
	private void calculateValidTurns() {
		int roll;
		int rollCount = 0;

		do {
			roll = rollDice();
			dice.add(roll);
			rollCount++;

			for(Meeple meeple: gameState.getMeeples()){
				if(meeple.getColor() == gameState.getTurnColor()){
					if (isValid(meeple, roll)){
						if (!validTurns.contains(meeple))
							validTurns.add(meeple);
					}	
				}		
			}
		
		} while (validTurns.size() == 0 && rollCount < 3);
	}
	
	private boolean isValid(Meeple meeple, int roll){
		
		// neue figur nur bei 6
		if (meeple.getPosition() == 0 && roll != 6){
			return false;
		}
		
		//springen ins nichts
		if (meeple.parsePosition(meeple.getPosition(), meeple.getPosition() + roll) > 44){
			return false;
		}
		
		//springen auf eigene figur
    	for(Meeple meeple2: gameState.getMeeples()){
    		if (meeple.parsePosition(meeple.getPosition(), meeple.getPosition() + roll) == meeple2.getPosition()
    				&& meeple2.getColor() == gameState.getTurnColor()){
    			return false;
    		}
    	}
    	
	    return true;
	}
	
	public com.appspot.ludounchained.cvo.Turn getCVO() {
		return new com.appspot.ludounchained.cvo.Turn(this);
	}
}
