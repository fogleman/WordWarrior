package com.michaelfogleman.wordwarrior.protocol.command;

public class WhisperCommand extends Command {
	
	private String message;
	
	public WhisperCommand(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}

}
