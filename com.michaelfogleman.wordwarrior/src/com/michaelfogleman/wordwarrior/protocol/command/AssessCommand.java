package com.michaelfogleman.wordwarrior.protocol.command;

public class AssessCommand extends Command {
	
	private String handle;
	private int scoreDifference;
	
	public AssessCommand(String handle) {
		this(handle, 50);
	}
	
	public AssessCommand(String handle, int scoreDifference) {
		this.handle = handle;
		this.scoreDifference = scoreDifference;
	}
	
	public String getHandle() {
		return handle;
	}
	
	public int getScoreDifference() {
		return scoreDifference;
	}
	
}
