package com.michaelfogleman.wordwarrior.protocol.command;

public class ExamineAdjournedCommand extends Command {
	
	private String handle;
	private int id;
	
	public ExamineAdjournedCommand(String handle, int id) {
		this.handle = handle;
		this.id = id;
	}
	
	public String getHandle() {
		return handle;
	}
	
	public int getId() {
		return id;
	}

}
