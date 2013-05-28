package com.appspot.ludounchained.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Embeddable;
import javax.persistence.Enumerated;

import com.appspot.ludounchained.util.PlayerColor;

@Embeddable
public class Field implements Serializable {

	private static final long serialVersionUID = 5467860780430273931L;

	@Basic
	@Enumerated
	private PlayerColor color;
	
	@Basic
	private int position;
	
	public Field() {
		super();
	}
	
	public Field(PlayerColor color, int position) {
		this.color = color;
		this.position = position;
	}
	
	public PlayerColor getColor() {
		return color;
	}
	
	public void setColor(PlayerColor color) {
		this.color = color;
	}
	
	public int getPosition() {
		return position;
	}
	
	public void setPosition(int position) {
		this.position = position;
	}
	
	public com.appspot.ludounchained.cvo.Field getCVO() {
		return new com.appspot.ludounchained.cvo.Field(this);
	}
	
}
