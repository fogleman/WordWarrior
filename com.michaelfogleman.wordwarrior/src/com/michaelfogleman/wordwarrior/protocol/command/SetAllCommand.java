package com.michaelfogleman.wordwarrior.protocol.command;

import com.michaelfogleman.wordwarrior.model.Settings;

public class SetAllCommand extends Command {
	
	private Settings settings;
	
	public SetAllCommand(Settings settings) {
		this.settings = settings;
	}
	
	public Settings getSettings() {
		return settings;
	}

}
