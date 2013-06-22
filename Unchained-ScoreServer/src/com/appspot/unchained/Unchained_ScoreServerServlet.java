package com.appspot.unchained;

import java.io.IOException;
import javax.servlet.http.*;

import com.appspot.unchained.remoteApi.LudoScorePuller;

@SuppressWarnings("serial")
public class Unchained_ScoreServerServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		resp.getWriter().println("Hello, world");
		
		LudoScorePuller puller = new LudoScorePuller("test@example.com","");
		puller.pullLudoScore();
		
	}
	
}
