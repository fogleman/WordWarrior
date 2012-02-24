package com.michaelfogleman.wordwarrior.protocol.command;

import java.util.ArrayList;
import java.util.List;

public class CheckWordsCommand extends Command {
	
	private List<String> words;
	
	public CheckWordsCommand(String[] words) {
		this.words = new ArrayList<String>();
		for (String word : words) {
			this.words.add(word);
		}
	}
	
	public CheckWordsCommand(List<String> words) {
		this.words = new ArrayList<String>(words);
	}
	
	public String[] getWords() {
		String[] result = new String[words.size()];
		words.toArray(result);
		return result;
	}

}
