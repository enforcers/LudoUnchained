package com.appspot.ludounchained.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.appspot.ludounchained.util.PlayerColor;
import com.google.appengine.api.datastore.Key;

@Entity
public class GameState implements Serializable {

	private static final long serialVersionUID = 880231339492879836L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Key gameStateId;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	List<Field> fields;

	public GameState() {
		super();
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
	
	public void addPlayerFields(PlayerColor color) {
		if (fields == null)
			fields = new ArrayList<Field>();

		fields.add(new Field(color, 0));
		fields.add(new Field(color, 0));
		fields.add(new Field(color, 0));
		fields.add(new Field(color, 0));
	}
	
	public void removePlayerFields(PlayerColor color) {
		if (fields == null)
			return;
		
		for (Field field : fields) {
			if (field.getColor() == color)
				fields.remove(field);
		}
	}
	
	public com.appspot.ludounchained.cvo.GameState getCVO() {
		return new com.appspot.ludounchained.cvo.GameState(this);
	}
}
