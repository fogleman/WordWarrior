package com.michaelfogleman.wordwarrior.model;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMoveEngine implements IMoveEngine {
	
	private ITileValues tileValues;
	
	public AbstractMoveEngine(ITileValues tileValues) {
		this.tileValues = tileValues;
	}
	
	public ITileValues getTileValues() {
		return tileValues;
	}

	public MoveResult doMove(IBoard board, Move move) {
		MoveResult result = compute(board, move, true);
		if (result == null) {
			// TODO throw invalid move exception?
		}
		return result;
	}
	
	public MoveResult testMove(IBoard board, Move move) {
		return compute(board, move, false);
	}

	public void undoMove(IBoard board, MoveResult result) {
		Tile[] tiles = result.getPreviousBoardState();
		Move move = result.getMove();
		Orientation orientation = move.getOrientation();
		int dx = orientation.getDx();
		int dy = orientation.getDy();
		int x = move.getColumn();
		int y = move.getRow();
		
		for (int i = 0; i < tiles.length; i++) {
			Tile tile = tiles[i];
			board.setTile(y, x, tile);
			x += dx;
			y += dy;
		}
	}

	public boolean isValidMove(IBoard board, Move move) {
		return compute(board, move, false) != null;
	}

	public int getScore(IBoard board, Move move) {
		MoveResult result = compute(board, move, false);
		if (result == null) {
			// TODO throw invalid move exception?
		}
		return result.getScore();
	}
	
	private MoveResult compute(IBoard board, Move move, boolean doMove) {
		if (board == null) return null;
		if (move == null) return null;
		Tile[] word = move.getWord();
		if (word == null) return null;
		int length = word.length;
		if (length < 2) return null;
		if (length > Math.max(board.getHeight(), board.getWidth())) return null;
		Orientation orientation = move.getOrientation();
		if (orientation == null) return null;
		int dx = orientation.getDx();
		int dy = orientation.getDy();
		int x = move.getColumn();
		int y = move.getRow();
		int width = board.getWidth();
		int height = board.getHeight();
		int tilesUsed = 0;
		boolean adjacent = false;
		int multiplier = 1;
		int mainScore = 0;
		int subScores = 0;
		
		List<TilePlacement> placements = new ArrayList<TilePlacement>();
		List<Tile> previousBoardState = new ArrayList<Tile>();
		List<String> wordsFormed = new ArrayList<String>();
		StringBuffer mainWord = new StringBuffer();
		for (int i = 0; i < length; i++) {
			if (x < 0 || x >= width) return null;
			if (y < 0 || y >= height) return null;
			
			Tile moveTile = word[i];
			Tile boardTile = board.getTile(y, x);
			
			// validation
			char c = moveTile.getLetter();
			//char b = boardTile.getLetter();
			if (c < 'a' || c > 'z') return null;
			if (boardTile == Tile.NONE) {
				tilesUsed++;
			}
			else {
				if (!moveTile.equals(boardTile)) return null;
			}
			
			if (!adjacent) {
				if (board.getTile(y-1,x) != Tile.NONE) adjacent = true;
				else if (board.getTile(y+1,x) != Tile.NONE) adjacent = true;
				else if (board.getTile(y,x-1) != Tile.NONE) adjacent = true;
				else if (board.getTile(y,x+1) != Tile.NONE) adjacent = true;
				else if (board.getTileCount() == 0) {
					if (x == board.getWidth()/2 && y == board.getHeight()/2) adjacent = true;
				}
			}
			
			// scoring and word formation
			mainWord.append(moveTile.getLetter());
			if (moveTile.equals(boardTile)) {
				mainScore += tileValues.getValue(moveTile);
			}
			else {
				StringBuffer subWord = new StringBuffer();
				mainScore += tileValues.getValue(moveTile) * board.getLetterMultiplier(y, x);
				multiplier *= board.getWordMultiplier(y, x);
				
				subWord.append(moveTile.getLetter());
				int subScore = tileValues.getValue(moveTile) * board.getLetterMultiplier(y, x);
				for (int n = 1; true; n++) {
					int sx = x + dy * n;
					int sy = y + dx * n;
					Tile tile = board.getTile(sy, sx);
					if (tile == null || tile == Tile.NONE) break;
					subScore += tileValues.getValue(tile);
					subWord.append(tile.getLetter());
				}
				for (int n = 1; true; n++) {
					int sx = x - dy * n;
					int sy = y - dx * n;
					Tile tile = board.getTile(sy, sx);
					if (tile == null || tile == Tile.NONE) break;
					subScore += tileValues.getValue(tile);
					subWord.insert(0, tile.getLetter());
				}
				
				if (subWord.length() > 1) {
					subScore *= board.getWordMultiplier(y, x);
					subScores += subScore;
					wordsFormed.add(subWord.toString());
				}
			}
			
			// placement
			if (boardTile == Tile.NONE) {
				placements.add(new TilePlacement(y, x, moveTile));
			}
			previousBoardState.add(boardTile);
			
			x += dx;
			y += dy;
		}
		
		// validation
		if (!adjacent) return null;
		if (tilesUsed < 1) return null;
		
		// tiles before and after main word not allowed, must be explicit
		for (int n = 1; true; n++) {
			int sx = move.getColumn() + dx * n + dx * (length - 1);
			int sy = move.getRow() + dy * n + dy * (length - 1);
			Tile tile = board.getTile(sy, sx);
			if (tile == null || tile == Tile.NONE) break;
			return null; //mainWord.append(tile.getLetter());
		}
		for (int n = 1; true; n++) {
			int sx = move.getColumn() - dx * n;
			int sy = move.getRow() - dy * n;
			Tile tile = board.getTile(sy, sx);
			if (tile == null || tile == Tile.NONE) break;
			return null; //mainWord.insert(0, tile.getLetter());
		}
		
		wordsFormed.add(0, mainWord.toString());
		
		// scoring
		mainScore *= multiplier;
		int score = mainScore + subScores;
		// TODO do not hardcode bingos
		if (tilesUsed == 7) {
			score += 50;
		}
		
		// do move if requested
		if (doMove) {
			for (TilePlacement p : placements) {
				board.setTile(p.y, p.x, p.tile);
			}
		}
		
		// build result
		String[] words = new String[wordsFormed.size()];
		wordsFormed.toArray(words);
		
		Tile[] state = new Tile[previousBoardState.size()];
		previousBoardState.toArray(state);
		
		Tile[] tiles = new Tile[placements.size()];
		for (int i = 0; i < tiles.length; i++) {
			TilePlacement p = placements.get(i);
			tiles[i] = p.tile;
		}
		
		MoveResult result = new MoveResult(move, score, words, state, tiles);
		return result;
	}
	
	private static class TilePlacement {
		int x;
		int y;
		Tile tile;
		public TilePlacement(int y, int x, Tile tile) {
			this.x = x;
			this.y = y;
			this.tile = tile;
		}
	}

}
