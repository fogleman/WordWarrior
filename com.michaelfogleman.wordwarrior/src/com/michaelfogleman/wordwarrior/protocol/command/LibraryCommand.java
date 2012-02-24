package com.michaelfogleman.wordwarrior.protocol.command;

public class LibraryCommand extends Command {

	private String handle;
	
	public LibraryCommand() {
		this(null);
	}
	
	public LibraryCommand(String handle) {
		this.handle = handle;
	}

	public String getHandle() {
		return handle;
	}

}
