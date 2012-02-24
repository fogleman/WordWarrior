package com.michaelfogleman.wordwarrior.protocol.response;

public class PassResponse extends Response {
	
	private int minutes;
	private int seconds;
	
	public PassResponse(int minutes, int seconds) {
		this.minutes = minutes;
		this.seconds = seconds;
	}
	
	public int getMinutes() {
		return minutes;
	}
	
	public int getSeconds() {
		return seconds;
	}

}
