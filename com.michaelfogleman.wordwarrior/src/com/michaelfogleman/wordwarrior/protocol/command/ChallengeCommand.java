package com.michaelfogleman.wordwarrior.protocol.command;

import com.michaelfogleman.wordwarrior.model.DictionaryType;

public class ChallengeCommand extends Command {
	
	private DictionaryType dictionary;
	private String[] words;
	
	public ChallengeCommand(DictionaryType dictionaryType, String[] words) {
		this.dictionary = dictionaryType;
		this.words = words;
	}
	
	public DictionaryType getDictionary() {
		return dictionary;
	}
	
	public String[] getWords() {
		return words;
	}

}
