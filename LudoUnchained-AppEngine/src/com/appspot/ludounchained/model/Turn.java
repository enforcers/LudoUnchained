package com.appspot.ludounchained.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.persistence.Basic;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.datanucleus.annotations.Unowned;

@Entity
public class Turn implements Serializable {

	private static final long serialVersionUID = 8923953940132844166L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key id;

	@ElementCollection
	private List<Integer> dice;

	@Basic(fetch = FetchType.EAGER) @Unowned private List<Meeple> validTurns;
	@Basic(fetch = FetchType.EAGER) @Unowned private GameState gameState;

	public Turn() {
		super();
	}

	public Turn(GameState gameState) {
		this.gameState = gameState;

		dice = new ArrayList<Integer>();
		validTurns = new ArrayList<Meeple>();
		calculateValidTurns();
	}
	
	public Key getId() {
		return id;
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
	
	public /*EVENT TYP ENUM*/ void execute(long meepleId) {
		// TODO Implementierung
		// gibt event typ als enum zurück
		// z.b. erfolgreich ausgeführt, spieler geschlagen, zieht aus haus, zieht ins ziel haus
		// evtl. mit verweis auf farbe
		// dafür muss Turn objekt persistiert werden (verweis auf ID bei execute() in controllerendpoint)
		
		for (Meeple m : gameState.getMeeples()) {
			if (m.getId().getId() == meepleId) {
				// Figur bewegen
				if (m.getPosition() == 0 && getRoll() == 6)
					m.moveFromHome();
				else
					m.moveBy(getRoll());
				
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
	
	public int getRoll() {
		return getDice().get(getDice().size() - 1);
	}
	
	public com.appspot.ludounchained.cvo.Turn getCVO() {
		return new com.appspot.ludounchained.cvo.Turn(this);
	}
}
