package com.michaelfogleman.wordwarrior.protocol.command;

public class RemoveBuddyCommand extends Command {
	
	private String handle;
	
	public RemoveBuddyCommand(String handle) {
		this.handle = handle;
	}
	
	public String getHandle() {
		return handle;
	}

}
