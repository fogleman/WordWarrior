package com.michaelfogleman.wordwarrior.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Clock {
	
	private long millis;
	private long startTime;
	private boolean running;
	
	private Timer timer;
	private List<IClockListener> listeners;
	
	public Clock() {
		listeners = new ArrayList<IClockListener>();
		set(0, 0);
	}
	
	public Clock(int minutes, int seconds) {
		set(minutes, seconds);
	}
	
	public void addListener(IClockListener listener) {
		synchronized(listeners) {
			listeners.add(listener);
		}
	}
	
	public void removeListener(IClockListener listener) {
		synchronized(listeners) {
			listeners.remove(listener);
		}
	}
	
	private void clockExpired() {
		synchronized(listeners) {
			for (IClockListener listener : listeners) {
				listener.clockExpired(this);
			}
		}
	}
	
	public synchronized int getMinutes() {
		return (int)(getMillis() / 60000L);
	}
	
	public synchronized int getSeconds() {
		return (int)((getMillis() - getMinutes() * 60 * 1000) / 1000);
	}
	
	public synchronized void increment(int minutes, int seconds) {
		set(getMinutes() + minutes, getSeconds() + seconds);
	}
	
	public synchronized void set(int minutes, int seconds) {
		seconds = minutes * 60 + seconds;
		this.millis = seconds * 1000;
		startTime = System.currentTimeMillis();
		scheduleTimer();
	}
	
	public synchronized long getMillis() {
		if (!running) return millis;
		long difference = System.currentTimeMillis() - startTime;
		long result = millis - difference;
		if (result < 0) result = 0;
		return result;
	}
	
	public synchronized void start() {
		if (running) return;
		startTime = System.currentTimeMillis();
		running = true;
		scheduleTimer();
	}
	
	public synchronized void stop() {
		if (!running) return;
		millis = getMillis();
		running = false;
		stopTimer();
	}
	
	private void scheduleTimer() {
		stopTimer();
		if (running) {
			timer = new Timer();
			timer.schedule(createTimerTask(), getMillis());
		}
	}
	
	private void stopTimer() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}
	
	public String toString() {
		StringBuffer b = new StringBuffer();
		int minutes = getMinutes();
		int seconds = getSeconds();
		if (minutes < 10) b.append('0');
		b.append(minutes);
		b.append(':');
		if (seconds < 10) b.append('0');
		b.append(seconds);
		return b.toString();
	}
	
	private TimerTask createTimerTask() {
		return new TimerTask() {
			public void run() {
				synchronized (Clock.this) {
					clockExpired();
				}
			}
		};
	}

}
