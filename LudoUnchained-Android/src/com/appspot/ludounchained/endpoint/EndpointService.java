package com.appspot.ludounchained.endpoint;

import java.util.List;

import com.appspot.ludounchained.controllerEndpoint.model.Game;
import com.appspot.ludounchained.controllerEndpoint.model.Session;
import com.appspot.ludounchained.exception.RemoteException;

public interface EndpointService {

	public Session login (String username, String password) throws RemoteException;
	public void logout() throws RemoteException;
	public Session register(String username, String password) throws RemoteException;
	public Game newGame() throws RemoteException;
	public Game joinGame(Game game) throws RemoteException;
	public List<Game> listGames() throws RemoteException;

}
