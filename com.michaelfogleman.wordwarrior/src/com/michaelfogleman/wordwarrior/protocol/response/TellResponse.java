package com.michaelfogleman.wordwarrior.protocol.response;

import com.michaelfogleman.wordwarrior.model.UserType;

public class TellResponse extends Response {
	
	private String handle;
	private UserType userType;
	private String message;
	
	public TellResponse(String handle, String message) {
		this(handle, message, UserType.PLAYER);
	}
	
	public TellResponse(String handle, String message, UserType userType) {
		this.handle = handle;
		this.message = message;
		this.userType = userType;
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
		b.append(" tells you: ");
		b.append(message);
		return b.toString();
	}
	
}
