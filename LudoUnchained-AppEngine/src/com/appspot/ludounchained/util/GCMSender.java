package com.appspot.ludounchained.util;

import java.io.IOException;
import java.util.List;

import com.appspot.ludounchained.model.Session;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

public class GCMSender {
	private static final String API_KEY = "AIzaSyChTYbh2W34v6sSVwea-V8INYbB8Vo-r5k";
	public static final int GAME_STARTED = 0;
	public static final int PLAYER_JOINED = 1;
	
	public static void informUsers(List<Session> receivers, int event) {
		String eventText = getEventText(event);
		
		for (Session session : receivers) {
			String regId = session.getRegistrationId();
			
			doSend(regId, eventText, true);
		}
	}

	private static Result doSend(String regId, String message, boolean sync) {
		Sender sender = new Sender(API_KEY);
		Message msg = new Message.Builder()
			.addData("message", message)
			.addData("sync", new Boolean(sync).toString())
			.build();
		
		Result result = null;

		try {
			result = sender.send(msg, regId, 5);
			System.out.println(result.getMessageId());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	private static String getEventText(int event) {
		switch (event) {
			case 0: return "The game was started!";
			case 1: return "Player %s is now spectating.";
			default: return null;
		}
	}
}
