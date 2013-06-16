package com.appspot.ludounchained.cvo;

import java.io.Serializable;

import com.appspot.ludounchained.util.PlayerColor;
import com.google.appengine.api.datastore.Key;

public class Meeple implements Serializable {

	private static final long serialVersionUID = 1402004711981987164L;

	private Key id;
	private PlayerColor color;
	private int position;
	
	public Meeple() {
		super();
	}
	
	public Meeple(com.appspot.ludounchained.model.Meeple meeple) {
		if (meeple != null) {
			id = meeple.getId();
			color = meeple.getColor();
			position = meeple.getPosition();
		}
	}
	
	public Key getId() {
		return id;
	}
	
	public PlayerColor getColor() {
		return color;
	}
	
	public int getPosition() {
		return position;
	}

}
