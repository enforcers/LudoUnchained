package com.appspot.ScoreServer;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.tools.remoteapi.RemoteApiInstaller;
import com.google.appengine.tools.remoteapi.RemoteApiOptions;
import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

/**
 * accesses the scoredata from gameserver and and adds scores/summarizes scores to the database
 * @author clange
 *
 */
public class LudoScorePuller {
    private final RemoteApiOptions options;

    public LudoScorePuller() throws IOException {
    	

    	String username ="ludounchained.fhm@gmail.com";
    	String password ="wifhm123";
        // Authenticating with username and password is slow, so we'll do it
        // once during construction and then store the credentials for reuse.
        this.options = new RemoteApiOptions()
            .server("localhost", 8888)
            .credentials(username,password);
        RemoteApiInstaller installer = new RemoteApiInstaller();
        installer.install(options);
        try {
            // Update the options with reusable credentials so we can skip
            // authentication on subsequent calls.
            options.reuseCredentials(username, installer.serializeCredentials());
        } finally {
            installer.uninstall();
        }
    }

   public void pullLudoScore() throws IOException {
        RemoteApiInstaller installer = new RemoteApiInstaller();
        installer.install(options);
 
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        Query query = new Query("Score");
        PreparedQuery pq = ds.prepare(query);
        
        //Transaction txn = ds.beginTransaction();
        
        try {
            for (Entity result : pq.asIterable()) {
            	  String player = (String) result.getProperty("player");
            	  long points = (long)result.getProperty("score");
            	  
            	  Score score = new Score(player,(int)points,1);
            	  summerize(score);
            	  ds.delete(result.getKey());
            }
        } finally {
            installer.uninstall();
        }
        
        
    }
   
	private void summerize(Score score){
		System.out.println("summerizing");

		EntityManager em = EMF.get().createEntityManager();
		TypedQuery<Score> q = em.createQuery("select s from Score s where s.player = :player and s.gameId = :gameId",Score.class);
		// set params
		q.setParameter("player", score.getPlayer());
		q.setParameter("gameId", score.getGameId());
		try{
			Score dbScore = q.getSingleResult();
			dbScore.addScore(score.getScore());
			System.out.println("Adding Score of " + score.getScore() + " for Player " + score.getPlayer() + " and game with id:" + score.getGameId());
			em.persist(dbScore);
		}catch (NoResultException e){
			System.out.println("Creating new Score for Player " + score.getPlayer() + " and game with id:" + score.getGameId());
			em.persist(score);
		}finally{
			em.close();
		}
			
		
	}
}
