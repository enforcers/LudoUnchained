package com.appspot.ludounchained.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.datanucleus.annotations.Unowned;

@Entity
public class Game implements Serializable {

	private static final long serialVersionUID = -8472403885032549742L;
	
	public enum Color { RED, BLUE, GREEN, YELLOW };

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

	public Game() {
		super();
	}
	
	public Game(User user) {
		addPlayer(Color.RED, user);
	} 
	
	public Key getGameId() {
		return gameId;
	}
	
	public void addPlayer(Color color, User user) {
		switch (color) {
			case RED    : setRedPlayer(user); break;
			case BLUE   : setBluePlayer(user); break;
			case GREEN  : setGreenPlayer(user); break;
			case YELLOW : setYellowPlayer(user); break;
		}
	}
	
	public User getPlayer(Color color) {
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
}
