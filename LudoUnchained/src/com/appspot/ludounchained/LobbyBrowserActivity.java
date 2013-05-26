package com.appspot.ludounchained;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class LobbyBrowserActivity extends Activity {
	protected LudoUnchainedApplication appState;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lobby_browser);
		
		appState = (LudoUnchainedApplication) getApplicationContext();
		
		TextView sessionView = (TextView) findViewById(R.id.session_display);
		sessionView.setText(appState.getSession().toString());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.lobby_browser, menu);
		return true;
	}
	
	public void flushPreferences(View v) {
		SharedPreferences settings = getSharedPreferences("auth", 0);
		settings.edit().clear().commit();
		
		startActivity(new Intent(getApplicationContext(), MainActivity.class));
		finish();
	}

}
