package com.michaelfogleman.wordwarrior.model;

public class GameAdapter implements IGameListener {

	public void playerMoved(IGame game, IPlayer player, MoveResult moveResult) {
	}

	public void playerPassedTurn(IGame game, IPlayer player) {
	}
	
	public void playerExchangedTiles(IGame game, IPlayer player, Tile[] tiles, Tile[] newRack) {
	}
	
	public void playerChallenged(IGame game, IPlayer player, boolean successful, String[] words) {
	}
	
	public void playerUndidMove(IGame game, IPlayer player, MoveResult moveResult) {
	}
	
	public void playerClockExpired(IGame game, IPlayer player, boolean penalty) {
	}
	
	public void gameStateChanged(IGame game, GameState previous, GameState current) {
	}

}
