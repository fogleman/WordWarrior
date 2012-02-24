package com.michaelfogleman.wordwarrior.protocol;

public class Data {
	
	private byte[] data;
	
	public Data(byte[] data) {
		this.data = data;
	}
	
	public byte[] getBytes() {
		return data;
	}
	
	public String toString() {
		return new String(data);
	}

}
