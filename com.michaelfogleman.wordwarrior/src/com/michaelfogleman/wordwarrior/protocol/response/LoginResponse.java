package com.michaelfogleman.wordwarrior.protocol.response;

public class LoginResponse extends Response {
	
	private String message;
	
	public LoginResponse(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String toString() {
		return message;
	}

}
