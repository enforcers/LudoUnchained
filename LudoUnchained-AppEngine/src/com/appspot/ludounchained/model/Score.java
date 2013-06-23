package com.appspot.ludounchained.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Key;

@Entity
public class Score implements Serializable {
	private static final long serialVersionUID = -8472403885032549742L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key scoreID;
	private String player;
	private int score;
	private int gameid;
	
	public Score() {
		super();
	}
	public Score (String player, int score){
		this.player = player;
		this.score = score;
	}
}
