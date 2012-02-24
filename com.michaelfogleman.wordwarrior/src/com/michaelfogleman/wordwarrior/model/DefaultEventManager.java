package com.michaelfogleman.wordwarrior.model;

import java.util.ArrayList;
import java.util.List;

public class DefaultEventManager implements IEventManager {
	
	private List<IEventListener> listeners;
	
	public DefaultEventManager() {
		listeners = new ArrayList<IEventListener>();
	}

	public void publishEvent(String message) {
		eventPublished(message);
	}
	
	public void addEventListener(IEventListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	public void removeEventListener(IEventListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}
	
	private void eventPublished(String message) {
		synchronized (listeners) {
			for (IEventListener listener : listeners) {
				listener.eventPublished(message);
			}
		}
	}

}
