package com.appspot.ludounchained.endpoint;

import java.util.List;

import com.appspot.ludounchained.controllerEndpoint.model.*;
import com.appspot.ludounchained.exception.RemoteException;

public interface EndpointService {

	public Session login (String username, String password) throws RemoteException;
	public void logout() throws RemoteException;
	public Session register(String username, String password) throws RemoteException;
	public Game newGame() throws RemoteException;
	public Game joinGame(Game game) throws RemoteException;
	public Game leaveGame(Game game) throws RemoteException;
	public void requestJoinGame(Game game) throws RemoteException;
	public void acceptJoinGame(Game game, String requesterSessionId) throws RemoteException;
	public Game startGame(Game game) throws RemoteException;
	public List<Game> listGames() throws RemoteException;
	public Game getGame(Game game) throws RemoteException;
	public Turn rollDice(Game game) throws RemoteException;
	public void executeTurn(Game game, Turn turn, Meeple meeple) throws RemoteException;

}
