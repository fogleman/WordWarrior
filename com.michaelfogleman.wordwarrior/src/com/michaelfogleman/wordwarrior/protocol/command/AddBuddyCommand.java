package com.michaelfogleman.wordwarrior.protocol.command;

public class AddBuddyCommand extends Command {
	
	private String handle;
	
	public AddBuddyCommand(String handle) {
		this.handle = handle;
	}
	
	public String getHandle() {
		return handle;
	}

}
