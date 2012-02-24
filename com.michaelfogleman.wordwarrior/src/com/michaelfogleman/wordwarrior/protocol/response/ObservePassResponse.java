package com.michaelfogleman.wordwarrior.protocol.response;

public class ObservePassResponse extends Response {
	
	private int minutes;
	private int seconds;
	
	public ObservePassResponse(int minutes, int seconds) {
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
