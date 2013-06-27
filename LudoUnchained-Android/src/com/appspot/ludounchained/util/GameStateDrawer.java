package com.appspot.ludounchained.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.appspot.ludounchained.GameActivity;
import com.appspot.ludounchained.LudoUnchainedApplication;
import com.appspot.ludounchained.R;
import com.appspot.ludounchained.controllerEndpoint.model.Meeple;
import com.appspot.ludounchained.controllerEndpoint.model.Game;
import com.appspot.ludounchained.controllerEndpoint.model.GameState;
import com.appspot.ludounchained.controllerEndpoint.model.User;

public class GameStateDrawer extends BaseAdapter {
	
	private GameState gameState;
	private Game game;
	private Context context;
	private LudoUnchainedApplication appState;

	private List<Integer> gameMeepleMap;
	private HashMap<PlayerColor, List<Integer>> playerMeepleMap;
	private Meeple[] meeples;
	private Meeple[][] meepleMap;
	
	private List<Meeple> validMeeples = new ArrayList<Meeple>();
	
	public GameStateDrawer(Context context, Game game) {//GameState gameState) {
		super();
		
		this.context = context;
		this.appState = (LudoUnchainedApplication) context.getApplicationContext();
		this.gameMeepleMap = Arrays.asList(44,45,46,47,48,37,26,15,4,5,6,17,28,39,50,51,52,53,54,65,76,75,74,73,72,83,94,105,116,115,114,103,92,81,70,69,68,67,66,55);

		this.playerMeepleMap = new HashMap<PlayerColor, List<Integer>>();
		this.playerMeepleMap.put(PlayerColor.RED, Arrays.asList(0,1,11,12,56,57,58,59));
		this.playerMeepleMap.put(PlayerColor.BLUE, Arrays.asList(9,10,20,21,16,27,38,49));
		this.playerMeepleMap.put(PlayerColor.GREEN, Arrays.asList(108,109,119,120,64,63,62,61));
		this.playerMeepleMap.put(PlayerColor.YELLOW, Arrays.asList(99,100,110,111,104,93,82,71));
		
		update(game);
		
	}
	
	public void update(Game game) {
		this.game = game;
		this.gameState = game.getGameState();

		convertMeeples();
		drawUI();
		this.notifyDataSetChanged();
	}
	
	public void setValidMeeples(List<Meeple> validMeeples) {
		this.validMeeples = validMeeples;
	}
	
	private void convertMeeples() {
		meeples = new Meeple[121];
		for (Meeple meeple : gameState.getMeeples()) {
			int position = meeple.getPosition();
			PlayerColor color = PlayerColor.valueOf(meeple.getColor());
			
			if (position == 0) {
				// im Haus
				position = getNextEmptyStartField(color);
			} else if (position > 0 && position <= 40) {
				// im Feld
				position = this.gameMeepleMap.get(position - 1);
			} else if (position > 40 && position <= 44) {
				// im Ziel
				position = getPlayerEndMeepleMap(color).get(position - 40 - 1);
			} else {
				break;
			}
			
			meeples[position] = meeple; 
		}
	}
	
	private void drawUI() {
		GameActivity activity = (GameActivity) context;
		State state = State.valueOf(game.getState());
		User currentUser = appState.getSession().getUser();
		User turn = PlayerColor.valueOf(gameState.getTurnColor()).getPlayer(game);
		
		SparseBooleanArray menuItems = activity.getMenuItemsMap();

		switch (state) {
			case LOBBY:
				// only display start game if lobby leader
				menuItems.put(R.id.action_game_start, game.getRedPlayer().equals(currentUser));
				// only display request join, if user isn't already active player
				menuItems.put(R.id.action_game_request_join, !game.getPlayers().contains(currentUser));

				break;
			case RUNNING:
				// only display dice, if the current user has turn
				menuItems.put(R.id.action_game_dice_roll, turn.equals(currentUser));
				menuItems.put(R.id.action_game_start, false);
				menuItems.put(R.id.action_game_request_join, false);
				
				if(turn.equals(currentUser)) {
					activity.startTurnCountdown();
					appState.notify("It's your turn. Roll the dice!");
				}

				break;
			case FINISHED:
				menuItems.put(R.id.action_game_dice_roll, false);
				appState.notify("Game has been finished!");
				break;
		}
		
		activity.setMenuItemsMap(menuItems);
		activity.invalidateOptionsMenu();
	}
	
	@Override
	public int getCount() {
		return 121;
	}

	@Override
	public Object getItem(int position) {
		return getMeeple(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	@Override
	public boolean isEnabled(int position) {
		boolean valid = false;
		
		if (getItem(position) instanceof Meeple) {
			Meeple meeple = (Meeple) getItem(position);

			if (validMeeples != null) {
				for (Meeple m : validMeeples) {
					if ( meeple.getColor().equals(m.getColor()) && meeple.getPosition() == m.getPosition()) 
						valid = true;
				}
			}
		}
		
		return valid;
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

		Meeple meeple = getMeeple(position);

		int drawableR = R.drawable.game_field_placeholder;

		if (meeple != null) {
			switch (PlayerColor.valueOf(meeple.getColor())) {
				case BLUE: drawableR = R.drawable.game_field_blue; break;
				case GREEN: drawableR = R.drawable.game_field_green; break;
				case RED: drawableR = R.drawable.game_field_red; break;
				case YELLOW: drawableR = R.drawable.game_field_yellow; break;
			}
		} else {
			if (getGameMeepleMap().contains(position) || getAllPlayerMeepleMap().contains(position)) {
				drawableR = R.drawable.game_field_empty;
			}
		}
		
		imageView.setImageResource(drawableR);
		
		if (isEnabled(position)) {
			AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
			aa.setDuration(300);
			aa.setRepeatCount(Animation.INFINITE);
			aa.setRepeatMode(Animation.REVERSE);
			
			imageView.setAnimation(aa);
		} else {
			imageView.setAnimation(null);
		}

		return imageView;
	}
	
	public List<Integer> getGameMeepleMap() {
		return gameMeepleMap;
	}
	
	public List<Integer> getAllPlayerMeepleMap() {
		List<Integer> result = new ArrayList<Integer>();
		result.addAll(getPlayerMeepleMap(PlayerColor.RED));
		result.addAll(getPlayerMeepleMap(PlayerColor.BLUE));
		result.addAll(getPlayerMeepleMap(PlayerColor.GREEN));
		result.addAll(getPlayerMeepleMap(PlayerColor.YELLOW));
		
		return result;
	}
	
	public List<Integer> getPlayerMeepleMap(PlayerColor color) {
		return playerMeepleMap.get(color);
	}
	
	public int getNextEmptyStartField(PlayerColor color) {
		List<Integer> startFields = getPlayerStartMeepleMap(color);
		for (int pos : startFields) {
			if (this.meeples[pos] == null)
				return pos;
		}
		
		return 0;
	}
	
	public List<Integer> getPlayerStartMeepleMap(PlayerColor color) {
		return playerMeepleMap.get(color).subList(0, 4);
	}
	
	public List<Integer> getPlayerEndMeepleMap(PlayerColor color) {
		return playerMeepleMap.get(color).subList(4, 8);
	}
	
	public GameState getGameState() {
		return gameState;
	}
	
	public Meeple[][] getMeepleMap() {
		return meepleMap;
	}
	
	public Meeple[] getMeeples() {
		return meeples;
	}
	
	public Meeple getMeeple(int position) {
		return meeples[position];
	}
	
}
