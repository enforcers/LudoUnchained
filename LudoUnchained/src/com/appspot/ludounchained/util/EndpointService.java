package com.appspot.ludounchained.util;

import java.io.IOException;

import com.appspot.ludounchained.usercontrollerEndpoint.UsercontrollerEndpoint;
import com.appspot.ludounchained.usercontrollerEndpoint.model.Game;
import com.appspot.ludounchained.usercontrollerEndpoint.model.Session;
import com.appspot.ludounchained.usercontrollerEndpoint.model.User;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson.JacksonFactory;

public enum EndpointService {
	CALL;
	
	public Session login (String username, String password) {
		Session result = null;

		UsercontrollerEndpoint.Builder endpointBuilder = new UsercontrollerEndpoint.Builder(
    			AndroidHttp.newCompatibleTransport(),
    			new JacksonFactory(),
    			new HttpRequestInitializer() {
    				public void initialize(HttpRequest httpRequest) { }
    			});

		UsercontrollerEndpoint endpoint = CloudEndpointUtils.updateBuilder(endpointBuilder).build();
    	
    	try {
    		result = endpoint.login(username, password).execute();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	
    	return result;
	}
	
	public Session register(String username, String password) {
		Session result = null;

		UsercontrollerEndpoint.Builder endpointBuilder = new UsercontrollerEndpoint.Builder(
    			AndroidHttp.newCompatibleTransport(),
    			new JacksonFactory(),
    			new HttpRequestInitializer() {
    				public void initialize(HttpRequest httpRequest) { }
    			});

		UsercontrollerEndpoint endpoint = CloudEndpointUtils.updateBuilder(endpointBuilder).build();
    	
    	try {
    		User user = new User().setUsername(username);
    		user.setPassword(password);

    		result = endpoint.register(user).execute();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	
    	return result;
	}

	public Game newGame(Session session) {
		Game result = null;
		session =  (Session) session;
		
		UsercontrollerEndpoint.Builder endpointBuilder = new UsercontrollerEndpoint.Builder(
    			AndroidHttp.newCompatibleTransport(),
    			new JacksonFactory(),
    			new HttpRequestInitializer() {
    				public void initialize(HttpRequest httpRequest) { }
    			});

		UsercontrollerEndpoint endpoint = CloudEndpointUtils.updateBuilder(endpointBuilder).build();
    	
    	try {
    		result = endpoint.newGame(session).execute();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}

		return result;
	}
}
