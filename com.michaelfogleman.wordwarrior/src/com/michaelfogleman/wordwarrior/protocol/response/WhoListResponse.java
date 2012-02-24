package com.michaelfogleman.wordwarrior.protocol.response;

import java.util.ArrayList;
import java.util.List;

public class WhoListResponse extends Response {
	
	private int count;
	private List<WhoResponse> list;
	
	public WhoListResponse(int count) {
		list = new ArrayList<WhoResponse>();
		this.count = count;
	}
	
	public List<WhoResponse> getList() {
		return list;
	}
	
	public void addItem(WhoResponse response) {
		list.add(response);
	}
	
	public int getCount() {
		return count;
	}

}
