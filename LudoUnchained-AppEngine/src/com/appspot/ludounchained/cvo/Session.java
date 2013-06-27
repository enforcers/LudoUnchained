package com.appspot.ludounchained.cvo;

/**
 * @author vfast
 */

import java.io.Serializable;
import java.util.Date;

public class Session implements Serializable {

	private static final long serialVersionUID = 7840212860773415001L;

	private String sessionId;
	private User user;
	private String registrationId;
	private Date createdAt;
	private Date updatedAt;
	
	public Session() {
		super();
	}
	
	public Session(com.appspot.ludounchained.model.Session session) {
		if (session != null) {
			sessionId = session.getSessionId();
			registrationId = session.getRegistrationId();
			user = new User(session.getUser());
			createdAt = session.getCreatedAt();
			updatedAt = session.getUpdatedAt();
		}
	}
	
	public String getSessionId() {
		return sessionId;
	}

	public User getUser() {
		return user;
	}
	
	public String getRegistrationId() {
		return registrationId;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public Date getCreatedAt() {
		return createdAt;
	}
	
	public Date getUpdatedAt() {
		return updatedAt;
	}
	
}
