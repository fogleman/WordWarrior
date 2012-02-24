package com.michaelfogleman.wordwarrior.protocol.command;

public class KibitzCommand extends Command {
	
	private String message;
	
	public KibitzCommand(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}

}
