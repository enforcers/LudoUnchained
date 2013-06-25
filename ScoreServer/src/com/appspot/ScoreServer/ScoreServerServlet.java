package com.appspot.ScoreServer;

import java.io.IOException;
import javax.servlet.http.*;
/**
 * starts the scorepuller process to retrieve scores every 2 minutes
 * @author clange
 *
 */

@SuppressWarnings("serial")
public class ScoreServerServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		resp.getWriter().println("zack!");
		
		LudoScorePuller scorePuller = new LudoScorePuller();
		scorePuller.pullLudoScore();
	}
}
