package com.michaelfogleman.wordwarrior.protocol.command;

import com.michaelfogleman.wordwarrior.model.MoveResult;

public class UndoMoveCommand extends Command {
	
	private MoveResult moveResult;
	private int minutes;
	private int seconds;
	
	public UndoMoveCommand(MoveResult moveResult, int minutes, int seconds) {
		this.moveResult = moveResult;
		this.minutes = minutes;
		this.seconds = seconds;
	}
	
	public MoveResult getMoveResult() {
		return moveResult;
	}
	
	public int getMinutes() {
		return minutes;
	}
	
	public int getSeconds() {
		return seconds;
	}

}
