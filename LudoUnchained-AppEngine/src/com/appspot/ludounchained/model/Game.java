package com.appspot.ludounchained.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.appspot.ludounchained.util.PlayerColor;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.datanucleus.annotations.Unowned;

@Entity
public class Game implements Serializable {

	private static final long serialVersionUID = -8472403885032549742L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key gameId;

	@Basic(fetch = FetchType.EAGER) @Unowned private User redPlayer;
	@Basic(fetch = FetchType.EAGER) @Unowned private User bluePlayer;
	@Basic(fetch = FetchType.EAGER) @Unowned private User greenPlayer;
	@Basic(fetch = FetchType.EAGER) @Unowned private User yellowPlayer;

	@Unowned
	@OneToMany(fetch = FetchType.EAGER)
	List<User> spectators;
	
	@OneToOne(cascade = CascadeType.ALL)
	private GameState gameState;

	public Game() {
		super();
	}
	
	public Game(User user) {
		addPlayer(PlayerColor.RED, user);
		this.gameState = new GameState();
	} 
	
	public Key getGameId() {
		return gameId;
	}
	
	public void addPlayer(PlayerColor color, User user) {
		switch (color) {
			case RED    : setRedPlayer(user); break;
			case BLUE   : setBluePlayer(user); break;
			case GREEN  : setGreenPlayer(user); break;
			case YELLOW : setYellowPlayer(user); break;
		}
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
		return
				(redPlayer == null ? 0 : 1) +
				(bluePlayer == null ? 0 : 1) +
				(greenPlayer == null ? 0 : 1) +
				(yellowPlayer == null ? 0 : 1);
	}
	
	public List<User> getSpectators() {
		return spectators;
	}
	
	public GameState getGameState() {
		return gameState;
	}
	
	public void setSpectators(List<User> spectators) {
		this.spectators = spectators;
	}
	
	public void addSpectator(User user) {
		spectators.add(user);
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
	
	public com.appspot.ludounchained.cvo.Game getCVO() {
		return new com.appspot.ludounchained.cvo.Game(this);
	}
}
