package com.michaelfogleman.wordwarrior.protocol;

import com.michaelfogleman.wordwarrior.protocol.command.Command;
import com.michaelfogleman.wordwarrior.protocol.response.Response;

public interface IProtocol {
	
	public boolean isKeepAliveSupported();
	
	public long getKeepAliveRate();
	
	public Response[] deserialize(Data data);
	
	public Data[] serialize(Command command);
	
	public Data[] preprocess(Data data);

}
