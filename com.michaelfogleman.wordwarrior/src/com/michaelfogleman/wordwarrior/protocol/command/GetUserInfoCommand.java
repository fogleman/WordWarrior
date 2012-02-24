package com.michaelfogleman.wordwarrior.protocol.command;

public class GetUserInfoCommand extends Command {

	private String handle;

	public GetUserInfoCommand(String handle) {
		this.handle = handle;
	}

	public String getHandle() {
		return handle;
	}

}
