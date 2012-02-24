package com.michaelfogleman.wordwarrior.protocol.command;

public class AddChannelCommand extends Command {
	
	private int channel;
	
	public AddChannelCommand(int channel) {
		this.channel = channel;
	}
	
	public int getChannel() {
		return channel;
	}

}
