package com.michaelfogleman.wordwarrior.model;

public interface IBuddyListener {
	
	public void buddyListUpdated();
	public void buddyChangedState(User buddy);
	public void buddyLoggedIn(User buddy);
	public void buddyLoggedOut(User buddy);
	
}
