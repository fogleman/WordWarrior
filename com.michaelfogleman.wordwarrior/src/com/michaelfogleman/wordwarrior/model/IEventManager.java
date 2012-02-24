package com.michaelfogleman.wordwarrior.model;

public interface IEventManager {
	
	public void publishEvent(String message);
	
	public void addEventListener(IEventListener listener);
	
	public void removeEventListener(IEventListener listener);

}
