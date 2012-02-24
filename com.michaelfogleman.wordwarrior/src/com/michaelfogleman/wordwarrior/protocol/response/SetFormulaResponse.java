package com.michaelfogleman.wordwarrior.protocol.response;

import com.michaelfogleman.wordwarrior.model.Settings;

public class SetFormulaResponse extends Response {
	
	private Settings settings;
	
	public SetFormulaResponse(Settings settings) {
		this.settings = settings;
	}
	
	public Settings getSettings() {
		return settings;
	}

}
