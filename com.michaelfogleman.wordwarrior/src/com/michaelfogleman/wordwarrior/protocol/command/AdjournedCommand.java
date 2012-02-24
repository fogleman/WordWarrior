package com.michaelfogleman.wordwarrior.protocol.command;

public class AdjournedCommand extends Command {

	private String handle;
	
	public AdjournedCommand() {
		this(null);
	}
	
	public AdjournedCommand(String handle) {
		this.handle = handle;
	}

	public String getHandle() {
		return handle;
	}

}
