package com.michaelfogleman.wordwarrior.model;

import java.util.ArrayList;
import java.util.List;

public class AbstractBoard implements IBoard {
	
	private int width;
	private int height;
	private Tile[][] data;
	private int[][] wordMultiplier;
	private int[][] letterMultiplier;
	private int tileCount;
	private List<IBoardListener> listeners;
	
	public AbstractBoard(int width, int height) {
		this.width = width;
		this.height = height;
		listeners = new ArrayList<IBoardListener>();
		wordMultiplier = new int[height][width];
		letterMultiplier = new int[height][width];
		data = new Tile[height][width];
		reset();
	}
	
	public AbstractBoard(IBoard board) {
		this(board.getWidth(), board.getHeight());
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				setWordMultiplier(y, x, board.getWordMultiplier(y, x));
				setLetterMultiplier(y, x, board.getLetterMultiplier(y, x));
				setTile(y, x, board.getTile(y, x));
			}
		}
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getWidth() {
		return width;
	}
	
	protected void setWordMultiplier(int row, int column, int multiplier) {
		wordMultiplier[row][column] = multiplier;
	}
	
	protected void setLetterMultiplier(int row, int column, int multiplier) {
		letterMultiplier[row][column] = multiplier;
	}
	
	public void addBoardListener(IBoardListener listener) {
		listeners.add(listener);
	}
	
	public void removeBoardListener(IBoardListener listener) {
		listeners.remove(listener);
	}
	
	public void notifyListeners() {
		for (IBoardListener listener : listeners) {
			listener.changed();
		}
	}
	
	public void reset() {
		tileCount = 0;
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				data[y][x] = Tile.NONE;
			}
		}
	}
	
	public int getTileCount() {
		return tileCount;
	}

	public int getWordMultiplier(int row, int column) {
		return wordMultiplier[row][column];
	}

	public int getLetterMultiplier(int row, int column) {
		return letterMultiplier[row][column];
	}

	public Tile getTile(int row, int column) {
		if (row < 0 || row >= getHeight()) return Tile.NONE;
		if (column < 0 || column >= getWidth()) return Tile.NONE;
		return data[row][column];
	}

	public void setTile(int row, int column, Tile tile) {
		if (row < 0 || row >= getHeight()) return;
		if (column < 0 || column >= getWidth()) return;
		Tile old = data[row][column];
		data[row][column] = tile;
		if (old != null && old != Tile.NONE) {
			tileCount--;
		}
		if (tile != null && tile != Tile.NONE) {
			tileCount++;
		}
	}

	public void removeTile(int row, int column) {
		setTile(row, column, Tile.NONE);
	}
	
	public String toString() {
		StringBuffer b = new StringBuffer();
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				Tile tile = getTile(y, x);
				if (tile == Tile.NONE) {
					b.append('-');
				}
				else {
					char c = tile.getLetter();
					if (tile.isWild()) {
						c = Character.toUpperCase(c);
					}
					b.append(c);
				}
			}
			b.append('\n');
		}
		b.append(getTileCount()).append('\n');
		return b.toString();
	}

}
