package com.michaelfogleman.wordwarrior.model;

public interface IGameAction {
	
	public String getName();
	
	public int getIndex();
	public void setIndex(int index);
	
	public IPlayer getPlayer();
	public void setPlayer(IPlayer player);

}
