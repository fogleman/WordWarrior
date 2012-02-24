package com.michaelfogleman.wordwarrior.model;

public interface IPlayer {
	
	public IGame getGame();
	
	public int getPosition();
	
	public String getName();
	
	public int getRating();
	
	public void setRating(int rating);
	
	public Clock getClock();
	
	public ITileRack getTileRack();
	
	public int getScore();
	
	public void setScore(int score);
	
	public void incrementScore(int amount);
	
	public void addPenalty();
	
	public int getPenalties();
	
	public boolean isLocal();
	
	public boolean isHuman();
	
	public void setMoveMaker(IMoveMaker moveMaker);
	
	public IMoveMaker getMoveMaker();

}
