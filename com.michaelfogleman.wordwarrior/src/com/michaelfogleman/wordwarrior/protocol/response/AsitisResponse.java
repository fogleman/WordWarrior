package com.michaelfogleman.wordwarrior.protocol.response;

public class AsitisResponse extends Response {

	private String text;

	public AsitisResponse(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public String toString() {
		return text;
	}

}
