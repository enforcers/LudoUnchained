package com.appspot.ludounchained.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
	
	// Spieler der am zug ist
	@Enumerated(EnumType.STRING)
	private PlayerColor turnColor;

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
		this.meeples = meeple;
	}
	
	public PlayerColor getTurnColor() {
		return turnColor;
	}
	
	public void setTurnColor(PlayerColor color) {
		this.turnColor = color;
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
	//total unschön irgendwie
	public void executeTurn(Meeple meeple, int moves){
		//neue position
		for(Meeple m: this.meeples){
			if (meeple.getPosition() == m.getPosition()){
				m.setPosition(meeple.getPosition() + moves);
			}
		}
		
		//ggf gekickten meeple auf 0 setzen
		for(Meeple m: this.meeples){
			if (meeple.getPosition() + moves == m.getPosition()){
				m.setPosition(0);
			}
		}
		
	}
	
	public int doAITurn() {
		this.setTurnColor(PlayerColor.BLUE);
		int AIRoll = 0;
		
		do {
			Turn turn = new Turn(this);
	
			if (turn.getValidTurns().size() > 0) {
				AIRoll = turn.getDice().get(turn.getDice().size() - 1);
	
				for (Meeple m : this.getMeeples()) {
					if (m.getId().equals(turn.getValidTurns().get(0).getId())) {
						if (m.getPosition() == 0 && AIRoll == 6)
							m.moveFromHome();
						else
							m.moveBy(AIRoll);
						
						// Spieler schlagen
						for (Meeple mo : this.getMeeples()) {
							if (mo.getColor() != m.getColor()) {
								if (mo.getPosition() == m.getPosition()) {
									mo.moveToHome();
								}
							}
						}
					}
				}
				
			}
		} while (AIRoll == 6);
		
		this.setTurnColor(PlayerColor.RED);
		
		return AIRoll;
	}
	
	public com.appspot.ludounchained.cvo.GameState getCVO() {
		return new com.appspot.ludounchained.cvo.GameState(this);
	}

}
