package com.appspot.ScoreServer;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Key;

@Entity
public class Score implements Serializable{
	private static final long serialVersionUID = -8472401455032549742L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key scoreID;
	
	private String player;
	private int score;
	
	private int gameId; //ludo = 1
	
	public Score() {
		super();
	}
	public Score (String player, int score, int gameId){
		this.player = player;
		this.score = score;
		this.gameId = gameId;
	}

	public String getPlayer(){
		return this.player;
	}
	
	public int getScore(){
		return this.score;
	}
}
