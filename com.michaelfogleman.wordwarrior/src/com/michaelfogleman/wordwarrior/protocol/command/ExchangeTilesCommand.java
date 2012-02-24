package com.michaelfogleman.wordwarrior.protocol.command;

import com.michaelfogleman.wordwarrior.model.Tile;

public class ExchangeTilesCommand extends Command {
	
	private Tile[] tiles;
	private Tile[] newRack;
	private int minutes;
	private int seconds;
	
	public ExchangeTilesCommand(Tile[] tiles, Tile[] newRack, int minutes, int seconds) {
		this.tiles = tiles;
		this.newRack = newRack;
		this.minutes = minutes;
		this.seconds = seconds;
	}
	
	public Tile[] getTiles() {
		return tiles;
	}
	
	public Tile[] getNewRack() {
		return newRack;
	}
	
	public int getMinutes() {
		return minutes;
	}
	
	public int getSeconds() {
		return seconds;
	}

}
