package com.michaelfogleman.wordwarrior;

import com.michaelfogleman.wordwarrior.engine.Dictionary;
import com.michaelfogleman.wordwarrior.engine.Node;
import com.michaelfogleman.wordwarrior.model.DictionaryType;

public class Boggle {
	String[][] data = new String[][] {
		{"h","a","o","d"},
		{"h","a","o","d"},
		{"h","a","o","d"},
		{"h","a","o","d"},
	};
	public static void main(String[] args) {
		Dictionary d = Dictionary.getInstance(DictionaryType.TWL06);
		Node n = d.getRoot();
		
	}
}
