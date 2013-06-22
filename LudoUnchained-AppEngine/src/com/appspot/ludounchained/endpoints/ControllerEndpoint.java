package com.appspot.ludounchained.endpoints;

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
import com.appspot.ludounchained.model.Score;
import com.appspot.ludounchained.model.Session;
import com.appspot.ludounchained.model.Turn;
import com.appspot.ludounchained.model.User;
import com.appspot.ludounchained.util.GCMSender;
import com.appspot.ludounchained.util.MD5;
import com.appspot.ludounchained.util.PlayerColor;
import com.appspot.ludounchained.EMF;
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
			// test daten für scoreserver
			mgr.persist(new Score(username,2));
			mgr.persist(new Score(username,1));
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
				game.setPlayer(game.getPlayerColor(session.getUser()), null);
				game.removeSpectator(session.getUser());

				if (game.getPlayerCount() == 0) {
					mgr.remove(game);
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
			
			// TODO: set next player turn
			if (turn.getRoll() != 6) {
				if (game.isSinglePlayer()) {
					AI = new AIPlayer(game.getGameState());
					AI.play();
				} else {
					//game.nextPlayer();
				}
			}
		} finally {
			mgr.close();
			GCMSender.informUsers(getUserSessions(game), GCMSender.PLAYER_MOVE, session.getUser(), turn.getRoll());
			
			if (AI != null) {
				for (Turn t : AI.getTurns()) {
					if (t.getValidTurns().size() > 0) {
						int AIRoll = t.getDice().get(t.getDice().size() - 1);
						GCMSender.informUsers(getUserSessions(game), GCMSender.COMPUTER_MOVE, AIRoll);
					}
				}
			}
		}
	}

/*
	@ApiMethod(name = "executeTurn")
	public void executeTurn(
			@Named("sessionId") String sessionId,
			@Named("gameId") long gameId,
			@Named("roll") int roll,
			@Named("meepleId") long meepleId) {
		Session session = validateSession(sessionId);
		Game game = null;
		GameState gameState = null;
		AIPlayer AI = null;
		
		EntityManager mgr = getEntityManager();
		
		try {
			game = mgr.find(Game.class, gameId);
			gameState = game.getGameState();
			
			for (Meeple m : gameState.getMeeples()) {
				if (m.getId().getId() == meepleId) {
					// Figur bewegen
					if (m.getPosition() == 0 && roll == 6)
						m.moveFromHome();
					else
						m.moveBy(roll);
					
					// Spieler schlagen
					for (Meeple mo : gameState.getMeeples()) {
						if (mo.getColor() != m.getColor()) {
							if (mo.getPosition() == m.getPosition()) {
								mo.moveToHome();
							}
						}
					}
				}
			}
			
			// TODO: set next player turn
			if (roll != 6) {
				if (game.isSinglePlayer()) {
					// TODO: AI Turn
					AI = new AIPlayer(gameState);
					AI.play();
				} else {
					//gameState.nextPlayer();
				}
			}
		} finally {
			mgr.close();
			GCMSender.informUsers(getUserSessions(game), GCMSender.PLAYER_MOVE, session.getUser(), roll);
			
			if (AI != null) {
				for (Turn t : AI.getTurns()) {
					if (t.getValidTurns().size() > 0) {
						int AIRoll = t.getDice().get(t.getDice().size() - 1);
						GCMSender.informUsers(getUserSessions(game), GCMSender.COMPUTER_MOVE, AIRoll);
					}
				}
			}
		}
	}
*/

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
