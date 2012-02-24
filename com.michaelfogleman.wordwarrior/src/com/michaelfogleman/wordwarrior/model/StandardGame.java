package com.michaelfogleman.wordwarrior.model;

public class StandardGame extends AbstractGame {
	
	public StandardGame(DictionaryType dictionaryType, ChallengeMode challengeMode, int playerCount) {
		super(
			dictionaryType,
			challengeMode,
			new StandardBoard(),
			new StandardMoveEngine(),
			new StandardTilePool(),
			playerCount);
	}

}
