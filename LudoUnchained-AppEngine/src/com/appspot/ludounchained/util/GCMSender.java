package com.appspot.ludounchained.util;

import java.io.IOException;
import java.util.List;

import com.appspot.ludounchained.model.Session;
import com.appspot.ludounchained.model.User;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

public class GCMSender {
	private static final String API_KEY = "AIzaSyChTYbh2W34v6sSVwea-V8INYbB8Vo-r5k";
	public static final int GAME_STARTED = 0;
	public static final int PLAYER_JOINED = 1;
	public static final int PLAYER_LEFT = 2;
	public static final int PLAYER_ACCEPTED = 3;
	public static final int PLAYER_MOVE = 4;
	public static final int COMPUTER_MOVE = 5;
	
	public static void informUsers(List<Session> receivers, int event) {
		String eventText = getEventText(event);
		
		for (Session session : receivers) {
			String regId = session.getRegistrationId();
			
			doSend(regId, eventText, true);
		}
	}
	
	public static void informUsers(List<Session> receivers, int event, User user, int diceRoll) {
		String eventText = String.format(getEventText(event), user.getUsername(), diceRoll);
		
		for (Session session : receivers) {
			String regId = session.getRegistrationId();
			
			doSend(regId, eventText, true);
		}
	}
	
	public static void informUsers(List<Session> receivers, int event, int diceRoll) {
		String eventText = String.format(getEventText(event), diceRoll);
		
		for (Session session : receivers) {
			String regId = session.getRegistrationId();
			
			doSend(regId, eventText, true);
		}
	}
	
	public static void informUsers(List<Session> receivers, int event, User user) {
		String eventText = String.format(getEventText(event), user.getUsername());
		
		for (Session session : receivers) {
			String regId = session.getRegistrationId();
			
			doSend(regId, eventText, true);
		}
	}
	
	public static void informUsers(List<Session> receivers, int event, User user1, User user2) {
		String eventText = String.format(getEventText(event), user1.getUsername(), user2.getUsername());
		
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
			case 1: return "%s is now spectating.";
			case 2: return "%s left the game.";
			case 3: return "%s has been accepted as active player.";
			case 4: return "%s rolled a %d and moved a meeple.";
			case 5: return "Computer rolled a %d and moved a meeple.";
			default: return null;
		}
	}
}
