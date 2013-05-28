package com.appspot.ludounchained;

import com.appspot.ludounchained.controllerEndpoint.model.Field;
import com.appspot.ludounchained.controllerEndpoint.model.GameState;
import com.appspot.ludounchained.util.GameStateDrawer;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class GameActivity extends Activity {
	protected LudoUnchainedApplication appState;
	protected GridView mGameStateGrid;
	protected ImageView mRollDice;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		appState = (LudoUnchainedApplication) getApplicationContext();
		mGameStateGrid = (GridView) findViewById(R.id.game_field);
		mRollDice = (ImageView) findViewById(R.id.roll_dice);
		
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game, menu);
		return true;
	}
	
	public void drawGameState() {
		GameState gameState = appState.getGame().getGameState();
		Log.v("GAMESTATE", gameState.toString());
		mGameStateGrid.setAdapter(new GameFieldAdapter(gameState));
	}
	
	public class GameFieldAdapter extends BaseAdapter {
		GameStateDrawer gsd;

		public GameFieldAdapter(GameState gameState) {
			super();
			gsd = new GameStateDrawer(gameState);
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
				if (gsd.getGameFieldMap().contains(position)) {
					drawableR = R.drawable.game_field_empty;
				}
			}
			
			imageView.setImageResource(drawableR);

			return imageView;
		}
		
	}

}
