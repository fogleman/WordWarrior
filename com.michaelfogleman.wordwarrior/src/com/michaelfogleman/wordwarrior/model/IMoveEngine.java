package com.michaelfogleman.wordwarrior.model;

public interface IMoveEngine {
	
	public MoveResult doMove(IBoard board, Move move);
	
	public MoveResult testMove(IBoard board, Move move);
	
	public void undoMove(IBoard board, MoveResult move);
	
	public boolean isValidMove(IBoard board, Move move);
	
	public int getScore(IBoard board, Move move);
	
	public ITileValues getTileValues();

}
