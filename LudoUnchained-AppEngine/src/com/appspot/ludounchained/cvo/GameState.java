package com.appspot.ludounchained.cvo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.Key;

public class GameState implements Serializable {

	private static final long serialVersionUID = -6007318243480887126L;

	private Key gameStateId;
	List<Meeple> meeples;
	

	public GameState() {
		super();
	}
	
	public GameState(com.appspot.ludounchained.model.GameState gameState) {
		if (gameState != null) {
			gameStateId = gameState.getGameStateId();
			
			meeples = new ArrayList<Meeple>();
			for (com.appspot.ludounchained.model.Meeple obj : gameState.getMeeples()) {
				meeples.add(obj.getCVO());
			}
		}
	}
	
	public Key getGameStateId() {
		return gameStateId;
	}
	
	public List<Meeple> getMeeples() {
		return meeples;
	}
}
