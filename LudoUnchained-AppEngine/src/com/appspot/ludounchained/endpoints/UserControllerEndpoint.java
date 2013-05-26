package com.appspot.ludounchained.endpoints;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.appspot.ludounchained.model.Game;
import com.appspot.ludounchained.model.Session;
import com.appspot.ludounchained.model.User;
import com.appspot.ludounchained.util.MD5;
import com.appspot.ludounchained.EMF;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.datanucleus.query.JPACursorHelper;

@Api(name = "usercontrollerEndpoint", namespace = @ApiNamespace(ownerDomain = "appspot.com", ownerName = "appspot.com", packagePath = "ludounchained"))
public class UserControllerEndpoint {

	@ApiMethod(name = "login")
	public Session login(@Named("username") String username, @Named("password") String password) {
		EntityManager mgr = getEntityManager();
		User user = null;
		try {
			user = mgr.find(User.class, username);
		} finally {
			mgr.close();
		}

		if (user != null && user.getPassword().equals(MD5.encrypt(password))) {
			return getSession(user);
		} else {
			return null;
		}
	}
	
	@ApiMethod(name = "register")
	public Session register(User user) {
		EntityManager mgr = getEntityManager();
		try {
			if (containsUser(user)) {
				throw new EntityExistsException("Object already exists");
			}

			mgr.persist(user);
		} finally {
			mgr.close();
		}

		return getSession(user);
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listGames")
	public CollectionResponse<Game> listGames(
			Session session,
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("limit") Integer limit) {

		validateSession(session);

		EntityManager mgr = null;
		Cursor cursor = null;
		List<Game> execute = null;
		
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
			for (Game obj : execute)
				;
		} finally {
			mgr.close();
		}
		
		return CollectionResponse.<Game> builder().setItems(execute)
				.setNextPageToken(cursorString).build();
	}
	
	@ApiMethod(name = "newGame")
	public Game newGame(Session session) {
		validateSession(session);
		Game game = null;

		EntityManager mgr = getEntityManager();
		try {
			game = new Game(session.getUser());
			mgr.persist(game);
		} finally {
			mgr.close();
		}

		return game;
	}
	
	private boolean containsUser(User user) {
		EntityManager mgr = getEntityManager();
		boolean contains = true;

		try {
			User item = mgr.find(User.class, user.getUsername());
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
	
	private void validateSession(Session session) {
		EntityManager mgr = getEntityManager();
		
		try {
			session = mgr.find(Session.class, session.getSessionId());
		} finally {
			mgr.close();
		}
		
		if (session == null) {
			//throw new Exception("Invalid session");
			/**
			 * Implement global exception handling 
			 */
		}
	}

	private static EntityManager getEntityManager() {
		return EMF.get().createEntityManager();
	}
}
