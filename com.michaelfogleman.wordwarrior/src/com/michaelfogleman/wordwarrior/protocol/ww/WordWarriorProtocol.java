package com.michaelfogleman.wordwarrior.protocol.ww;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.michaelfogleman.wordwarrior.protocol.Data;
import com.michaelfogleman.wordwarrior.protocol.ProtocolAdapter;
import com.michaelfogleman.wordwarrior.protocol.command.Command;
import com.michaelfogleman.wordwarrior.protocol.command.LoginCommand;
import com.michaelfogleman.wordwarrior.protocol.response.LoginResponse;
import com.michaelfogleman.wordwarrior.protocol.response.Response;

public class WordWarriorProtocol extends ProtocolAdapter {

	public Response[] deserialize(Data data) {
		List<Response> list = new ArrayList<Response>();
		byte[] bytes = data.getBytes();
		ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
		buffer.put(bytes);
		buffer.flip();
		Packet packet = new Packet(buffer);
		int type = packet.getInt();
		
		switch(type) {
		case 0:
			LoginResponse response = new LoginResponse("Welcome to the Word Warrior server!");
			list.add(response);
			break;
		}
		
		Response[] result = new Response[list.size()];
		list.toArray(result);
		return result;
	}

	public Data[] serialize(Command command) {
		ByteBuffer buffer = ByteBuffer.allocate(4096);
		Packet packet = new Packet(buffer);
		
		if (command instanceof LoginCommand) {
			LoginCommand cmd = (LoginCommand)command;
			packet.putInt(0);
			packet.putString(cmd.getHandle());
			packet.putString(cmd.getPassword());
		}
		else {
			return null;
		}
		
		buffer.flip();
		int length = buffer.limit();
		ByteBuffer data = ByteBuffer.allocate(4 + length);
		data.putInt(length);
		data.put(buffer);
		data.flip();
		return new Data[] { new Data(data.array()) };
	}

}
