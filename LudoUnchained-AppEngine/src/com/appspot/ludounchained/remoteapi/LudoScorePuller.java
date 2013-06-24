package com.appspot.ludounchained.remoteapi;

import com.appspot.ludounchained.EMF;
import com.appspot.ludounchained.model.Score;
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

    public LudoScorePuller(String username, String password) throws IOException {
        this.options = new RemoteApiOptions()
            .server("localhost", 8889)
            .credentials(username,password);
        RemoteApiInstaller installer = new RemoteApiInstaller();
        installer.install(options);
        try {
            options.reuseCredentials(username, installer.serializeCredentials());
        } finally {
            installer.uninstall();
        }
    }

   public List<Score> pullLudoScores() throws IOException {
        RemoteApiInstaller installer = new RemoteApiInstaller();
        installer.install(options);
        EntityManager em = EMF.get().createEntityManager();
        List<Score> scores = new ArrayList<Score>();
              
        try {
        	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        	Query query = new Query("select sum(s.score) as sum From Score s Group by s.player where s.gameId = 1 order by sum");
        	
        	PreparedQuery pq = ds.prepare(query);  
        	
            for (Entity result : pq.asIterable()) {
            	  String player = (String) result.getProperty("player");
            	  long points = (long)result.getProperty("score");
            	  scores.add(new Score(player,(int)points));
            }
            
        } finally {
            installer.uninstall();
            em.close();
        }
        return scores;
    }
	
}
