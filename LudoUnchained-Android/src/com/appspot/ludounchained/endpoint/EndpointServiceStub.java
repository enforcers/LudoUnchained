package com.appspot.ludounchained.endpoint;

import java.io.IOException;
import java.util.List;

import com.appspot.ludounchained.LudoUnchainedApplication;
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
	
	private LudoUnchainedApplication appState;
	
	public EndpointServiceStub(LudoUnchainedApplication context) {
		appState = context;
	}
	
	public Session login (String username, String password) throws RemoteException {
		Session result = null;
		ControllerEndpoint endpoint = getEndpoint();

    	try {
    		result = endpoint.login(username, password).execute();
    		appState.setSession(result);
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	
    	if (result == null)
    		throw new InvalidLoginException("Login credentials are invalid");

    	GCMIntentService.register(appState);

    	return result;
	}
	
	public void logout() {
		ControllerEndpoint endpoint = getEndpoint();
    	
    	try {
    		endpoint.logout(appState.getSession().getSessionId()).execute();
    		appState.setSession(null);
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	
	}
	
	public Session register(String username, String password) {
		Session result = null;
		ControllerEndpoint endpoint = getEndpoint();
    	
    	try {
    		result = endpoint.register(username, password).execute();
    		appState.setSession(result);
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	
    	if (result == null)
    		return null;
    		//throw new ..;

    	GCMIntentService.register(appState);

    	return result;
	}

	public Game newGame() {
		Game result = null;
		ControllerEndpoint endpoint = getEndpoint();
    	
    	try {
    		result = endpoint.newGame(appState.getSession().getSessionId()).execute();
    		appState.setGame(result);
    	} catch (IOException e) {
    		e.printStackTrace();
    	}

		return result;
	}
	
	public Game joinGame(Game game) {
		Game result = null;
		ControllerEndpoint endpoint = getEndpoint();
    	
    	try {
    		result = endpoint.joinGame(appState.getSession().getSessionId(), game.getGameId().getId()).execute();
    		appState.setGame(result);
    	} catch (IOException e) {
    		e.printStackTrace();
    	}

		return result;
	}
	
	public Game leaveGame(Game game) {
		Game result = null;
		ControllerEndpoint endpoint = getEndpoint();
    	
    	try {
    		result = endpoint.leaveGame(appState.getSession().getSessionId(), game.getGameId().getId()).execute();
    		appState.setGame(null);
    	} catch (IOException e) {
    		e.printStackTrace();
    	}

		return result;
	}
	
	public Game startGame(Game game) {
		Game result = null;
		ControllerEndpoint endpoint = getEndpoint();

    	try {
    		result = endpoint.startGame(appState.getSession().getSessionId(), game.getGameId().getId()).execute();
    		appState.setGame(result);
    	} catch (IOException e) {
    		e.printStackTrace();
    	}

		return result;
	}
	
	public List<Game> listGames() {
		CollectionResponseGame result = new CollectionResponseGame();
		ControllerEndpoint endpoint = getEndpoint();

		try {
			result = endpoint.listGame(appState.getSession().getSessionId()).execute();
		} catch (IOException e) {
    		e.printStackTrace();
    	}
		
		return result.getItems();
	}
	
	public Game getGame(Game game) throws RemoteException {
		Game result = null;
		ControllerEndpoint endpoint = getEndpoint();

    	try {
    		result = endpoint.getGame(appState.getSession().getSessionId(), game.getGameId().getId()).execute();
    		appState.setGame(result);
    	} catch (IOException e) {
    		e.printStackTrace();
    	}

		return result;
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
