package com.appspot.ludounchained.cvo;

import java.io.Serializable;

import com.appspot.ludounchained.util.PlayerColor;

public class Field implements Serializable {

	private static final long serialVersionUID = 1402004711981987164L;

	private PlayerColor color;
	private int position;
	
	public Field() {
		super();
	}
	
	public Field(com.appspot.ludounchained.model.Field field) {
		if (field != null) {
			color = field.getColor();
			position = field.getPosition();
		}
	}
	
	public PlayerColor getColor() {
		return color;
	}
	
	public int getPosition() {
		return position;
	}

}
