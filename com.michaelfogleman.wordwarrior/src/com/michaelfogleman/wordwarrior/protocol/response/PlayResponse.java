package com.michaelfogleman.wordwarrior.protocol.response;

import com.michaelfogleman.wordwarrior.model.ChallengeMode;
import com.michaelfogleman.wordwarrior.model.DictionaryType;
import com.michaelfogleman.wordwarrior.model.Match;
import com.michaelfogleman.wordwarrior.model.Tile;
import com.michaelfogleman.wordwarrior.model.UserType;

public class PlayResponse extends Response {
	
	private Match match;
	private int turn;
	private Tile[] a;
	private Tile[] b;
	
	// Receive: "0 PLAY 565 matman 0 60 0 1 1 1 2 uasesgc ifoavpr"
	public PlayResponse(String handle, int rating, int time, int increment, boolean rated, boolean noescape, DictionaryType dictionary, ChallengeMode challengeMode, int turn, Tile[] a, Tile[] b) {
		match = new Match(handle, rating, dictionary, time, increment, rated, noescape, challengeMode, UserType.PLAYER);
		this.turn = turn;
		this.a = a;
		this.b = b;
	}
	
	public Match getMatch() {
		return match;
	}
	
	public int getTurn() {
		return turn;
	}
	
	public Tile[] getPlayer1Tiles() {
		return a;
	}
	
	public Tile[] getPlayer2Tiles() {
		return b;
	}

}
