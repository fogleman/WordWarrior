package com.michaelfogleman.wordwarrior.protocol.ww;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

public class Packet {
	
	private static final Charset CHARSET = Charset.forName("US-ASCII");
	private static final CharsetEncoder CHARSET_ENCODER = CHARSET.newEncoder();
	private static final CharsetDecoder CHARSET_DECODER = CHARSET.newDecoder();
	
	private ByteBuffer data;
	
	public Packet(ByteBuffer data) {
		this.data = data;
	}
	
//	public Packet(Packet packet) {
//		ByteBuffer buffer = packet.getData().duplicate();
//		buffer.position(0);
//		data = ByteBuffer.allocate(buffer.limit());
//		data.put(buffer);
//	}
	
	public ByteBuffer getData() {
		return data;
	}
	
	public void clear() {
		data.clear();
	}
	
	
	
	public String getString() {
		try {
			short length = getShort();
			ByteBuffer buffer = data.slice();
			buffer.limit(length);
			data.position(data.position() + length);
			return new String(CHARSET_DECODER.decode(buffer).array());
		} catch (CharacterCodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getString(int index) {
		try {
			short length = getShort(index);
			ByteBuffer buffer = data.duplicate();
			buffer.position(index + 2);
			buffer.limit(index + length + 2);
			return new String(CHARSET_DECODER.decode(buffer).array());
		} catch (CharacterCodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void putString(String value) {
		try {
			ByteBuffer buffer = CHARSET_ENCODER.encode(CharBuffer.wrap(value));
			putShort((short)value.length());
			data.put(buffer);
		} catch (CharacterCodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void putString(int index, String value) {
		try {
			ByteBuffer buffer = CHARSET_ENCODER.encode(CharBuffer.wrap(value));
			ByteBuffer dup = data.duplicate();
			dup.position(index);
			dup.putShort((short)value.length());
			dup.put(buffer);
		} catch (CharacterCodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	
	public ByteBuffer getByteBuffer() {
		short length = getShort();
		ByteBuffer buffer = ByteBuffer.allocate(length);
		buffer.put((ByteBuffer)data.slice().limit(length));
		buffer.flip();
		return buffer;
	}
	
	public ByteBuffer getByteBuffer(int index) {
		short length = getShort(index);
		ByteBuffer buffer = ByteBuffer.allocate(length);
		ByteBuffer dup = data.duplicate();
		dup.position(index + 2);
		dup.limit(index + length + 2);
		buffer.put(dup);
		buffer.flip();
		return buffer;
	}
	
	public void putByteBuffer(ByteBuffer buffer) {
		data.put(buffer);
	}
	
	public void putByteBuffer(int index, ByteBuffer buffer) {
		ByteBuffer dup = data.duplicate();
		dup.position(index);
		dup.put(buffer);
	}
	
	
	
	public byte getByte() {
		return data.get();
	}
	
	public byte getByte(int index) {
		return data.get(index);
	}
	
	public void putByte(byte value) {
		data.put(value);
	}
	
	public void putByte(int index, byte value) {
		data.put(index, value);
	}
	
	
	
	public char getChar() {
		return data.getChar();
	}
	
	public char getChar(int index) {
		return data.getChar(index);
	}
	
	public void putChar(char value) {
		data.putChar(value);
	}
	
	public void putChar(int index, char value) {
		data.putChar(index, value);
	}
	
	
	
	public double getDouble() {
		return data.getDouble();
	}
	
	public double getDouble(int index) {
		return data.getDouble(index);
	}
	
	public void putDouble(double value) {
		data.putDouble(value);
	}
	
	public void putDouble(int index, double value) {
		data.putDouble(index, value);
	}
	
	
	
	public float getFloat() {
		return data.getFloat();
	}
	
	public float getFloat(int index) {
		return data.getFloat(index);
	}
	
	public void putFloat(float value) {
		data.putFloat(value);
	}
	
	public void putFloat(int index, float value) {
		data.putFloat(index, value);
	}
	
	
	
	public int getInt() {
		return data.getInt();
	}
	
	public int getInt(int index) {
		return data.getInt(index);
	}
	
	public void putInt(int value) {
		data.putInt(value);
	}
	
	public void putInt(int index, int value) {
		data.putInt(index, value);
	}
	
	
	
	public long getLong() {
		return data.getLong();
	}
	
	public long getLong(int index) {
		return data.getLong(index);
	}
	
	public void putLong(long value) {
		data.putLong(value);
	}
	
	public void putLong(int index, long value) {
		data.putLong(index, value);
	}
	
	
	
	public short getShort() {
		return data.getShort();
	}
	
	public short getShort(int index) {
		return data.getShort(index);
	}
	
	public void putShort(short value) {
		data.putShort(value);
	}
	
	public void putShort(int index, short value) {
		data.putShort(index, value);
	}
	
}
