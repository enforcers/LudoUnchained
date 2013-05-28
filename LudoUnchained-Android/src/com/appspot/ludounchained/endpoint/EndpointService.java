package com.appspot.ludounchained.endpoint;

import java.util.List;

import com.appspot.ludounchained.controllerEndpoint.model.Game;
import com.appspot.ludounchained.controllerEndpoint.model.Session;
import com.appspot.ludounchained.exception.RemoteException;

public interface EndpointService {

	public Session login (String username, String password) throws RemoteException;
	public void logout(Session session) throws RemoteException;
	public Session register(String username, String password) throws RemoteException;
	public Game newGame(Session session) throws RemoteException;
	public List<Game> listGames(Session session) throws RemoteException;

}
