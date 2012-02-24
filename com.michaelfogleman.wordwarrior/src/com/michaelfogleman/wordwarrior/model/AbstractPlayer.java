package com.michaelfogleman.wordwarrior.model;

public abstract class AbstractPlayer implements IPlayer {
	
	private IGame game;
	private int position;
	private String name;
	private int rating;
	private Clock clock;
	private ITileRack tileRack;
	private int score;
	private int penalties;
	private IMoveMaker moveMaker;
	
	public AbstractPlayer(IGame game, int position, String name, ITileRack tileRack) {
		this.game = game;
		this.position = position;
		this.name = name;
		this.tileRack = tileRack;
		this.score = 0;
		this.clock = new Clock();
		game.addPlayer(this);
	}
	
	public IGame getGame() {
		return game;
	}
	
	public int getPosition() {
		return position;
	}

	public String getName() {
		return name;
	}

	public int getRating() {
		return rating;
	}
	
	public void setRating(int rating) {
		this.rating = rating;
	}

	public Clock getClock() {
		return clock;
	}

	public ITileRack getTileRack() {
		return tileRack;
	}

	public int getScore() {
		return score;
	}
	
	public void setScore(int score) {
		this.score = score;
	}
	
	public void incrementScore(int amount) {
		score += amount;
	}
	
	public void addPenalty() {
		penalties++;
		incrementScore(-10);
		clock.set(1, 0);
	}
	
	public int getPenalties() {
		return penalties;
	}

	public boolean isLocal() {
		return false;
	}

	public boolean isHuman() {
		return moveMaker == null;
		// TODO don't assume move maker means AI
	}
	
	public IMoveMaker getMoveMaker() {
		return moveMaker;
	}
	
	public void setMoveMaker(IMoveMaker moveMaker) {
		this.moveMaker = moveMaker;
	}
	
}
