package com.michaelfogleman.wordwarrior.protocol.response;

import com.michaelfogleman.wordwarrior.model.ChallengeMode;
import com.michaelfogleman.wordwarrior.model.DictionaryType;
import com.michaelfogleman.wordwarrior.model.Seek;
import com.michaelfogleman.wordwarrior.model.UserType;

public class SeekResponse extends Response {
	
	private Seek seek;
	
	public SeekResponse(String handle, int rating, int time, int increment, boolean rated, boolean noescape, DictionaryType dictionary, ChallengeMode challengeMode, UserType userType) {
		seek = new Seek(handle, rating, time, increment, rated, noescape, dictionary, challengeMode, userType);
	}
	
	public Seek getSeek() {
		return seek;
	}
	
	public String toString() {
		return seek.toString();
	}

}
