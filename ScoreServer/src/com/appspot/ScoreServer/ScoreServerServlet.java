package com.appspot.ScoreServer;

import java.io.IOException;
import javax.servlet.http.*;


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
