package com.michaelfogleman.wordwarrior.protocol.response;

import com.michaelfogleman.wordwarrior.model.User;

public class BuddyLoginResponse extends Response {
	
	private User user;
	
	public BuddyLoginResponse(User user) {
		this.user = user;
	}
	
	public User getUser() {
		return user;
	}
	
	public String toString() {
		return user.toString();
	}

}
