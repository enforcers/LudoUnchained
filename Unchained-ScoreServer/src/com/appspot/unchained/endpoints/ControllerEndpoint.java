package com.appspot.unchained.endpoints;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.persistence.EntityManager;
import com.appspot.unchained.EMF;
import com.appspot.unchained.model.Score;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

@Api(name = "controllerEndpoint", namespace = @ApiNamespace(ownerDomain = "appspot.com", ownerName = "appspot.com", packagePath = "ludounchained"))
public class ControllerEndpoint {
	@SuppressWarnings("unchecked")
	@ApiMethod(name = "getLudoScore")
	
	// return the top 10
	public List<com.appspot.unchained.cvo.Score> getScore(@Named("username") String username,@Named("gameId") int gameId){
		EntityManager mgr = getEntityManager();
		List<Score> scores;
		List<com.appspot.unchained.cvo.Score> cvoScores = new ArrayList<com.appspot.unchained.cvo.Score>();
		try {
			//score = mgr.find(Score.class, username);
			scores = mgr.createQuery("select sum(s.score) From Score s Group by s.player where s.gameId = 1").setMaxResults(2).getResultList();
		} finally {
			mgr.close();
		}
		for(Score score:scores){
			cvoScores.add(new com.appspot.unchained.cvo.Score(score) );
		}
		return cvoScores;
	}
	
	private static EntityManager getEntityManager() {
		return EMF.get().createEntityManager();
	}
}
