package com.michaelfogleman.wordwarrior.model;

public class Seek {
	
	private int rating;
	private String handle;
	private DictionaryType dictionary;
	private int time;
	private int increment;
	private boolean rated;
	private boolean noescape;
	private ChallengeMode challengeMode;
	private UserType userType;
	
	public Seek(String handle, int rating, int time, int increment, boolean rated, boolean noescape, DictionaryType dictionary, ChallengeMode challengeMode, UserType userType) {
		this.rating = rating;
		this.handle = handle;
		this.dictionary = dictionary;
		this.time = time;
		this.increment = increment;
		this.rated = rated;
		this.noescape = noescape;
		this.challengeMode = challengeMode;
		this.userType = userType;
	}
	
	public String getHandle() {
		return handle;
	}
	
	public int getRating() {
		return rating;
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
	
	public boolean isNoEscape() {
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
		b.append(") is seeking: ");
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
		b.append(noescape ? "noescape " : "");
		return b.toString();
	}

}
