package com.appspot.ludounchained;

/**
 * @author lmintert
 */

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import com.appspot.ludounchained.controllerEndpoint.model.Session;
import com.appspot.ludounchained.exception.RemoteException;
import com.appspot.ludounchained.util.BackgroundTask;

public class MainActivity extends Activity {
	protected LudoUnchainedApplication appState;
	protected TextView mSessionId;
	protected Session mLoginSession;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		appState = (LudoUnchainedApplication) getApplicationContext();

		SharedPreferences settings = getSharedPreferences("auth", 0);
		boolean rememberMe = settings.getBoolean("auto", false);

		if (rememberMe) {
			final String username = settings.getString("username", null);
			final String password = settings.getString("password", null);
			
			new BackgroundTask.SilentTask<Session>() {
				@Override
				protected Session doInBackground(Void... params) {
					try {
						return appState.getEndpoint().login(username, password);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					return null;
				}
				
				@Override
				protected void onPostExecute(final Session result) {
					super.onPostExecute(result);
					
					if (result != null) {
						startActivity(new Intent(getApplicationContext(), LobbyBrowserActivity.class));
					} else {
						Log.d(MainActivity.class.getName(), "Automated login failed");
						startActivity(new Intent(getApplicationContext(), LoginActivity.class));
					}
					
					finish();
				}
			}.execute();
			
		} else {
			startActivity(new Intent(getApplicationContext(), LoginActivity.class));
			finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
