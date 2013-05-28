package com.appspot.ludounchained.endpoint;

import java.io.IOException;
import java.util.List;

import com.appspot.ludounchained.controllerEndpoint.ControllerEndpoint;
import com.appspot.ludounchained.controllerEndpoint.model.CollectionResponseGame;
import com.appspot.ludounchained.controllerEndpoint.model.Game;
import com.appspot.ludounchained.controllerEndpoint.model.Session;
import com.appspot.ludounchained.exception.InvalidLoginException;
import com.appspot.ludounchained.exception.RemoteException;
import com.appspot.ludounchained.util.CloudEndpointUtils;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson.JacksonFactory;

public class EndpointServiceStub implements EndpointService {
	
	public Session login (String username, String password) throws RemoteException {
		Session result = null;
		ControllerEndpoint endpoint = getEndpoint();
    	
    	try {
    		result = endpoint.login(username, password).execute();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}

    	if (result == null)
    		throw new InvalidLoginException("Login credentials are invalid");

    	return result;
	}
	
	public void logout(Session session) {
		ControllerEndpoint endpoint = getEndpoint();
    	
    	try {
    		endpoint.logout(session.getSessionId()).execute();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	
	}
	
	public Session register(String username, String password) {
		Session result = null;
		ControllerEndpoint endpoint = getEndpoint();
    	
    	try {
    		result = endpoint.register(username, password).execute();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	
    	return result;
	}

	public Game newGame(Session session) {
		Game result = null;
		session = (Session) session;
		ControllerEndpoint endpoint = getEndpoint();
    	
    	try {
    		result = endpoint.newGame(session.getSessionId()).execute();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}

		return result;
	}
	
	public List<Game> listGames(Session session) {
		CollectionResponseGame result = new CollectionResponseGame();
		ControllerEndpoint endpoint = getEndpoint();

		try {
			result = endpoint.listGame(session.getSessionId()).execute();
		} catch (IOException e) {
    		e.printStackTrace();
    	}
		
		return result.getItems();
	}
	
	private ControllerEndpoint getEndpoint() {
		ControllerEndpoint.Builder endpointBuilder = new ControllerEndpoint.Builder(
    			AndroidHttp.newCompatibleTransport(),
    			new JacksonFactory(),
    			new HttpRequestInitializer() {
    				public void initialize(HttpRequest httpRequest) { }
    			});

		return CloudEndpointUtils.updateBuilder(endpointBuilder).build();
	}
}
