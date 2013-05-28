package com.appspot.ludounchained.endpoints;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.appspot.ludounchained.model.Game;
import com.appspot.ludounchained.model.GameState;
import com.appspot.ludounchained.model.Session;
import com.appspot.ludounchained.model.User;
import com.appspot.ludounchained.util.MD5;
import com.appspot.ludounchained.EMF;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.Key;
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
			game = new Game(session.getUser());
			mgr.persist(game);
			//gameState = game.getGameState();
		} finally {
			mgr.close();
		}
		
		result = game.getCVO();
		result.setGameState(game.getGameState().getCVO());

		return result;

	}

	@ApiMethod(name = "joinGame")
	public com.appspot.ludounchained.cvo.GameState joinGame(
			@Named("sessionId") String sessionId,
			Key gameId) {
		Session session = validateSession(sessionId);
		GameState gameState = null;
		
		EntityManager mgr = getEntityManager();
		try {
			Game game = mgr.find(Game.class, gameId);
			game.addSpectator(session.getUser());
			gameState = game.getGameState();
		} finally {
			mgr.close();
		}
		
		if (gameState != null) {
			return gameState.getCVO();
		} else {
			return null;
		}
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
		
		try {
			session = mgr.find(Session.class, user.getUsername());

			if (session == null) {
				session = new Session(user);
				mgr.persist(session);
			}
		} finally {
			mgr.close();
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
