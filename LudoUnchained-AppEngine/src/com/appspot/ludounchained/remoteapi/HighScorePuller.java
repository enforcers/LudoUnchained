package com.appspot.ludounchained.remoteapi;

import com.appspot.ludounchained.EMF;
import com.appspot.ludounchained.model.HighScore;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.tools.remoteapi.RemoteApiInstaller;
import com.google.appengine.tools.remoteapi.RemoteApiOptions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
/**
 * gets the Highscore for Ludounchained from highscoreserver
 * @author clange
 *
 */
public class HighScorePuller {
    private final RemoteApiOptions options;

    public HighScorePuller() throws IOException {
    	String username ="ludounchained.fhm@gmail.com";
    	String password ="wifhm123";
        this.options = new RemoteApiOptions()
            .server("unchainedscoreserver.appspot.com", 443)
            .credentials(username,password);
        RemoteApiInstaller installer = new RemoteApiInstaller();
        installer.install(options);
        try {
            options.reuseCredentials(username, installer.serializeCredentials());
        } finally {
            installer.uninstall();
        }
    }

   public List<HighScore> pullLudoScores() throws IOException {
        RemoteApiInstaller installer = new RemoteApiInstaller();
        installer.install(options);
        EntityManager em = EMF.get().createEntityManager();
        List<HighScore> scores = new ArrayList<HighScore>();
              
        try {
        	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        	//Query query = new Query("select sum(s.score) as sum From Score s Group by s.player where s.gameId = 1 order by sum");
        	Query query = new Query("Score");
        	//query.addFilter("gameID", FilterOperator.EQUAL , 1);
        	PreparedQuery pq = ds.prepare(query);  
        	
        	
        	
            for (Entity result : pq.asIterable()) {
            	  String player = (String) result.getProperty("player");
            	  long points = (long)result.getProperty("score");
            	  scores.add(new HighScore(player,(int)points));
            	  
            }
            
            /*
            //testdaten
            scores.clear();
            scores.add(new RawScore("hans",25));
            scores.add(new RawScore("toni",20));
            */
            Collections.sort(scores);
            Collections.reverse(scores);
        } finally {
            installer.uninstall();
            em.close();
        }
        return scores;
    }
	
}
