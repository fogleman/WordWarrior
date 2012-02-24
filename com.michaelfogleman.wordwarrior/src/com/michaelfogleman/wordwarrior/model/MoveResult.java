package com.michaelfogleman.wordwarrior.model;

public class MoveResult implements Comparable<MoveResult> {
	
	private Move move;
	private int score;
	private String[] words;
	private Tile[] state;
	private Tile[] tiles;
	
	public MoveResult(Move move, int score, String[] words, Tile[] state, Tile[] tiles) {
		this.move = new Move(move);
		this.score = score;
		this.words = words;
		this.state = state;
		this.tiles = tiles;
	}
	
	public Move getMove() {
		return move;
	}
	
	public int getScore() {
		return score;
	}
	
	public String[] getWords() {
		return words;
	}
	
	public Tile[] getPreviousBoardState() {
		return state;
	}
	
	public Tile[] getTilesUsed() {
		return tiles;
	}
	
	public int compareTo(MoveResult that) {
		return that.score - this.score;
	}
	
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append(move);
		b.append(" ");
		b.append(score);
		b.append(" [");
		for (int i = 0; i < words.length; i++) {
			String word = words[i];
			b.append(word);
			if (i < words.length-1) b.append(",");
		}
		b.append("]");
		return b.toString();
	}
	
}
