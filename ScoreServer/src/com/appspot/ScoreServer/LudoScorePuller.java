package com.appspot.ScoreServer;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.tools.remoteapi.RemoteApiInstaller;
import com.google.appengine.tools.remoteapi.RemoteApiOptions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

public class LudoScorePuller {
    private final RemoteApiOptions options;

    public LudoScorePuller() throws IOException {
    	

    	String username ="test@test.com";
    	String password ="";
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
        
        ArrayList<Score> scores = new ArrayList<Score>();

        try {
            for (Entity result : pq.asIterable()) {
            	  String player = (String) result.getProperty("player");
            	  long points = (long)result.getProperty("score");
            	  
            	  Score score = new Score(player,(int)points,1);
            	  scores.add(score);
            	  
            	  ds.delete(result.getKey());
            }
        } finally {
            installer.uninstall();
        }
        
        EntityManager em = EMF.get().createEntityManager();
        summerize(scores);
        try {
        	
        	for (Score score : scores) {
        		em.persist(score);
        	}
        } finally {
        	em.close();
        }
    }
	private void summerize(List<Score> scores){
		System.out.println("summerizing");
		List<Score> sumscores = new ArrayList<Score>();
		for(Score score:scores){
			boolean hit = false;
			System.out.println(score.getPlayer());
			for(Score sumscore:sumscores){
				if(score.getPlayer().equals(sumscore.getPlayer())){
					sumscore.addScore(score.getScore());
					hit = true;
				}
			}
			
			if(hit==false){
				sumscores.add(score);
			}
		}
		
		EntityManager em = EMF.get().createEntityManager();
		for(Score score:sumscores){
			summerizedScore sumscore = new summerizedScore(score);
			em.persist(sumscore);
		}
		em.close();
	}
}
