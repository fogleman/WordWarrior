package com.michaelfogleman.wordwarrior.protocol.command;

public class AskCommand extends Command {
	
	private String message;
	
	public AskCommand(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}

}
