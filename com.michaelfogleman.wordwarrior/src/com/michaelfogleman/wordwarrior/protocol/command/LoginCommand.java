package com.michaelfogleman.wordwarrior.protocol.command;

public class LoginCommand extends Command {

	private String handle;
	private String password;

	public LoginCommand(String handle, String password) {
		this.handle = handle;
		this.password = password;
	}

	public String getHandle() {
		return handle;
	}

	public String getPassword() {
		return password;
	}

}
