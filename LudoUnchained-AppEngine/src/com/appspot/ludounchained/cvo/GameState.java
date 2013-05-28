package com.appspot.ludounchained.cvo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.Key;

public class GameState implements Serializable {

	private static final long serialVersionUID = -6007318243480887126L;

	private Key gameStateId;
	List<Field> fields;
	

	public GameState() {
		super();
	}
	
	public GameState(com.appspot.ludounchained.model.GameState gameState) {
		if (gameState != null) {
			gameStateId = gameState.getGameStateId();
			
			fields = new ArrayList<Field>();
			for (com.appspot.ludounchained.model.Field obj : gameState.getFields()) {
				fields.add(obj.getCVO());
			}
		}
	}
	
	public Key getGameStateId() {
		return gameStateId;
	}
	
	public List<Field> getFields() {
		return fields;
	}
}
