package com.appspot.ludounchained;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

import com.appspot.ludounchained.model.Game;

public class Dice implements Serializable {

	private static final long serialVersionUID = 8923953940132844166L;

	private int value;
	private List<Integer> validFields;
	
	public Dice() {
		super();
	}

	public Dice(Game game) {
		Random random = new Random();
		value = random.nextInt(5) + 1;
		
		calculateValidFields();
	}
	
	public int getValue() {
		return value;
	}
	
	public List<Integer> getValidFields() {
		return validFields;
	}
	
	private void calculateValidFields() {
		validFields.add(0);
	}
}
