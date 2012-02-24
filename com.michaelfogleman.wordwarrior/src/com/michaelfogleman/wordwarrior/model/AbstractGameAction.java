package com.michaelfogleman.wordwarrior.model;

public abstract class AbstractGameAction implements IGameAction {
	
	private int index;
	private IPlayer player;
	
	public int getIndex() {
		return index;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public IPlayer getPlayer() {
		return player;
	}
	
	public void setPlayer(IPlayer player) {
		this.player = player;
	}

}
