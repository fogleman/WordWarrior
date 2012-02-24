package com.michaelfogleman.wordwarrior.model;

public enum UserState implements Comparable<UserState> {
	
	AVAILABLE('a', "available"),
	PLAYING('*', "playing"),
	OBSERVING('^', "observing"),
	EXAMINING('E', "examining");
	
	private char id;
	private String name;
	
	private UserState(char id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public char getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public static UserState getInstance(char id) {
		for (UserState state : UserState.values()) {
			if (state.id == id) {
				return state;
			}
		}
		return null;
	}

}
