package com.appspot.ludounchained;

import java.util.List;

import com.appspot.ludounchained.controllerEndpoint.model.Meeple;
import com.appspot.ludounchained.controllerEndpoint.model.Game;
import com.appspot.ludounchained.controllerEndpoint.model.Turn;
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
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
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
	protected Turn mDiceRoll;
	protected List<Integer> mDiceQue;
	protected int mCurrentDiceRoll = 0;

	protected SparseBooleanArray mMenuItems = new SparseBooleanArray();

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
				Meeple meeple = (Meeple) parent.getItemAtPosition(position);
				
				executeTurn(meeple);
				
				Toast.makeText(getApplicationContext(), "POSITION: " + position + " ; " + meeple.getPosition(), Toast.LENGTH_SHORT).show();
			}
			
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game, menu);
		
		menu.findItem(R.id.action_game_start).setVisible(mMenuItems.get(R.id.action_game_start, false));
		menu.findItem(R.id.action_game_request_join).setVisible(mMenuItems.get(R.id.action_game_request_join, false));
		menu.findItem(R.id.action_game_dice_roll).setVisible(mMenuItems.get(R.id.action_game_dice_roll, false));
		menu.findItem(R.id.action_game_back).setVisible(mMenuItems.get(R.id.action_game_back, true));
		
		Log.v("Dice Roll menu:", mCurrentDiceRoll + "");
		int drawable = R.drawable.ic_menu_dice;
		MenuItem diceRoll = menu.findItem(R.id.action_game_dice_roll);
		
		switch (mCurrentDiceRoll) {
			case 1: drawable = R.drawable.ic_menu_dice_1; break;
			case 2: drawable = R.drawable.ic_menu_dice_2; break;
			case 3: drawable = R.drawable.ic_menu_dice_3; break;
			case 4: drawable = R.drawable.ic_menu_dice_4; break;
			case 5: drawable = R.drawable.ic_menu_dice_5; break;
			case 6: drawable = R.drawable.ic_menu_dice_6; break;
		}
		
		diceRoll.setIcon(drawable);

		return true;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		IntentFilter intentFilter = new IntentFilter("com.google.android.c2dm.intent.RECEIVE");
		
		mReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				final Intent message = intent;
				final ScrollView scrollView = (ScrollView) findViewById(R.id.game_event_scrollview);
				final TextView eventView = (TextView) findViewById(R.id.game_event_view);

				if (message.getBooleanExtra("refresh", true)) { // TODO: boolean refresh check?
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
							eventView.append(message.getStringExtra("message") + "\n");
							scrollView.smoothScrollTo(0, eventView.getBottom());
						}
					}.execute();
				} else {
					eventView.append(message.getStringExtra("message") + "\n");
					scrollView.smoothScrollTo(0, eventView.getBottom());
				}
			}
			
		};
		this.registerReceiver(mReceiver, intentFilter);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
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
	
	public void doGameStart(MenuItem m) {
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
	
	public void doLeaveGame(MenuItem m) {
		onBackPressed();
	}
	
	public void doRequestJoin(MenuItem m) {
		
	}
	
	public void doDiceRoll(MenuItem m) {
		if (mDiceQue == null) {
			new BackgroundTask.SilentTask<Turn>() {
				@Override
				protected Turn doInBackground(Void... params) {
					try {
						return appState.getEndpoint().rollDice(appState.getGame());
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					return null;
				}
				
				@Override
				protected void onPostExecute(final Turn result) {
					super.onPostExecute(result);
					mDiceRoll = result;
					mDiceQue = mDiceRoll.clone().getDice();
					Log.v("Turn:", result.toString());
					
					getNextDiceRoll();
				}
			}.execute();
		} else {
			getNextDiceRoll();
		}
	}
	
	private void getNextDiceRoll() {
		if (mDiceRoll != null) {
			mGameStateAdapter.setValidMeeples(null);
		
			for (Integer roll : mDiceQue) {
	
				mCurrentDiceRoll = roll;
				invalidateOptionsMenu();
				
				if (mDiceQue.size() == 1 && mDiceRoll.getValidTurns() != null) {
					List<Meeple> validTurns = mDiceRoll.getValidTurns();
					
					if (validTurns.size() > 0) {
						mGameStateAdapter.setValidMeeples(validTurns);
						// TODO: Auswahl der Spielfigur
					}
				}
	
				mDiceQue.remove(roll);
				
				if (mDiceQue.size() == 0)
					mDiceQue = null;
	
				break;
			}
			
			mGameStateAdapter.notifyDataSetChanged();
			mGameStateGrid.invalidate();
		}
	}
	
	public void executeTurn(final Meeple meeple) {
		new BackgroundTask.SilentTask<Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				try {
					appState.getEndpoint().executeTurn(appState.getGame(), mDiceRoll, meeple);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return null;
			}
			
			@Override
			protected void onPostExecute(final Void result) {
				super.onPostExecute(result);
				
				mCurrentDiceRoll = 0;
				mGameStateAdapter.setValidMeeples(null);
				mGameStateAdapter.notifyDataSetChanged();
				mGameStateGrid.invalidate();
			}
		}.execute();
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
	
	public SparseBooleanArray getMenuItemsMap() {
		return mMenuItems;
	}
	
	public void setMenuItemsMap(SparseBooleanArray menuItems) {
		mMenuItems = menuItems;
	}
}
