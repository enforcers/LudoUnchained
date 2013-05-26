package com.appspot.ludounchained;

import com.appspot.ludounchained.usercontrollerEndpoint.model.Game;
import com.appspot.ludounchained.usercontrollerEndpoint.model.Session;
import com.appspot.ludounchained.usercontrollerEndpoint.model.User;

import android.app.Application;
import android.content.Context;

public class LudoUnchainedApplication extends Application {
	private Session loginSession;
	private User user;
	private Game game;
	private static Context mContext;
	
	@Override
	public void onCreate() {
		super.onCreate();
		LudoUnchainedApplication.mContext = this;
	}
	
	public static Context getContext() {
		return mContext;
	}
	
	public Session getSession() {
		return loginSession;
	}
	
	public void setSession(Session session) {
		this.loginSession = session;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public Game getGame() {
		return game;
	}
	
	public void setGame(Game game) {
		this.game = game;
	}
}