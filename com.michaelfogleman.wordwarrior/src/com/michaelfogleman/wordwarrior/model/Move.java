package com.michaelfogleman.wordwarrior.model;

import java.util.StringTokenizer;

public class Move {
	
	private Tile[] word;
	private int row;
	private int column;
	private Orientation orientation;
	
	public Move() {
	}
	
	public Move(Move move) {
		this.word = move.word;
		this.row = move.row;
		this.column = move.column;
		this.orientation = move.orientation;
	}
	
	public void setRow(int row) {
		this.row = row;
	}
	
	public void setColumn(int column) {
		this.column = column;
	}
	
	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
	}
	
	public void setWord(Tile[] word) {
		this.word = word;
	}
	
	public int getRow() {
		return row;
	}
	
	public int getColumn() {
		return column;
	}
	
	public Tile[] getWord() {
		return word;
	}
	
	public Orientation getOrientation() {
		return orientation;
	}
	
	public String getCoordinateString() {
		StringBuffer b = new StringBuffer();
		char c = (char)('A' + row);
		if (orientation == Orientation.HORIZONTAL) {
			b.append(c);
			b.append(column + 1);
		}
		else if (orientation == Orientation.VERTICAL) {
			b.append(column + 1);
			b.append(c);
		}
		return b.toString();
	}
	
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("MOVE ");
		b.append(getCoordinateString());
		b.append(" ");
		for (int i = 0; i < word.length; i++) {
			Tile tile = word[i];
			b.append(tile);
		}
		return b.toString();
	}
	
	public boolean equals(Object object) {
		if (object instanceof Move) {
			Move move = (Move)object;
			if (this.row != move.row) return false;
			if (this.column != move.column) return false;
			if (this.orientation != move.orientation) return false;
			if (this.word.length != move.word.length) return false;
			for (int i = 0; i < word.length; i++) {
				Tile t1 = this.word[i];
				Tile t2 = move.word[i];
				if (!t1.equals(t2)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	public static Move getInstance(String moveString) {
		Move move = new Move();
		
		StringTokenizer t = new StringTokenizer(moveString);
		String coord = t.nextToken();
		String word = t.nextToken();
		
		char c = Character.toUpperCase(coord.charAt(0));
		String cols = null;
		int row = -1;
		int col = -1;
		if (c >= 'A' && c <= 'O') {
			cols = coord.substring(1);
			move.orientation = Orientation.HORIZONTAL;
		}
		else {
			c = Character.toUpperCase(coord.charAt(coord.length()-1));
			cols = coord.substring(0, coord.length()-1);
			move.orientation = Orientation.VERTICAL;
		}
		col = Integer.parseInt(cols) - 1;
		row = c - 'A';
		
		Tile[] tiles = new Tile[word.length()];
		for (int i = 0; i < tiles.length; i++) {
			char ch = word.charAt(i);
			Tile tile = new Tile(ch, Character.isUpperCase(ch));
			tiles[i] = tile;
		}
		
		move.word = tiles;
		move.row = row;
		move.column = col;
		
		return move;
	}

}
