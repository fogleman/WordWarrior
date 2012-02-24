package com.michaelfogleman.wordwarrior.protocol.response;

import com.michaelfogleman.wordwarrior.model.UserType;

public class KibitzResponse extends Response {
	
	private String handle;
	private UserType userType;
	private String message;
	
	public KibitzResponse(String handle, UserType userType, String message) {
		this.handle = handle;
		this.userType = userType;
		this.message = message;
	}
	
	public String getHandle() {
		return handle;
	}
	
	public String getMessage() {
		return message;
	}
	
	public UserType getUserType() {
		return userType;
	}
	
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append(handle);
		char c = userType.getChar();
		if (c != ' ') {
			b.append("(");
			b.append(c);
			b.append(")");
		}
		b.append(" kibitzes: ");
		b.append(message);
		return b.toString();
	}
	
}
