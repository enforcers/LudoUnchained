package com.appspot.ludounchained.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.datanucleus.api.jpa.annotations.Extension;

import com.google.appengine.datanucleus.annotations.Unowned;


@Entity
public class Session {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Extension(vendorName="datanucleus", key="gae.encoded-pk", value="true")
	private String sessionId;
	
	@Extension(vendorName="datanucleus", key="gae.pk-name", value="true")
	private String username;

	@Unowned
	@OneToOne(cascade=CascadeType.DETACH, fetch=FetchType.EAGER)
	private User user;

	private Date createdAt;
	
	public Session(User user) {
		this.user = user;
		this.username = user.getUsername();
		this.createdAt = new Date();
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public Date getCreatedAt() {
		return createdAt;
	}

	public String getSessionId() {
		return sessionId;
	}
	
}
