package com.appspot.ludounchained.endpoints;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.appspot.ludounchained.model.AIPlayer;
import com.appspot.ludounchained.model.Game;
import com.appspot.ludounchained.model.Game.State;
import com.appspot.ludounchained.model.GameState;
import com.appspot.ludounchained.model.HighScore;
import com.appspot.ludounchained.model.RawScore;
import com.appspot.ludounchained.model.Session;
import com.appspot.ludounchained.model.Turn;
import com.appspot.ludounchained.model.User;
import com.appspot.ludounchained.util.GCMSender;
import com.appspot.ludounchained.util.MD5;
import com.appspot.ludounchained.util.PlayerColor;
import com.appspot.ludounchained.EMF;
import com.appspot.ludounchained.remoteapi.HighScorePuller;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.datanucleus.query.JPACursorHelper;

@Api(name = "controllerEndpoint", namespace = @ApiNamespace(ownerDomain = "appspot.com", ownerName = "appspot.com", packagePath = "ludounchained"))
public class ControllerEndpoint {

	@ApiMethod(name = "login")
	public com.appspot.ludounchained.cvo.Session login(
			@Named("username") String username,
			@Named("password") String password)
	{
		EntityManager mgr = getEntityManager();
		User user = null;

		try {
			user = mgr.find(User.class, username);
		} finally {
			mgr.close();
		}

		if (user != null && user.getPassword().equals(MD5.encrypt(password))) {
			return getSession(user).getCVO();
		} else {
			return null;
		}
	}
	
	@ApiMethod(name = "registerDevice")
	public com.appspot.ludounchained.cvo.Session registerDevice(
			@Named("sessionId") String sessionId,
			@Named("registrationId") String registrationId)
	{
		EntityManager mgr = getEntityManager();
		Session session = null;
		
		try {
			session = mgr.find(Session.class, sessionId);
			session.setRegistrationId(registrationId);
		} catch (Exception e) {
			e.printStackTrace();
			session = null;
		} finally {
			mgr.close();
		}
		
		if (session != null)
			return session.getCVO();
		else
			return null;
	}
	
	@ApiMethod(name = "logout")
	public void logout(
			@Named("sessionId") String sessionId)
	{
		EntityManager mgr = getEntityManager();
		Session session = null;
		try {
			session = mgr.find(Session.class, sessionId);
			mgr.remove(session);
		} finally {
			mgr.close();
		}
	}
	
	@ApiMethod(name = "register")
	public com.appspot.ludounchained.cvo.Session register(
			@Named("username") String username,
			@Named("password") String password)
	{
		EntityManager mgr = getEntityManager();

		if (!containsUser(username)) {
			User user = null;

			try {
				user = new User();
				user.setUsername(username);
				user.setPassword(password);

				mgr.persist(user);
			} finally {
				mgr.close();
			}
			
			return getSession(user).getCVO();
		}
		
		return null;
	}

	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listGame")
	public CollectionResponse<com.appspot.ludounchained.cvo.Game> listGame(
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("limit") Integer limit,
			@Named("sessionId") String sessionId) {
		
		Session session = validateSession(sessionId);

		EntityManager mgr = null;
		Cursor cursor = null;
		List<Game> execute = null;
		List<com.appspot.ludounchained.cvo.Game> result = new ArrayList<com.appspot.ludounchained.cvo.Game>();
		
		try {
			mgr = getEntityManager();
			Query query = mgr.createQuery("select from Game as Game");
			if (cursorString != null && cursorString != "") {
				cursor = Cursor.fromWebSafeString(cursorString);
				query.setHint(JPACursorHelper.CURSOR_HINT, cursor);
			}
			
			if (limit != null) {
				query.setFirstResult(0);
				query.setMaxResults(limit);
			}
			
			execute = (List<Game>) query.getResultList();
			cursor = JPACursorHelper.getCursor(execute);
			if (cursor != null)
				cursorString = cursor.toWebSafeString();
			
			// Tight loop for fetching all entities from datastore and accomodate
			// for lazy fetch.
			for (Game obj : execute) {
				result.add(obj.getCVO());
			}
		} finally {
			mgr.close();
		}
		
		return CollectionResponse.<com.appspot.ludounchained.cvo.Game> builder().setItems(result)
				.setNextPageToken(cursorString).build();
	}

	@ApiMethod(name = "newGame")
	public com.appspot.ludounchained.cvo.Game newGame(@Named("sessionId") String sessionId) {
		Session session = validateSession(sessionId);
		Game game = null;
		com.appspot.ludounchained.cvo.Game result = null;

		EntityManager mgr = getEntityManager();

		try {
			User user = session.getUser();
			mgr.merge(user);

			game = new Game(user);
			mgr.persist(game);
		} finally {
			mgr.close();
		}
		
		if (game != null) {
			result = game.getCVO();
			result.setGameState(game.getGameState().getCVO());
		}
		
		return result;

	}

	@ApiMethod(name = "joinGame")
	public com.appspot.ludounchained.cvo.Game joinGame(
			@Named("sessionId") String sessionId,
			@Named("gameId") long gameId) {
		Session session = validateSession(sessionId);
		Game game = null;
		GameState gameState = null;
		com.appspot.ludounchained.cvo.Game result = null;

		EntityManager mgr = getEntityManager();

		try {
			game = mgr.find(Game.class, gameId);
			game.getPlayers(); // lazy fetch
			game.addSpectator(session.getUser());
			gameState = game.getGameState();
			gameState.getMeeples(); // lazy fetch

			GCMSender.informUsers(getUserSessions(game), GCMSender.PLAYER_JOINED, session.getUser());
		} finally {
			mgr.close();
		}

		if (game != null) {
			result = game.getCVO();

			if (gameState != null)
				result.setGameState(gameState.getCVO());
		}
		
		return result;
	}
	
	@ApiMethod(name = "requestJoinGame")
	public void requestJoinGame(
			@Named("sessionId") String sessionId,
			@Named("gameId") long gameId) {
		Session session = validateSession(sessionId);
		Game game = null;
		Session lobbyLeader = null;

		EntityManager mgr = getEntityManager();

		try {
			game = mgr.find(Game.class, gameId);
			lobbyLeader = getSession(game.getRedPlayer());
		} finally {
			
			if (lobbyLeader != null) {
				GCMSender.informJoinRequest(lobbyLeader, GCMSender.PLAYER_REQUEST_JOIN, session);
			}
			mgr.close();
		}
	}
	
	@ApiMethod(name = "acceptJoinGame")
	public void acceptJoinGame(
			@Named("sessionId") String sessionId,
			@Named("gameId") long gameId,
			@Named("requesterSessionId") String requesterSessionId) {
		Session session = validateSession(sessionId);
		Session requesterSession = validateSession(requesterSessionId);
		boolean playerSet = false;

		Game game = null;
		Session lobbyLeader = null;

		EntityManager mgr = getEntityManager();

		try {
			game = mgr.find(Game.class, gameId);
			lobbyLeader = getSession(game.getRedPlayer());
			
			System.out.println(requesterSession.getUser().getUsername());
			if (lobbyLeader.getSessionId().equals(session.getSessionId())) { // check for lobby leader
				if (game.getPlayerCount() < 4) {
					PlayerColor nextFreeColor = PlayerColor.values()[game.getPlayerCount()];
					game.removeSpectator(requesterSession.getUser());
					game.setPlayer(nextFreeColor, requesterSession.getUser());
					playerSet = true;
					System.out.println("ADDED AS " + nextFreeColor.toString());
				}
			}
		} finally {
			mgr.close();

			if (playerSet)
				GCMSender.informUsers(getUserSessions(game), GCMSender.PLAYER_ACCEPTED, requesterSession.getUser());
		}
	}
	
	@ApiMethod(name = "getGame")
	public com.appspot.ludounchained.cvo.Game getGame(
			@Named("sessionId") String sessionId,
			@Named("gameId") long gameId) {
		validateSession(sessionId);
		Game game = null;
		GameState gameState = null;
		com.appspot.ludounchained.cvo.Game result = null;
		
		EntityManager mgr = getEntityManager();

		try {
			game = mgr.find(Game.class, gameId);
			game.getPlayers(); // lazy fetch
			game.getSpectators(); // lazy fetch
			gameState = game.getGameState();
			gameState.getMeeples(); // lazy fetch
		} finally {
			mgr.close();
		}

		if (game != null) {
			result = game.getCVO();

			if (gameState != null)
				result.setGameState(gameState.getCVO());
		}
		
		return result;
	}
	
	@ApiMethod(name = "startGame")
	public com.appspot.ludounchained.cvo.Game startGame(
			@Named("sessionId") String sessionId,
			@Named("gameId") long gameId) {
		validateSession(sessionId);
		
		Game game = null;
		EntityManager mgr = getEntityManager();
		
		try {
			game = mgr.find(Game.class, gameId);
			game.getPlayers(); // lazy fetch
			game.getSpectators(); // lazy fetch
			GameState gameState = game.getGameState();
			gameState.getMeeples(); // lazy fetch
			
			game.setState(State.RUNNING);

			if (game.isSinglePlayer())
				gameState.addPlayerMeeples(PlayerColor.BLUE);

			GCMSender.informUsers(getUserSessions(game), GCMSender.GAME_STARTED);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mgr.close();
		}
		
		return game.getCVO();
	}
	
	@ApiMethod(name = "leaveGame")
	public com.appspot.ludounchained.cvo.Game leaveGame(
			@Named("sessionId") String sessionId,
			@Named("gameId") long gameId) {
		Session session = validateSession(sessionId);
		Game game = null;
		
		EntityManager mgr = getEntityManager();
		
		try {
			game = mgr.find(Game.class, gameId);

			if (game != null) {
				boolean singlePlayer = game.isSinglePlayer();
				
				if (game.getState() == Game.State.RUNNING && game.getPlayers().contains(session.getUser())) {
					mgr.persist(new RawScore(session.getUser().getUsername(),-1));
				}

				game.setPlayer(game.getPlayerColor(session.getUser()), null);

				game.removeSpectator(session.getUser());

				if (game.getPlayerCount() == 0) {
					mgr.remove(game);
				} else if(game.getPlayerCount() == 1 && !singlePlayer) {
					game.setState(Game.State.FINISHED);
				}
				
				GCMSender.informUsers(getUserSessions(game), GCMSender.PLAYER_LEFT, session.getUser());
			}
		} finally {
			mgr.close();
		}

		if (game != null) {
			return game.getCVO();
		}
		
		return null;
	}
	
	@ApiMethod(name = "rollDice")
	public com.appspot.ludounchained.cvo.Turn rollDice(
			@Named("sessionId") String sessionId,
			@Named("gameId") long gameId) {
		Session session = validateSession(sessionId);
		Game game = null;
		GameState gameState = null;
		Turn turn = null;
		
		EntityManager mgr = getEntityManager();
		
		try {
			game = mgr.find(Game.class, gameId);
			gameState = game.getGameState();

			if (game != null && game.getPlayer(gameState.getTurnColor()).equals(session.getUser())) {
				turn = new Turn(gameState);
				mgr.persist(turn);
			}
		} finally {
			mgr.close();
		}

		return turn.getCVO();
	}

	@ApiMethod(name = "executeTurn")
	public void executeTurn(
			@Named("sessionId") String sessionId,
			@Named("gameId") long gameId,
			@Named("turnId") long turnId,
			@Named("meepleId") long meepleId) {
		Session session = validateSession(sessionId);
		Turn turn = null;
		Game game = null;
		AIPlayer AI = null;
		
		EntityManager mgr = getEntityManager();
		
		try {
			game = mgr.find(Game.class, gameId);
			turn = mgr.find(Turn.class, turnId);
			
			turn.execute(meepleId);
			
			if (game.isFinished(game.getGameState().getTurnColor())) {
				int score = game.getPlayerCount();
				
				if (!game.isSinglePlayer())
					score--;

				mgr.persist(new RawScore(session.getUser().getUsername(), score));
			}
			
			GCMSender.informUsers(getUserSessions(game), turn);

			if (turn.getRoll() != 6) {
				if (game.isSinglePlayer()) {
					game.nextPlayer();

					AI = new AIPlayer(game.getGameState());
					AI.play();

					game.nextPlayer();
				} else {
					game.nextPlayer();
				}
			}
			
			mgr.remove(turn);
		} finally {
			mgr.close();
			
			if (AI != null) {
				for (Turn t : AI.getTurns()) {
					GCMSender.informUsers(getUserSessions(game), t);
				}
			}
		}
	}
	
	@ApiMethod(name = "listScore")
	public CollectionResponse<HighScore> listScore(
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("limit") Integer limit) {

		//EntityManager mgr = null;
		//Cursor cursor = null;
		List<HighScore> scores = new ArrayList<HighScore>();;
		
		
		try {
			HighScorePuller puller = new HighScorePuller();
			scores  = puller.pullLudoScores();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/*
		try {
			mgr = getEntityManager();
			Query query = mgr.createQuery("select sum(s.score) From Score s Group by s.player where s.gameId = 1");
			if (cursorString != null && cursorString != "") {
				cursor = Cursor.fromWebSafeString(cursorString);
				query.setHint(JPACursorHelper.CURSOR_HINT, cursor);
			}

			if (limit != null) {
				query.setFirstResult(0);
				query.setMaxResults(limit);
			}

			execute = (List<Score>) query.getResultList();

			cursor = JPACursorHelper.getCursor(execute);
			if (cursor != null)
				cursorString = cursor.toWebSafeString();

			// Tight loop for fetching all entities from datastore and accomodate
			// for lazy fetch.
			for (Score obj : cvoScores)
				;
		} finally {
			mgr.close();
		}
		*/
		if(scores.isEmpty()){
			scores.add(new HighScore("eins",100));
			scores.add(new HighScore("zwei",50));
			scores.add(new HighScore("drei",25));
			System.out.println("Test Highscore transmitted: " + scores.size());
		}
		return CollectionResponse.<HighScore> builder().setItems(scores)
				.setNextPageToken(cursorString).build();
	}

	private List<Session> getUserSessions(Game game) {
		ArrayList<Session> result = new ArrayList<Session>();
		Session session = null;
		
		for (User user : game.getPlayers()) {
			if ((session = getSession(user)) != null)
				result.add(session);
		}
		
		for (User user : game.getSpectators()) {
			if ((session = getSession(user)) != null)
				result.add(session);
		}
		
		return result;
	}

	private boolean containsUser(String username) {
		EntityManager mgr = getEntityManager();
		boolean contains = true;

		try {
			User item = mgr.find(User.class, username);
			if (item == null) {
				contains = false;
			}
		} finally {
			mgr.close();
		}
		return contains;
	}
	
	private Session getSession(User user) {
		EntityManager mgr = getEntityManager();
		Session session = null;
		
		if (user != null) {
			try {
				session = mgr.find(Session.class, user.getUsername());
	
				if (session == null) {
					session = new Session(user);
					mgr.persist(session);
				}
			} finally {
				mgr.close();
			}
		}
		
		return session;
	}
	
	private Session validateSession(String sessionId) {
		EntityManager mgr = getEntityManager();
		Session result = null;

		try {
			result = mgr.find(Session.class, sessionId);
			result.setUpdatedAt();
		} finally {
			mgr.close();
		}
		
		return result;
	}

	private static EntityManager getEntityManager() {
		return EMF.get().createEntityManager();
	}
}
