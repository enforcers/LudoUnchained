package com.appspot.ludounchained.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Key;

public class Score {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key scoreID;
	private String player;
	private int score;
	private int gameid;
	
	public Score (String player, int score){
		this.player = player;
		this.score = score;
	}
}
