package com.michaelfogleman.wordwarrior.model;

//          -> Pause ->
//          |         |
// Ready -> In Progress -> Confirmation -> Ended (Normal)
//          |
//          --------> Ended (Adjourned, Resigned, Aborted, Consecutive Passes)

public enum GameState {
	
	READY,
	IN_PROGRESS,
	PAUSED,
	CONFIRMATION,
	ENDED;

}
