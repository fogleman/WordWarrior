package com.michaelfogleman.wordwarrior.gui;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	private static final String PERSPECTIVE_ID = "com.michaelfogleman.wordwarrior.gui.perspective";

	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	public String getInitialWindowPerspectiveId() {
		return PERSPECTIVE_ID;
	}
	
	public void postStartup() {
		// close ghost game views
		IViewPart view;
		IWorkbenchPage page = getWorkbenchConfigurer().getWorkbench().getActiveWorkbenchWindow().getActivePage();
		while ((view = getGameView(page)) != null) {
			page.hideView(view);
		}
	}
	
	private IViewPart getGameView(IWorkbenchPage page) {
		for (IViewReference reference : page.getViewReferences()) {
			IViewPart view = reference.getView(false);
			if (view instanceof GameView) {
				return view;
			}
		}
		return null;
	}
	
	public void initialize(IWorkbenchConfigurer configurer) {
		super.initialize(configurer);
		configurer.setSaveAndRestore(true);
	}

}
