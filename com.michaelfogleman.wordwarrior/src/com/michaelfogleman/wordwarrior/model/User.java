package com.michaelfogleman.wordwarrior.model;

public class User implements Comparable<User> {
	
	private String handle;
	private int rating;
	private DictionaryType dictionaryType;
	private UserState userState;
	private UserType userType;
	
	public DictionaryType getDictionaryType() {
		return dictionaryType;
	}
	
	public void setDictionaryType(DictionaryType dictionaryType) {
		this.dictionaryType = dictionaryType;
	}
	
	public String getHandle() {
		return handle;
	}
	
	public void setHandle(String handle) {
		this.handle = handle;
	}
	
	public int getRating() {
		return rating;
	}
	
	public void setRating(int rating) {
		this.rating = rating;
	}
	
	public UserState getUserState() {
		return userState;
	}
	
	public void setUserState(UserState userState) {
		this.userState = userState;
	}
	
	public UserType getUserType() {
		return userType;
	}
	
	public void setUserType(UserType userType) {
		this.userType = userType;
	}
	
	public int compareTo(User user) {
		int a = compareUserState(user);
		if (a != 0) return a;
		int b = compareHandle(user);
		if (b != 0) return b;
		return 0;
	}
	
	private int compareUserState(User user) {
		if (userState == null && user.userState == null) {
			return 0;
		}
		else if (userState == null) {
			return 1;
		}
		else if (user.userState == null) {
			return -1;
		}
		else {
			return userState.compareTo(user.userState);
		}
	}
	
	private int compareHandle(User user) {
		if (handle == null && user.handle == null) {
			return 0;
		}
		else if (handle == null) {
			return 1;
		}
		else if (user.handle == null) {
			return -1;
		}
		else {
			return handle.compareToIgnoreCase(user.handle);
		}
	}
	
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append(handle);
		b.append("(");
		b.append(rating);
		b.append(") ");
		b.append(userState == null ? "unknown" : userState.getName());
		b.append(" ");
		b.append(userType == null ? "unknown" : userType.getName());
		b.append(" ");
		b.append(dictionaryType == null ? "unknown" : dictionaryType.getName());
		return b.toString();
	}

}
