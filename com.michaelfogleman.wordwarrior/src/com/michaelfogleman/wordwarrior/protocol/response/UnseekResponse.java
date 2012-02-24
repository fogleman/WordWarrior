package com.michaelfogleman.wordwarrior.protocol.response;

public class UnseekResponse extends Response {

	private String handle;

	public UnseekResponse(String handle) {
		this.handle = handle;
	}
	
	public String getHandle() {
		return handle;
	}

	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append(handle);
		b.append(" is no longer seeking a game");
		return b.toString();
	}

}
