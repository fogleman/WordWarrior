package com.michaelfogleman.wordwarrior.model;

public interface IMoveMaker {
	
	public IGameAction makeMove(IGame game, IPlayer player);
	
	public boolean challengeMove(IGame game, MoveResult result);

}
