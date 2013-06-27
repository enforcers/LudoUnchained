package com.appspot.ludounchained;

/**
 * @author vfast, awakup
 */

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appspot.ludounchained.model.Game;
import com.appspot.ludounchained.model.Session;
import com.appspot.ludounchained.model.User;
import com.appspot.ludounchained.util.PlayerColor;

@SuppressWarnings({"serial", "unchecked"})
public class CronCleanup extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		EntityManager mgr = EMF.get().createEntityManager();
		
		try {
			// Remove sessions, that are unused for more than 30 minutes
			Query query = mgr.createQuery("select from Session as Session");
			List <Session> sessions = (List<Session>) query.getResultList();
			
			Date now = new Date();
			
			for (Session session : sessions) {
				TimeUnit timeUnit = TimeUnit.MINUTES;
				long timeDiff = timeUnit.convert(
						now.getTime() - session.getUpdatedAt().getTime(),
						TimeUnit.MILLISECONDS);
				
				if (timeDiff > 30) {
					mgr.remove(session);
				}
			}
			
			// Delete "dead" games, with no valid players
			query = mgr.createQuery("select from Game as Game");
			List<Game> games = (List<Game>) query.getResultList();
			for (Game game : games) {
				for (PlayerColor color : PlayerColor.values()) {
					if (game.getPlayer(color) != null) {
						if (getSession(game.getPlayer(color)) == null)
							game.setPlayer(color, null);
					}
				}
				
				if (game.getPlayerCount() == 0)
					mgr.remove(game);
			}
		} finally {
			mgr.close();
		}

		resp.setContentType("text/plain");
		resp.getWriter().println("Cleanup succeed.");
	}
	
	private Session getSession(User user) {
		EntityManager mgr = EMF.get().createEntityManager();
		Session session = null;
		
		if (user != null) {
			try {
				session = mgr.find(Session.class, user.getUsername());
			} finally {
				mgr.close();
			}
		}
		
		return session;
	}
}
