package com.michaelfogleman.wordwarrior.gui;

import java.net.URL;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import com.michaelfogleman.wordwarrior.ScrabblePlugin;
import com.michaelfogleman.wordwarrior.model.IBuddyListener;
import com.michaelfogleman.wordwarrior.model.IBuddyManager;
import com.michaelfogleman.wordwarrior.model.User;
import com.michaelfogleman.wordwarrior.protocol.Connection;
import com.michaelfogleman.wordwarrior.protocol.command.ObserveCommand;

public class BuddyListView extends ViewPart {
	
	public static final String ID = BuddyListView.class.getName();
	
	private IBuddyManager buddyManager;
	private IBuddyListener buddyListener;
	private TableViewer viewer;
	private IMemento memento;
	
	private Action addBuddy;
	private Action removeBuddy;
	private Action match;
	private Action observe;
	
	public void createPartControl(Composite parent) {
		buddyManager = ScrabblePlugin.getDefault().getSession().getBuddyManager();
		buddyListener = createBuddyListener();
		buddyManager.addBuddyListener(buddyListener);

		Table table = new Table(parent, SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		viewer = createTableViewer(table);
		viewer.setInput(buddyManager);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		createActions();
		createToolbar();
		createMenu();
	}
	
	private void createActions() {
		String text;
		
		text = "Add New Buddy";
		addBuddy = new Action(text, getImageDescriptor("images/add.gif")) {
			public void run() {
				InputDialog in = new InputDialog(getShell(), "Add Buddy", "Enter the handle for the player you would like to add to your buddy list.", "", null);
				if (in.open() == InputDialog.OK) {
					String handle = in.getValue();
					if (handle != null && handle.length() > 0) {
						buddyManager.addBuddy(handle);
					}
				}
			};
		};
		addBuddy.setToolTipText(text);
		addBuddy.setDescription(text);
		
		text = "Remove Selected Buddy";
		removeBuddy = new Action(text, getImageDescriptor("images/abort.gif")) {
			public void run() {
				String handle = getSelectedHandle();
				if (handle != null) {
					MessageBox msg = new MessageBox(getShell(), SWT.ICON_QUESTION | SWT.NO | SWT.YES);
					msg.setText("Remove Buddy");
					msg.setMessage("Are you sure you want to remove " + handle + " from your buddy list?");
					int result = msg.open();
					if (result == SWT.YES) {
						buddyManager.removeBuddy(handle);
					}
				}
			};
		};
		removeBuddy.setToolTipText(text);
		removeBuddy.setDescription(text);
		
		text = "Play with Selected Buddy";
		match = new Action(text, getImageDescriptor("images/match.gif")) {
			public void run() {
				String handle = getSelectedHandle();
				if (handle != null) {
					MatchDialog dialog = new MatchDialog(getShell(), handle);
					dialog.open();
				}
			};
		};
		match.setToolTipText(text);
		match.setDescription(text);
		
		text = "Observe Selected Buddy";
		observe = new Action(text, getImageDescriptor("images/observe.gif")) {
			public void run() {
				String handle = getSelectedHandle();
				if (handle != null) {
					Connection connection = ScrabblePlugin.getDefault().getSession().getConnection();
					connection.send(new ObserveCommand(handle));
				}
			};
		};
		observe.setToolTipText(text);
		observe.setDescription(text);
	}
	
	private void createToolbar() {
		IToolBarManager toolbar = getViewSite().getActionBars().getToolBarManager();
		toolbar.add(addBuddy);
		toolbar.add(match);
		toolbar.add(observe);
		toolbar.add(removeBuddy);
	}
	
	private void createMenu() {
		IMenuManager menu = getViewSite().getActionBars().getMenuManager();
		menu.add(addBuddy);
		menu.add(match);
		menu.add(observe);
		menu.add(removeBuddy);
	}
	
	private ImageDescriptor getImageDescriptor(String relativePath) {
		ScrabbleGuiPlugin plugin = ScrabbleGuiPlugin.getDefault();
		URL url = plugin.getBundle().getEntry(relativePath);
		return ImageDescriptor.createFromURL(url);
	}
	
	private Shell getShell() {
		return viewer.getTable().getShell();
	}
	
	private String getSelectedHandle() {
		Table table = viewer.getTable();
		int index = table.getSelectionIndex();
		int count = table.getItemCount();
		if (index >= 0 && index < count) {
			TableItem item = table.getItem(table.getSelectionIndex());
			String handle = item.getText(0);
			return handle;
		}
		return null;
	}
	
	public void dispose() {
		buddyManager.removeBuddyListener(buddyListener);
		super.dispose();
	}
	
	public void setFocus() {
		viewer.getTable().setFocus();
	}
	
	private void refresh() {
		viewer.getTable().getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (!viewer.getTable().isDisposed()) {
					viewer.refresh();
					viewer.getTable().layout(true, true);
				}
			}
		});
	}
	
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		this.memento = memento;
	}
	
	public void saveState(IMemento memento) {
		super.saveState(memento);
		Util.saveTableViewer(memento, viewer);
	}
	
	private TableViewer createTableViewer(Table table) {
		TableViewer tableViewer = new TableViewer(table);

		TableLayout layout = new TableLayout();
		layout.addColumnData(new ColumnWeightData(25, 96, true));
		layout.addColumnData(new ColumnWeightData(15, 64, true));
		layout.addColumnData(new ColumnWeightData(20, 64, true));
		layout.addColumnData(new ColumnWeightData(20, 64, true));
		layout.addColumnData(new ColumnWeightData(20, 64, true));
		table.setLayout(layout);
		
		TableColumn column;
		column = new TableColumn(table, SWT.LEFT);
		column.setText("Handle");
		column = new TableColumn(table, SWT.LEFT);
		column.setText("Rating");
		column = new TableColumn(table, SWT.LEFT);
		column.setText("Dictionary");
		column = new TableColumn(table, SWT.LEFT);
		column.setText("State");
		column = new TableColumn(table, SWT.LEFT);
		column.setText("Type");
		
		table.setHeaderVisible(true);
		configureViewer(tableViewer);
		Util.loadTableViewer(memento, tableViewer);
		return tableViewer;
	}
	
	private void configureViewer(TableViewer viewer) {
		viewer.setContentProvider(new IStructuredContentProvider() {
			public Object[] getElements(Object inputElement) {
				IBuddyManager manager = (IBuddyManager)inputElement;
				List<User> list = manager.getBuddies();
				User[] result = new User[list.size()];
				list.toArray(result);
				return result;
			}
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}
			public void dispose() {
			}
		});
		viewer.setLabelProvider(new ITableLabelProvider() {
			public String getColumnText(Object element, int columnIndex) {
				User user = (User)element;
				if (user == null) return null;
				switch (columnIndex) {
					case 0:
						return user.getHandle();
					case 1:
						if (user.getRating() <= 0) return null;
						return Integer.toString(user.getRating());
					case 2:
						if (user.getDictionaryType() == null) return null;
						return user.getDictionaryType().getName();
					case 3:
						if (user.getUserState() == null) return null;
						return user.getUserState().getName();
					case 4:
						if (user.getUserType() == null) return null;
						return user.getUserType().getName();
				}
				return null;
			}
			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}
			public boolean isLabelProperty(Object element, String property) {
				return false;
			}
			public void addListener(ILabelProviderListener listener) {	
			}
			public void removeListener(ILabelProviderListener listener) {
			}
			public void dispose() {
			}
		});
	}
	
	private IBuddyListener createBuddyListener() {
		return new IBuddyListener() {
			public void buddyListUpdated() {
				refresh();
			}
			public void buddyChangedState(User buddy) {
				refresh();
			}
			public void buddyLoggedIn(User buddy) {
				refresh();
			}
			public void buddyLoggedOut(User buddy) {
				refresh();
			}
		};
	}

}
