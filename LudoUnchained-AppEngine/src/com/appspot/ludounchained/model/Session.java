package com.appspot.ludounchained.model;

/**
 * @author vfast, awakup
 */

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.datanucleus.api.jpa.annotations.Extension;

import com.google.appengine.datanucleus.annotations.Unowned;


@Entity
public class Session implements Serializable {

	private static final long serialVersionUID = -7172334400670796313L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Extension(vendorName="datanucleus", key="gae.encoded-pk", value="true")
	private String sessionId;
	
	@Extension(vendorName="datanucleus", key="gae.pk-name", value="true")
	private String username;

	@Basic(fetch = FetchType.EAGER) @Unowned private User user;
	
	private String registrationId;

	private Date createdAt;
	private Date updatedAt;
	
	public Session() {
		super();
	}
	
	public Session(User user) {
		this.user = user;
		this.username = user.getUsername();
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public String getRegistrationId() {
		return this.registrationId;
	}
	
	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}
	
	public Date getCreatedAt() {
		return createdAt;
	}
	
	public Date getUpdatedAt() {
		return updatedAt;
	}

	public String getSessionId() {
		return sessionId;
	}
	
	@PrePersist
	private void setCreatedAt() {
		createdAt = new Date();
	}
	
	@PreUpdate
	public void setUpdatedAt() {
		updatedAt = new Date();
	}
	
	public com.appspot.ludounchained.cvo.Session getCVO() {
		return new com.appspot.ludounchained.cvo.Session(this);
	}
	
}
