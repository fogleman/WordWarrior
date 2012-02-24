package com.michaelfogleman.wordwarrior.protocol.command;

import com.michaelfogleman.wordwarrior.model.Match;

public class DeclineMatchCommand extends Command {
	
	private Match match;
	
	public DeclineMatchCommand(Match match) {
		this.match = match;
	}
	
	public Match getMatch() {
		return match;
	}

}
