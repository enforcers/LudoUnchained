package com.appspot.ScoreServer;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import com.google.appengine.api.datastore.Key;
/**
 * represents a score entry for each player for each game
 * @author clange
 *
 */

@Entity
public class Score implements Serializable, Comparable<Score>{
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
	public Key getKey(){
		return this.scoreID;
	}

	public String getPlayer(){
		return this.player;
	}
	
	public int getScore(){
		return this.score;
	}
	public void addScore(int i){
		this.score += i;
	}
	
	public int getGameId() {
		return gameId;
	}
    @Override
    public int compareTo(Score score) {
    	Integer s = this.score;
        return s.compareTo(score.getScore());
    }
}
