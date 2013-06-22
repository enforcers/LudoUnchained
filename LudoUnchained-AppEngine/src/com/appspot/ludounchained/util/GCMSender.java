package com.appspot.ludounchained.util;

import java.io.IOException;
import java.util.List;

import com.appspot.ludounchained.model.Session;
import com.appspot.ludounchained.model.Turn;
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
	public static final int PLAYER_BEAT = 5;
	public static final int PLAYER_HOUSE_START = 6;
	public static final int PLAYER_HOUSE_END = 7;
	public static final int PLAYER_REQUEST_JOIN = 8;
	public static final int PLAYER_NO_MOVE = 9;
	
	public static void informUsers(List<Session> receivers, Turn turn) {
		Turn.Event turnEvent = turn.getEvent();
		String eventText;

		switch (turnEvent) {
			case BEAT:
				eventText = String.format(
						getEventText(PLAYER_BEAT),
						turnEvent.getPlayer1().toString(),
						turn.getRoll(),
						turnEvent.getPlayer2().toString());
				break;
			case HOUSE_END:
				eventText = String.format(
						getEventText(PLAYER_HOUSE_END),
						turnEvent.getPlayer1().toString(),
						turn.getRoll());
				break;
			case HOUSE_START:
				eventText = String.format(
						getEventText(PLAYER_HOUSE_START),
						turnEvent.getPlayer1().toString(),
						turn.getRoll());
				break;
			case MOVE:
				eventText = String.format(
						getEventText(PLAYER_MOVE),
						turnEvent.getPlayer1().toString(),
						turn.getRoll());
				break;
			case NO_MOVE:
				eventText = String.format(
						getEventText(PLAYER_NO_MOVE),
						turnEvent.getPlayer1().toString());
				break;
			default: return;
		}
		
		for (Session session : receivers) {
			String regId = session.getRegistrationId();
			
			doSend(regId, eventText, true);
		}
	}
	
	public static void informJoinRequest(Session lobbyLeader, int event, Session session) {
		String eventText = getEventText(event);
		
		Sender sender = new Sender(API_KEY);
		Message msg = new Message.Builder()
			.addData("message", eventText)
			.addData("sync", "false")
			.addData("requestJoin", "true")
			.addData("requestJoinUser", session.getUser().getUsername())
			.addData("requestJoinSession", session.getSessionId())
			.build();
		
		try {
			sender.send(msg, lobbyLeader.getRegistrationId(), 5);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void informUsers(List<Session> receivers, int event) {
		String eventText = getEventText(event);
		
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

	private static Result doSend(String regId, String message, boolean sync) {
		Sender sender = new Sender(API_KEY);
		Message msg = new Message.Builder()
			.addData("message", message)
			.addData("sync", new Boolean(sync).toString())
			.build();
		
		Result result = null;

		try {
			result = sender.send(msg, regId, 5);
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
			case 4: return "%s rolled a %d and moved.";
			case 5: return "%s rolled a %d and beats %s.";
			case 6: return "%s rolled a %d and moved out of house.";
			case 7: return "%s rolled a %d and moved into final house.";
			case 8: return "A player wants to join your game.";
			case 9: return "%s can't move and skips this turn.";
			default: return null;
		}
	}
}
