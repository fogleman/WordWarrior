package com.michaelfogleman.wordwarrior.protocol.response;

import com.michaelfogleman.wordwarrior.model.Move;
import com.michaelfogleman.wordwarrior.model.Tile;

public class ObserveMoveResponse extends Response {
	
	private Move move;
	private int minutes;
	private int seconds;
	private Tile[] tiles;
	
	public ObserveMoveResponse(Move move, int minutes, int seconds, Tile[] tiles) {
		this.move = move;
		this.minutes = minutes;
		this.seconds = seconds;
		this.tiles = tiles;
	}
	
	public Move getMove() {
		return move;
	}
	
	public Tile[] getTiles() {
		return tiles;
	}
	
	public int getMinutes() {
		return minutes;
	}
	
	public int getSeconds() {
		return seconds;
	}
	
}
