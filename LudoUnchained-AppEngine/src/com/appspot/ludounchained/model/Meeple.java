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
	Key id;

	@Basic
	@Enumerated(EnumType.STRING)
	private PlayerColor color;
	
	@Basic
	private int position;
	
	public Meeple() {
		super();
	}
	
	public Key getId() {
		return id;
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
	
	public int simulateMoveBy(int amount) {
		if (position > 40)
			return position + amount;

		int tempPosition = position;

		tempPosition += amount;

		if (tempPosition != 40)
			tempPosition = tempPosition % 40;

		switch (color) {
			case RED:
				if (position <= 40 && position + amount > 40) {
					tempPosition += 40 - 1;
				}
				break;
			case BLUE:
				if (position <= 10 && position + amount > 10) {
					tempPosition += 40 - 1 - 10;
				}
				break;
			case GREEN:
				if (position <= 20 && position + amount > 20)
					tempPosition += 40 - 1 - 20;
				break;
			case YELLOW:
				if (position <= 30 && position + amount > 30)
					tempPosition += 40 - 1 - 30;
				break;
		}
		
		return tempPosition;
	}
	
	public void moveBy(int amount) {
		position = simulateMoveBy(amount);
	}

	public void setPosition(int position) {
		this.position = position;
/*		if (this.position == 0) {
			switch (color) {
				case RED: this.position = position; break;
				case BLUE: this.position = position + 10; break;
				case GREEN: this.position = position + 20; break;
				case YELLOW: this.position = position + 30; break;
			}
		} else {
			this.position = this.parsePosition(this.position, position);
		}
*/
	}
	
	public com.appspot.ludounchained.cvo.Meeple getCVO() {
		return new com.appspot.ludounchained.cvo.Meeple(this);
	}
	
	public void moveFromHome() {
		switch (color) {
			case RED: position = 1; break;
			case BLUE: position = 11; break;
			case GREEN: position = 21; break;
			case YELLOW: position = 31; break;
		}
	}
	
	public void moveToHome() {
		position = 0;
	}
/*
	public int parsePosition(int oldPosition,int newPosition){
		switch(this.color){
		case BLUE:
			if (newPosition > 40)
				return newPosition - 40;

			if (oldPosition <= 10 && newPosition > 10)
				return newPosition - 10 + 40;
		case GREEN:
			if (newPosition > 40)
				return newPosition - 40;

			if (oldPosition <= 20 && newPosition > 20)
				return newPosition - 20 + 40;
		case YELLOW:
			if (newPosition > 40)
				return newPosition - 40;
			
			if (oldPosition <= 30 && newPosition > 30)
				return newPosition - 30 + 40;

		default:
			return newPosition;
		}
	}
*/
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		
		if (obj == null || obj.getClass() != this.getClass())
			return false;
		
		Meeple meeple = (Meeple) obj;
		
		return (color.equals(meeple.getColor()) && position == meeple.getPosition());
	}
}
