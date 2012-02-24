package com.michaelfogleman.wordwarrior.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.michaelfogleman.wordwarrior.protocol.Connection;
import com.michaelfogleman.wordwarrior.protocol.command.AdjournedCommand;
import com.michaelfogleman.wordwarrior.protocol.command.HistoryCommand;
import com.michaelfogleman.wordwarrior.protocol.command.LibraryCommand;
import com.michaelfogleman.wordwarrior.protocol.response.AdjournedResponse;
import com.michaelfogleman.wordwarrior.protocol.response.HistoryResponse;
import com.michaelfogleman.wordwarrior.protocol.response.IResponseHandler;
import com.michaelfogleman.wordwarrior.protocol.response.LibraryResponse;
import com.michaelfogleman.wordwarrior.protocol.response.ResponseAdapter;

class DefaultArchiveManager implements IArchiveManager {
	
	private Connection connection;
	private Map<String, List<GameReference>> map;
	private List<IArchiveListener> listeners;
	private String owner;
	
	public DefaultArchiveManager(Connection connection) {
		this(connection, null);
	}
	
	public DefaultArchiveManager(Connection connection, String owner) {
		this.connection = connection;
		this.map = new HashMap<String, List<GameReference>>();
		this.listeners = new ArrayList<IArchiveListener>();
		connection.addResponseHandler(createResponseHandler());
		refresh(owner);
	}
	
	public String getActiveOwner() {
		return owner;
	}

	public void refresh(String owner) {
		this.owner = owner;
		if (owner != null) {
			map.remove(owner);
			connection.send(new HistoryCommand(owner));
			connection.send(new LibraryCommand(owner));
			connection.send(new AdjournedCommand(owner));
		}
	}
	
	public List<GameReference> getActiveGameReferences() {
		return getGameReferences(owner);
	}

	public List<GameReference> getAllGameReferences() {
		List<GameReference> result = new ArrayList<GameReference>();
		for (Iterator<List<GameReference>> i = map.values().iterator(); i.hasNext();) {
			List<GameReference> list = i.next();
			result.addAll(list);
		}
		Collections.sort(result);
		return result;
	}
	
	public List<GameReference> getGameReferences(String owner) {
		List<GameReference> result = new ArrayList<GameReference>();
		if (owner != null) {
			List<GameReference> list = map.get(owner);
			if (list != null) {
				result.addAll(list);
			}
		}
		Collections.sort(result);
		return result;
	}
	
	public void addArchiveListener(IArchiveListener listener) {
		synchronized(listeners) {
			listeners.add(listener);
		}
	}

	public void removeArchiveListener(IArchiveListener listener) {
		synchronized(listeners) {
			listeners.remove(listener);
		}
	}
	
	private IResponseHandler createResponseHandler() {
		return new ResponseAdapter() {
			// TODO consolidate these response types
			public void handle(HistoryResponse response) {
				String owner = response.getOwner();
				List<GameReference> references = response.getGameReferences();
				List<GameReference> list = map.get(owner);
				if (list == null) {
					map.put(owner, references);
				}
				else {
					list.addAll(references);
				}
				archiveUpdated();
			}
			public void handle(LibraryResponse response) {
				String owner = response.getOwner();
				List<GameReference> references = response.getGameReferences();
				List<GameReference> list = map.get(owner);
				if (list == null) {
					map.put(owner, references);
				}
				else {
					list.addAll(references);
				}
				archiveUpdated();
			}
			public void handle(AdjournedResponse response) {
				String owner = response.getOwner();
				List<GameReference> references = response.getGameReferences();
				List<GameReference> list = map.get(owner);
				if (list == null) {
					map.put(owner, references);
				}
				else {
					list.addAll(references);
				}
				archiveUpdated();
			}
		};
	}
	
	private void archiveUpdated() {
		synchronized(listeners) {
			for (IArchiveListener listener : listeners) {
				listener.archiveUpdated();
			}
		}
	}

}
