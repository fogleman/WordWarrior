package com.michaelfogleman.wordwarrior.model;

public class RemotePlayer extends AbstractPlayer {
	
	public RemotePlayer(IGame game, int position, String name, ITileRack tileRack) {
		super(game, position, name, tileRack);
	}
	
	public boolean isLocal() {
		return false;
	}

}
