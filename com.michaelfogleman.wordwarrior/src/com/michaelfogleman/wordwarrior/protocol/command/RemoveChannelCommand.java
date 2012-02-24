package com.michaelfogleman.wordwarrior.protocol.command;

public class RemoveChannelCommand extends Command {
	
	private int channel;
	
	public RemoveChannelCommand(int channel) {
		this.channel = channel;
	}
	
	public int getChannel() {
		return channel;
	}

}
