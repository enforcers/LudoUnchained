package com.appspot.ludounchained.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.appspot.ludounchained.util.PlayerColor;
import com.google.appengine.api.datastore.Key;

@Entity
public class Meeple implements Serializable {

	private static final long serialVersionUID = 5467860780430273931L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Key fieldId;

	@Basic
	@Enumerated(EnumType.STRING)
	private PlayerColor color;
	
	@Basic
	private int position;
	
	public Meeple() {
		super();
	}
	
	public Meeple(PlayerColor color, int position) {
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
		this.position = this.parsePosition(this.position, position);
	}
	
	public com.appspot.ludounchained.cvo.Meeple getCVO() {
		return new com.appspot.ludounchained.cvo.Meeple(this);
	}
	
	public int parsePosition(int oldPosition,int newPosition){
		switch(this.color){
		case BLUE:
			if(oldPosition<=10 && newPosition > 10){
				return newPosition - 10 + 40;
			}
		case GREEN:
			if(oldPosition<=20 && newPosition > 20){
				return newPosition - 20 + 40;
			}
		case YELLOW:
			if(oldPosition<=30 && newPosition > 30){
				return newPosition - 30 + 40;
			}
		}
		return newPosition;
		
	}
}
