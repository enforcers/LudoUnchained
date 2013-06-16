package com.appspot.ludounchained.cvo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Turn implements Serializable {

	private static final long serialVersionUID = 8442693464968497422L;

	private List<Integer> dice;
	private List<Meeple> validTurns;

	public Turn() {
		super();
	}

	public Turn(com.appspot.ludounchained.model.Turn turn) {
		dice = turn.getDice();

		validTurns = new ArrayList<Meeple>();

		for (com.appspot.ludounchained.model.Meeple obj : turn.getValidTurns()) {
			validTurns.add(obj.getCVO());
		}
	}
	
	public List<Integer> getDice() {
		return dice;
	}
	
	public List<Meeple> getValidTurns() {
		return validTurns;
	}
}
