package com.michaelfogleman.wordwarrior.protocol.ww;

import com.michaelfogleman.wordwarrior.protocol.SocketConnection;

public class WordWarriorConnection extends SocketConnection {
	
	public final static String[] HOSTS = new String[] {
		"127.0.0.1"
	};
	
	public final static int[] PORTS = new int[] {
		80
	};
	
	public WordWarriorConnection() {
		this(HOSTS[0], PORTS[0]);
	}
	
	public WordWarriorConnection(String host, int port) {
		super(new WordWarriorProtocol(), host, port);
	}

}
