package com.michaelfogleman.wordwarrior.protocol.response;

import com.michaelfogleman.wordwarrior.model.User;

public class WhoResponse extends Response {
	
	private User user;
	
	public WhoResponse(User user) {
		this.user = user;
	}
	
	public User getUser() {
		return user;
	}
	
	public String toString() {
		return user.toString();
	}

}
