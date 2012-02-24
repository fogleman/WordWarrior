package com.michaelfogleman.wordwarrior.gui;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);
		layout.setFixed(false);
		
		IFolderLayout gameFolder = layout.createFolder("games", IPageLayout.LEFT, 1.0f, layout.getEditorArea());
		gameFolder.addPlaceholder(GameView.ID + ":*");
		
		IFolderLayout folder;
		
		folder = layout.createFolder("topFolder", IPageLayout.RIGHT, 0.65f, "games");
		folder.addView(MoveListView.ID);
		folder.addView(TilePoolView.ID);
		folder.addView(AnalysisView.ID);
		
		folder = layout.createFolder("bottomFolder", IPageLayout.BOTTOM, 0.5f, "topFolder");
		folder.addView(ConsoleView.ID);
		folder.addView(SeekGraphView.ID);
		folder.addView(BuddyListView.ID);
		folder.addView(ArchiveView.ID);
	}

}
