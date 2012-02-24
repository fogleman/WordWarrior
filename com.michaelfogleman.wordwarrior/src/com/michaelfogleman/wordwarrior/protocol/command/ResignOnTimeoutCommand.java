package com.michaelfogleman.wordwarrior.protocol.command;

import com.michaelfogleman.wordwarrior.model.IGame;
import com.michaelfogleman.wordwarrior.model.IPlayer;

public class ResignOnTimeoutCommand extends Command {
	
	private IGame game;
	private IPlayer player;
	
	public ResignOnTimeoutCommand(IGame game, IPlayer player) {
		this.game = game;
		this.player = player;
	}
	
	public IGame getGame() {
		return game;
	}
	
	public IPlayer getPlayer() {
		return player;
	}

}
