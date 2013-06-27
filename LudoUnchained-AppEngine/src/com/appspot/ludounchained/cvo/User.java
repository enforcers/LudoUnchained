package com.appspot.ludounchained.cvo;

/**
 * @author vfast
 */

import java.io.Serializable;

public class User implements Serializable {

	private static final long serialVersionUID = -7385925814784204452L;

	private String username;
	
	public User() {
		super();
	}
	
	public User(com.appspot.ludounchained.model.User user) {
		if (user != null) {
			username = user.getUsername();
		}
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}

}
