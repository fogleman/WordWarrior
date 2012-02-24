package com.michaelfogleman.wordwarrior.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.michaelfogleman.wordwarrior.Convert;
import com.michaelfogleman.wordwarrior.model.*;

public class Search {
	
	private Dictionary dictionary;
	private IMoveEngine mover;
	private IBoard board;
	private ITileRack rack;
	private boolean advanced;
	
	private int best;
	private Move bestMove;
	private int validMoves;
	private boolean verbose;
	private boolean tracking;
	
	public Search(Dictionary dictionary, IMoveEngine mover, IBoard board, ITileRack rack) {
		this.dictionary = dictionary;
		this.mover = mover;
		this.board = board;
		this.rack = rack;
	}
	
	public Search(IGame game, IPlayer player) {
		this(Dictionary.getInstance(game.getDictionaryType()), game.getMoveEngine(), game.getBoard(), player.getTileRack());
	}
	
	public Search(IGame game, ITileRack rack) {
		this(Dictionary.getInstance(game.getDictionaryType()), game.getMoveEngine(), game.getBoard(), rack);
	}
	
	public void setTracking(boolean tracking) {
		this.tracking = tracking;
	}
	
	public boolean isTracking() {
		return tracking;
	}
	
	public void setAdvanced(boolean advanced) {
		this.advanced = advanced;
	}
	
	public boolean isAdvanced() {
		return advanced;
	}
	
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
	
	public boolean isVerbose() {
		return verbose;
	}
	
	public Move getBestMove() {
		return bestMove;
	}
	
	private String getRowState(int row) {
		StringBuffer result = new StringBuffer();
		for (int col = 0; col < board.getWidth(); col++) {
			char c = board.getTile(row, col).getRepresentation();
			result.append(c);
		}
		return result.toString();
	}
	
	private String getColState(int col) {
		StringBuffer result = new StringBuffer();
		for (int row = 0; row < board.getHeight(); row++) {
			char c = board.getTile(row, col).getRepresentation();
			result.append(c);
		}
		return result.toString();
	}
	
	public void run() {
		long start = System.currentTimeMillis();
		best = Integer.MIN_VALUE;
		validMoves = 0;
		boolean[][] squares;
		Move move = new Move();
		move.setOrientation(Orientation.HORIZONTAL);
		squares = getCandidateSquares(board, rack, move.getOrientation());
		for (int row = 0; row < board.getHeight(); row++) {
			String state = getRowState(row);
			boolean[] neighbored = getRowNeighbors(board, row);
			boolean[][] letters = getRowLetterCandidates(board, row);
			move.setRow(row);
			for (int col = 0; col < board.getWidth(); col++) {
				if (!squares[row][col]) continue;
				move.setColumn(col);
				List<String> words = dictionary.getWords(rack.toString(), state, neighbored, col, letters);
				for (String word : words) {
					move.setWord(Convert.toTiles(word));
					tryMove(move);
				}
			}
		}
		
		move.setOrientation(Orientation.VERTICAL);
		squares = getCandidateSquares(board, rack, move.getOrientation());
		for (int col = 0; col < board.getWidth(); col++) {
			String state = getColState(col);
			boolean[] neighbored = getColumnNeighbors(board, col);
			boolean[][] letters = getColumnLetterCandidates(board, col);
			move.setColumn(col);
			for (int row = 0; row < board.getHeight(); row++) {
				if (!squares[row][col]) continue;
				move.setRow(row);
				List<String> words = dictionary.getWords(rack.toString(), state, neighbored, row, letters);
				for (String word : words) {
					move.setWord(Convert.toTiles(word));
					tryMove(move);
				}
			}
		}
		
		long end = System.currentTimeMillis();
		if (verbose) {
			if (advanced) System.out.print("advanced ");
			System.out.println("search completed in " + (end-start) + "ms");
			System.out.println(validMoves + " valid moves");
		}
	}
	
	private IMoveEvaluator evaluator = null;
//	private IMoveEvaluator evaluator = new DensityEvaluator();
	
	private void tryMove(Move move) {
		MoveResult result = mover.testMove(board, move);
		
		if (result == null) return;
		
		String[] words = result.getWords();
		boolean valid = true;
		for (int i = 0; i < words.length; i++) {
			String w = words[i];
			if (!dictionary.isWord(w)) {
				valid = false;
				break;
			}
		}
		if (!valid) return;
		
		validMoves++;
		int score = result.getScore();
		
		if (advanced) {
			score -= getTilePenalty(move.getWord());
		}
		
		if (evaluator != null) {
			IBoard nextBoard = new AbstractBoard(board);
			mover.doMove(nextBoard, move);
			score = evaluator.evaluateMove(result, board, nextBoard);
		}
		
		if (score > best) {
			best = score;
			bestMove = new Move(move);
			if (verbose) printMove(result, words, score);
		}
		
		if (tracking) {
			addResult(result);
		}
	}
	
	private List<MoveResult> results = new ArrayList<MoveResult>();
	private static final int MAX_RESULTS = 30;
	
	private void addResult(MoveResult result) {
		results.add(result);
		Collections.sort(results);
		if (results.size() > MAX_RESULTS) {
			results.remove(results.size()-1);
		}
	}
	
	public List<MoveResult> getBestMoves() {
		return results;
	}
	
	private int getTilePenalty(Tile[] tiles) {
		int result = 0;
		int length = tiles.length;
		for (int i = 0; i < length; i++) {
			Tile tile = tiles[i];
			char c = tile.getLetter();
			if (tile.isWild()) {
				result += 8;
			}
			else {
				if (c == 's') result += 8;
				else if (c == 'x') result += 12;
			}
		}
		result -= tiles.length;
		return result;
	}
	
	private static String getPrefix(IBoard board, int x, int y, Orientation orientation) {
		x -= orientation.getDx();
		y -= orientation.getDy();
		char c = board.getTile(y, x).getLetter();
		if (c == ' ') return "";
		return getPrefix(board, x, y, orientation) + c;
	}
	
	private static String getSuffix(IBoard board, int x, int y, Orientation orientation) {
		x += orientation.getDx();
		y += orientation.getDy();
		char c = board.getTile(y, x).getLetter();
		if (c == ' ') return "";
		return c + getSuffix(board, x, y, orientation);
	}
	
	private boolean[][] getRowLetterCandidates(IBoard board, int row) {
		int w = board.getWidth();
		boolean[][] result = new boolean[w][26];
		int y = row;
		for (int x = 0; x < w; x++) {
			String prefix = getPrefix(board, x, y, Orientation.VERTICAL);
			String suffix = getSuffix(board, x, y, Orientation.VERTICAL);
			if (prefix.length() == 0 && suffix.length() == 0) {
				for (int i = 0; i < 26; i++) {
					result[x][i] = true;
				}
			}
			else {
				List<Character> candidates = dictionary.getCandidateTiles(prefix, suffix);
				for (char c : candidates) {
					int i = c - 'a';
					result[x][i] = true;
				}
			}
		}
		return result;
	}
	
	private boolean[][] getColumnLetterCandidates(IBoard board, int column) {
		int h = board.getHeight();
		boolean[][] result = new boolean[h][26];
		int x = column;
		for (int y = 0; y < h; y++) {
			String prefix = getPrefix(board, x, y, Orientation.HORIZONTAL);
			String suffix = getSuffix(board, x, y, Orientation.HORIZONTAL);
			if (prefix.length() == 0 && suffix.length() == 0) {
				for (int i = 0; i < 26; i++) {
					result[y][i] = true;
				}
			}
			else {
				List<Character> candidates = dictionary.getCandidateTiles(prefix, suffix);
				for (char c : candidates) {
					int i = c - 'a';
					result[y][i] = true;
				}
			}
		}
		return result;
	}
	
	private static boolean[] getRowNeighbors(IBoard board, int row) {
		int w = board.getWidth();
		boolean[] result = new boolean[w];
		int y = row;
		for (int x = 0; x < w; x++) {
			result[x] = hasNeighbor(board, x, y);
		}
		return result;
	}
	
	private static boolean[] getColumnNeighbors(IBoard board, int column) {
		int h = board.getHeight();
		boolean[] result = new boolean[h];
		int x = column;
		for (int y = 0; y < h; y++) {
			result[y] = hasNeighbor(board, x, y);
		}
		return result;
	}
	
	private static boolean[][] getCandidateSquares(IBoard board, ITileRack rack, Orientation orientation) {
		int w = board.getWidth();
		int h = board.getHeight();
		int n = rack.size();
		boolean[][] result = new boolean[h][w];
		
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				result[y][x] = isCandidate(board, x, y, n, orientation);
			}
		}
		
		return result;
	}
	
	private static boolean isCandidate(IBoard board, int x, int y, int n, Orientation orientation) {
		int dx = orientation.getDx();
		int dy = orientation.getDy();
		if (hasNeighbor(board, x, y)) return true;
		for (int i = 1; i < n; i++) {
			int nx = x+dx*i;
			int ny = y+dy*i;
			if (!board.getTile(ny, nx).isEmpty()) return true;
			if (hasNeighbor(board, nx, ny)) return true;
		}
		return false;
	}
	
	private static boolean hasNeighbor(IBoard board, int x, int y) {
		if (!board.getTile(y+1, x).isEmpty()) return true;
		if (!board.getTile(y-1, x).isEmpty()) return true;
		if (!board.getTile(y, x+1).isEmpty()) return true;
		if (!board.getTile(y, x-1).isEmpty()) return true;
		if (board.getTileCount() == 0) {
			if (x == board.getWidth() / 2 && y == board.getHeight() / 2) return true;
		}
		return false;
	}
	
//	private static String print(boolean[][] a) {
//		StringBuffer b = new StringBuffer();
//		for (int y = 0; y < a.length; y++) {
//			for (int x = 0; x < a[y].length; x++) {
//				b.append(a[y][x] ? "X" : "-");
//			}
//			b.append("\n");
//		}
//		return b.toString();
//	}
	
	private static void printMove(MoveResult result, String[] words, int evaluation) {
		StringBuffer output = new StringBuffer();
		Move move = result.getMove();
		output.append(padl(Integer.toString(evaluation), 4));
		output.append(padl(Integer.toString(result.getScore()), 4));
		output.append(padl(move.getCoordinateString(), 4));
		output.append("  ");
		output.append(padr(Convert.toString(move.getWord()), 12));
		output.append("  ");
		if (words != null) {
			for (int i = 0; i < words.length; i++) {
				String w = words[i];
				if (i != 0) output.append(", ");
				output.append(w);
			}
		}
		System.out.println(output);
	}
	
	private static String padl(String string, int width) {
		int length = string.length();
		int add = width - length;
		StringBuffer b = new StringBuffer();
		for (int i = 0; i < add; i++) b.append(' ');
		b.append(string);
		return b.toString();
	}
	
	private static String padr(String string, int width) {
		int length = string.length();
		int add = width - length;
		StringBuffer b = new StringBuffer();
		b.append(string);
		for (int i = 0; i < add; i++) b.append(' ');
		return b.toString();
	}

}
