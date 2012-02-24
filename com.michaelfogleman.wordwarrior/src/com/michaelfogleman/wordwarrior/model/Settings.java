package com.michaelfogleman.wordwarrior.model;

import java.util.HashMap;
import java.util.Map;

public class Settings {
	
	private Map<String, String> settings;
	
	public Settings() {
		settings = new HashMap<String, String>();
	}
	
	public Settings(Settings settings) {
		this();
		setAll(settings);
	}
	
	public boolean containsKey(String key) {
		synchronized (settings) {
			return settings.containsKey(key);
		}
	}
	
	public String containsKeyString(String key) {
		return containsKey(key) ? "1" : "0";
	}
	
	public void setAll(Settings other) {
		synchronized (settings) {
			for (String key : other.settings.keySet()) {
				String value = other.getString(key);
				this.set(key, value);
			}
		}
	}
	
	public void set(String key, String value) {
		synchronized (settings) {
			settings.put(key, value);
		}
	}
	
	public void set(String key, int value) {
		set(key, Integer.toString(value));
	}
	
	public void set(String key, boolean value) {
		set(key, value ? "1" : "0");
	}
	
	public String getString(String key) {
		return getString(key, null);
	}
	
	public String getString(String key, String defaultValue) {
		String value;
		synchronized (settings) {
			value = settings.get(key);
		}
		if (value == null) return defaultValue;
		return value;
	}
	
	public int getInt(String key) {
		return getInt(key, -1);
	}
	
	public int getInt(String key, int defaultValue) {
		try {
			return Integer.parseInt(getString(key, Integer.toString(defaultValue)));
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
	
	public boolean getBoolean(String key) {
		return getBoolean(key, false);
	}
	
	public boolean getBoolean(String key, boolean defaultValue) {
		int value = getInt(key);
		if (value == -1) return defaultValue;
		return value != 0;
	}
	
	public boolean equals(Object object) {
		if (object instanceof Settings) {
			Settings that = (Settings)object;
			synchronized (this.settings) {
				synchronized (that.settings) {
					return this.settings.equals(that.settings);
				}
			}
		}
		return false;
	}
	
	public int hashCode() {
		synchronized (settings) {
			return settings.hashCode();
		}
	}
	
	public String toString() {
		return settings.toString();
	}

}
