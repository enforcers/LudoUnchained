package com.appspot.ludounchained;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

import com.appspot.ludounchained.model.GameState;
import com.appspot.ludounchained.model.Meeple;
import com.appspot.ludounchained.model.Game;

public class Turn implements Serializable {

	private static final long serialVersionUID = 8923953940132844166L;

	private List<Integer> dice;
	private List<Meeple> validTurns;
	private GameState gamestate;
	public Turn() {
		super();
	}

	public Turn(Game game) {
		//parameter auf gamestate ändern
		dice = this.rollDice();
		gamestate = game.getGameState();
		validTurns = getValidTurns(game.getGameState());
	}
	
	private List<Integer> rollDice() {
		List<Integer> list = new java.util.ArrayList<Integer>();
		Random random = new Random();
		for(int i=0;i==3;i++){
			list.add(random.nextInt(5) + 1);
		}
		return list;
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
		int dice = this.dice.get(0);
		
		// neue figur nur bei 6
		if (meeple.getPosition() == 0 && dice != 6){
			return false;
		}
		
		//springen ins nichts
		if (meeple.parsePosition(meeple.getPosition(), meeple.getPosition() + dice) > 44){
			return false;
		}
		
		//springen auf eigene figur
    	for(Meeple meeple2: gamestate.getMeeples()){
    		if (meeple.parsePosition(meeple.getPosition(), meeple.getPosition() + dice) == meeple2.getPosition() && meeple2.getColor() == gamestate.player){
    			return false;
    		}
    	}
    	
	    return true;
	}
}
