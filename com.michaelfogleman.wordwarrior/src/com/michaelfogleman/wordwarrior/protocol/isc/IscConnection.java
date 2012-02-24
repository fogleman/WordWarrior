package com.michaelfogleman.wordwarrior.protocol.isc;

import com.michaelfogleman.wordwarrior.protocol.SocketUtfConnection;

public class IscConnection extends SocketUtfConnection {
	
	public final static String[] HOSTS = new String[] {
		"66.98.172.34"
	};
	
	public final static int[] PORTS = new int[] {
		1321
	};
	
	public IscConnection() {
		this(HOSTS[0], PORTS[0]);
	}
	
	public IscConnection(String host, int port) {
		super(new Isc182Protocol(), host, port);
	}

}
