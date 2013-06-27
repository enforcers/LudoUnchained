package com.appspot.ludounchained;

/**
 * @author lmintert
 */

import java.util.List;

import com.appspot.ludounchained.controllerEndpoint.model.HighScore;
import com.appspot.ludounchained.exception.RemoteException;
import com.appspot.ludounchained.util.BackgroundTask;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
/**
 * creates the highscore list
 * @author clange
 *
 */
public class HighScoreActivity extends Activity {
	protected LudoUnchainedApplication appState;
	protected ListView mScoreOverview;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_high_score);
		
		appState = (LudoUnchainedApplication) getApplicationContext();
		
		mScoreOverview = (ListView) findViewById(R.id.Highscore);
		fillScoreOverview();
	}
	
	private void fillScoreOverview() {
		new BackgroundTask.Task<List<HighScore>>(this) {
			
			@Override
			protected List<HighScore> doInBackground(Void... params) {
				try {
					return appState.getEndpoint().listScores();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return null;
			}
			
			@Override
			protected void onPostExecute(final List<HighScore> result) {
				super.onPostExecute(result);

				ScoreListAdapter mScoreListAdapter = new ScoreListAdapter(getApplicationContext(), result);
				mScoreOverview.setAdapter(mScoreListAdapter);
			}
		}.execute();		
	}
	
	public class ScoreListAdapter extends ArrayAdapter<HighScore> {
		private final Context context;
		private List<HighScore> objects;
		public ScoreListAdapter(Context context, List<HighScore> objects) {
			super(context, R.layout.lobby_row, objects);
			this.context = context;
			this.objects = objects;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.score_row, parent, false);
			Integer pos = position+1;
			HighScore score = objects.get(position);
			
			
			if (score != null){
				TextView place = (TextView) rowView.findViewById(R.id.score_row_place);
				TextView player = (TextView) rowView.findViewById(R.id.score_row_player);
				TextView scr = (TextView) rowView.findViewById(R.id.score_row_score);
				if (place != null){
					place.setText(pos.toString());
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

