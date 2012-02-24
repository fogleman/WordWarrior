package com.michaelfogleman.wordwarrior.model;

public enum UserType {
	
	PLAYER(0, ' ', "player"),
	COMPUTER(1, 'C', "computer"),
	ADMIN(2, '!', "admin"),
	BOT(3, 'B', "bot"),
	HELPER(4, 'H', "helper");
	
	private int id;
	private char c;
	private String name;
	
	private UserType(int id, char c, String name) {
		this.id = id;
		this.c = c;
		this.name = name;
	}
	
	public int getId() {
		return id;
	}
	
	public char getChar() {
		return c;
	}
	
	public String getName() {
		return name;
	}
	
	public static UserType getInstance(int id) {
		for (UserType type : UserType.values()) {
			if (type.id == id) {
				return type;
			}
		}
		return PLAYER;
	}

}
