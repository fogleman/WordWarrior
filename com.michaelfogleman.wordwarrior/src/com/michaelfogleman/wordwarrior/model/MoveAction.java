package com.michaelfogleman.wordwarrior.model;

public class MoveAction extends AbstractGameAction {
	
	private MoveResult moveResult;
	private Tile[] previousTiles;
	
	public MoveAction(MoveResult moveResult, Tile[] previousTiles) {
		this.moveResult = moveResult;
		this.previousTiles = previousTiles;
	}
	
	public String getName() {
		return "Move";
	}
	
	public Tile[] getPreviousTiles() {
		return previousTiles;
	}
	
	public MoveResult getMoveResult() {
		return moveResult;
	}
	
	public String toString() {
		return moveResult.toString();
	}

}
