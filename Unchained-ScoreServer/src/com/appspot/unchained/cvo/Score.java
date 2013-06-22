package com.appspot.unchained.cvo;

public class Score {
	private String Player;
	private int score;
	public Score (com.appspot.unchained.model.Score score){
		this.score = score.getScore();
		this.Player = score.getPlayer();
	}
}
