package com.michaelfogleman.wordwarrior.model;

public interface IBoard {
	
	public int getWidth();
	
	public int getHeight();
	
	public int getWordMultiplier(int row, int column);
	
	public int getLetterMultiplier(int row, int column);
	
	public Tile getTile(int row, int column);
	
	public void setTile(int row, int column, Tile tile);
	
	public void removeTile(int row, int column);
	
	public int getTileCount();
	
	public void reset();
	
	public void addBoardListener(IBoardListener listener);
	
	public void removeBoardListener(IBoardListener listener);
	
	public void notifyListeners();

}
