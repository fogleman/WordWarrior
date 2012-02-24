package com.michaelfogleman.wordwarrior.protocol.response;

public class AdjustResponse extends Response {
	
	private String handle;
	
	public AdjustResponse(String handle) {
		this.handle = handle;
	}
	
	public String getHandle() {
		return handle;
	}
	
}
