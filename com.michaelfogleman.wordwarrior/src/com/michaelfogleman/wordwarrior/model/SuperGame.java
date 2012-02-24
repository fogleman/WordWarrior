package com.michaelfogleman.wordwarrior.model;

public class SuperGame extends AbstractGame {
	
	public SuperGame(DictionaryType dictionaryType, ChallengeMode challengeMode, int playerCount) {
		super(
			dictionaryType,
			challengeMode,
			new SuperBoard(),
			new StandardMoveEngine(),
			new SuperTilePool(),
			playerCount);
	}

}
