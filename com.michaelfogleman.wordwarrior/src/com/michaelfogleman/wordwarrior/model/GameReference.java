package com.michaelfogleman.wordwarrior.model;

import java.util.Calendar;

public class GameReference implements Comparable<GameReference> {
	
	public static final int HISTORY_TYPE = 1;
	public static final int LIBRARY_TYPE = 2;
	public static final int ADJOURNED_TYPE = 4;
	
	private String owner;
	private int type;
	private int id;
	private String handle1;
	private int rating1;
	private String handle2;
	private int rating2;
	private DictionaryType dictionary;
	private String result;
	private Calendar date;
	
	public GameReference(String owner, int type) {
		this.owner = owner;
		this.type = type;
	}
	
	public int compareTo(GameReference that) {
		if (this.type != that.type) return this.type - that.type;
		return this.id - that.id;
	}
	
	public String getOwner() {
		return owner;
	}
	
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public void setDate(Calendar date) {
		this.date = date;
	}
	
	public Calendar getDate() {
		return date;
	}
	
	public void setDictionary(DictionaryType dictionary) {
		this.dictionary = dictionary;
	}
	
	public DictionaryType getDictionary() {
		return dictionary;
	}
	
	public void setHandle1(String handle1) {
		this.handle1 = handle1;
	}
	
	public String getHandle1() {
		return handle1;
	}
	
	public void setHandle2(String handle2) {
		this.handle2 = handle2;
	}
	
	public String getHandle2() {
		return handle2;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public void setRating1(int rating1) {
		this.rating1 = rating1;
	}
	
	public int getRating1() {
		return rating1;
	}
	
	public void setRating2(int rating2) {
		this.rating2 = rating2;
	}
	
	public int getRating2() {
		return rating2;
	}
	
	public void setResult(String result) {
		this.result = result;
	}
	
	public String getResult() {
		return result;
	}

}
