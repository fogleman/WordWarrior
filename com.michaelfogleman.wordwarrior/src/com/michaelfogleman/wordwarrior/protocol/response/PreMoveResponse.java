package com.michaelfogleman.wordwarrior.protocol.response;

import com.michaelfogleman.wordwarrior.model.MoveResult;

public class PreMoveResponse extends Response {
	
	private boolean valid;
	private MoveResult moveResult;
	
	public PreMoveResponse(boolean valid, MoveResult moveResult) {
		this.valid = valid;
		this.moveResult = moveResult;
	}
	
	public boolean isValid() {
		return valid;
	}
	
	public MoveResult getMoveResult() {
		return moveResult;
	}
	
}
