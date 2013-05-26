package com.appspot.ludounchained.model;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.datanucleus.annotations.Unowned;

@Entity
public class Game {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key gameId;

	@Unowned
	@OneToOne(cascade=CascadeType.DETACH, fetch=FetchType.EAGER)
	private User gameLeader;

	@OneToMany(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
	@Unowned
	Map<Integer, User> users;
	
	public Game(User user) {
		users = new HashMap<Integer, User>();
		users.put(0, user);
		gameLeader = user;
	}
	
	public Key getGameId() {
		return gameId;
	}
	
	public void addUser(int color, User user) {
		users.put(color, user);
	}
	
	public User getGameLeader() {
		return gameLeader;
	}
	
	public Map<Integer, User> getUsers() {
		return users;
	}
}
