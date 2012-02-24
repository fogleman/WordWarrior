package com.michaelfogleman.wordwarrior.protocol.command;

public class MessageCommand extends Command {
	
	private String handle;
	private String message;
	
	public MessageCommand(String handle, String message) {
		this.handle = handle;
		this.message = message;
	}
	
	public String getHandle() {
		return handle;
	}
	
	public String getMessage() {
		return message;
	}

}
