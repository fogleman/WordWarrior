package com.michaelfogleman.wordwarrior.model;

public enum ChallengeMode {
	
	SINGLE(0, "SINGLE", false, 0),
	DOUBLE(1, "DOUBLE", true, 0),
	FIVE_POINTS(2, "FIVE_POINTS", false, 5),
	VOID(3, "VOID", false, 0);
	
	private int id;
	private String name;
	private boolean loseTurn;
	private int penalty;
	
	ChallengeMode(int id, String name, boolean loseTurn, int penalty) {
		this.id = id;
		this.name = name;
		this.loseTurn = loseTurn;
		this.penalty = penalty;
	}
	
	public int getPenalty() {
		return penalty;
	}
	
	public boolean isLoseTurn() {
		return loseTurn;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public static ChallengeMode getInstance(int id) {
		for (ChallengeMode mode : ChallengeMode.values()) {
			if (mode.id == id) {
				return mode;
			}
		}
		return null;
	}

}
