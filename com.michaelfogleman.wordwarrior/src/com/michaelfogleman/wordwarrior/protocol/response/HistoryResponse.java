package com.michaelfogleman.wordwarrior.protocol.response;

import java.util.ArrayList;
import java.util.List;

import com.michaelfogleman.wordwarrior.model.GameReference;

public class HistoryResponse extends Response {
	
	private String owner;
	private List<GameReference> entries;
	
	public HistoryResponse(String owner, GameReference[] entries) {
		this.owner = owner;
		this.entries = new ArrayList<GameReference>(entries.length);
		for (GameReference entry : entries) {
			this.entries.add(entry);
		}
	}
	
	public HistoryResponse(String owner, List<GameReference> entries) {
		this.owner = owner;
		this.entries = new ArrayList<GameReference>(entries);
	}
	
	public List<GameReference> getGameReferences() {
		return new ArrayList<GameReference>(entries);
	}
	
	public String getOwner() {
		return owner;
	}
	
}
