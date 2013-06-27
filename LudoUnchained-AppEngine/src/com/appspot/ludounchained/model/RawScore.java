package com.appspot.ludounchained.model;

/**
 * @author clange
 */

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import com.google.appengine.api.datastore.Key;
/**
 * stores the score points for players per match (every match played has on entry)
 * @author clange
 *
 */
@Entity
public class RawScore implements Serializable{
	private static final long serialVersionUID = -8472403885032549742L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key scoreID;
	private String player;
	private int score;
	private int gameid;
	
	public RawScore() {
		super();
	}

	public RawScore (String player, int score){
		this.player = player;
		this.score = score;
	}
	
	public String getPlayer() {
		return player;
	}
	
	public int getScore() {
		return score;
	}
	
	public int getGameId() {
		return gameid;
	}

}
