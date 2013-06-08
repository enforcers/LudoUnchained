package com.appspot.ludounchained.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import com.appspot.ludounchained.util.PlayerColor;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.datanucleus.annotations.Unowned;

@Entity
public class Game implements Serializable {

	private static final long serialVersionUID = -8472403885032549742L;
	
	public enum State { LOBBY, RUNNING, FINISHED };
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key gameId;

	@Unowned private User redPlayer;
	@Unowned private User bluePlayer;
	@Unowned private User greenPlayer;
	@Unowned private User yellowPlayer;
	@Unowned List<User> spectators;
	
	@OneToOne(cascade = CascadeType.ALL)
	private GameState gameState;
	
	@Enumerated(EnumType.STRING)
	private State state;
	
	@Enumerated(EnumType.STRING)
	private PlayerColor turn;

	public Game() {
		super();
	}
	
	public Game(User user) {
		turn = PlayerColor.RED;
		state = State.LOBBY;
		gameState = new GameState();
		setPlayer(PlayerColor.RED, user);
	} 
	
	public Key getGameId() {
		return gameId;
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
	
	public PlayerColor getPlayerColor(User user) {
		if (user.equals(getRedPlayer()))
			return PlayerColor.RED;
		
		if (user.equals(getBluePlayer()))
			return PlayerColor.BLUE;
		
		if (user.equals(getGreenPlayer()))
			return PlayerColor.GREEN;
		
		if (user.equals(getYellowPlayer()))
			return PlayerColor.YELLOW;
		
		return null;
	}
	
	public void setPlayer(PlayerColor color, User user) {
		if (color == null)
			return;

		switch (color) {
			case RED    : setRedPlayer(user); break;
			case BLUE   : setBluePlayer(user); break;
			case GREEN  : setGreenPlayer(user); break;
			case YELLOW : setYellowPlayer(user); break;
			default     : return;
		}
		
		if (user == null)
			gameState.removePlayerFields(color);
		else
			gameState.addPlayerFields(color);
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
/*				(redPlayer == null ? 0 : 1) +
				(bluePlayer == null ? 0 : 1) +
				(greenPlayer == null ? 0 : 1) +
				(yellowPlayer == null ? 0 : 1);*/
	}
	
	public List<User> getSpectators() {
		return spectators;
	}
	
	public GameState getGameState() {
		return gameState;
	}
	
	public void setGameState(State state) {
		this.state = state;
	}
	
	public void setSpectators(List<User> spectators) {
		this.spectators = spectators;
	}
	
	public void addSpectator(User user) {
		if (spectators == null)
			spectators = new ArrayList<User>();

		spectators.add(user);
	}
	
	public void removeSpectator(User user) {
		spectators = new ArrayList<User>();
		/*for (User spectator : spectators) {
			if (spectator.getUsername().equals(user.getUsername()))
				spectators.remove(spectator);
		}*/
	}
	
	public User getRedPlayer() {
		return redPlayer;
	}
	
	public void setRedPlayer(User user) {
		this.redPlayer = user;
	}
	
	public User getBluePlayer() {
		return bluePlayer;
	}
	
	public void setBluePlayer(User user) {
		this.bluePlayer = user;
	}
	
	public User getGreenPlayer() {
		return greenPlayer;
	}
	
	public void setGreenPlayer(User user) {
		this.greenPlayer = user;
	}
	
	public User getYellowPlayer() {
		return yellowPlayer;
	}
	
	public void setYellowPlayer(User user) {
		this.yellowPlayer = user;
	}
	
	public State getState() {
		return state;
	}
	
	public void setState(State state) {
		this.state = state;
	}
	
	public PlayerColor getTurn() {
		return turn;
	}
	
	public void setTurn(PlayerColor turn) {
		this.turn = turn;
	}
	
	public boolean isSinglePlayer() {
		return getPlayerCount() == 1;
	}
	
	public com.appspot.ludounchained.cvo.Game getCVO() {
		return new com.appspot.ludounchained.cvo.Game(this);
	}
}
