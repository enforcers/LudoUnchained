package com.appspot.ludounchained;

import java.util.List;

import com.appspot.ludounchained.controllerEndpoint.model.Game;
import com.appspot.ludounchained.exception.RemoteException;
import com.appspot.ludounchained.util.BackgroundTask;
import com.appspot.ludounchained.util.State;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class LobbyBrowserActivity extends Activity {
	protected LudoUnchainedApplication appState;
	protected ListView mLobbyOverview;
	protected GameListAdapter mGameListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lobby_browser);
		
		appState = (LudoUnchainedApplication) getApplicationContext();
		
		mLobbyOverview = (ListView) findViewById(R.id.lobby_overview);
		Log.v("SESSION", "S:" + appState.getSession().toString());
		//fillGameOverview();

		final LobbyBrowserActivity _this = this;
		mLobbyOverview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				final Game game = (Game) parent.getItemAtPosition(position);
				
				new BackgroundTask.Task<Game>(_this) {
					@Override
					protected Game doInBackground(Void... params) {
						try {
							return appState.getEndpoint().joinGame(game);
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						return null;
					}
					
					@Override
					protected void onPostExecute(final Game result) {
						super.onPostExecute(result);
						
						if (result != null) {
							startActivity(new Intent(getApplicationContext(), GameActivity.class));
						}
					}
				}.execute();
			}
			
		});
	}
	
	@Override
	public void onResume() {
		super.onResume();
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
		new BackgroundTask.Task<Game>(this) {
			@Override
			protected Game doInBackground(Void... params) {
				try {
					return appState.getEndpoint().newGame();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return null;
			}
			
			@Override
			protected void onPostExecute(final Game result) {
				super.onPostExecute(result);
				
				if (result != null) {
					startActivity(new Intent(getApplicationContext(), GameActivity.class));
				}
			}
		}.execute();
	}

	private void fillGameOverview() {
		new BackgroundTask.Task<List<Game>>(this) {
			@Override
			protected List<Game> doInBackground(Void... params) {
				try {
					return appState.getEndpoint().listGames();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return null;
			}
			
			@Override
			protected void onPostExecute(final List<Game> result) {
				super.onPostExecute(result);
				
				if (mGameListAdapter == null) {
					if (result != null) {
						mGameListAdapter = new GameListAdapter(getApplicationContext(), result);
						mLobbyOverview.setAdapter(mGameListAdapter);
					}
				} else {
					mGameListAdapter.setGames(result);
					mGameListAdapter.notifyDataSetChanged();
					mLobbyOverview.invalidate();
				}
			}
		}.execute();		
	}
	
	public class GameListAdapter extends ArrayAdapter<Game> {
		private final Context context;
		private List<Game> objects;

		public GameListAdapter(Context context, List<Game> objects) {
			super(context, R.layout.lobby_row, objects);
			this.context = context;
			this.objects = objects;
		}
		
		public void setGames(List<Game> objects) {
			this.objects = objects;
		}
		
		@Override
		public Game getItem(int position) {
			return objects.get(position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.lobby_row, parent, false);

			ImageView lobbyState = (ImageView) rowView.findViewById(R.id.lobby_state);
			TextView lobbyName = (TextView) rowView.findViewById(R.id.lobby_row_name);
			TextView redPlayer = (TextView) rowView.findViewById(R.id.lobby_player_red);
			TextView bluePlayer = (TextView) rowView.findViewById(R.id.lobby_player_blue);
			TextView greenPlayer = (TextView) rowView.findViewById(R.id.lobby_player_green);
			TextView yellowPlayer = (TextView) rowView.findViewById(R.id.lobby_player_yellow);

			Game game = objects.get(position);
			String username = game.getRedPlayer().getUsername();
			int playerCount = game.getPlayerCount();
			State state = State.valueOf(game.getState());
			
			switch (state) {
				case RUNNING: lobbyState.setImageResource(R.drawable.ic_lobby_full); break;
				default: lobbyState.setImageResource(R.drawable.ic_lobby_open); break;
			}

			lobbyName.setText(username + "'s Game (" + playerCount + "/4)");
			
			if (game.getRedPlayer() != null)
				redPlayer.setText(game.getRedPlayer().getUsername());
			
			if (game.getBluePlayer() != null)
				bluePlayer.setText(game.getBluePlayer().getUsername());
			
			if (game.getGreenPlayer() != null)
				greenPlayer.setText(game.getGreenPlayer().getUsername());
			
			if (game.getYellowPlayer() != null)
				yellowPlayer.setText(game.getYellowPlayer().getUsername());

			return rowView;
		}
		
	}
	
	public void logout(MenuItem m) {
		new BackgroundTask.SilentTask<Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				try {
					appState.getEndpoint().logout();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return null;
			}
			
			@Override
			protected void onPostExecute(final Void result) {
				super.onPostExecute(result);

				SharedPreferences settings = getSharedPreferences("auth", 0);
				settings.edit().clear().commit();
				
				startActivity(new Intent(getApplicationContext(), MainActivity.class));
				finish();
			}
		}.execute();
	}
}