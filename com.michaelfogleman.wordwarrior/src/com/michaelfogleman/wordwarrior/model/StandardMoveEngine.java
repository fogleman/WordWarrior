package com.michaelfogleman.wordwarrior.model;

public class StandardMoveEngine extends AbstractMoveEngine {
	
	public StandardMoveEngine() {
		super(new StandardTileValues());
	}
	
	public StandardMoveEngine(ITileValues tileValues) {
		super(tileValues);
	}

}
