package com.michaelfogleman.wordwarrior.protocol.response;

import com.michaelfogleman.wordwarrior.model.IGame;

public class ObserveStartResponse extends Response {
	
	private IGame game;
	
	public ObserveStartResponse(IGame game) {
		this.game = game;
	}
	
	public IGame getGame() {
		return game;
	}
	
}
