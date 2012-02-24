package com.michaelfogleman.wordwarrior.protocol.response;

import com.michaelfogleman.wordwarrior.model.Settings;

public class SetAllResponse extends Response {
	
	private Settings settings;
	
	public SetAllResponse(Settings settings) {
		this.settings = settings;
	}
	
	public Settings getSettings() {
		return settings;
	}
	
}
