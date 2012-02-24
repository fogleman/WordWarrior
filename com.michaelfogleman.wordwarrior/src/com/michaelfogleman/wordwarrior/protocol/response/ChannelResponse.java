package com.michaelfogleman.wordwarrior.protocol.response;

import com.michaelfogleman.wordwarrior.model.UserType;

public class ChannelResponse extends Response {
	
	private int channel;
	private String handle;
	private UserType userType;
	private String message;
	
	public ChannelResponse(int channel, String handle, UserType userType, String message) {
		this.channel = channel;
		this.handle = handle;
		this.userType = userType;
		this.message = message;
	}
	
	public int getChannel() {
		return channel;
	}
	
	public String getHandle() {
		return handle;
	}
	
	public UserType getUserType() {
		return userType;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("#");
		b.append(channel);
		b.append(" ");
		b.append(handle);
		char c = userType.getChar();
		if (c != ' ') {
			b.append("(");
			b.append(c);
			b.append(")");
		}
		b.append(" -> ");
		b.append(message);
		return b.toString();
	}

}
