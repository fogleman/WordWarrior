package com.michaelfogleman.wordwarrior.protocol.command;

public class MatchCommand extends Command {

	private String handle;

	public MatchCommand(String handle) {
		this.handle = handle;
	}

	public String getHandle() {
		return handle;
	}

}
