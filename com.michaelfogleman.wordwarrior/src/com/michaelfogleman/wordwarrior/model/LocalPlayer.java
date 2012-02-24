package com.michaelfogleman.wordwarrior.model;

public class LocalPlayer extends AbstractPlayer {
	
	public LocalPlayer(IGame game, int position, String name, ITileRack tileRack) {
		super(game, position, name, tileRack);
	}
	
	public boolean isLocal() {
		return true;
	}

}
