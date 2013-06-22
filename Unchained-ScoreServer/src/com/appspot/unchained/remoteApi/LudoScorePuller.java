package com.appspot.unchained.remoteApi;

import com.appspot.unchained.EMF;
import com.appspot.unchained.model.Score;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.tools.remoteapi.RemoteApiInstaller;
import com.google.appengine.tools.remoteapi.RemoteApiOptions;
import java.io.IOException;

import javax.persistence.EntityManager;

public class LudoScorePuller {
    private final RemoteApiOptions options;

    public LudoScorePuller(String username, String password)
        throws IOException {
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
        EntityManager em = EMF.get().createEntityManager();
        
        Score testscore = new Score("test", 50,1);
        
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        Query query = new Query("Score");
        PreparedQuery pq = ds.prepare(query);
        //Transaction tx = ds.beginTransaction();        
        try {
            for (Entity result : pq.asIterable()) {
            	  String player = (String) result.getProperty("player");
            	  long points = (long)result.getProperty("score");
            	  Score score = new Score(player,(int)points,1);
            	  em.persist(score);
            	  
            	  ds.delete(result.getKey());
            }
            //tx.commit();
        } finally {
        	//tx.rollback();
            installer.uninstall();
            em.close();
        }
    }
	
}
