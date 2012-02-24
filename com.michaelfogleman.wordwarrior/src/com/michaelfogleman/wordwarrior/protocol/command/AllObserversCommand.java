package com.michaelfogleman.wordwarrior.protocol.command;

public class AllObserversCommand extends Command {
	
	private String handle;
	
	public AllObserversCommand(String handle) {
		this.handle = handle;
	}
	
	public String getHandle() {
		return handle;
	}

}
