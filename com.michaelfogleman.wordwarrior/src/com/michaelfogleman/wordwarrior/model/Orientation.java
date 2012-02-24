package com.michaelfogleman.wordwarrior.model;

public enum Orientation {
	
	VERTICAL(0,1),
	HORIZONTAL(1,0);
	
	private int dx;
	private int dy;
	
	private Orientation(int dx, int dy) {
		this.dx = dx;
		this.dy = dy;
	}
	
	public int getDx() {
		return dx;
	}
	
	public int getDy() {
		return dy;
	}

}
