package com.michaelfogleman.wordwarrior.protocol.command;

public class PingUserCommand extends Command {
	
	private String handle;
	
	public PingUserCommand(String handle) {
		this.handle = handle;
	}
	
	public String getHandle() {
		return handle;
	}

}
