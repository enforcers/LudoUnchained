package com.appspot.ludounchained;

import java.util.ArrayList;
import java.util.List;

import com.appspot.ludounchained.controllerEndpoint.model.Game;
import com.appspot.ludounchained.exception.RemoteException;
import com.appspot.ludounchained.util.BackgroundTask;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class LobbyBrowserActivity extends Activity {
	protected LudoUnchainedApplication appState;
	protected ListView mLobbyOverview;
	protected ArrayList<Game> mLobbyList = new ArrayList<Game>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lobby_browser);
		
		appState = (LudoUnchainedApplication) getApplicationContext();
		
		mLobbyOverview = (ListView) findViewById(R.id.lobby_overview);
		Log.v("SESSION", "S:" + appState.getSession().toString());
		fillGameOverview();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.lobby_browser, menu);
		return true;
	}
	
	public void refreshGames(MenuItem m) {
		fillGameOverview();
	}
	
	public void newGame(MenuItem m) {
		new BackgroundTask().new Task(this) {
			@Override
			protected Object doInBackground(Void... params) {
				try {
					return appState.getEndpoint().newGame(appState.getSession());
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return null;
			}
			
			@Override
			protected void onPostExecute(final Object result) {
				super.onPostExecute(result);
				
				if (result != null) {
					Game game = (Game)result;
					appState.setGame(game);
					startActivity(new Intent(getApplicationContext(), GameActivity.class));
				}
			}
		}.execute();
	}

	private void fillGameOverview() {
		new BackgroundTask().new Task(this) {
			@Override
			protected Object doInBackground(Void... params) {
				try {
					return appState.getEndpoint().listGames(appState.getSession());
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return null;
			}
			
			@SuppressWarnings("unchecked")
			@Override
			protected void onPostExecute(final Object result) {
				super.onPostExecute(result);
				
				if (result != null) {
					List<Game> games = (List<Game>)result;
					Log.v("LIST LOBBY COUNT", games.size() + " ");
					
					GameListAdapter gameListAdapter = new GameListAdapter(getApplicationContext(), games);
					mLobbyOverview.setAdapter(gameListAdapter);
				}
			}
		}.execute();		
	}
	
	public class GameListAdapter extends ArrayAdapter<Game> {
		private final Context context;
		private final List<Game> objects;

		public GameListAdapter(Context context, List<Game> objects) {
			super(context, R.layout.lobby_row, objects);
			this.context = context;
			this.objects = objects;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.lobby_row, parent, false);
			TextView lobbyName = (TextView) rowView.findViewById(R.id.lobby_user_cell);
			TextView lobbyPlayers = (TextView) rowView.findViewById(R.id.lobby_players_cell);

			Game game = objects.get(position);
			String gameName = game.getRedPlayer().getUsername() + "'s Game";
			int playerCount = game.getPlayerCount();

			lobbyName.setText(gameName);
			lobbyPlayers.setText(playerCount + "/4");
			
			return rowView;
		}
		
	}
	
	public void logout(MenuItem m) {
		new BackgroundTask().new SilentTask() {
			@Override
			protected Object doInBackground(Void... params) {
				try {
					appState.getEndpoint().logout(appState.getSession());
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return null;
			}
			
			@Override
			protected void onPostExecute(final Object result) {
				super.onPostExecute(result);

				SharedPreferences settings = getSharedPreferences("auth", 0);
				settings.edit().clear().commit();
				
				startActivity(new Intent(getApplicationContext(), MainActivity.class));
				finish();
			}
		}.execute();
	}
}