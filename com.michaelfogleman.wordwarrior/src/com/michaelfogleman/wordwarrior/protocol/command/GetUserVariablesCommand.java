package com.michaelfogleman.wordwarrior.protocol.command;

public class GetUserVariablesCommand extends Command {

	private String handle;

	public GetUserVariablesCommand(String handle) {
		this.handle = handle;
	}

	public String getHandle() {
		return handle;
	}

}
