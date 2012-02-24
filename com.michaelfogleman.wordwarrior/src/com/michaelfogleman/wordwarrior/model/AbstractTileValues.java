package com.michaelfogleman.wordwarrior.model;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractTileValues implements ITileValues {
	
	private Map<Character, Integer> values;
	
	public AbstractTileValues() {
		values = new HashMap<Character, Integer>();
		setValues();
	}
	
	protected abstract void setValues();
	
	protected void setValue(Tile tile, int value) {
		values.put(tile.getLetter(), value);
	}

	public int getValue(Tile tile) {
		char c = tile.getLetter();
		if (tile.isWild()) c = '?';
		if (values.containsKey(c)) {
			return values.get(c);
		}
		return 0;
	}

}
