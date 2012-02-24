package com.michaelfogleman.wordwarrior.protocol.isc;

import java.util.ArrayList;
import java.util.List;

class Outbound {

	private String name;
	private List<String> arguments;

	public Outbound(String name) {
		arguments = new ArrayList<String>();
		setName(name);
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void add(String argument) {
		arguments.add(argument);
	}

	public void add(int argument) {
		arguments.add(Integer.toString(argument));
	}
	
	public void add(boolean argument) {
		add(argument ? 1 : 0);
	}

	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append(getName());
		for (String argument : arguments) {
			b.append(' ');
			b.append(argument);
		}
		return b.toString();
	}

}
