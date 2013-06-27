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
	public Game leaveGame() throws RemoteException;
	public void requestJoinGame() throws RemoteException;
	public void acceptJoinGame(String requesterSessionId) throws RemoteException;
	public Game startGame() throws RemoteException;
	public List<Game> listGames() throws RemoteException;
	public List<HighScore> listScores() throws RemoteException;
	public Game getGame(Game game) throws RemoteException;
	public Turn rollDice() throws RemoteException;
	public void executeTurn(Turn turn, Meeple meeple) throws RemoteException;

}
