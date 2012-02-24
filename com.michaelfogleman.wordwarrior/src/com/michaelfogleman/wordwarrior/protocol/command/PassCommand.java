package com.michaelfogleman.wordwarrior.protocol.command;

public class PassCommand extends Command {
	
	private int minutes;
	private int seconds;
	private boolean endGame;
	private int localScore;
	private int remoteScore;
	
	public PassCommand(int minutes, int seconds) {
		this(minutes, seconds, false, 0, 0);
	}
	
	public PassCommand(int minutes, int seconds, boolean endGame, int localScore, int remoteScore) {
		this.minutes = minutes;
		this.seconds = seconds;
		this.endGame = endGame;
		this.localScore = localScore;
		this.remoteScore = remoteScore;
	}
	
	public int getLocalScore() {
		return localScore;
	}
	
	public int getRemoteScore() {
		return remoteScore;
	}
	
	public boolean isEndGame() {
		return endGame;
	}
	
	public int getMinutes() {
		return minutes;
	}
	
	public int getSeconds() {
		return seconds;
	}

}
