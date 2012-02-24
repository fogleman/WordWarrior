package com.michaelfogleman.wordwarrior.protocol.response;

import com.michaelfogleman.wordwarrior.model.ChallengeMode;
import com.michaelfogleman.wordwarrior.model.DictionaryType;
import com.michaelfogleman.wordwarrior.model.Match;
import com.michaelfogleman.wordwarrior.model.UserType;

public class MatchResponse extends Response {
	
	private Match match;
	
	public MatchResponse(String handle, int rating, DictionaryType dictionary, int time, int increment, boolean rated, boolean noescape, ChallengeMode challengeMode, UserType userType) {
		match = new Match(handle, rating, dictionary, time, increment, rated, noescape, challengeMode, userType);
	}
	
	public Match getMatch() {
		return match;
	}
	
	public String toString() {
		return match.toString();
	}
	
}
