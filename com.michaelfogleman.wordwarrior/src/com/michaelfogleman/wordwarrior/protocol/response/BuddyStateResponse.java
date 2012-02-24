package com.michaelfogleman.wordwarrior.protocol.response;

import com.michaelfogleman.wordwarrior.model.User;

public class BuddyStateResponse extends Response {
	
	private User user;
	
	public BuddyStateResponse(User user) {
		this.user = user;
	}
	
	public User getUser() {
		return user;
	}
	
	public String toString() {
		return user.toString();
	}

}
