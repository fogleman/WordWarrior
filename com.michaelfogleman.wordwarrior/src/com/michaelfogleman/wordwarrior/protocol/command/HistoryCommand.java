package com.michaelfogleman.wordwarrior.protocol.command;

public class HistoryCommand extends Command {

	private String handle;
	
	public HistoryCommand() {
		this(null);
	}
	
	public HistoryCommand(String handle) {
		this.handle = handle;
	}

	public String getHandle() {
		return handle;
	}

}
