package com.appspot.ludounchained;

import com.appspot.ludounchained.controllerEndpoint.model.Session;
import com.appspot.ludounchained.util.BackgroundTask;
import com.appspot.ludounchained.util.EndpointService;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

public class RegisterActivity extends Activity {
	protected LudoUnchainedApplication appState;
	protected EditText mUsername;
	protected EditText mPassword;
	protected CheckBox mRememberMe;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		appState = (LudoUnchainedApplication) getApplicationContext();
		
		mUsername = (EditText) findViewById(R.id.register_username);
		mPassword = (EditText) findViewById(R.id.register_password);
		mRememberMe = (CheckBox) findViewById(R.id.register_remember_me);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register, menu);
		return true;
	}
	
	public void doLogin(View v) {
		startActivity(new Intent(getApplicationContext(), LoginActivity.class));
		finish();
	}
	
	public void doRegister(View v) {
		final String username = mUsername.getText().toString();
		final String password = mPassword.getText().toString();

		new BackgroundTask().new Task(this) {
			@Override
			protected Object doInBackground(Void... params) {
				return EndpointService.CALL.register(username, password);
			}
			
			@Override
			protected void onPostExecute(final Object result) {
				super.onPostExecute(result);
				
				if (result != null) {
					Session session = (Session)result;
					appState.setSession(session);
					
					if (mRememberMe.isChecked()) {
						SharedPreferences settings = getSharedPreferences("auth", 0);
						SharedPreferences.Editor editor = settings.edit();

						editor.putBoolean("auto", true);
						editor.putString("username", username);
						editor.putString("password", password);
						
						editor.commit();
					}
					
					startActivity(new Intent(getApplicationContext(), LobbyBrowserActivity.class));
					finish();
				} else {
					// failed
				}
			}
		}.execute();
	}

}
