package com.michaelfogleman.wordwarrior.engine;

import com.michaelfogleman.wordwarrior.model.*;

public class BasicMoveMaker implements IMoveMaker {
	
	public IGameAction makeMove(IGame game, IPlayer player) {
		if (player.getTileRack().size() > 0) {
			SearchThread thread = new SearchThread(game, player);
			thread.start();
		}
		return null;
	}
	
	public boolean challengeMove(IGame game, MoveResult result) {
		Dictionary dictionary = Dictionary.getInstance(game.getDictionaryType());
		String[] words = result.getWords();
		for (int i = 0; i < words.length; i++) {
			String word = words[i];
			if (!dictionary.isWord(word)) {
				return true;
			}
		}
		return false;
	}
	
	private static class SearchThread {
		private IGame game;
		private IPlayer player;
		
		public SearchThread(IGame game, IPlayer player) {
			this.game = game;
			this.player = player;
		}
		
		public void start() {
			Thread thread = new Thread(new Runnable() {
				public void run() {
					SearchThread.this.run();
				}
			});
			thread.setDaemon(true);
			thread.start();
		}
		
		private void run() {
			long start = System.currentTimeMillis();
			IMoveEngine engine = game.getMoveEngine();
			IBoard board = game.getBoard();
			Search search = new Search(game, player);
			search.setAdvanced(true);
			search.setVerbose(true);
			search.run();
			Move move = search.getBestMove();
			IGameAction action = new PassTurnAction();
			if (move != null) {
				MoveResult moveResult = engine.testMove(board, move);
				if (moveResult != null) {
					action = new MoveAction(moveResult, null);
				}
			}
			long now = System.currentTimeMillis();
			long delay = 1000 - (now - start);
			if (delay > 0) {
//				Util.sleep(delay);
			}
			game.doAction(action);
		}
	}

}
