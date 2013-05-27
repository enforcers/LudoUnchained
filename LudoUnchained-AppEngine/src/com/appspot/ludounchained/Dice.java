package com.appspot.ludounchained;

import java.util.Random;

public class Dice {
	public static int roll(){
		Random random = new Random();
		return random.nextInt(5) + 1;
	}
}
