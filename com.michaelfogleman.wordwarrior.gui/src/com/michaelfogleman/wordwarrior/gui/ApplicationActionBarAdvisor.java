package com.michaelfogleman.wordwarrior.gui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

import com.michaelfogleman.wordwarrior.ScrabblePlugin;
import com.michaelfogleman.wordwarrior.protocol.command.ObserveCommand;

/**
 * An action bar advisor is responsible for creating, adding, and disposing of
 * the actions added to a workbench window. Each window will be populated with
 * new actions.
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	// Actions - important to allocate these only in makeActions, and then use
	// them
	// in the fill methods. This ensures that the actions aren't recreated
	// when fillActionBars is called with FILL_PROXY.
	private IAction newGameAction;
	private IAction connectAction;
	private IAction disconnectAction;
	private IAction matchAction;
	private IAction seekAction;
	private IAction playerSearchAction;
	private IAction observeBestAction;
	private IAction observeAction;
	private IWorkbenchAction exitAction;
	private IWorkbenchAction resetPerspectiveAction;
	private IAction toggleSeekGraphView;
	private IAction toggleBuddyListView;
	private IAction toggleMoveListView;
	private IAction toggleAnalysisView;
	private IAction toggleTilePoolView;
	private IAction toggleConsoleView;
	private IAction toggleArchive;

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	protected void makeActions(final IWorkbenchWindow window) {
		// Creates the actions and registers them.
		// Registering is needed to ensure that key bindings work.
		// The corresponding commands keybindings are defined in the plugin.xml
		// file.
		// Registering also provides automatic disposal of the actions when
		// the window is closed.

		exitAction = ActionFactory.QUIT.create(window);
		register(exitAction);
		
		resetPerspectiveAction = ActionFactory.RESET_PERSPECTIVE.create(window);
		register(resetPerspectiveAction);
		
		toggleSeekGraphView = new Action("Toggle Games") {
			public void run() {
				toggleView(window, SeekGraphView.ID);
			}
		};
		
		toggleBuddyListView = new Action("Toggle Buddies") {
			public void run() {
				toggleView(window, BuddyListView.ID);
			}
		};
		
		toggleMoveListView = new Action("Toggle Moves") {
			public void run() {
				toggleView(window, MoveListView.ID);
			}
		};
		
		toggleAnalysisView = new Action("Toggle Analysis") {
			public void run() {
				toggleView(window, AnalysisView.ID);
			}
		};
		
		toggleTilePoolView = new Action("Toggle Tiles") {
			public void run() {
				toggleView(window, TilePoolView.ID);
			}
		};
		
		toggleConsoleView = new Action("Toggle Console") {
			public void run() {
				toggleView(window, ConsoleView.ID);
			}
		};
		
		toggleArchive = new Action("Toggle Archive") {
			public void run() {
				toggleView(window, ArchiveView.ID);
			}
		};
		
		connectAction = new Action("Connect...") {
			public void run() {
				ConnectionDialog dialog = new ConnectionDialog(window.getShell());
				dialog.open();
			}
		};
		
		disconnectAction = new Action("Disconnect") {
			public void run() {
				ScrabblePlugin.getDefault().getSession().close();
			}
		};
		
		observeBestAction = new Action("Observe Top Game") {
			public void run() {
				ScrabblePlugin.getDefault().getSession().getConnection().send(new ObserveCommand());
			}
		};
		
		observeAction = new Action("Observe Player...") {
			public void run() {
				InputDialog in = new InputDialog(window.getShell(), "Match", "Enter the handle for the player you would like to observe.", "", null);
				if (in.open() == InputDialog.OK) {
					String result = in.getValue();
					if (result != null && result.length() > 0) {
						ScrabblePlugin.getDefault().getSession().getConnection().send(new ObserveCommand(result));
					}
				}
			}
		};
		
		playerSearchAction = new Action("Find Player...") {
			public void run() {
				UserSearchDialog dialog = new UserSearchDialog(window.getShell());
				dialog.open();
			}
		};
		
		newGameAction = new Action("New Offline Game...") {
			public void run() {
				NewGameDialog dialog = new NewGameDialog(window.getShell());
				dialog.open();
			}
		};
		
		matchAction = new Action("Match Player...") {
			public void run() {
				MatchDialog dialog = new MatchDialog(window.getShell());
				dialog.open();
			}
		};
		
		seekAction = new Action("Seek Game...") {
			public void run() {
				SeekDialog dialog = new SeekDialog(window.getShell());
				dialog.open();
			}
		};
	}
	
	private void toggleView(IWorkbenchWindow window, String id) {
		IViewPart view = window.getActivePage().findView(id);
		if (view != null) {
			window.getActivePage().hideView(view);
		}
		else {
			try {
				window.getActivePage().showView(id);
			} catch (PartInitException e) {
			}
		}
	}

	protected void fillMenuBar(IMenuManager menuBar) {
		MenuManager sessionMenu = new MenuManager("&Session", "session");
		menuBar.add(sessionMenu);
		sessionMenu.add(connectAction);
		sessionMenu.add(disconnectAction);
		sessionMenu.add(new Separator());
		sessionMenu.add(exitAction);
		
		MenuManager gameMenu = new MenuManager("&Game", "game");
		menuBar.add(gameMenu);
		gameMenu.add(newGameAction);
		gameMenu.add(matchAction);
		gameMenu.add(seekAction);
		gameMenu.add(playerSearchAction);
		gameMenu.add(new Separator());
		gameMenu.add(observeBestAction);
		gameMenu.add(observeAction);
		
		MenuManager viewMenu = new MenuManager("&Window", "window");
		menuBar.add(viewMenu);
		viewMenu.add(toggleArchive);
		viewMenu.add(toggleAnalysisView);
		viewMenu.add(toggleBuddyListView);
		viewMenu.add(toggleConsoleView);
		viewMenu.add(toggleSeekGraphView);
		viewMenu.add(toggleMoveListView);
		viewMenu.add(toggleTilePoolView);
		viewMenu.add(new Separator());
		viewMenu.add(resetPerspectiveAction);
	}

}
