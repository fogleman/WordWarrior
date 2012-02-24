package com.michaelfogleman.wordwarrior.protocol.command;

public class ObserveCommand extends Command {
	
	private String handle;
	
	public ObserveCommand() {
	}
	
	public ObserveCommand(String handle) {
		this.handle = handle;
	}
	
	public String getHandle() {
		return handle;
	}

}
