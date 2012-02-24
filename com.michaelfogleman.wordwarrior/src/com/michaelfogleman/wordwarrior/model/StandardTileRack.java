package com.michaelfogleman.wordwarrior.model;

import java.util.List;

public class StandardTileRack extends AbstractTileRack {
	
	public StandardTileRack() {
		super(7);
	}
	
	public StandardTileRack(List<Tile> tiles) {
		super(tiles);
	}

}
