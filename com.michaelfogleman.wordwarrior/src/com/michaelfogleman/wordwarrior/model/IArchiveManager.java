package com.michaelfogleman.wordwarrior.model;

import java.util.List;

public interface IArchiveManager {
	
	public String getActiveOwner();
	
	public void refresh(String owner);
	
	public List<GameReference> getActiveGameReferences();
	
	public List<GameReference> getAllGameReferences();
	
	public List<GameReference> getGameReferences(String owner);
	
	public void addArchiveListener(IArchiveListener listener);
	
	public void removeArchiveListener(IArchiveListener listener);

}
