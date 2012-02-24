package com.michaelfogleman.wordwarrior.protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.michaelfogleman.wordwarrior.Util;

public class SocketUtfConnection extends Connection {
	
	private String host;
	private int port;
	private Socket socket;
	private DataOutputStream out;
	private DataInputStream in;
	
	public SocketUtfConnection(IProtocol protocol, String host, int port) {
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
				String string = new String(data.getBytes());
				try {
					out.writeUTF(string);
				} catch (IOException e) {
					e.printStackTrace();
					close();
				}
			}
		}
	}
	
	protected Data read() {
		try {
			String data = in.readUTF();
			return new Data(data.getBytes());
		} catch (IOException e) {
			return null;
		}
	}

}
