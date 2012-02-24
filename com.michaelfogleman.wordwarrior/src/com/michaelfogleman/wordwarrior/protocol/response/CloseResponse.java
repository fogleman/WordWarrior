package com.michaelfogleman.wordwarrior.protocol.response;

public class CloseResponse extends Response {

	private String reason;

	public CloseResponse(String reason) {
		this.reason = reason;
	}
	
	public String toString() {
		return reason;
	}

}
