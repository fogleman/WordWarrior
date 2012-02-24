package com.michaelfogleman.wordwarrior.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public abstract class AbstractTilePool implements ITilePool {
	
//	private static Random random = new Random(298475892L);
	private static Random random = new Random();
	
	private Map<Character, Integer> frequencies;
	private List<Tile> tiles;
	
	public AbstractTilePool() {
		frequencies = new HashMap<Character, Integer>();
		tiles = new ArrayList<Tile>();
		setFrequencies();
		reset();
	}
	
	public AbstractTilePool(ITilePool pool) {
		frequencies = new HashMap<Character, Integer>(pool.getFrequencies());
		tiles = new ArrayList<Tile>(pool.getRemainingTiles());
	}
	
	protected abstract void setFrequencies();
	
	protected void setFrequency(Tile tile, int frequency) {
		frequencies.put(tile.getLetter(), frequency);
	}

	public void reset() {
		tiles.clear();
		for (char c : frequencies.keySet()) {
			int frequency = frequencies.get(c);
			for (int n = 0; n < frequency; n++) {
				Tile tile = new Tile(c);
				add(tile);
			}
		}
		Collections.shuffle(tiles, random);
	}

	public int size() {
		return tiles.size();
	}
	
	public boolean empty() {
		return size() == 0;
	}
	
	public Tile remove() {
		Collections.shuffle(tiles, random);
		return tiles.remove(tiles.size()-1);
	}
	
	public List<Tile> getRemainingTiles() {
		return new ArrayList<Tile>(tiles);
	}
	
	public void add(Tile tile) {
		tiles.add(tile);
	}
	
	public void add(Tile[] tiles) {
		for (Tile tile : tiles) {
			add(tile);
		}
	}
	
	public void remove(Tile tile) {
		if (tile.isWild()) {
			tile = new Tile('?');
		}
		tiles.remove(tile);
	}
	
	public void remove(Tile[] tiles) {
		for (Tile tile : tiles) {
			remove(tile);
		}
	}

	public int getFrequency(Tile tile) {
		Character key = new Character(tile.getLetter());
		if (frequencies.containsKey(key)) {
			return frequencies.get(key);
		}
		return 0;
	}
	
	public Map<Character, Integer> getFrequencies() {
		return frequencies;
	}
	
	public String toString() {
		StringBuffer b = new StringBuffer();
		for (Tile tile : getRemainingTiles()) {
			b.append(tile);
		}
		return b.toString();
	}

}
