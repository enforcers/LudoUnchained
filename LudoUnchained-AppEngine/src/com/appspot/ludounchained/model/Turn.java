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
		
		boolean result = false;
		
		// neue figur nur bei 6
		if (meeple.getPosition() == 0 && roll != 6){
			return false;
		}
		
		//springen ins nichts
		if (meeple.simulateMoveBy(roll) <= 44) {
			result = true;
		} else {
			return false;
		}

		//springen auf eigene figur
    	for(Meeple meeple2: gameState.getMeeples()){
    		if (meeple2.getColor() == gameState.getTurnColor()) {
    			if (meeple.simulateMoveBy(roll) == meeple2.getPosition()
        				&& meeple.getPosition() != 0) {
    				result = false;
    			}

        		if (meeple.getPosition() == 0 && roll == 6) {
        			switch (gameState.getTurnColor()) {
	    				case RED:
	    					if (meeple2.getPosition() == 1)
	    						return false;
	    					break;
	    				case BLUE:
	    					if (meeple2.getPosition() == 11)
	    						return false;
	    					break;
	    				case GREEN:
	    					if (meeple2.getPosition() == 21)
	    						return false;
	    					break;
	    				case YELLOW:
	    					if (meeple2.getPosition() == 31)
	    						return false;
	    					break;
        			}
        		}
    		}
    	}
    	
	    return result;
	}
	
	public /*EVENT TYP ENUM*/ void execute() {
		// TODO Implementierung
		// gibt event typ als enum zurück
		// z.b. erfolgreich ausgeführt, spieler geschlagen, zieht aus haus, zieht ins ziel haus
		// evtl. mit verweis auf farbe
		// dafür muss Turn objekt persistiert werden (verweis auf ID bei execute() in controllerendpoint)
		
		
	}
	
	public com.appspot.ludounchained.cvo.Turn getCVO() {
		return new com.appspot.ludounchained.cvo.Turn(this);
	}
}
