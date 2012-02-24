package com.michaelfogleman.wordwarrior.protocol.response;

import com.michaelfogleman.wordwarrior.model.Tile;

public class ChangeResponse extends Response {
	
	private Tile[] tiles;
	private int minutes;
	private int seconds;
	private int count;
	
	public ChangeResponse(Tile[] tiles, int minutes, int seconds, int count) {
		this.tiles = tiles;
		this.minutes = minutes;
		this.seconds = seconds;
		this.count = count;
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
	
	public int getCount() {
		return count;
	}

}
