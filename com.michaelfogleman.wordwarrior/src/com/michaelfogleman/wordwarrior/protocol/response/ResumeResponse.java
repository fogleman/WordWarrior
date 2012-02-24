package com.michaelfogleman.wordwarrior.protocol.response;

import com.michaelfogleman.wordwarrior.model.IGame;

public class ResumeResponse extends Response {
	
	private IGame game;
	
	public ResumeResponse(IGame game) {
		this.game = game;
	}
	
	public IGame getGame() {
		return game;
	}
	
}
