package com.appspot.ludounchained.util;

import com.appspot.ludounchained.controllerEndpoint.model.Game;
import com.appspot.ludounchained.controllerEndpoint.model.User;

public enum PlayerColor {
	RED, BLUE, GREEN, YELLOW;

	public User getPlayer(Game game) {
		switch (this) {
			case RED    : return game.getRedPlayer();
			case BLUE   : return game.getBluePlayer();
			case GREEN  : return game.getGreenPlayer();
			case YELLOW : return game.getYellowPlayer();
		}
		
		return null;
	}
}
