package com.michaelfogleman.wordwarrior.protocol.isc;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

class Inbound {
	
	private List<String> arguments;
	private String data;
	private int index;
	private int length;
	
	public Inbound(byte[] data) {
		this(new String(data));
	}
	
	public Inbound(String data) {
		if (data.startsWith("0 ")) {
			data = data.substring(2);
		}
		
		arguments = new ArrayList<String>();
		this.data = data;
		length = data.length();
		index = 0;
		
		StringTokenizer t = new StringTokenizer(data);
		while (t.hasMoreTokens()) {
			addArgument(t.nextToken());
		}
	}
	
	public String getString() {
		return next();
	}
	
	public int getInt() {
		return Integer.parseInt(getString());
	}
	
	public boolean getBoolean() {
		int n = getInt();
		return (n != 0);
	}
	
	public char getChar() {
		return getString().charAt(0);
	}
	
	public String getString(int index) {
		return arguments.get(index);
	}
	
	public int getInt(int index) {
		try {
			return Integer.parseInt(getString(index));
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	
	public boolean getBoolean(int index) {
		int n = getInt(index);
		return (n != 0);
	}
	
	public char getChar(int index) {
		return getString(index).charAt(0);
	}
	
	private void addArgument(String argument) {
		arguments.add(argument);
	}
	
	public boolean hasNext() {
		return remainder().trim().length() > 0;
	}
	
	public String next() {
		StringBuffer b = new StringBuffer();
		int state = 0;
		for (; index < length; index++) {
			char c = data.charAt(index);
			if (c > 32) {
				state = 1;
			}
			if (state == 1) {
				if (c > 32) {
					b.append(c);
				}
				else {
					index++;
					break;
				}
			}
		}
		if (b.length() == 0) {
			return null;
		}
		return b.toString();
	}
	
	public String remainder() {
		if (index >= length) return "";
		return data.substring(index);
	}

}
