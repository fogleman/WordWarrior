package com.michaelfogleman.wordwarrior.protocol.command;

public class HelpCommand extends Command {

	private String topic;
	
	public HelpCommand() {
		this(null);
	}
	
	public HelpCommand(String topic) {
		this.topic = topic;
	}

	public String getTopic() {
		return topic;
	}

}
