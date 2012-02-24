package com.michaelfogleman.wordwarrior.protocol.command;

import com.michaelfogleman.wordwarrior.model.Match;
import com.michaelfogleman.wordwarrior.model.ITileRack;

public class AcceptMatchCommand extends Command {
	
	private Match match;
	private int firstPlayer;
	private ITileRack[] racks;
	
	public AcceptMatchCommand(Match match, int firstPlayer, ITileRack[] racks) {
		this.match = match;
		this.firstPlayer = firstPlayer;
		this.racks = racks;
	}
	
	public Match getMatch() {
		return match;
	}
	
	public int getFirstPlayer() {
		return firstPlayer;
	}
	
	public ITileRack[] getRacks() {
		return racks;
	}

}
