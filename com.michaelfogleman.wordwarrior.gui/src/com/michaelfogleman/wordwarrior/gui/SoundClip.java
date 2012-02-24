package com.michaelfogleman.wordwarrior.gui;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundClip {
	
	public static final SoundClip REMOTE_MOVE = new SoundClip("sounds/receive.wav");
	public static final SoundClip LOCAL_MOVE = new SoundClip("sounds/send.wav");
	
	private Clip clip;
	
	private SoundClip(String fileName) {
		clip = loadClip(getFileUrl(fileName));
	}
	
	public void play() {
		if (clip.isActive()) return;
		clip.setFramePosition(0);
		clip.loop(0);
	}
	
	public boolean isClosed() {
		return !clip.isOpen();
	}
	
	private static URL getFileUrl(String fileName) {
		ScrabbleGuiPlugin plugin = ScrabbleGuiPlugin.getDefault();
		URL url = plugin.getBundle().getEntry(fileName);
		return url;
	}
	
	private static Clip loadClip(URL url) {
		AudioInputStream in = null;
		Clip clip = null;
		
		try {
			in = AudioSystem.getAudioInputStream(url);
			AudioFormat format = in.getFormat();
			DataLine.Info info = new DataLine.Info(Clip.class, format);
			clip = (Clip)AudioSystem.getLine(info);
			clip.open(in);
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) in.close();
			} catch (IOException e) { }
		}
		
		return clip;
	}
	
	public static void loadSoundClips() {
		// do nothing
	}

}
