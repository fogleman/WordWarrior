package com.michaelfogleman.wordwarrior.model;

public class ExchangeTilesAction extends AbstractGameAction {
	
	private int count;
	
	public ExchangeTilesAction(int count) {
		this.count = count;
	}
	
	public String getName() {
		return "Change";
	}
	
	public String toString() {
		return "CHANGE " + count;
	}

}
