package com.michaelfogleman.wordwarrior.protocol.response;

public class ObserveChallengeResponse extends Response {
	
	private boolean successful;
	
	public ObserveChallengeResponse(boolean successful) {
		this.successful = successful;
	}
	
	public boolean isSuccessful() {
		return successful;
	}
	
}
