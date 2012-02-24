package com.michaelfogleman.wordwarrior.model;

import java.util.List;
import java.util.Map;

public interface ITilePool {
	
	public void reset();
	
	public int size();
	
	public boolean empty();
	
	public Tile remove();
	
	public List<Tile> getRemainingTiles();
	
	public void add(Tile tile);
	
	public void add(Tile[] tiles);
	
	public void remove(Tile tile);
	
	public void remove(Tile[] tiles);
	
	public int getFrequency(Tile tile);
	
	public Map<Character, Integer> getFrequencies();

}
