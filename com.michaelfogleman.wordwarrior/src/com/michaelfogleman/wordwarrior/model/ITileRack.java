package com.michaelfogleman.wordwarrior.model;

public interface ITileRack {
	
	public int size();
	
	public boolean empty();
	
	public int capacity();
	
	public void add(Tile tile);
	
	public void add(int index, Tile tile);
	
	public void add(Tile[] tiles);
	
	public boolean remove(Tile tile);
	
	public void remove(Tile[] tiles);
	
	public Tile remove(int index);
	
	public Tile[] getTiles();
	
	public Tile[] fill(ITilePool pool);
	
	public void clear();
	
	public void replaceAll(ITilePool pool, Tile[] newTiles);
	
	public void replaceTop(ITilePool pool, Tile[] newTiles);
	
	public void shuffle();
	
	public boolean contains(Tile tile);

}
