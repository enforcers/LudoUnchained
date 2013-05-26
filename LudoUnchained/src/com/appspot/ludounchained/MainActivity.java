package com.appspot.ludounchained;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import com.appspot.ludounchained.controllerEndpoint.model.Session;
import com.appspot.ludounchained.util.BackgroundTask;
import com.appspot.ludounchained.util.EndpointService;

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
			
			new BackgroundTask().new SilentTask() {
				@Override
				protected Object doInBackground(Void... params) {
					return EndpointService.CALL.login(username, password);
				}
				
				@Override
				protected void onPostExecute(final Object result) {
					super.onPostExecute(result);
					
					if (result != null) {
						Session session = (Session)result;
						appState.setSession(session);
						
						startActivity(new Intent(getApplicationContext(), LobbyBrowserActivity.class));
						finish();
					} else {
						Log.v("Authentification", "Automated login failed");
						startActivity(new Intent(getApplicationContext(), LoginActivity.class));
					}
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
/*	
	public void doNewGame(View v) {
		new BackgroundTask().new Task(this) {
			@Override
			protected Object doInBackground(Void... params) {
				return EndpointService.CALL.newGame(mLoginSession);
			}
			
			@Override
			protected void onPostExecute(final Object result) {
				super.onPostExecute(result);
				
				if (result != null) {
					Game game = (Game)result;
					
					Log.v("NEW GAME", game.toString());
				}
			}
		}.execute();
	}
*/
}
