package com.michaelfogleman.wordwarrior.engine;

import java.util.Collections;
import java.util.List;

import com.michaelfogleman.wordwarrior.model.IGame;
import com.michaelfogleman.wordwarrior.model.IPlayer;
import com.michaelfogleman.wordwarrior.model.ITileRack;
import com.michaelfogleman.wordwarrior.model.Move;
import com.michaelfogleman.wordwarrior.model.MoveResult;
import com.michaelfogleman.wordwarrior.model.StandardTileRack;
import com.michaelfogleman.wordwarrior.model.Tile;

public class Engine {
	
	private IGame game;
	private IPlayer player;
	private Move best;
	
	public Engine(IGame game, IPlayer player) {
		this.game = game;
		this.player = player;
	}
	
	public Move getBestMove() {
		return best;
	}
	
	public void run() {
		// find high scoring moves
		Search search = new Search(game, player);
		search.setAdvanced(true);
		search.setTracking(true);
		search.setVerbose(true);
		search.run();
		
		List<MoveResult> list = search.getBestMoves();
		
		// manufacture a random potential opponent rack
		List<Tile> pool = game.getTrackedTiles(player);
		Collections.shuffle(pool);
		int rackSize = player.getTileRack().capacity();
		while (pool.size() > rackSize) {
			pool.remove(pool.size()-1);
		}
		ITileRack rack = new StandardTileRack(pool);
		
		MoveResult bestMove = null;
		int bestScore = Integer.MIN_VALUE;
		for (MoveResult moveResult : list) {
			System.out.println("Testing: " + moveResult.getMove());
			game.getMoveEngine().doMove(game.getBoard(), moveResult.getMove());
			int score = moveResult.getScore() - getAverageScore(rack);
			if (score > bestScore) {
				bestScore = score;
				bestMove = moveResult;
			}
			game.getMoveEngine().undoMove(game.getBoard(), moveResult);
		}
		best = bestMove.getMove();
	}
	
	private int getAverageScore(ITileRack rack) {
		Search search = new Search(game, rack);
		search.setAdvanced(false);
		search.setTracking(true);
		search.setVerbose(false);
		search.run();
		
		List<MoveResult> list = search.getBestMoves();
		int total = 0;
		int count = 0;
		for (MoveResult moveResult : list) {
			count++;
			total += moveResult.getScore();
		}
		
		if (count == 0) return 0;
		return total / count;
	}

}
