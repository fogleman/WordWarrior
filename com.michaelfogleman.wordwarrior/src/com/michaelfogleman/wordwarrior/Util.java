package com.michaelfogleman.wordwarrior;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

public class Util {
	
	private static Random random = new Random();
	
	private Util() {
	}
	
	public static int randomInt(int n) {
		return random.nextInt(n);
	}
	
	public static void check(boolean assertion, String message) {
		if (!assertion) {
			throw new RuntimeException(message);
		}
	}
	
	public static void check(boolean assertion) {
		check(assertion, "Assertion Failed");
	}
	
	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}
	
	public static Thread startThread(Runnable runnable, boolean daemon) {
		Thread thread = new Thread(runnable);
		thread.setDaemon(daemon);
		thread.start();
		return thread;
	}
	
	public static String getTime() {
//		long millis = System.currentTimeMillis();
//		return "(" + dateFormat.format(new Date(millis)) + ") ";
		return "";
	}
	
	public static String sortLetters(String word) {
		char[] array = word.toCharArray();
		Arrays.sort(array);
		return new String(array);
	}
	
	public static String[] split(String string) {
		StringTokenizer t = new StringTokenizer(string);
		List<String> list = new ArrayList<String>();
		while (t.hasMoreTokens()) {
			String token = t.nextToken();
			list.add(token);
		}
		String[] result = new String[list.size()];
		list.toArray(result);
		return result;
	}
	
	public static void close(BufferedReader in) {
		if (in != null) {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void close(InputStream in) {
		if (in != null) {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void close(OutputStream out) {
		if (out != null) {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void close(Socket socket) {
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void join(Thread thread) {
		if (thread != null) {
			synchronized(thread) {
				thread.interrupt();
				while (thread.isAlive()) {
					try {
						thread.join();
					} catch (InterruptedException e) {
					}
				}
			}
		}
	}

}
