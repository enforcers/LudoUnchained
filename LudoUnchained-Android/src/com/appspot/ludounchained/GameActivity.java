package com.appspot.ludounchained;

import com.appspot.ludounchained.controllerEndpoint.model.Field;
import com.appspot.ludounchained.controllerEndpoint.model.Game;
import com.appspot.ludounchained.exception.RemoteException;
import com.appspot.ludounchained.util.BackgroundTask;
import com.appspot.ludounchained.util.GameStateDrawer;
import com.appspot.ludounchained.util.State;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends Activity {
	protected LudoUnchainedApplication appState;
	protected GridView mGameStateGrid;
	protected GameStateDrawer mGameStateAdapter;
	protected ImageView mRollDice;
	protected BroadcastReceiver mReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		appState = (LudoUnchainedApplication) getApplicationContext();
		mGameStateGrid = (GridView) findViewById(R.id.game_field);
		mRollDice = null;//(ImageView) findViewById(R.id.roll_dice);
		
		drawGameState();
		
		mGameStateGrid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				Field field = (Field) parent.getItemAtPosition(position);
				Toast.makeText(getApplicationContext(), "POSITION: " + position + " ; " + field.getPosition(), Toast.LENGTH_SHORT).show();
			}
			
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		IntentFilter intentFilter = new IntentFilter("com.google.android.c2dm.intent.RECEIVE");
		
		mReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				ScrollView scrollView = (ScrollView) findViewById(R.id.game_event_scrollview);
				TextView eventView = (TextView) findViewById(R.id.game_event_view);
				eventView.append(intent.getStringExtra("message") + "\n");
				scrollView.smoothScrollTo(0, eventView.getBottom());
				
				refreshGameState();
			}
			
		};
		Log.v("GCM Receiver", "Registered to GameActivity");
		this.registerReceiver(mReceiver, intentFilter);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		Log.v("GCM Receiver", "Unregistered from GameActivity");
		this.unregisterReceiver(mReceiver);
	}

	@Override
	public void onBackPressed() {
		Game game = appState.getGame();
		State state = State.valueOf(game.getState());
		
		boolean isPlayer = game.getPlayers().contains(appState.getUser());

		if (state == State.RUNNING && isPlayer) {
			new AlertDialog.Builder(this)
				.setMessage("Are you sure you want to leave a running game? (You will get points substracted)")
				.setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						leaveGame();
					}
				})
				.setNegativeButton("Stay", null)
				.show();
		} else {
			leaveGame();
			super.onBackPressed();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game, menu);
		return true;
	}
	
	public void drawGameState() {
		Game game = appState.getGame();

		TextView redPlayer = (TextView) findViewById(R.id.game_playername_red);
		TextView bluePlayer = (TextView) findViewById(R.id.game_playername_blue);
		TextView greenPlayer = (TextView) findViewById(R.id.game_playername_green);
		TextView yellowPlayer = (TextView) findViewById(R.id.game_playername_yellow);
		
		if (game.getRedPlayer() != null)
			redPlayer.setText(game.getRedPlayer().getUsername());
		
		if (game.getBluePlayer() != null)
			bluePlayer.setText(game.getBluePlayer().getUsername());
		
		if (game.getGreenPlayer() != null)
			greenPlayer.setText(game.getGreenPlayer().getUsername());
		
		if (game.getYellowPlayer() != null)
			yellowPlayer.setText(game.getYellowPlayer().getUsername());

		if (mGameStateAdapter != null) { // check if recycle
			mGameStateAdapter.update(appState.getGame());
			mGameStateGrid.invalidate();
		} else {
			mGameStateAdapter = new GameStateDrawer(this, appState.getGame());
			mGameStateGrid.setAdapter(mGameStateAdapter);
		}
	}
	
	public void doGameStart(View v) {
		new BackgroundTask.SilentTask<Game>() {
			@Override
			protected Game doInBackground(Void... params) {
				try {
					return appState.getEndpoint().startGame(appState.getGame());
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return null;
			}
			
			@Override
			protected void onPostExecute(final Game result) {
				super.onPostExecute(result);
			}
		}.execute();
	}
	
	public void doRequestJoin(View v) {
		
	}
	
	public void leaveGame() {
		new BackgroundTask.SilentTask<Game>() {
			@Override
			protected Game doInBackground(Void... params) {
				try {
					return appState.getEndpoint().leaveGame(appState.getGame());
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return null;
			}
			
			@Override
			protected void onPostExecute(final Game result) {
				super.onPostExecute(result);
				finish();
			}
		}.execute();
	}
	
	public void refreshGameState() {
		new BackgroundTask.SilentTask<Game>() {
			@Override
			protected Game doInBackground(Void... params) {
				try {
					return appState.getEndpoint().getGame(appState.getGame());
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return null;
			}
			
			@Override
			protected void onPostExecute(final Game result) {
				super.onPostExecute(result);

				drawGameState();
			}
		}.execute();
	}
}
