package com.appspot.ludounchained.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.appspot.ludounchained.LudoUnchainedApplication;
import com.appspot.ludounchained.R;
import com.appspot.ludounchained.controllerEndpoint.model.Field;
import com.appspot.ludounchained.controllerEndpoint.model.Game;
import com.appspot.ludounchained.controllerEndpoint.model.GameState;
import com.appspot.ludounchained.controllerEndpoint.model.User;

public class GameStateDrawer extends BaseAdapter {
	public enum PlayerColor { RED, BLUE, GREEN, YELLOW }
	
	private GameState gameState;
	private Game game;
	private Context context;
	private LudoUnchainedApplication appState;

	private List<Integer> gameFieldMap;
	private HashMap<PlayerColor, List<Integer>> playerFieldMap;
	private Field[] fields = new Field[121];
	private Field[][] fieldMap;
	
	public GameStateDrawer(Context context, Game game) {//GameState gameState) {
		super();
		
		this.context = context;
		this.appState = (LudoUnchainedApplication) context.getApplicationContext();
		this.gameFieldMap = Arrays.asList(44,45,46,47,48,37,26,15,4,5,6,17,28,39,50,51,52,53,54,65,76,75,74,73,72,83,94,105,116,115,114,103,92,81,70,69,68,67,66,55);

		this.playerFieldMap = new HashMap<PlayerColor, List<Integer>>();
		this.playerFieldMap.put(PlayerColor.RED, Arrays.asList(0,1,11,12,56,57,58,59));
		this.playerFieldMap.put(PlayerColor.BLUE, Arrays.asList(9,10,20,21,16,27,38,49));
		this.playerFieldMap.put(PlayerColor.GREEN, Arrays.asList(108,109,119,120,64,63,62,61));
		this.playerFieldMap.put(PlayerColor.YELLOW, Arrays.asList(99,100,110,111,104,93,82,71));
		
		update(game);
		
	}
	
	public void update(Game game) {
		this.game = game;
		this.gameState = game.getGameState();
		convertFields();
		drawUI();
		this.notifyDataSetChanged();
	}
	
	private void convertFields() {
		for (Field field : gameState.getFields()) {
			int position = field.getPosition();
			PlayerColor color = PlayerColor.valueOf(field.getColor());
			
			if (position == 0) {
				// im Haus
				position = getNextEmptyStartField(color);
			} else if (position > 0 && position <= 40) {
				// im Feld
				position = this.gameFieldMap.get(position);
			} else if (position > 40 && position <= 44) {
				// im Ziel
				position = getPlayerEndFieldMap(color).get(position - 40);
			} else {
				break;
			}
			
			fields[position] = field; 
		}
	}
	
	private void drawUI() {
		Activity activity = (Activity) context;
		State state = State.valueOf(game.getState());
		User currentUser = appState.getSession().getUser();
		
		Button startGame = (Button) activity.findViewById(R.id.game_start);
		Button requestJoin = (Button) activity.findViewById(R.id.game_request_join);
		
		switch (state) {
			case LOBBY:
				if (game.getRedPlayer().equals(currentUser)) // check if lobby leader
					startGame.setVisibility(View.VISIBLE);
				
				if (! game.getPlayers().contains(currentUser))
					requestJoin.setVisibility(View.VISIBLE);

				break;
			case RUNNING:
				break;
			case FINISHED:
				break;
		}
	}
	
	@Override
	public int getCount() {
		return 121;
	}

	@Override
	public Object getItem(int position) {
		return getField(position);
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
		    imageView = new ImageView(appState);
		    imageView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.WRAP_CONTENT, GridView.LayoutParams.WRAP_CONTENT));
		    imageView.setScaleType(ImageView.ScaleType.CENTER);
		} else {
		    imageView = (ImageView) convertView;
		}

		Field field = getField(position);

		int drawableR = R.drawable.game_field_placeholder;

		if (field != null) {
			switch (GameStateDrawer.PlayerColor.valueOf(field.getColor())) {
				case BLUE: drawableR = R.drawable.game_field_blue; break;
				case GREEN: drawableR = R.drawable.game_field_green; break;
				case RED: drawableR = R.drawable.game_field_red; break;
				case YELLOW: drawableR = R.drawable.game_field_yellow; break;
			}
		} else {
			if (getGameFieldMap().contains(position) || getAllPlayerFieldMap().contains(position)) {
				drawableR = R.drawable.game_field_empty;
			}
		}
		
		imageView.setImageResource(drawableR);

		return imageView;
	}
	
	public List<Integer> getGameFieldMap() {
		return gameFieldMap;
	}
	
	public List<Integer> getAllPlayerFieldMap() {
		List<Integer> result = new ArrayList<Integer>();
		result.addAll(getPlayerFieldMap(PlayerColor.RED));
		result.addAll(getPlayerFieldMap(PlayerColor.BLUE));
		result.addAll(getPlayerFieldMap(PlayerColor.GREEN));
		result.addAll(getPlayerFieldMap(PlayerColor.YELLOW));
		
		return result;
	}
	
	public List<Integer> getPlayerFieldMap(PlayerColor color) {
		return playerFieldMap.get(color);
	}
	
	public int getNextEmptyStartField(PlayerColor color) {
		List<Integer> startFields = getPlayerStartFieldMap(color);
		for (int pos : startFields) {
			if (this.fields[pos] == null)
				return pos;
		}
		
		return 0;
	}
	
	public List<Integer> getPlayerStartFieldMap(PlayerColor color) {
		return playerFieldMap.get(color).subList(0, 4);
	}
	
	public List<Integer> getPlayerEndFieldMap(PlayerColor color) {
		return playerFieldMap.get(color).subList(4, 8);
	}
	
	public GameState getGameState() {
		return gameState;
	}
	
	public Field[][] getFieldMap() {
		return fieldMap;
	}
	
	public Field[] getFields() {
		return fields;
	}
	
	public Field getField(int position) {
		return fields[position];
	}
	
}
