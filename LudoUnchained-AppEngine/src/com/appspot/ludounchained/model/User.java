package com.appspot.ludounchained.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.appspot.ludounchained.util.MD5;


@Entity
public class User implements Serializable {

	private static final long serialVersionUID = 4690361206200989747L;

	@Id
	private String username;
	
	private String password;
	
	public User() {
		super();
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = MD5.encrypt(password);
	}

}
