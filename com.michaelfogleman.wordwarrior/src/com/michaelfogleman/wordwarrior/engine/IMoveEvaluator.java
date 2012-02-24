package com.michaelfogleman.wordwarrior.engine;

import com.michaelfogleman.wordwarrior.model.IBoard;
import com.michaelfogleman.wordwarrior.model.MoveResult;

public interface IMoveEvaluator {
	
	public int evaluateMove(MoveResult move, IBoard before, IBoard after);

}
