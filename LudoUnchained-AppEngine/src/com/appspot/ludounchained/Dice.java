package com.appspot.ludounchained;

import java.util.Random;

public class Dice {
	private Random random = new Random();
	int value = random.nextInt(5) + 1;
}

