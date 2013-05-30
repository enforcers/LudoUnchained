package com.appspot.ludounchained;

import com.appspot.ludounchained.controllerEndpoint.model.Field;
import com.appspot.ludounchained.controllerEndpoint.model.Game;
import com.appspot.ludounchained.util.GameStateDrawer;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends Activity {
	protected LudoUnchainedApplication appState;
	protected GridView mGameStateGrid;
	protected GameStateDrawer mGameStateAdapter;
	protected ImageView mRollDice;

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
	protected void onPause() {
		super.onPause();
		
/*
		new BackgroundTask().new SilentTask() {
			@Override
			protected Object doInBackground(Void... params) {
				try {
					return appState.getEndpoint().leaveGame(appState.getGame());
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
*/
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
			mGameStateAdapter.setGameState(appState.getGame().getGameState());
			mGameStateAdapter.notifyDataSetChanged();
			mGameStateGrid.invalidate();
		} else {
			mGameStateAdapter = new GameStateDrawer(getApplicationContext(), appState.getGame().getGameState());
			mGameStateGrid.setAdapter(mGameStateAdapter);
		}
	}
	
	public void doGameStart(View v) {
		
	}
/*
	public class GameFieldAdapter extends BaseAdapter {
		GameStateDrawer gsd;

		public GameFieldAdapter() {
			super();
			gsd = new GameStateDrawer(appState.getGame().getGameState());
		}

		@Override
		public int getCount() {
			return 121;
		}

		@Override
		public Object getItem(int position) {
			return gsd.getField(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
		
		@Override
		public boolean isEnabled(int position) {
			return getItem(position) instanceof Field ? true : false;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView;
			if (convertView == null) {  // if it's not recycled
			    imageView = new ImageView(getApplicationContext());
			    imageView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.WRAP_CONTENT, GridView.LayoutParams.WRAP_CONTENT));
			    imageView.setScaleType(ImageView.ScaleType.CENTER);
			} else {
			    imageView = (ImageView) convertView;
			}

			Field field = gsd.getField(position);

			int drawableR = R.drawable.game_field_placeholder;

			if (field != null) {
				switch (GameStateDrawer.PlayerColor.valueOf(field.getColor())) {
					case BLUE: drawableR = R.drawable.game_field_blue; break;
					case GREEN: drawableR = R.drawable.game_field_green; break;
					case RED: drawableR = R.drawable.game_field_red; break;
					case YELLOW: drawableR = R.drawable.game_field_yellow; break;
				}
			} else {
				if (gsd.getGameFieldMap().contains(position) || gsd.getAllPlayerFieldMap().contains(position)) {
					drawableR = R.drawable.game_field_empty;
				}
			}
			
			imageView.setImageResource(drawableR);

			return imageView;
		}
		
	}
*/
}
