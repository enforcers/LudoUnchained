package com.appspot.ludounchained.util;

import java.io.IOException;

import com.appspot.ludounchained.model.Session;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

public class GCMSender {
	private static final String API_KEY = "AIzaSyChTYbh2W34v6sSVwea-V8INYbB8Vo-r5k";

	public static Result doSend(Session session, String message) {
		Sender sender = new Sender(API_KEY);
		Message msg = new Message.Builder().addData("message", message).build();
		
		Result result = null;

		try {
			result = sender.send(msg, session.getRegistrationId(), 5);
			System.out.println(result.getMessageId());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
		
	}
}
