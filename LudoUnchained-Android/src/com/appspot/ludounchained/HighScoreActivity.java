package com.appspot.ludounchained;

import java.util.List;

import com.appspot.ludounchained.LobbyBrowserActivity.GameListAdapter;
import com.appspot.ludounchained.controllerEndpoint.model.Game;
import com.appspot.ludounchained.controllerEndpoint.model.GameState;
import com.appspot.ludounchained.controllerEndpoint.model.Score;
import com.appspot.ludounchained.exception.RemoteException;
import com.appspot.ludounchained.util.BackgroundTask;
import com.appspot.ludounchained.util.State;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class HighScoreActivity extends Activity {
	protected LudoUnchainedApplication appState;
	protected ListView mScoreOverview;
	//protected ScoreListAdapter mScoreListAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_high_score);
		
		appState = (LudoUnchainedApplication) getApplicationContext();
		
		mScoreOverview = (ListView) findViewById(R.id.Highscore);
		fillScoreOverview();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.high_score, menu);
		return true;
	}
	
	private void fillScoreOverview() {
		new BackgroundTask.Task<List<Score>>(this) {
			
			@Override
			protected List<Score> doInBackground(Void... params) {
				try {
					return appState.getEndpoint().listScores();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return null;
			}
			
			@Override
			protected void onPostExecute(final List<Score> result) {
				super.onPostExecute(result);

				ScoreListAdapter mScoreListAdapter = new ScoreListAdapter(getApplicationContext(), result);
				mScoreOverview.setAdapter(mScoreListAdapter);
			}
		}.execute();		
	}
	
	public class ScoreListAdapter extends ArrayAdapter<Score> {
		private final Context context;
		private List<Score> objects;

		public ScoreListAdapter(Context context, List<Score> objects) {
			super(context, R.layout.lobby_row, objects);
			this.context = context;
			this.objects = objects;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.score_row, parent, false);

			Score score = objects.get(position);
			
			if (score != null){
				TextView place = (TextView) rowView.findViewById(R.id.score_row_place);
				TextView player = (TextView) rowView.findViewById(R.id.score_row_player);
				TextView scr = (TextView) rowView.findViewById(R.id.score_row_score);
				if (place != null){
					place.setText("1");
				}
				if (player != null){
					player.setText(score.getPlayer());
				}
				if (scr != null){
					scr.setText(score.getScore().toString());
				}
			}
			return rowView;
		}
		
	}
	
}

