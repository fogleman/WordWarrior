package com.michaelfogleman.wordwarrior.protocol.response;

public class HelpResponse extends Response {

	private String text;

	public HelpResponse(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public String toString() {
		return text;
	}

}
