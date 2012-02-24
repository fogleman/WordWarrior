package com.michaelfogleman.wordwarrior;

import java.util.List;

import com.michaelfogleman.wordwarrior.model.Tile;

public class Convert {
	
	private Convert() {
	}
	
	public static Tile[] toTiles(String s) {
		// check for empty rack
		if (s.indexOf('-') >= 0) return new Tile[0];
		Tile[] result = new Tile[s.length()];
		for (int i = 0; i < result.length; i++) {
			char c = s.charAt(i);
			Tile tile = new Tile(c);
			result[i] = tile;
		}
		return result;
	}
	
	public static String toString(Tile[] tiles) {
		StringBuffer b = new StringBuffer();
		for (Tile tile : tiles) {
			b.append(tile);
		}
		return b.toString();
	}
	
	public static String toString(List<Tile> tiles) {
		StringBuffer b = new StringBuffer();
		for (Tile tile : tiles) {
			b.append(tile);
		}
		return b.toString();
	}

}
