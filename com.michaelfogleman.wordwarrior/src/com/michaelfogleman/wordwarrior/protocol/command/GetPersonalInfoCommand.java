package com.michaelfogleman.wordwarrior.protocol.command;

public class GetPersonalInfoCommand extends Command {

	private String handle;

	public GetPersonalInfoCommand(String handle) {
		this.handle = handle;
	}

	public String getHandle() {
		return handle;
	}

}
