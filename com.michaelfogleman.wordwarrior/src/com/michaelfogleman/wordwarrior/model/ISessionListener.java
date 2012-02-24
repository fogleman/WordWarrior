package com.michaelfogleman.wordwarrior.model;

public interface ISessionListener {
	
	public void gameCreated(IGame game);
	
	public void sessionClosed(Session session);
	
	public void matchReceived(Match match);

}
