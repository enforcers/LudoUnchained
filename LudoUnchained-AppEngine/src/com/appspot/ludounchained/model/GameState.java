package com.appspot.ludounchained.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.appspot.ludounchained.util.PlayerColor;
import com.google.appengine.api.datastore.Key;

@Entity
public class GameState implements Serializable {

	private static final long serialVersionUID = 880231339492879836L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key gameStateId;
	
	@Embedded
	List<Field> fields;

	public GameState() {
		super();
		init();
	}
	
	public Key getGameStateId() {
		return gameStateId;
	}
	
	public List<Field> getFields() {
		return fields;
	}
	
	public void setFields(List<Field> fields) {
		this.fields = fields;
	}
	
	public void addField(Field field) {
		fields.add(field);
	}
	
	private void init() {
		fields = new ArrayList<Field>();
		addField(new Field(PlayerColor.RED, 0));
		addField(new Field(PlayerColor.RED, 0));
		addField(new Field(PlayerColor.RED, 0));
		addField(new Field(PlayerColor.RED, 0));
	}
	
	public com.appspot.ludounchained.cvo.GameState getCVO() {
		return new com.appspot.ludounchained.cvo.GameState(this);
	}
}
