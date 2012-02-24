package com.michaelfogleman.wordwarrior.model;

import java.util.List;

public interface IGame {
	
	public void dispose();
	
	public void start();
	
	public void addPlayer(IPlayer player);
	
	public void doMove(Move move);
	
	public void doExchangeTiles(Tile[] tiles);
	
	public void doPassTurn();
	
	public void doChallenge(IPlayer challenger);
	
	public void doChallenge(IPlayer challenger, boolean isSuccessful);
	
	public void doAction(IGameAction action);
	
	public IGameAction getLastAction();
	
	public IGameAction[] getHistory();
	
	public MoveResult[] getAnalysis();
	
	public GameState getGameState();
	
	public DictionaryType getDictionaryType();
	
	public ChallengeMode getChallengeMode();
	
	public IBoard getBoard();
	
	public IMoveEngine getMoveEngine();
	
	public ITileValues getTileValues();
	
	public ITilePool getTilePool();
	
	public IPlayer[] getPlayers();
	
	public IPlayer getPlayer(String handle);
	
	public IPlayer getCurrentPlayer();
	
	public IPlayer getWinner();
	
	public IPlayer getLocalPlayer();
	
	public List<Tile> getTrackedTiles(IPlayer player);
	
	public int getTimeLimit();
	
	public void setTimeLimit(int minutes);
	
	public int getTimeIncrement();
	
	public void setTimeIncrement(int minutes);
	
	public void addGameListener(IGameListener listener);
	
	public void removeGameListener(IGameListener listener);
	
	public void setAnalysisMode(boolean on);
	
	public boolean isAnalysisMode();
	
//	public boolean isFinished();
	
	public boolean isLocal();
	
	public boolean isObserved();

}
