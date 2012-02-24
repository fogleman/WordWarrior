package com.michaelfogleman.wordwarrior.model;

public class Match {
	
	private int rating;
	private String handle;
	private DictionaryType dictionary;
	private int time;
	private int increment;
	private boolean rated;
	private boolean noescape;
	private ChallengeMode challengeMode;
	private UserType userType;
	
	public Match(String handle, int rating, DictionaryType dictionary, int time, int increment, boolean rated, boolean noescape, ChallengeMode challengeMode, UserType userType) {
		this.handle = handle;
		this.rating = rating;
		this.dictionary = dictionary;
		this.time = time;
		this.increment = increment;
		this.rated = rated;
		this.noescape = noescape;
		this.challengeMode = challengeMode;
		this.userType = userType;
	}
	
	public Match(Seek seek) {
		this.handle = seek.getHandle();
		this.rating = seek.getRating();
		this.dictionary = seek.getDictionary();
		this.time = seek.getTime();
		this.increment = seek.getIncrement();
		this.rated = seek.isRated();
		this.noescape = seek.isNoEscape();
		this.challengeMode = seek.getChallengeMode();
		this.userType = seek.getUserType();
	}
	
	public int getRating() {
		return rating;
	}

	public String getHandle() {
		return handle;
	}

	public DictionaryType getDictionary() {
		return dictionary;
	}

	public int getTime() {
		return time;
	}

	public int getIncrement() {
		return increment;
	}

	public boolean isRated() {
		return rated;
	}

	public boolean isNoescape() {
		return noescape;
	}

	public ChallengeMode getChallengeMode() {
		return challengeMode;
	}

	public UserType getUserType() {
		return userType;
	}

	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append(handle);
		b.append("(");
		b.append(rating);
		b.append(") is challenging you to a game: ");
		b.append(time);
		b.append("/");
		b.append(increment);
		b.append(" ");
		b.append(challengeMode.getName());
		b.append(" ");
		b.append(dictionary.getName());
		b.append(" ");
		b.append(rated ? "rated" : "unrated");
		b.append(" ");
		b.append(noescape ? "noescape " : " ");
		b.append(userType.getName());
		return b.toString();
	}

}
