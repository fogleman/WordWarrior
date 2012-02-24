package com.michaelfogleman.wordwarrior.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.michaelfogleman.wordwarrior.Util;
import com.michaelfogleman.wordwarrior.engine.BasicMoveMaker;
import com.michaelfogleman.wordwarrior.engine.Dictionary;
import com.michaelfogleman.wordwarrior.protocol.Connection;
import com.michaelfogleman.wordwarrior.protocol.IConnectionListener;
import com.michaelfogleman.wordwarrior.protocol.command.*;
import com.michaelfogleman.wordwarrior.protocol.response.*;

public class Session extends ResponseAdapter {
	
	private Connection connection;
	private List<ISessionListener> listeners;
	private IBuddyManager buddyManager;
	private IArchiveManager archiveManager;
	
	private String handle;
	private Map<String, IGame> games;
	private Settings settings;
	
	public Session(Connection connection) {
		this.connection = connection;
		listeners = new ArrayList<ISessionListener>();
		games = new HashMap<String, IGame>();
		settings = new Settings();
		buddyManager = new DefaultBuddyManager(connection);
		archiveManager = new DefaultArchiveManager(connection);
		connection.addConnectionListener(new IConnectionListener() {
			public void connectionClosed(Connection connection) {
				Session.this.connectionClosed();
			}
		});
		connection.addResponseHandler(this);
	}
	
	public IBuddyManager getBuddyManager() {
		return buddyManager;
	}
	
	public IArchiveManager getArchiveManager() {
		return archiveManager;
	}
	
	public void createLocalGame() {
		IGame game = createLocalGame(DictionaryType.TWL06, ChallengeMode.VOID, 2);
		game.setTimeLimit(60);
		IPlayer human = new LocalPlayer(game, 1, "Human", new StandardTileRack());
		human.setRating(0);
//		human.setMoveMaker(new BasicMoveMaker());
		IPlayer computer = new LocalPlayer(game, 2, "Computer", new StandardTileRack());
		computer.setRating(0);
		computer.setMoveMaker(new BasicMoveMaker());
		game.start();
	}
	
	public IGame createLocalGame(DictionaryType dictionaryType, ChallengeMode challengeMode, int playerCount) {
		IGame game = new StandardGame(dictionaryType, challengeMode, playerCount);
		addGame(game.toString(), game);
		return game;
	}
	
	public void closeGame(IGame game) {
		game.dispose();
		for (String id : games.keySet()) {
			IGame g = games.get(id);
			if (g == game) {
				games.remove(id);
				break;
			}
		}
	}
	
	private void addGame(String id, IGame game) {
		games.put(id, game);
		game.addGameListener(createGameListener());
		gameCreated(game);
	}
	
	private IGame getGame(String id) {
		return games.get(id);
	}
	
	private void connectionClosed() {
		//game = null;
		sessionClosed();
	}
	
	public Connection getConnection() {
		return connection;
	}
	
	public Settings getSettings() {
		return settings;
	}
	
	public void login(String handle, String password) {
		this.handle = handle;
		connection.open(handle, password);
		archiveManager.refresh(handle);
	}
	
	public void close() {
		connection.close();
	}
	
//	public Game getDefaultGame() {
//		return game;//getGame(connection.toString());
//	}
	
	private IGame getRemoteGame() {
		return getGame(connection.toString());
	}
	
	public void validateMove(IGame game, Move move) {
		MoveResult moveResult = game.getMoveEngine().testMove(game.getBoard(), move);
		if (game.isLocal()) {
			Dictionary dictionary = Dictionary.getInstance(game.getDictionaryType());
			boolean valid = true;
			for (String word : moveResult.getWords()) {
				if (!dictionary.isWord(word)) {
					valid = false;
					break;
				}
			}
			if (valid) {
				game.doMove(move);
			}
		}
		else {
			connection.send(new PreMoveCommand(game.getDictionaryType(), moveResult));
		}
	}
	
	public void handle(AdjustResponse response) {
		IGame game = getRemoteGame();
		if (game == null) return;
		IPlayer player = game.getPlayer(response.getHandle());
		if (player == null) return;
		player.addPenalty();
	}
	
	public void handle(PreMoveResponse response) {
		IGame game = getRemoteGame();
		if (game == null) return;
		if (response.isValid()) {
			game.doMove(response.getMoveResult().getMove());
		}
	}
	
	public void handle(SetAllResponse response) {
		settings.setAll(response.getSettings());
	}
	
	public void handle(SetFormulaResponse response) {
		settings.setAll(response.getSettings());
	}
	
	public void handle(ChallengeResponse response) {
		IGame game = getRemoteGame();
		if (game == null) return;
		IPlayer player = game.getCurrentPlayer();
		game.doChallenge(player, response.isSuccessful());
	}
	
	public void handle(ChangeResponse response) {
		IGame game = getRemoteGame();
		if (game == null) return;
		IPlayer player = game.getCurrentPlayer();
		player.getClock().set(response.getMinutes(), response.getSeconds());
		game.doExchangeTiles(player.getTileRack().getTiles());
		player.getTileRack().replaceAll(game.getTilePool(), response.getTiles());
	}
	
	public void handle(PassResponse response) {
		IGame game = getRemoteGame();
		if (game == null) return;
		IPlayer player = game.getCurrentPlayer();
		if (player.isLocal()) return;
		game.doPassTurn();
		player.getClock().set(response.getMinutes(), response.getSeconds());
	}
	
	public void handle(MoveResponse response) {
		IGame game = getRemoteGame();
		if (game == null) return;
		IPlayer player = game.getCurrentPlayer();
		game.doMove(response.getMove());
		player.getClock().set(response.getMinutes(), response.getSeconds());
		player.getTileRack().replaceAll(game.getTilePool(), response.getTiles());
	}
	
	// TODO redundant observe code
	
	public void handle(ResumeResponse response) {
		addGame(connection.toString(), response.getGame());
	}
	
	public void handle(ObserveStartResponse response) {
		addGame(connection.toString(), response.getGame());
	}
	
	public void handle(ObserveMoveResponse response) {
		IGame game = getRemoteGame();
		if (game == null) return;
		IPlayer player = game.getCurrentPlayer();
		game.doMove(response.getMove());
		player.getClock().set(response.getMinutes(), response.getSeconds());
		player.getTileRack().replaceAll(game.getTilePool(), response.getTiles());
	}
	
	public void handle(ObserveChallengeResponse response) {
		IGame game = getRemoteGame();
		if (game == null) return;
		IPlayer player = game.getCurrentPlayer();
		game.doChallenge(player, response.isSuccessful());
	}
	
	public void handle(ObserveChangeResponse response) {
		IGame game = getRemoteGame();
		if (game == null) return;
		IPlayer player = game.getCurrentPlayer();
		player.getClock().set(response.getMinutes(), response.getSeconds());
		game.doExchangeTiles(player.getTileRack().getTiles());
		player.getTileRack().replaceAll(game.getTilePool(), response.getTiles());
	}
	
	public void handle(ObservePassResponse response) {
		IGame game = getRemoteGame();
		if (game == null) return;
		IPlayer player = game.getCurrentPlayer();
		if (player.isLocal()) return;
		game.doPassTurn();
		player.getClock().set(response.getMinutes(), response.getSeconds());
	}
	
	
	
	public void handle(AcceptResponse response) {
		Match match = response.getMatch();
		String handle = match.getHandle();
		
		Tile[] player1Tiles = response.getPlayer1Tiles();
		Tile[] player2Tiles = response.getPlayer2Tiles();
		int firstPlayer = response.getTurn();
		
		IGame game = new StandardGame(match.getDictionary(), match.getChallengeMode(), 2);
		game.setTimeLimit(match.getTime());
		game.setTimeIncrement(match.getIncrement());
		
		int position = (firstPlayer == 1) ? 2 : 1;
		IPlayer localPlayer = new LocalPlayer(game, position, this.handle, new StandardTileRack());
		position = (firstPlayer == 1) ? 1 : 2;
		IPlayer remotePlayer = new RemotePlayer(game, position, handle, new StandardTileRack());
		remotePlayer.setRating(match.getRating());
		
		localPlayer.getTileRack().replaceAll(game.getTilePool(), player2Tiles);
		remotePlayer.getTileRack().replaceAll(game.getTilePool(), player1Tiles);
		
		game.start();
		
		addGame(connection.toString(), game);
	}
	
	public void handle(MatchResponse response) {
		//startGame(response.getMatch(), false);
		matchReceived(response.getMatch());
	}
	
	public void startGame(Seek seek) {
		startGame(new Match(seek), true);
	}
	
	public void startGame(Match match) {
		startGame(match, false);
	}
	
	private void startGame(Match match, boolean isSeek) {
		String handle = match.getHandle();
		
		IGame game = new StandardGame(match.getDictionary(), match.getChallengeMode(), 2);
		game.setTimeLimit(match.getTime());
		game.setTimeIncrement(match.getIncrement());
		
		int firstPlayer = Util.randomInt(2) + 1;
		int position = (firstPlayer == 1) ? 1 : 2;
		IPlayer localPlayer = new LocalPlayer(game, position, this.handle, new StandardTileRack());
		position = (firstPlayer == 1) ? 2 : 1;
		IPlayer remotePlayer = new RemotePlayer(game, position, handle, new StandardTileRack());
		remotePlayer.setRating(match.getRating());
		
		// TODO just pass ref to game?
		ITileRack[] racks = new ITileRack[] { localPlayer.getTileRack(), remotePlayer.getTileRack()};
		
		if (isSeek) {
			connection.send(new PlayCommand(match, firstPlayer, racks));
		}
		else {
			connection.send(new AcceptMatchCommand(match, firstPlayer, racks));
		}
		
		game.start();
		
		addGame(connection.toString(), game);
	}
	
	private IGameListener createGameListener() {
		return new GameAdapter() {
			public void playerMoved(IGame game, IPlayer player, MoveResult moveResult) {
				if (!game.isLocal() && player.isLocal()) {
					int minutes = player.getClock().getMinutes();
					int seconds = player.getClock().getSeconds();
					ITileRack rack = player.getTileRack();
					connection.send(new MoveCommand(moveResult, minutes, seconds, rack));
				}
			}
			public void playerPassedTurn(IGame game, IPlayer player) {
				if (!game.isLocal() && player.isLocal()) {
					int minutes = player.getClock().getMinutes();
					int seconds = player.getClock().getSeconds();
					boolean endGame = false;
					int local = 0;
					int remote = 0;
					if (game.getGameState() == GameState.ENDED) {
						endGame = true;
						for (IPlayer p : game.getPlayers()) {
							if (p.isLocal()) {
								local = p.getScore();
							}
							else {
								remote = p.getScore();
							}
						}
					}
					connection.send(new PassCommand(minutes, seconds, endGame, local, remote));
				}
			}
			public void playerExchangedTiles(IGame game, IPlayer player, Tile[] tiles, Tile[] newRack) {
				if (!game.isLocal() && player.isLocal()) {
					int minutes = player.getClock().getMinutes();
					int seconds = player.getClock().getSeconds();
					connection.send(new ExchangeTilesCommand(tiles, newRack, minutes, seconds));
				}
			}
			public void playerChallenged(IGame game, IPlayer player, boolean successful, String[] words) {
				if (!game.isLocal() && player.isLocal()) {
					connection.send(new ChallengeCommand(game.getDictionaryType(), words));
				}
			}
			public void playerUndidMove(IGame game, IPlayer player, MoveResult moveResult) {
				if (!game.isLocal() && player.isLocal()) {
					int minutes = player.getClock().getMinutes();
					int seconds = player.getClock().getSeconds();
					connection.send(new UndoMoveCommand(moveResult, minutes, seconds));
				}
			}
			public void playerClockExpired(IGame game, IPlayer player, boolean penalty) {
				if (!game.isLocal() && player.isLocal()) {
					if (penalty) {
						// TODO add handle to command
						connection.send(new AdjustCommand());
					}
					else {
						connection.send(new ResignOnTimeoutCommand(game, player));
					}
				}
			}
		};
	}
	
	
	
	public void addSessionListener(ISessionListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}		
	}
	
	public void removeSessionListener(ISessionListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}
	
	private void sessionClosed() {
		synchronized (listeners) {
			for (ISessionListener listener : listeners) {
				listener.sessionClosed(this);
			}
		}
	}
	
	private void gameCreated(IGame game) {
		synchronized (listeners) {
			for (ISessionListener listener : listeners) {
				listener.gameCreated(game);
			}
		}
	}
	
	private void matchReceived(Match match) {
		synchronized (listeners) {
			for (ISessionListener listener : listeners) {
				listener.matchReceived(match);
			}
		}
	}

}
