package com.michaelfogleman.wordwarrior.model;

import java.util.List;

public interface IBuddyManager {
	
	public List<String> getBuddyHandles();
	
	public List<User> getBuddies();
	
	public void addBuddy(String handle);
	
	public void removeBuddy(String handle);
	
	public void addBuddyListener(IBuddyListener listener);
	
	public void removeBuddyListener(IBuddyListener listener);

}
