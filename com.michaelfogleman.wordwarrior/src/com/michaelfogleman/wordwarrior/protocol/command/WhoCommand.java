package com.michaelfogleman.wordwarrior.protocol.command;

import com.michaelfogleman.wordwarrior.model.DictionaryType;
import com.michaelfogleman.wordwarrior.model.UserState;
import com.michaelfogleman.wordwarrior.model.UserType;

public class WhoCommand extends Command {
	
	private UserState userState;
	private UserType userType;
	private DictionaryType dictionaryType;
	private boolean established;
	private boolean fairplay;
	private int minimumRating;
	private int maximumRating;
	
	public WhoCommand() {
		minimumRating = 0;
		maximumRating = 9999;
	}
	
	public void setDictionaryType(DictionaryType dictionaryType) {
		this.dictionaryType = dictionaryType;
	}
	
	public void setEstablished(boolean established) {
		this.established = established;
	}
	
	public void setFairplay(boolean fairplay) {
		this.fairplay = fairplay;
	}
	
	public void setMaximumRating(int maximumRating) {
		this.maximumRating = maximumRating;
	}
	
	public void setMinimumRating(int minimumRating) {
		this.minimumRating = minimumRating;
	}
	
	public void setUserState(UserState userState) {
		this.userState = userState;
	}
	
	public void setUserType(UserType userType) {
		this.userType = userType;
	}
	
	public int getMaximumRating() {
		return maximumRating;
	}
	
	public int getMinimumRating() {
		return minimumRating;
	}
	
	public String getFlags() {
		StringBuffer b = new StringBuffer();
		if (userState != null) {
			if (userState == UserState.AVAILABLE) {
				b.append("-*");
			}
			else {
				b.append(userState.getId());
			}
		}
		if (userType != null) {
			if (userType == UserType.PLAYER) {
				b.append("-B-C-H-!");
			}
			else {
				b.append(userType.getChar());
			}
		}
		if (dictionaryType != null) {
			if (dictionaryType == DictionaryType.TWL06) b.append('t');
			if (dictionaryType == DictionaryType.SOWPODS) b.append('s');
			if (dictionaryType == DictionaryType.LOC2000) b.append('l');
			if (dictionaryType == DictionaryType.SWL ) b.append('d');
			if (dictionaryType == DictionaryType.MULTI) b.append('m');
			if (dictionaryType == DictionaryType.ODS) b.append('o');
			if (dictionaryType == DictionaryType.PARO) b.append('i');
		}
		if (established) {
			b.append('r');
		}
		if (fairplay) {
			b.append('f');
		}
		return b.toString();
	}

}
