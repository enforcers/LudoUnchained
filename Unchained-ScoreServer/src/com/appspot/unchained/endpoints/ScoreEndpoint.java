package com.appspot.unchained.endpoints;

import com.appspot.unchained.EMF;
import com.appspot.unchained.model.Score;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.datanucleus.query.JPACursorHelper;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityManager;
import javax.persistence.Query;

@Api(name = "scoreendpoint", namespace = @ApiNamespace(ownerDomain = "appspot.com", ownerName = "appspot.com", packagePath = "unchained.model"))
public class ScoreEndpoint {

	/**
	 * This method lists all the entities inserted in datastore.
	 * It uses HTTP GET method and paging support.
	 *
	 * @return A CollectionResponse class containing the list of all entities
	 * persisted and a cursor to the next page.
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listScore")
	public CollectionResponse<com.appspot.unchained.cvo.Score> listScore(
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("limit") Integer limit) {

		EntityManager mgr = null;
		Cursor cursor = null;
		List<Score> execute = null;

		List<com.appspot.unchained.cvo.Score> cvoScores = new ArrayList<com.appspot.unchained.cvo.Score>();
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
			//
			for(Score score:execute){
				cvoScores.add(new com.appspot.unchained.cvo.Score(score) );
			}
			//
			cursor = JPACursorHelper.getCursor(execute);
			if (cursor != null)
				cursorString = cursor.toWebSafeString();

			// Tight loop for fetching all entities from datastore and accomodate
			// for lazy fetch.
			for (com.appspot.unchained.cvo.Score obj : cvoScores)
				;
		} finally {
			mgr.close();
		}

		return CollectionResponse.<com.appspot.unchained.cvo.Score> builder().setItems(cvoScores)
				.setNextPageToken(cursorString).build();
	}

	private static EntityManager getEntityManager() {
		return EMF.get().createEntityManager();
	}

}
