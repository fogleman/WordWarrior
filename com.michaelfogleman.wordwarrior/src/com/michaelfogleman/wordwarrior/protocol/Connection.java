package com.michaelfogleman.wordwarrior.protocol;

import java.util.ArrayList;
import java.util.List;

import com.michaelfogleman.wordwarrior.Util;
import com.michaelfogleman.wordwarrior.protocol.command.Command;
import com.michaelfogleman.wordwarrior.protocol.command.KeepAliveCommand;
import com.michaelfogleman.wordwarrior.protocol.command.LoginCommand;
import com.michaelfogleman.wordwarrior.protocol.response.AsitisResponse;
import com.michaelfogleman.wordwarrior.protocol.response.CloseResponse;
import com.michaelfogleman.wordwarrior.protocol.response.IResponseHandler;
import com.michaelfogleman.wordwarrior.protocol.response.Response;
import com.michaelfogleman.wordwarrior.protocol.response.ResponseAdapter;
import com.michaelfogleman.wordwarrior.protocol.response.ResponseDispatcher;

public abstract class Connection {
	
	private IProtocol protocol;
	private List<IResponseHandler> responseHandlers;
	private List<Response> responseQueue;
	private Thread dispatchThread;
	private Thread keepAliveThread;
	private Thread readerThread;
	private boolean closed;
	private Object closeLock = new Object();
	private List<IConnectionListener> connectionListeners;
	
	public Connection(IProtocol protocol) {
		Util.check(protocol != null, "no protocol specified");
		this.protocol = protocol;
		responseHandlers = new ArrayList<IResponseHandler>();
		responseQueue = new ArrayList<Response>();
		connectionListeners = new ArrayList<IConnectionListener>();
		setClosed(true);
		addResponseHandler(createResponseHandler());
	}
	
	public void open(String handle, String password) {
		setClosed(false);
		init();
		dispatchThread = Util.startThread(createDispatchRunnable(), false);
		if (protocol.isKeepAliveSupported()) {
			keepAliveThread = Util.startThread(createKeepAliveRunnable(), false);
		}
		readerThread = Util.startThread(createReaderRunnable(), false);
		Thread.yield();
		send(new LoginCommand(handle, password));
	}
	
	public void close() {
		synchronized(closeLock) {
			if (isClosed()) return;
			setClosed(true);
		}
		System.out.println("Closing Connection");
		destroy();
		Thread current = Thread.currentThread();
		if (current != keepAliveThread) {
			Util.join(keepAliveThread);
		}
		if (current != readerThread) {
			Util.join(readerThread);
		}
		if (current != dispatchThread) {
			Util.join(dispatchThread);
		}
		notifyConnectionListeners();
		System.out.println("Connection Closed");
	}
	
	public boolean isClosed() {
		synchronized(closeLock) {
			return closed;
		}
	}
	
	private void setClosed(boolean closed) {
		synchronized(closeLock) {
			this.closed = closed;
		}
	}
	
	public void addConnectionListener(IConnectionListener listener) {
		connectionListeners.add(listener);
	}
	
	public void removeConnectionListener(IConnectionListener listener) {
		connectionListeners.remove(listener);
	}
	
	private void notifyConnectionListeners() {
		for (IConnectionListener listener : connectionListeners) {
			listener.connectionClosed(this);
		}
	}
	
	protected abstract void init();
	
	protected abstract void destroy();
	
	protected abstract Data read();
	
	protected abstract void write(Data data);
	
	public void send(Command command) {
		Data[] data = protocol.serialize(command);
		if (data == null) {
			String s = Util.getTime() + "*** Unhandled Command: \"" + command + "\"";
			System.out.println(s);
			//enqueue(new AsitisResponse(s));
		}
		else {
			for (int i = 0; i < data.length; i++) {
				Data d = data[i];
				System.out.println(Util.getTime() + "Send: \"" + d + "\"");
				write(d);
			}
		}
	}
	
	private void receive(Data data) {
		Data[] pre = protocol.preprocess(data);
		if (pre != null && pre.length > 0) {
			for (int i = 0; i < pre.length; i++) {
				Data d = pre[i];
				write(d);
			}
			return;
		}
		Response[] responses = protocol.deserialize(data);
		if (responses == null || responses.length == 0) {
			String s = Util.getTime() + "*** Unhandled Response: \"" + data + "\"";
			System.out.println(s);
			enqueue(new AsitisResponse(s));
		}
		else {
			System.out.println(Util.getTime() + "Receive: \"" + data + "\"");
			for (int i = 0; i < responses.length; i++) {
				Response response = responses[i];
				enqueue(response);
			}
		}
	}
	
	private void enqueue(Response response) {
		synchronized(responseQueue) {
			//System.out.println("Enqueue: " + response);
			responseQueue.add(response);
			responseQueue.notifyAll();
		}
	}
	
	private Response dequeue() {
		synchronized(responseQueue) {
			while (!isClosed() && responseQueue.size() == 0) {
				try {
					responseQueue.wait();
				} catch (InterruptedException e) {
				}
			}
			if (responseQueue.size() > 0) {
				Response response = responseQueue.remove(0);
				//System.out.println("Dequeue: " + response);
				return response;
			}
			return null;
		}
	}
	
	private Runnable createReaderRunnable() {
		return new Runnable() {
			public void run() {
				System.out.println("ReaderThread Started");
				Data data;
				while (!isClosed() && (data = read()) != null) {
					receive(data);
				}
				close();
				System.out.println("ReaderThread Stopped");
			}
		};
	}
	
	private Runnable createDispatchRunnable() {
		return new Runnable() {
			public void run() {
				System.out.println("DispatchThread Started");
				while (!isClosed()) {
					Response response = dequeue();
					if (response != null) {
						notifyResponseHandlers(response);
					}
				}
				System.out.println("DispatchThread Stopped");
			}
		};
	}
	
	private Runnable createKeepAliveRunnable() {
		return new Runnable() {
			public void run() {
				System.out.println("KeepAliveThread Started");
				long rate = protocol.getKeepAliveRate();
				while (true) {
					Util.sleep(rate);
					if (isClosed()) {
						break;
					}
					else {
						send(new KeepAliveCommand());
					}
				}
				System.out.println("KeepAliveThread Stopped");
			}
		};
	}
	
	private IResponseHandler createResponseHandler() {
		return new ResponseAdapter() {
			public void handle(CloseResponse response) {
				close();
			}
		};
	}
	
	public void addResponseHandler(IResponseHandler handler) {
		synchronized(responseHandlers) {
			responseHandlers.add(handler);
		}
	}
	
	public void removeResponseHandler(IResponseHandler handler) {
		synchronized(responseHandlers) {
			responseHandlers.remove(handler);
		}
	}
	
	protected void notifyResponseHandlers(Response response) {
		synchronized(responseHandlers) {
			System.out.println(Util.getTime() + response);
			for (IResponseHandler handler : responseHandlers) {
				ResponseDispatcher.dispatch(response, handler);
			}
		}
	}

}
