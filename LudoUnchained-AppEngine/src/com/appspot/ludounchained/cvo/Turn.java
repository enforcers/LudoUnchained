package com.appspot.ludounchained.cvo;

/**
 * @author awakup
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.Key;

public class Turn implements Serializable {

	private static final long serialVersionUID = 8442693464968497422L;

	private Key id;
	private List<Integer> dice;
	private List<Meeple> validTurns;

	public Turn() {
		super();
	}

	public Turn(com.appspot.ludounchained.model.Turn turn) {
		id = turn.getId();
		dice = turn.getDice();

		validTurns = new ArrayList<Meeple>();

		for (com.appspot.ludounchained.model.Meeple obj : turn.getValidTurns()) {
			validTurns.add(obj.getCVO());
		}
	}
	
	public Key getId() {
		return id;
	}
	
	public List<Integer> getDice() {
		return dice;
	}
	
	public List<Meeple> getValidTurns() {
		return validTurns;
	}
}
