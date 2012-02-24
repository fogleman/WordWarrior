package com.michaelfogleman.wordwarrior.protocol.response;

public class NullResponse extends Response {
	
	private String response;
	
	public NullResponse(String response) {
		this.response = response;
	}
	
	public String getResponse() {
		return response;
	}
	
}
