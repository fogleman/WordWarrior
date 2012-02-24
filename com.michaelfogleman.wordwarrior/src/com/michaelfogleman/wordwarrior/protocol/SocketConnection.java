package com.michaelfogleman.wordwarrior.protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.michaelfogleman.wordwarrior.Util;

public class SocketConnection extends Connection {
	
	private String host;
	private int port;
	private Socket socket;
	private DataOutputStream out;
	private DataInputStream in;
	
	public SocketConnection(IProtocol protocol, String host, int port) {
		super(protocol);
		this.host = host;
		this.port = port;
	}
	
	protected void init() {
		try {
			socket = new Socket(host, port);
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected void destroy() {
		Util.close(in);
		Util.close(out);
		Util.close(socket);
	}
	
	protected void write(Data data) {
		if (out == null) return;
		synchronized(out) {
			if (!isClosed()) {
				try {
					out.write(data.getBytes());
					out.flush();
				} catch (IOException e) {
					e.printStackTrace();
					close();
				}
			}
		}
	}
	
	protected Data read() {
		try {
			int length = in.readInt();
			byte[] data = new byte[length];
			in.read(data);
			return new Data(data);
		} catch (IOException e) {
			return null;
		}
	}

}
