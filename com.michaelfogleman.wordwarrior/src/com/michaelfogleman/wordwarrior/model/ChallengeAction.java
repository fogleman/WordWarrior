package com.michaelfogleman.wordwarrior.model;

public class ChallengeAction extends AbstractGameAction {
	
	private IPlayer challenger;
	private boolean successful;
	
	public ChallengeAction(IPlayer challenger, boolean successful) {
		this.challenger = challenger;
		this.successful = successful;
	}
	
	public String getName() {
		return "Challenge " + (successful ? "(Successful)" : "(Failed)");
	}
	
	public IPlayer getChallenger() {
		return challenger;
	}
	
	public boolean isSuccessful() {
		return successful;
	}
	
	public String toString() {
		return "CHALLENGE";
	}

}
