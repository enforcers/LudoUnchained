package com.appspot.ludounchained.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.appspot.ludounchained.controllerEndpoint.model.Field;
import com.appspot.ludounchained.controllerEndpoint.model.GameState;

public class GameStateDrawer {
	public enum PlayerColor { RED, BLUE, GREEN, YELLOW }
	
	private GameState gameState;

	private List<Integer> gameFieldMap;
	private HashMap<PlayerColor, List<Integer>> playerFieldMap;
	private Field[] fields = new Field[121];
	private Field[][] fieldMap;
	
	public GameStateDrawer(GameState gameState) {
		this.gameState = gameState;
		this.gameFieldMap = Arrays.asList(44,45,46,47,48,37,26,15,4,5,6,17,28,39,50,51,52,53,54,65,76,75,74,73,72,83,94,105,116,115,114,103,92,81,70,69,68,67,66,55);

		this.playerFieldMap = new HashMap<PlayerColor, List<Integer>>();
		this.playerFieldMap.put(PlayerColor.RED, Arrays.asList(0,1,11,12,56,57,58,59));
		this.playerFieldMap.put(PlayerColor.BLUE, Arrays.asList(9,10,20,21,16,27,38,49));
		this.playerFieldMap.put(PlayerColor.GREEN, Arrays.asList(108,109,119,120,64,63,62,61));
		this.playerFieldMap.put(PlayerColor.YELLOW, Arrays.asList(99,100,110,111,104,93,82,71));
		
		for (Field field : gameState.getFields()) {
			int position = field.getPosition();
			PlayerColor color = PlayerColor.valueOf(field.getColor());
			
			if (position == 0) {
				// im Haus
				position = getNextEmptyStartField(color);
			} else if (position > 0 && position <= 40) {
				// im Feld
				position = this.gameFieldMap.get(position);
			} else if (position > 40 && position <= 44) {
				// im Ziel
				position = getPlayerEndFieldMap(color).get(position - 40);
			} else {
				break;
			}
			
			fields[position] = field; 
		}
	}
	
	public List<Integer> getGameFieldMap() {
		return gameFieldMap;
	}
	
	public List<Integer> getPlayerFieldMap(PlayerColor color) {
		return playerFieldMap.get(color);
	}
	
	public int getNextEmptyStartField(PlayerColor color) {
		List<Integer> startFields = getPlayerStartFieldMap(color);
		for (int pos : startFields) {
			if (this.fields[pos] == null)
				return pos;
		}
		
		return 0;
	}
	
	public List<Integer> getPlayerStartFieldMap(PlayerColor color) {
		return playerFieldMap.get(color).subList(0, 4);
	}
	
	public List<Integer> getPlayerEndFieldMap(PlayerColor color) {
		return playerFieldMap.get(color).subList(4, 8);
	}
	
	public GameState getGameState() {
		return gameState;
	}
	
	public Field[][] getFieldMap() {
		return fieldMap;
	}
	
	public Field[] getFields() {
		return fields;
	}
	
	public Field getField(int position) {
		return fields[position];
	}
	
}
