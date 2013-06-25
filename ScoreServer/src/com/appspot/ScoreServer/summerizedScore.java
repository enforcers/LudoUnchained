package com.appspot.ScoreServer;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Key;

@Entity
public class summerizedScore implements Serializable{
	private static final long serialVersionUID = -8472403215032549742L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key scoreID;
	
	private String player;
	private int score;
	
	private int gameId; //ludo = 1
	
	public summerizedScore() {
		super();
	}
	public summerizedScore (Score score){
			this.player = score.getPlayer();
			this.score = score.getScore();
			this.gameId = score.getGameId();
	}

	public String getPlayer(){
		return this.player;
	}
	
	public int getScore(){
		return this.score;
	}
}
