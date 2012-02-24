package com.michaelfogleman.wordwarrior.engine;

import com.michaelfogleman.wordwarrior.model.IBoard;
import com.michaelfogleman.wordwarrior.model.MoveResult;
import com.michaelfogleman.wordwarrior.model.Tile;

public class DensityEvaluator implements IMoveEvaluator {
	
	public int evaluateMove(MoveResult move, IBoard before, IBoard after) {
		int a = computeDensity(after);
		int b = computeDensity(before);
		return a - b;
	}
	
	private int computeDensity(IBoard board) {
		int result = 0;
		int w = board.getWidth();
		int h = board.getHeight();
		int n = 0;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				if (!board.getTile(y, x).equals(Tile.NONE)) {
					result += Math.pow(countNeighboards(board, x, y), 2);
					n++;
				}
			}
		}
		return result;
	}
	
	private int countNeighboards(IBoard board, int x, int y) {
		int result = 0;
		for (int ny = y-1; ny <= y+1; ny++) {
			for (int nx = x-1; nx <= x+1; nx++) {
				if (x == nx && y == ny) continue;
				if (!board.getTile(ny, nx).equals(Tile.NONE)) {
					result++;
				}
			}
		}
		return result;
	}

}
