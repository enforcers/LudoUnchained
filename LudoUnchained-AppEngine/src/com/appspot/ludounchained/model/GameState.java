package com.appspot.ludounchained.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.appspot.ludounchained.util.PlayerColor;
import com.google.appengine.api.datastore.Key;

@Entity
public class GameState implements Serializable {

	private static final long serialVersionUID = 880231339492879836L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Key gameStateId;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	List<Meeple> meeples;

	private List<Meeple> meeple;
	
	// Spieler der am zug ist
	public PlayerColor player;

	public GameState() {
		super();
	}
	
	public Key getGameStateId() {
		return gameStateId;
	}
	
	public List<Meeple> getMeeples() {
		return meeples;
	}
	
	public void setMeeples(List<Meeple> meeple) {
		this.meeple = meeple;
	}
	
	public void addMeeple(Meeple meeple) {
		meeples.add(meeple);
	}
	
	public void addPlayerMeeples(PlayerColor color) {
		if (meeples == null)
			meeples = new ArrayList<Meeple>();

		meeples.add(new Meeple(color, 0));
		meeples.add(new Meeple(color, 0));
		meeples.add(new Meeple(color, 0));
		meeples.add(new Meeple(color, 0));
	}
	
	public void removePlayerMeeples(PlayerColor color) {
		if (meeples == null)
			return;
		
		for (Meeple meeple : meeples) {
			if (meeple.getColor() == color)
				meeples.remove(meeple);
		}
	}
	
	public com.appspot.ludounchained.cvo.GameState getCVO() {
		return new com.appspot.ludounchained.cvo.GameState(this);
	}

}
