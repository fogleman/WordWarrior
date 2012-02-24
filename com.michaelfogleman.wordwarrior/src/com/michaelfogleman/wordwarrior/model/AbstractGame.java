package com.michaelfogleman.wordwarrior.model;

import java.util.ArrayList;
import java.util.List;

import com.michaelfogleman.wordwarrior.ScrabblePlugin;
import com.michaelfogleman.wordwarrior.engine.Dictionary;
import com.michaelfogleman.wordwarrior.engine.Search;

public abstract class AbstractGame implements IGame {
	
	private DictionaryType dictionaryType;
	private ChallengeMode challengeMode;
	private IBoard board;
	private IMoveEngine moveEngine;
	private ITilePool tilePool;
	
	private GameState state;
	private IPlayer[] players;
	private int currentPlayer;
	private int playerCount;
	private int time;
	private int increment;
	private List<IGameAction> history;
	private List<IGameListener> listeners;
	private IClockListener clockListener;
	private IPlayer winner;
	private IEventManager eventManager;
	
	private boolean analysisMode;
	private MoveResult[] analysis;
	
	private boolean processedEndGame;
	
	public AbstractGame(
			DictionaryType dictionaryType,
			ChallengeMode challengeMode,
			IBoard board,
			IMoveEngine moveEngine,
			ITilePool tilePool,
			int playerCount) {
		
		listeners = new ArrayList<IGameListener>();
		history = new ArrayList<IGameAction>();
		clockListener = createClockListener();
		setGameState(GameState.READY);
		this.dictionaryType = dictionaryType;
		this.challengeMode = challengeMode;
		this.board = board;
		this.moveEngine = moveEngine;
		this.tilePool = tilePool;
		this.playerCount = playerCount;
		this.players = new IPlayer[playerCount];
		this.currentPlayer = 0;
		this.analysis = new MoveResult[0];
		this.analysisMode = true;
		this.winner = null;
		eventManager = ScrabblePlugin.getDefault().getEventManager();
	}
	
	public void dispose() {
		endGame(null);
		listeners.clear();
	}
	
	public String getDescription() {
		StringBuffer b = new StringBuffer();
		for (int i = 0; i < players.length; i++) {
			IPlayer player = players[i];
			String name = player == null ? "null" : player.getName();
			if (i > 0) b.append(" vs ");
			b.append(name);
		}
		b.append(": ");
		b.append(time).append('/').append(increment).append(' ');
		b.append(challengeMode.getName()).append(' ');
		b.append(dictionaryType.getName());
		return b.toString();
	}
	
	public List<Tile> getTrackedTiles(IPlayer player) {
		List<Tile> list = new ArrayList<Tile>(tilePool.getRemainingTiles());
		for (IPlayer p : players) {
			if (p == player) continue;
			Tile[] rack = p.getTileRack().getTiles();
			for (Tile tile : rack) {
				list.add(tile);
			}
		}
		return list;
	}
	
	public GameState getGameState() {
		return state;
	}
	
	private void setGameState(GameState state) {
		GameState previous = this.state;
		this.state = state;
		gameStateChanged(previous, state);
	}
	
	public void setAnalysisMode(boolean on) {
		this.analysisMode = on;
		if (on) {
			doAnalysis(getCurrentPlayer());
		}
	}
	
	public boolean isAnalysisMode() {
		return analysisMode;
	}
	
	public IGameAction[] getHistory() {
		IGameAction[] result = new IGameAction[history.size()];
		history.toArray(result);
		return result;
	}
	
	public IGameAction getLastAction() {
		if (history.size() > 0) {
			return history.get(history.size()-1);
		}
		return null;
	}
	
	private void addAction(IGameAction action) {
		action.setIndex(history.size()+1);
		action.setPlayer(getCurrentPlayer());
		history.add(action);
	}
	
	public void start() {
		if (state != GameState.READY) {
			throw new IllegalStateException("Game cannot be started if it is not in the READY state");
		}
		currentPlayer = playerCount-1;
		resetClocks();
		nextPlayer();
		setGameState(GameState.IN_PROGRESS);
		String message = getDescription();
		eventManager.publishEvent(message);
	}
	
	public boolean isLocal() {
		for (int i = 0; i < players.length; i++) {
			IPlayer player = players[i];
			if (!player.isLocal()) {
				return false;
			}
		}
		return true;
	}
	
	public boolean isObserved() {
		for (int i = 0; i < players.length; i++) {
			IPlayer player = players[i];
			if (player.isLocal()) {
				return false;
			}
		}
		return true;
	}
	
	private boolean isRackEmpty() {
		for (int i = 0; i < players.length; i++) {
			IPlayer player = players[i];
			if (player.getTileRack().size() == 0) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isOver() {
		// consecutive passes
		int n = 0;
		for (int i = history.size()-1; i >= 0; i--) {
			IGameAction action = history.get(i);
			if (!(action instanceof PassTurnAction)) {
				break;
			}
			n++;
		}
		if (n >= 4) {
			return true;
		}
		
		// pass after empty rack
		if (!isRackEmpty()) return false;
		IGameAction action = history.get(history.size()-1);
		return action instanceof PassTurnAction;
	}
	
//	private boolean isFinished() {
//		for (int i = 0; i < players.length; i++) {
//			IPlayer player = players[i];
//			if (player.getTileRack().size() == 0) {
//				return true;
//			}
//		}
//		int n = 0;
//		for (int i = history.size()-1; i >= 0; i--) {
//			IGameAction action = history.get(i);
//			if (!(action instanceof PassTurnAction)) {
//				break;
//			}
//			n++;
//		}
//		if (n >= 4) {
//			return true;
//		}
//		return false;
//	}
	
	// end game for some reason other than a normal win
	// player ran out of time, game adjourned, etc.
	private void endGame(IPlayer winner) {
		IPlayer previousPlayer = getCurrentPlayer();
		previousPlayer.getClock().stop();
		setWinner(winner);
		setGameState(GameState.ENDED);
	}
	
	private void nextPlayer() {
		IPlayer previousPlayer = getCurrentPlayer();
		previousPlayer.getClock().stop();
		previousPlayer.getClock().increment(getTimeIncrement(), 0);
		
		currentPlayer = (currentPlayer+1) % playerCount;
		
		if (isOver()) {
			processEndGame();
			setGameState(GameState.ENDED);
			return;
		}
		
		IPlayer nextPlayer = getCurrentPlayer();
		nextPlayer.getClock().start();
		
		IGameAction lastAction = getLastAction();
		if (lastAction instanceof MoveAction) {
			MoveAction moveAction = (MoveAction)lastAction;
			MoveResult moveResult = moveAction.getMoveResult();
			for (int i = 0; i < players.length; i++) {
				IPlayer p = players[i];
				if (p == previousPlayer) continue;
				IMoveMaker moveMaker = p.getMoveMaker();
				if (moveMaker != null) {
					if (moveMaker.challengeMove(this, moveResult)) {
						doChallenge(p);
					}
				}
			}
		}
		
		if (isRackEmpty()) {
			if (nextPlayer.getMoveMaker() == null) {
				setGameState(GameState.CONFIRMATION);
				return;
			}
			else {
				doPassTurn();
				return;
			}
		}
		
//		if (!nextPlayer.isLocal() || !nextPlayer.isHuman()) {
		if (isLocal() || isObserved()) {
			doAnalysis(nextPlayer);
		}
		
//		evaluateRack(nextPlayer);
		
		IMoveMaker moveMaker = nextPlayer.getMoveMaker();
		if (moveMaker != null) {
			IGameAction action = moveMaker.makeMove(this, nextPlayer);
			doAction(action);
		}
	}
	
//	private void evaluateRack(IPlayer player) {
//		Dictionary dictionary = Dictionary.getInstance(DictionaryType.TWL06_CLABBERS);
//		dictionary.getRackScore(player.getTileRack());
//	}
	
	private void doAnalysis(IPlayer player) {
		if (analysisMode && !dictionaryType.isClabbers()) {
			Search search = new Search(this, player);
			search.setVerbose(false);
			search.setAdvanced(false);
			search.setTracking(true);
			search.run();
			List<MoveResult> list = search.getBestMoves();
			MoveResult[] result = new MoveResult[list.size()];
			list.toArray(result);
			setAnalysis(result);
		}
	}
	
	public MoveResult[] getAnalysis() {
		return analysis;
	}
	
	private void setAnalysis(MoveResult[] analysis) {
		this.analysis = analysis;
	}
	
	private void processEndGame() {
		if (processedEndGame) return;
		processedEndGame = true;
		
		IPlayer winner = null;
		int score = 0;
		for (int i = 0; i < players.length; i++) {
			IPlayer player = players[i];
			Tile[] tiles = player.getTileRack().getTiles();
			if (tiles.length == 0) {
				winner = player;
			}
			else {
				for (int j = 0; j < tiles.length; j++) {
					Tile tile = tiles[j];
					score += moveEngine.getTileValues().getValue(tile);
				}
			}
		}
		if (winner != null) {
			winner.setScore(winner.getScore() + score * 2);
		}
		setWinner(getHighestScoringPlayer());
	}
	
	private void resetClocks() {
		for (int i = 0; i < players.length; i++) {
			IPlayer player = players[i];
			if (player != null) {
				player.getClock().set(time, 0);
			}
		}
	}
	
	public void addPlayer(IPlayer player) {
		int position = player.getPosition() - 1;
		if (players[position] != null) {
			throw new IllegalStateException("Player already exists in that position");
		}
		players[position] = player;
		player.getClock().set(time, 0);
		player.getTileRack().fill(tilePool);
		player.getClock().addListener(clockListener);
	}
	
	public void doMove(Move move) {
		IPlayer player = getCurrentPlayer();
		MoveResult result = moveEngine.doMove(board, move);
		
		String message = player.getName() + ": " + result.toString();
		eventManager.publishEvent(message);
		
		Tile[] previousTiles = player.getTileRack().getTiles();
		player.getTileRack().remove(result.getTilesUsed());
		player.getTileRack().fill(getTilePool());
		player.setScore(player.getScore() + result.getScore());
		addAction(new MoveAction(result, previousTiles));
		nextPlayer();
		playerMoved(player, result);
		
//		for (int i = 0; i < players.length; i++) {
//			IPlayer p = players[i];
//			if (p == player) continue;
//			IMoveMaker moveMaker = p.getMoveMaker();
//			if (moveMaker != null) {
//				if (moveMaker.challengeMove(this, result)) {
//					doChallenge(p);
//				}
//			}
//		}
	}
	
	public void doExchangeTiles(Tile[] tiles) {
		// TODO validate enough tiles left
		IPlayer player = getCurrentPlayer();
		
		String message = player.getName() + ": Exchanged Tiles";
		eventManager.publishEvent(message);
		
		player.getTileRack().remove(tiles);
		player.getTileRack().fill(getTilePool());
		getTilePool().add(tiles);
		addAction(new ExchangeTilesAction(tiles.length));
		nextPlayer();
		playerExchangedTiles(player, tiles, player.getTileRack().getTiles());
	}
	
	public void doChallenge(IPlayer challenger) {
		IGameAction action = getLastAction();
		if (!(action instanceof MoveAction)) {
			return;
		}
		MoveAction moveAction = (MoveAction)action;
		String[] words = moveAction.getMoveResult().getWords();
		Dictionary dictionary = Dictionary.getInstance(dictionaryType);
		boolean successful = false;
		for (int i = 0; i < words.length; i++) {
			String word = words[i];
			if (!dictionary.isWord(word)) {
				successful = true;
				break;
			}
		}
		doChallenge(challenger, successful);
	}
	
	public void doChallenge(IPlayer challenger, boolean successful) {
		IGameAction action = getLastAction();
		if (!(action instanceof MoveAction)) {
			return;
		}
		MoveAction moveAction = (MoveAction)action;
		MoveResult moveResult = moveAction.getMoveResult();
		
		String message;
		if (successful) {
			message = challenger.getName() + ": Successfully Challenged " + moveAction.getPlayer().getName();
		}
		else {
			message = challenger.getName() + ": Unsuccessfully Challenged " + moveAction.getPlayer().getName();
		}
		eventManager.publishEvent(message);
		
		String[] words = moveResult.getWords();
		addAction(new ChallengeAction(challenger, successful));
		
		if (successful) {
			moveEngine.undoMove(board, moveResult);
			IPlayer player = moveAction.getPlayer();
			ITileRack rack = player.getTileRack();
			rack.replaceAll(tilePool, moveAction.getPreviousTiles());
			player.incrementScore(-1 * moveResult.getScore());
			
			playerChallenged(challenger, successful, words);
			playerUndidMove(action.getPlayer(), moveResult);
		}
		else {
			int penalty = challengeMode.getPenalty();
			challenger.incrementScore(-1 * penalty);
			
			playerChallenged(challenger, successful, words);
			
			// TODO support challenger not current player
			if (!successful && challengeMode.isLoseTurn() && challenger.isLocal()) {
				doPassTurn();
			}
			else if (state == GameState.CONFIRMATION) {
				doPassTurn();
			}
		}
	}
	
	public void doPassTurn() {
		IPlayer player = getCurrentPlayer();
		
		String message = player.getName() + ": Passed Turn";
		eventManager.publishEvent(message);
		
		addAction(new PassTurnAction());
		nextPlayer();
		playerPassedTurn(player);
	}
	
	public void doAction(IGameAction action) {
		if (action != null) {
			if (action instanceof MoveAction) {
				MoveAction moveAction = (MoveAction)action;
				Move move = moveAction.getMoveResult().getMove();
				doMove(move);
			}
			else if (action instanceof PassTurnAction) {
				doPassTurn();
			}
			// TODO exchange action
		}
	}

	public IBoard getBoard() {
		return board;
	}
	
	public IMoveEngine getMoveEngine() {
		return moveEngine;
	}
	
	public DictionaryType getDictionaryType() {
		return dictionaryType;
	}
	
	public ChallengeMode getChallengeMode() {
		return challengeMode;
	}

	public IPlayer[] getPlayers() {
		return players;
	}
	
	public IPlayer getPlayer(String handle) {
		for (IPlayer player : players) {
			if (player.getName().equalsIgnoreCase(handle)) {
				return player;
			}
		}
		return null;
	}

	public IPlayer getCurrentPlayer() {
		return players[currentPlayer];
	}
	
	public IPlayer getWinner() {
		if (winner != null) {
			return winner;
		}
		if (!isOver()) {
			return null;
		}
		return getHighestScoringPlayer();
	}
	
	private IPlayer getHighestScoringPlayer() {
		int max = Integer.MIN_VALUE;
		IPlayer winner = null;
		for (IPlayer player : players) {
			int score = player.getScore();
			if (score > max) {
				max = score;
				winner = player;
			}
		}
		return winner;
	}
	
	private void setWinner(IPlayer winner) {
		this.winner = winner;
		if (winner != null) {
			String message = winner.getName() + " Wins!";
			eventManager.publishEvent(message);
		}
	}
	
	public IPlayer getLocalPlayer() {
		IPlayer current = getCurrentPlayer();
		if (current.isLocal() && current.isHuman()) return current;
		if (isObserved()) return current;
		int index = -1;
		for (int i = 0; i < players.length; i++) {
			if (players[i] == current) {
				index = i;
				break;
			}
		}
		for (int i = index-1; i >= 0; i--) {
			IPlayer player = players[i];
			if (player.isLocal() && player.isHuman()) return player;
		}
		for (int i = players.length-1; i > index; i--) {
			IPlayer player = players[i];
			if (player.isLocal() && player.isHuman()) return player;
		}
		return null;
	}

	public ITilePool getTilePool() {
		return tilePool;
	}

	public ITileValues getTileValues() {
		return moveEngine.getTileValues();
	}

	public int getTimeLimit() {
		return time;
	}
	
	public void setTimeLimit(int minutes) {
		this.time = minutes;
		resetClocks();
	}

	public int getTimeIncrement() {
		return increment;
	}
	
	public void setTimeIncrement(int minutes) {
		this.increment = minutes;
		resetClocks();
	}
	
	
	public void addGameListener(IGameListener listener) {
		listeners.add(listener);
	}
	
	public void removeGameListener(IGameListener listener) {
		listeners.remove(listener);
	}
	
	private IClockListener createClockListener() {
		return new IClockListener() {
			public void clockExpired(Clock clock) {
				System.out.println("Clock Expired: " + clock.hashCode());
				for (IPlayer player : players) {
					if (player.getClock() == clock) {
						handleTimeout(player);
					}
				}
			}
		};
	}
	
	private void handleTimeout(IPlayer player) {
		if (!player.isLocal()) return;
		if (player.getPenalties() == 0) {
			String message = player.getName() + " receives one more minute with a 10-point penalty.";
			eventManager.publishEvent(message);
			
			player.addPenalty();
			playerClockExpired(player, true);
		}
		else {
			IPlayer winner = null;
			for (IPlayer p : players) {
				if (p != player) {
					winner = p;
					break;
				}
			}
			
			String message = player.getName() + " has run out of time.";
			eventManager.publishEvent(message);
			
			endGame(winner);
			playerClockExpired(player, false);
		}
	}
	
	private void playerMoved(IPlayer player, MoveResult moveResult) {
		for (IGameListener listener : listeners) {
			listener.playerMoved(this, player, moveResult);
		}
	}
	
	private void playerPassedTurn(IPlayer player) {
		for (IGameListener listener : listeners) {
			listener.playerPassedTurn(this, player);
		}
	}
	
	private void playerExchangedTiles(IPlayer player, Tile[] tiles, Tile[] newRack) {
		for (IGameListener listener : listeners) {
			listener.playerExchangedTiles(this, player, tiles, newRack);
		}
	}
	
	private void playerChallenged(IPlayer player, boolean successful, String[] words) {
		for (IGameListener listener : listeners) {
			listener.playerChallenged(this, player, successful, words);
		}
	}
	
	private void playerUndidMove(IPlayer player, MoveResult moveResult) {
		for (IGameListener listener : listeners) {
			listener.playerUndidMove(this, player, moveResult);
		}
	}
	
	private void playerClockExpired(IPlayer player, boolean penalty) {
		for (IGameListener listener : listeners) {
			listener.playerClockExpired(this, player, penalty);
		}
	}
	
	private void gameStateChanged(GameState previous, GameState current) {
		for (IGameListener listener : listeners) {
			listener.gameStateChanged(this, previous, current);
		}
	}

}
