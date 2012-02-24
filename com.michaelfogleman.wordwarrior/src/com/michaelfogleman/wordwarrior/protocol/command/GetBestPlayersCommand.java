package com.michaelfogleman.wordwarrior.protocol.command;

import com.michaelfogleman.wordwarrior.model.DictionaryType;

public class GetBestPlayersCommand extends Command {
	
	private DictionaryType dictionary;
	
	public GetBestPlayersCommand(DictionaryType dictionary) {
		this.dictionary = dictionary;
	}
	
	public DictionaryType getDictionary() {
		return dictionary;
	}

}
