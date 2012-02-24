package com.michaelfogleman.wordwarrior.protocol;

import com.michaelfogleman.wordwarrior.protocol.command.Command;
import com.michaelfogleman.wordwarrior.protocol.response.Response;

public abstract class ProtocolAdapter implements IProtocol {

	public boolean isKeepAliveSupported() {
		return false;
	}

	public long getKeepAliveRate() {
		throw new UnsupportedOperationException();
	}
	
	public Data[] preprocess(Data data) {
		return null;
	}

	public abstract Response[] deserialize(Data data);

	public abstract Data[] serialize(Command command);

}
