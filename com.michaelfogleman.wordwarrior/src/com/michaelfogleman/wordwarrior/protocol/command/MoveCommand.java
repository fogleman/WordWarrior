package com.michaelfogleman.wordwarrior.protocol.command;

import com.michaelfogleman.wordwarrior.model.MoveResult;
import com.michaelfogleman.wordwarrior.model.ITileRack;

public class MoveCommand extends Command {
	
	private MoveResult result;
	private int minutes;
	private int seconds;
	private ITileRack rack;
	
	public MoveCommand(MoveResult result, int minutes, int seconds, ITileRack rack) {
		this.result = result;
		this.minutes = minutes;
		this.seconds = seconds;
		this.rack = rack;
	}
	
	public MoveResult getResult() {
		return result;
	}
	
	public int getMinutes() {
		return minutes;
	}
	
	public int getSeconds() {
		return seconds;
	}
	
	public ITileRack getRack() {
		return rack;
	}

}
// MOVE  H4 globe 20 24 13 cnnodwg