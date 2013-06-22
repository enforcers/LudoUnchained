package com.appspot.ludounchained.util;

public enum PlayerColor {
	RED, BLUE, GREEN, YELLOW;
	
	public PlayerColor getNext() {
		return values()[(ordinal() + 1) % values().length];
	}
}
