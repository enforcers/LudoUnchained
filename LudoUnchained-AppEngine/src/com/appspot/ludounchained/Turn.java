package com.appspot.ludounchained;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

import com.appspot.ludounchained.model.GameState;
import com.appspot.ludounchained.model.Meeple;
import com.appspot.ludounchained.model.Game;

public class Turn implements Serializable {

	private static final long serialVersionUID = 8923953940132844166L;

	private int dice;
	private List<Meeple> validTurns;
	private GameState gamestate;
	public Turn() {
		super();
	}

	public Turn(Game game) {
		dice = this.rollDice();
		gamestate = game.getGameState();
		validTurns = getValidTurns(game.getGameState());
	}
	
	private int rollDice() {
		Random random = new Random();
		return random.nextInt(5) + 1;
	}
	
	public List<Meeple> getValidTurns(GameState gamestate) {
		
		for(Meeple meeple: gamestate.getMeeples()){
			if(meeple.getColor()==gamestate.player){
				if (isValid(meeple)){
					validTurns.add(meeple);
				}	
			}		
		}
		return validTurns;
	}
	
	private boolean isValid(Meeple meeple){
		switch(dice){ 
	        case 6: 
	            if (meeple.getPosition()==0);
	            return true;
	        default: 
	        	for(Meeple meeple2: gamestate.getMeeples()){
	        		if (meeple.getPosition() + dice == meeple2.getPosition() && meeple2.getColor() == gamestate.player){
	        			return false;
	        		}
	        	}
	        	return true;
        } 
	}
}
