package com.appspot.ludounchained.endpoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.appspot.ludounchained.LudoUnchainedApplication;
import com.appspot.ludounchained.controllerEndpoint.ControllerEndpoint;
import com.appspot.ludounchained.controllerEndpoint.model.*;
import com.appspot.ludounchained.exception.InvalidLoginException;
import com.appspot.ludounchained.exception.RemoteException;
import com.appspot.ludounchained.util.CloudEndpointUtils;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson.JacksonFactory;

public class EndpointServiceStub implements EndpointService {
	
	private LudoUnchainedApplication appState;
	private boolean busy = false;
	
	public EndpointServiceStub(LudoUnchainedApplication context) {
		appState = context;
	}
	
	public Session login (String username, String password) throws RemoteException {
		Session result = null;
		ControllerEndpoint endpoint = getEndpoint();

    	try {
    		if (!busy) {
    			busy = true;
    			result = endpoint.login(username, password).execute();
    			appState.setSession(result);
    			busy = false;
    		}
    	} catch (IOException e) {
    		e.printStackTrace();
    		busy = false;
    	}
    	if (result == null)
    		throw new InvalidLoginException("Login credentials are invalid");

    	GCMIntentService.register(appState);

    	return result;
	}
	
	public void logout() {
		ControllerEndpoint endpoint = getEndpoint();
    	
    	try {
    		if (!busy) {
    			busy = true;
    			endpoint.logout(appState.getSession().getSessionId()).execute();
    			appState.setSession(null);
    			busy = false;
    		}
    		
    		GCMIntentService.unregister(appState);
    	} catch (IOException e) {
    		e.printStackTrace();
    		busy = false;
    	}
    	
	}
	/**
	 * gets the highscore from gameserver
	 * @author clange
	 */
	public List<HighScore> listScores(){
		CollectionResponseHighScore response = new CollectionResponseHighScore();
		ControllerEndpoint endpoint = getEndpoint();
		
		List<HighScore> result = new ArrayList<HighScore>();
		
    	try {
    		if (!busy) {
    			busy = true;
    			response = endpoint.listScore().execute();
    			busy = false;
    		}
    	} catch (IOException e) {
    		e.printStackTrace();
    		busy = false;
    	}
    	
		if (response.getItems() != null)
			result = response.getItems();
		
		return result;
	}
	
	public Session register(String username, String password) {
		Session result = null;
		ControllerEndpoint endpoint = getEndpoint();
    	
    	try {
    		if (!busy) {
    			busy = true;
    			result = endpoint.register(username, password).execute();
    			appState.setSession(result);
    			busy = false;
    		}
    	} catch (IOException e) {
    		e.printStackTrace();
    		busy = false;
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
    		if (!busy) {
    			busy = true;
    			result = endpoint.newGame(appState.getSession().getSessionId()).execute();
    			appState.setGame(result);
    			busy = false;
    		}
    	} catch (IOException e) {
    		e.printStackTrace();
    		busy = false;
    	}

		return result;
	}
	
	public Game joinGame(Game game) {
		Game result = null;
		ControllerEndpoint endpoint = getEndpoint();
    	
    	try {
    		if (!busy) {
    			busy = true;
    			result = endpoint.joinGame(appState.getSession().getSessionId(), game.getGameId().getId()).execute();
    			appState.setGame(result);
    			busy = false;
    		}
    	} catch (IOException e) {
    		e.printStackTrace();
    		busy = false;
    	}

		return result;
	}
	
	public Game leaveGame() {
		Game result = null;
		ControllerEndpoint endpoint = getEndpoint();
    	
    	try {
    		result = endpoint.leaveGame(appState.getSession().getSessionId(), appState.getGame().getGameId().getId()).execute();
    		appState.setGame(null);
    	} catch (IOException e) {
    		e.printStackTrace();
    	}

		return result;
	}
	
	public void requestJoinGame() throws RemoteException {
		ControllerEndpoint endpoint = getEndpoint();
    	
    	try {
    		if (!busy) {
    			busy = true;
    			endpoint.requestJoinGame(appState.getSession().getSessionId(), appState.getGame().getGameId().getId()).execute();
    			busy = false;
    		}
    	} catch (IOException e) {
    		e.printStackTrace();
    		busy = false;
    	}
	}

	public void acceptJoinGame(String requesterSessionId) throws RemoteException {
		ControllerEndpoint endpoint = getEndpoint();
    	
    	try {
    		if (!busy) {
    			busy = true;
    			endpoint.acceptJoinGame(appState.getSession().getSessionId(), appState.getGame().getGameId().getId(), requesterSessionId).execute();
    			busy = false;
    		}
    	} catch (IOException e) {
    		e.printStackTrace();
    		busy = false;
    	}
	}
	
	public Game startGame() {
		Game result = null;
		ControllerEndpoint endpoint = getEndpoint();

    	try {
    		if (!busy) {
    			busy = true;
    			result = endpoint.startGame(appState.getSession().getSessionId(), appState.getGame().getGameId().getId()).execute();
    			appState.setGame(result);
    			busy = false;
    		}
    	} catch (IOException e) {
    		e.printStackTrace();
    		busy = false;
    	}

		return result;
	}
	
	public List<Game> listGames() {
		List<Game> result = new ArrayList<Game>();
		CollectionResponseGame response = new CollectionResponseGame();
		ControllerEndpoint endpoint = getEndpoint();

		try {
			if (!busy) {
				busy = true;
				response = endpoint.listGame(appState.getSession().getSessionId()).execute();
				busy = false;
			}
		} catch (IOException e) {
    		e.printStackTrace();
    		busy = false;
    	}
		
		if (response.getItems() != null)
			result = response.getItems();
		
		return result;
	}
	
	public Game getGame(Game game) throws RemoteException {
		Game result = null;
		ControllerEndpoint endpoint = getEndpoint();

    	try {
    		if (!busy) {
    			busy = true;
    			result = endpoint.getGame(appState.getSession().getSessionId(), game.getGameId().getId()).execute();
    			appState.setGame(result);
    			busy = false;
    		}
    	} catch (IOException e) {
    		e.printStackTrace();
    		busy = false;
    	}

		return result;
	}
	
	public Turn rollDice() {
		Turn result = null;
		ControllerEndpoint endpoint = getEndpoint();

		try {
			if (!busy) {
				busy = true;
				result = endpoint.rollDice(appState.getSession().getSessionId(), appState.getGame().getGameId().getId()).execute();
				busy = false;
			}
		} catch (IOException e) {
    		e.printStackTrace();
    		busy = false;
    	}
		
		return result;
	}
	
	public void executeTurn(Turn turn, Meeple meeple)
			throws RemoteException {
		ControllerEndpoint endpoint = getEndpoint();

		try {
			if (!busy) {
				busy = true;
				String sessionId = appState.getSession().getSessionId();
				long gameId = appState.getGame().getGameId().getId();
				long turnId = turn.getId().getId();
				long meepleId = 0;
				
				if (meeple != null)
					meepleId = meeple.getId().getId();
				
				endpoint.executeTurn(sessionId, gameId, turnId, meepleId).execute();
				busy = false;
			}
		} catch (IOException e) {
    		e.printStackTrace();
    		busy = false;
    	}
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
