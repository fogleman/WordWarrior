package com.michaelfogleman.wordwarrior.protocol.command;

import com.michaelfogleman.wordwarrior.model.DictionaryType;
import com.michaelfogleman.wordwarrior.model.MoveResult;

public class PreMoveCommand extends Command {
	
	private DictionaryType dictionary;
	private MoveResult moveResult;
	
	public PreMoveCommand(DictionaryType dictionary, MoveResult moveResult) {
		this.dictionary = dictionary;
		this.moveResult = moveResult;
	}
	
	public DictionaryType getDictionary() {
		return dictionary;
	}
	
	public MoveResult getMoveResult() {
		return moveResult;
	}

}
