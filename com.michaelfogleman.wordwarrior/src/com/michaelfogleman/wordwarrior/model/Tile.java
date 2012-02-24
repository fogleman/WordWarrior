package com.michaelfogleman.wordwarrior.model;

import com.michaelfogleman.wordwarrior.Util;

public class Tile implements Comparable<Tile> {
	
	public final static Tile NONE = new Tile(' ');
	
	private char letter;
	private boolean wild;
	
	public Tile(char letter) {
		this(letter, letter == '?' || Character.isUpperCase(letter));
	}
	
	public Tile(char letter, boolean wild) {
		this.letter = Character.toLowerCase(letter);
		this.wild = wild;
		Util.check((this.letter >= 'a' && this.letter <= 'z') || this.letter == '?' || this.letter == ' ', "Invalid tile character:" + this.letter);
	}
	
	public boolean isEmpty() {
		return (letter == ' ');
	}
	
	public boolean isWild() {
		return wild; //(letter == '?') || Character.isUpperCase(letter);
	}
	
	public char getLetter() {
		return letter; //Character.toLowerCase(letter);
	}
	
	public char getRepresentation() {
		if (wild) {
			return Character.toUpperCase(letter);
		}
		return letter;
	}
	
//	public char getCharacter() {
//		return letter;
//	}
	
	public String toString() {
		return Character.toString(getRepresentation());
	}
	
	public int compareTo(Tile tile) {
		Character c1 = new Character(letter);
		Character c2 = new Character(tile.letter);
		return c1.compareTo(c2);
	}
	
	public boolean equals(Object object) {
		Tile tile = (Tile)object;
		return this.letter == tile.letter && this.wild == tile.wild; 
	}
	
	public int hashCode() {
		Character c = new Character(letter);
		Boolean b = new Boolean(wild);
		return c.hashCode() ^ b.hashCode();
	}

}
