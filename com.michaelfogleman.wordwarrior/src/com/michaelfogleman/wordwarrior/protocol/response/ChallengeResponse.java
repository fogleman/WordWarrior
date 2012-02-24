package com.michaelfogleman.wordwarrior.protocol.response;

public class ChallengeResponse extends Response {
	
	private boolean successful;
	
	public ChallengeResponse(boolean successful) {
		this.successful = successful;
	}
	
	public boolean isSuccessful() {
		return successful;
	}
	
}
