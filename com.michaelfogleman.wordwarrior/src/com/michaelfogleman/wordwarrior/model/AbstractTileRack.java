package com.michaelfogleman.wordwarrior.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractTileRack implements ITileRack {
	
	private List<Tile> tiles;
	private int capacity;
	
	public AbstractTileRack(int capacity) {
		this.capacity = capacity;
		tiles = new ArrayList<Tile>(capacity);
	}
	
	public AbstractTileRack(List<Tile> tiles) {
		this.capacity = tiles.size();
		this.tiles = new ArrayList<Tile>(tiles);
	}
	
	public void clear() {
		tiles.clear();
	}
	
	public int size() {
		return tiles.size();
	}
	
	public boolean empty() {
		return size() == 0;
	}
	
	public int capacity() {
		return capacity;
	}
	
	public void add(Tile tile) {
		tiles.add(tile);
	}
	
	public void add(int index, Tile tile) {
		tiles.add(index, tile);
	}
	
	public void add(Tile[] tiles) {
		for (Tile tile : tiles) {
			add(tile);
		}
	}
	
	public boolean remove(Tile tile) {
		if (tile.isWild()) {
			tile = new Tile('?');
		}
		return tiles.remove(tile);
	}
	
	public Tile remove(int index) {
		return tiles.remove(index);
	}
	
	public void remove(Tile[] tiles) {
		for (Tile tile : tiles) {
			remove(tile);
		}
	}
	
	public Tile[] getTiles() {
		Tile[] result = new Tile[tiles.size()];
		tiles.toArray(result);
		return result;
	}
	
	public Tile[] fill(ITilePool pool) {
		List<Tile> list = new ArrayList<Tile>();
		int n = capacity() - size();
		for (int i = 0; i < n && pool.size() > 0; i++) {
			Tile tile = pool.remove();
			list.add(tile);
			add(tile);
		}
		Tile[] result = new Tile[list.size()];
		list.toArray(result);
		return result;
	}
	
	public void replaceAll(ITilePool pool, Tile[] newTiles) {
		pool.add(getTiles());
		tiles.clear();
		pool.remove(newTiles);
		add(newTiles);
	}
	
	public void replaceTop(ITilePool pool, Tile[] newTiles) {
		for (int i = 0; i < newTiles.length; i++) {
			Tile tile = tiles.remove(tiles.size()-1);
			pool.add(tile);
		}
		add(newTiles);
	}
	
	public void shuffle() {
		Collections.shuffle(tiles);
	}
	
	public boolean contains(Tile tile) {
		return tiles.contains(tile);
	}
	
	public String toString() {
		StringBuffer b = new StringBuffer();
		for (Tile tile : tiles) {
			b.append(tile);
		}
		return b.toString();
	}

}
