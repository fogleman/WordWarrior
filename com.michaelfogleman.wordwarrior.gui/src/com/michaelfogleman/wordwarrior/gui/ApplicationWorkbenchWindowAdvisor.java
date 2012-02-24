package com.michaelfogleman.wordwarrior.gui;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import com.michaelfogleman.wordwarrior.ScrabblePlugin;
import com.michaelfogleman.wordwarrior.model.IGame;
import com.michaelfogleman.wordwarrior.model.Match;
import com.michaelfogleman.wordwarrior.model.Session;
import com.michaelfogleman.wordwarrior.model.SessionAdapter;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(750, 560));
		configurer.setShowCoolBar(false);
		configurer.setShowStatusLine(true);
		configurer.setTitle("Word Warrior");
	}
	
	public void postWindowCreate() {
		Shell shell = getWindowConfigurer().getWindow().getShell();
//		shell.setMaximized(true);
		shell.setActive();
		shell.forceActive();
		shell.setFocus();
		shell.forceFocus();
		
		ScrabblePlugin plugin = ScrabblePlugin.getDefault();
		Session session = plugin.getSession();
		session.addSessionListener(new SessionAdapter() {
			public void gameCreated(IGame game) {
				ApplicationWorkbenchWindowAdvisor.this.gameCreated(game);
			}
			public void matchReceived(Match match) {
				ApplicationWorkbenchWindowAdvisor.this.matchReceived(match);
			}
		});
		
		session.createLocalGame();
		plugin.preloadDictionary();
	}
	
	private void gameCreated(final IGame game) {
		exec(new Runnable() {
			public void run() {
				try {
					IViewPart viewPart = getWindowConfigurer().getWindow().getActivePage().showView(GameView.ID, game.toString(), IWorkbenchPage.VIEW_ACTIVATE);
					GameView view = (GameView)viewPart;
					view.setGame(game);
					view.setFocus();
				} catch (PartInitException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private void matchReceived(final Match match) {
		final Shell shell = getWindowConfigurer().getWindow().getShell();
		exec(new Runnable() {
			public void run() {
				AcceptDialog dialog = new AcceptDialog(shell, match);
				dialog.open();
			}
		});
	}
	
	private void exec(Runnable runnable) {
		final Shell shell = getWindowConfigurer().getWindow().getShell();
		final Display display = shell.getDisplay();
		if (!display.isDisposed()) {
			display.asyncExec(runnable);
		}
	}
	
}
