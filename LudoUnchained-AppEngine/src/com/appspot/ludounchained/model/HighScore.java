package com.appspot.ludounchained.model;

public class HighScore implements Comparable<HighScore> {
	private String player;
	private int score;

	public HighScore (String player, int score){
		this.player = player;
		this.score = score;
	}
	
	public String getPlayer() {
		return player;
	}
	
	public int getScore() {
		return score;
	}

    @Override
    public int compareTo(HighScore score) {
    	Integer s = this.score;
        return s.compareTo(score.getScore());
    }
}
