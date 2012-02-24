package com.michaelfogleman.wordwarrior.engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.michaelfogleman.wordwarrior.Convert;
import com.michaelfogleman.wordwarrior.ScrabblePlugin;
import com.michaelfogleman.wordwarrior.Util;
import com.michaelfogleman.wordwarrior.model.DictionaryType;
import com.michaelfogleman.wordwarrior.model.ITileRack;

public class Dictionary {
	
	private DictionaryType dictionaryType;
	private Node root;
	
	private Dictionary(DictionaryType dictionaryType) {
		this.dictionaryType = dictionaryType;
		root = new Node();
	}
	
	public DictionaryType getDictionaryType() {
		return dictionaryType;
	}
	
	public Node getRoot() {
		return root;
	}
	
	private void addWord(String string) {
		if (dictionaryType.isClabbers()) {
			string = Util.sortLetters(string.toLowerCase());
		}
		root.addString(string);
	}
	
	public boolean isWord(String string) {
		if (dictionaryType.isClabbers()) {
			string = Util.sortLetters(string.toLowerCase());
		}
//		return root.containsString(string);
		return Node.containsString(root, string);
	}
	
	public List<String> getWords(String tileString, String state, boolean[] neighbored, int index, boolean[][] letters) {
		if (dictionaryType.isClabbers()) {
			return getClabbersWords(tileString, state, neighbored, letters, index);
		}
		
		int[] tiles = getLetterFrequency(tileString);
		int wildcards = 0;
		for (int i = 0; i < tileString.length(); i++) {
			char c = tileString.charAt(i);
			if (c == '?') wildcards++;
		}
		
		List<String> list = root.getWords(tiles, wildcards, state, neighbored, letters, index);
		return list;
	}
	
	private static int[] getLetterFrequency(String string) {
		int[] letters = new int[26];
		int length = string.length();
		for (int i = 0; i < length; i++) {
			char c = string.charAt(i);
			if (c >= 'a' && c <= 'z') c = (char)('A' + c - 'a');
			else if (c < 'A' || c > 'Z') continue;
			int letter = c - 'A';
			letters[letter]++;
		}
		return letters;
	}
	
	
	
	// experimental rack evaluation
	// requires clabbers-mode dictionary
	public int getRackScore(ITileRack rack) {
		char[] tiles = Convert.toString(rack.getTiles()).toCharArray();
		Arrays.sort(tiles);
		
		int n = tiles.length;
		int m = (int)Math.pow(2, n);
		StringBuffer buffer = new StringBuffer();
		int[] distinctWords = new int[n+1];
		for (int mask = 1; mask < m; mask++) {
			buffer.setLength(0);
			for (int i = 0; i < n; i++) {
				int b = 1 << i;
				if ((mask & b) != 0) {
					buffer.append(tiles[i]);
				}
			}
			Node node = root.getDescendant(buffer.toString());
			if (node != null) {
				distinctWords[buffer.length()] += node.getValue();
			}
		}
		
		int result = 0;
		
		for (int i = 1; i < distinctWords.length; i++) {
			int count = distinctWords[i];
			System.out.println("Words of length " + i + ": " + count);
			result += i * i * count;
		}
		
		System.out.println("Rack score: " + result);
		
		return result;
	}
	
	
	
	// fill in the blank (one missing tile) for move gen optimization
	public List<Character> getCandidateTiles(String prefix, String suffix) {
		if (dictionaryType.isClabbers()) {
			return getClabbersCandidateTiles(prefix + suffix);
		}
		List<Character> result = new ArrayList<Character>();
		Node node = root.getDescendant(prefix);
		if (node != null) {
			Node[] children = node.getChildren();
			if (children != null) {
				for (Node child : children) {
					Node descendant = child.getDescendant(suffix);
					if (descendant != null && descendant.getValue() > 0) {
						result.add(child.getLetter());
					}
				}
			}
		}
		return result;
	}
	
	private List<Character> getClabbersCandidateTiles(String string) {
		List<Character> result = new ArrayList<Character>();
		for (char c = 'a'; c <= 'z'; c++) {
			String path = Util.sortLetters(string + c);
			Node node = root.getDescendant(path);
			if (node != null && node.getValue() > 0) {
				result.add(c);
			}
		}
		return result;
	}
	
	
	
	// new clabbers candidate move generation
	private List<String> getClabbersWords(String tileRack, String state, boolean[] neighbored, boolean[][] letters, int index) {
		List<String> result = new ArrayList<String>();
		if (tileRack.indexOf('?') >= 0) {
			String rack = tileRack.replaceFirst("\\?", "");
			for (char c = 'A'; c <= 'Z'; c++) {
				result.addAll(getClabbersWords(rack + c, state, neighbored, letters, index));
			}
		}
		else {
			getClabbersWords(result, tileRack, state, neighbored, letters, index);
		}
		return result;
	}
	
	private void getClabbersWords(List<String> result, String tileRack, String state, boolean[] neighbored, boolean[][] letters, int index) {
		// abort if previous tile not blank
		if (index > 0 && state.charAt(index-1) != ' ') {
			return;
		}
		
		int n = tileRack.length();
		int m = (int)Math.pow(2, n);
		List<Character> tiles = new ArrayList<Character>();
		StringBuffer all = new StringBuffer();
		
		// for each subset of tiles in rack
		for (int mask = 1; mask < m; mask++) {
			tiles.clear();
			for (int i = 0; i < n; i++) {
				int b = 1 << i;
				if ((mask & b) != 0) {
					tiles.add(tileRack.charAt(i));
				}
			}
			
			all.setLength(0);
			int j = 0;
			boolean filled = false;
			for (int i = index; i < state.length(); i++) {
				char c = state.charAt(i);
				if (c == ' ') {
					if (j < tiles.size()) {
						c = tiles.get(j++);
						all.append(c);
						if (j >= tiles.size()) {
							filled = true;
						}
					}
					else {
						break;
					}
				}
				else {
					all.append(c);
				}
			}
			
			// continue if didn't use all rack tiles
			if (!filled) {
				continue;
			}
			
			// continue if not a clabbers word
			if (!isWord(all.toString())) {
				continue;
			}
			
			getClabbersWords(result, new StringBuffer(), tiles, state, neighbored, letters, index, false);
		}
	}
	
	private void getClabbersWords(List<String> results, StringBuffer word, List<Character> tiles, String state, boolean[] neighbored, boolean[][] letters, int index, boolean adjacent) {
		if (index >= state.length() && adjacent) {
			results.add(word.toString());
			return;
		}
		char b = state.charAt(index);
		if (b == ' ') {
			boolean neighbor = neighbored[index];
			int n = tiles.size();
			if (n == 0 && adjacent) {
				results.add(word.toString());
				return;
			}
			for (int i = 0; i < n; i++) {
				char c = tiles.remove(i);
				int j = Character.toLowerCase(c) - 'a';
				if (letters[index][j]) {
					word.append(c);
					getClabbersWords(results, word, tiles, state, neighbored, letters, index+1, adjacent || neighbor);
					word.setLength(word.length()-1);
				}
				tiles.add(i, c);
			}
		}
		else {
			word.append(b);
			getClabbersWords(results, word, tiles, state, neighbored, letters, index+1, adjacent);
			word.setLength(word.length()-1);
		}
	}
	
	
	
	private static Map<DictionaryType, Dictionary> instances = new HashMap<DictionaryType, Dictionary>();
	
	public synchronized static Dictionary getInstance(DictionaryType type) {
		Dictionary result = instances.get(type);
		if (result == null) {
			result = createInstance(type);
			if (result != null) {
				instances.put(type, result);
			}
		}
		return result;
	}
	
	private static Dictionary createInstance(DictionaryType type) {
		Dictionary result = new Dictionary(type);
		String fileName = type.getFile();
		long start = System.currentTimeMillis();
		int count = loadWords(result, getFileUrl(fileName));
		long end = System.currentTimeMillis();
		if (count == 0) result = null;
		System.out.println("Loaded " + count + " words in " + (end-start) + "ms");
//		Node.convertToDawg(result.getRoot());
//		System.gc();
		return result;
	}
	
	private static URL getFileUrl(String fileName) {
		ScrabblePlugin plugin = ScrabblePlugin.getDefault();
		if (plugin == null) {
			try {
				return new File(fileName).toURI().toURL();
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return null;
			}
		}
		else {
			return plugin.getBundle().getEntry(fileName);
		}
	}
	
	private static int loadWords(Dictionary dictionary, URL url) {
		int result = 0;
		BufferedReader in = null;
		
		try {
			in = new BufferedReader(new InputStreamReader(url.openStream()));
			String line = null;
			while ((line = in.readLine()) != null) {
				String word = line.trim();
				if (word.length() > 0) {
					dictionary.addWord(word);
					result++;
				}
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			Util.close(in);
		}
		
		return result;
	}
	
}
