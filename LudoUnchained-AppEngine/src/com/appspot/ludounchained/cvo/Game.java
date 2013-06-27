package com.appspot.ludounchained.cvo;


/**
 * @author vfast, awakup
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.appspot.ludounchained.model.Game.State;
import com.appspot.ludounchained.util.PlayerColor;
import com.google.appengine.api.datastore.Key;

public class Game implements Serializable {
	
	private static final long serialVersionUID = -2843298703124170701L;
	
	private Key gameId;
	private User redPlayer;
	private User bluePlayer;
	private User greenPlayer;
	private User yellowPlayer;

	List<User> spectators;
	private GameState gameState;
	private State state;

	public Game() {
		super();
	}
	
	public Game(com.appspot.ludounchained.model.Game game) {
		if (game != null) {
			gameId = game.getGameId();
			state = game.getState();

			if (game.getRedPlayer() != null)
				redPlayer = new User(game.getRedPlayer());
			if (game.getBluePlayer() != null)
				bluePlayer = new User(game.getBluePlayer());
			if (game.getGreenPlayer() != null)
				greenPlayer = new User(game.getGreenPlayer());
			if (game.getYellowPlayer() != null)
				yellowPlayer = new User(game.getYellowPlayer());
			
			spectators = new ArrayList<User>();
			if (game.getSpectators() != null) {
				for (com.appspot.ludounchained.model.User obj : game.getSpectators()) {
					spectators.add(new User(obj));
				}
			}
		}
	} 
	
	public Key getGameId() {
		return gameId;
	}
	
	public User getPlayer(PlayerColor color) {
		switch (color) {
			case RED    : return getRedPlayer();
			case BLUE   : return getBluePlayer();
			case GREEN  : return getGreenPlayer();
			case YELLOW : return getYellowPlayer();
		}
		
		return null;
	}
	
	public int getPlayerCount() {
		return getPlayers().size();
	}
	
	public List<User> getPlayers() {
		List<User> result = new ArrayList<User>();

		if (getRedPlayer() != null)
			result.add(getRedPlayer());
		
		if (getBluePlayer() != null)
			result.add(getBluePlayer());
		
		if (getGreenPlayer() != null)
			result.add(getGreenPlayer());
		
		if (getYellowPlayer() != null)
			result.add(getYellowPlayer());
		
		return result;
	}

	public List<User> getSpectators() {
		return spectators;
	}
	
	public GameState getGameState() {
		return gameState;
	}
	
	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}
	
	public User getRedPlayer() {
		return redPlayer;
	}
	
	public User getBluePlayer() {
		return bluePlayer;
	}
	
	public User getGreenPlayer() {
		return greenPlayer;
	}
	
	public User getYellowPlayer() {
		return yellowPlayer;
	}
	
	public State getState() {
		return state;
	}
	
}