package com.michaelfogleman.wordwarrior.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.michaelfogleman.wordwarrior.ScrabblePlugin;
import com.michaelfogleman.wordwarrior.protocol.Connection;
import com.michaelfogleman.wordwarrior.protocol.command.AddBuddyCommand;
import com.michaelfogleman.wordwarrior.protocol.command.RemoveBuddyCommand;
import com.michaelfogleman.wordwarrior.protocol.response.BuddiesResponse;
import com.michaelfogleman.wordwarrior.protocol.response.BuddyLoginResponse;
import com.michaelfogleman.wordwarrior.protocol.response.BuddyLogoutResponse;
import com.michaelfogleman.wordwarrior.protocol.response.BuddyStateResponse;
import com.michaelfogleman.wordwarrior.protocol.response.IResponseHandler;
import com.michaelfogleman.wordwarrior.protocol.response.ResponseAdapter;

class DefaultBuddyManager implements IBuddyManager {
	
	private Connection connection;
	private Map<String, User> buddies;
	private List<IBuddyListener> listeners;
	private IEventManager eventManager;
	
	public DefaultBuddyManager(Connection connection) {
		this.connection = connection;
		this.buddies = new HashMap<String, User>();
		this.listeners = new ArrayList<IBuddyListener>();
		this.eventManager = ScrabblePlugin.getDefault().getEventManager();
		connection.addResponseHandler(createResponseHandler());
	}

	public void addBuddy(String handle) {
		connection.send(new AddBuddyCommand(handle));
	}

	public List<User> getBuddies() {
		List<User> result = new ArrayList<User>(buddies.values());
		Collections.sort(result);
		return result;
	}

	public List<String> getBuddyHandles() {
		return new ArrayList<String>(buddies.keySet());
	}

	public void removeBuddy(String handle) {
		connection.send(new RemoveBuddyCommand(handle));
		buddies.remove(handle);
	}
	
	public void addBuddyListener(IBuddyListener listener) {
		synchronized(listeners) {
			listeners.add(listener);
		}
	}

	public void removeBuddyListener(IBuddyListener listener) {
		synchronized(listeners) {
			listeners.remove(listener);
		}
	}
	
	private IResponseHandler createResponseHandler() {
		return new ResponseAdapter() {
			public void handle(BuddiesResponse response) {
				buddies.clear();
				for (String handle : response.getHandles()) {
					User user = new User();
					user.setHandle(handle);
					buddies.put(user.getHandle().toLowerCase(), user);
				}
				buddyListUpdated();
			}
			public void handle(BuddyLoginResponse response) {
				User user = response.getUser();
				buddies.put(user.getHandle().toLowerCase(), user);
				buddyLoggedIn(user);
				String message = "Your buddy, " + user.getHandle() + ", has just logged in!";
				eventManager.publishEvent(message);
			}
			public void handle(BuddyLogoutResponse response) {
				User user = new User();
				user.setHandle(response.getUser().getHandle());
				buddies.put(user.getHandle().toLowerCase(), user);
				buddyLoggedOut(user);
				String message = "Your buddy, " + user.getHandle() + ", has logged out.";
				eventManager.publishEvent(message);
			}
			public void handle(BuddyStateResponse response) {
				User user = response.getUser();
				buddies.put(user.getHandle().toLowerCase(), user);
				buddyChangedState(user);
				String message = "Your buddy, " + user.getHandle() + ", is now " + user.getUserState().getName() + ".";
				eventManager.publishEvent(message);
			}
		};
	}
	
	private void buddyListUpdated() {
		synchronized(listeners) {
			for (IBuddyListener listener : listeners) {
				listener.buddyListUpdated();
			}
		}
	}
	
	private void buddyChangedState(User buddy) {
		synchronized(listeners) {
			for (IBuddyListener listener : listeners) {
				listener.buddyChangedState(buddy);
			}
		}
	}
	
	private void buddyLoggedIn(User buddy) {
		synchronized(listeners) {
			for (IBuddyListener listener : listeners) {
				listener.buddyLoggedIn(buddy);
			}
		}
	}
	
	private void buddyLoggedOut(User buddy) {
		synchronized(listeners) {
			for (IBuddyListener listener : listeners) {
				listener.buddyLoggedOut(buddy);
			}
		}
	}

}
