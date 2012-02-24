package com.michaelfogleman.wordwarrior.protocol.response;

import java.util.ArrayList;
import java.util.List;

public class BuddiesResponse extends Response {
	
	private List<String> handles;
	
	public BuddiesResponse(String[] handles) {
		this.handles = new ArrayList<String>();
		for (String handle : handles) {
			this.handles.add(handle);
		}
	}
	
	public String[] getHandles() {
		String[] result = new String[handles.size()];
		handles.toArray(result);
		return result;
	}
	
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("buddies: ");
		for (String handle : handles) {
			b.append(handle).append(", ");
		}
		if (handles.size() > 0) {
			b.setLength(b.length()-2);
		}
		return b.toString();
	}

}
