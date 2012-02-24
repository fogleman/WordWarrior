package com.michaelfogleman.wordwarrior.gui;

import java.net.URL;
import java.text.DateFormat;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import com.michaelfogleman.wordwarrior.ScrabblePlugin;
import com.michaelfogleman.wordwarrior.model.GameReference;
import com.michaelfogleman.wordwarrior.model.IArchiveListener;
import com.michaelfogleman.wordwarrior.model.IArchiveManager;
import com.michaelfogleman.wordwarrior.protocol.Connection;
import com.michaelfogleman.wordwarrior.protocol.command.ExamineAdjournedCommand;
import com.michaelfogleman.wordwarrior.protocol.command.ExamineHistoryCommand;
import com.michaelfogleman.wordwarrior.protocol.command.ExamineLibraryCommand;
import com.michaelfogleman.wordwarrior.protocol.command.UnexamineCommand;

public class ArchiveView extends ViewPart {
	
	public static final String ID = ArchiveView.class.getName();
	
	private IArchiveManager archiveManager;
	private IArchiveListener archiveListener;
	private TableViewer viewer;
	private Action examine;
	private IMemento memento;
//	private Image winnerIcon;
	
	public void createPartControl(Composite parent) {
		archiveManager = ScrabblePlugin.getDefault().getSession().getArchiveManager();
		archiveListener = createHistoryListener();
		archiveManager.addArchiveListener(archiveListener);

		Table table = new Table(parent, SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		viewer = createTableViewer(table);
		viewer.setInput(archiveManager);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		
//		winnerIcon = getImageDescriptor("images/star.gif").createImage();
		
		createActions();
		createToolbar();
		createMenu();
	}
	
	private void createActions() {
		String text;
		text = "Examine Game";
		examine = new Action(text, getImageDescriptor("images/observe.gif")) {
			public void run() {
				GameReference reference = getSelectedGameReference();
				if (reference != null) {
					Connection connection = ScrabblePlugin.getDefault().getSession().getConnection();
					int type = reference.getType();
					// TODO consolidate these command types
					if (type == GameReference.HISTORY_TYPE) {
						connection.send(new ExamineHistoryCommand(reference.getOwner(), reference.getId()));
						connection.send(new UnexamineCommand());
					}
					else if (type == GameReference.LIBRARY_TYPE) {
						connection.send(new ExamineLibraryCommand(reference.getOwner(), reference.getId()));
						connection.send(new UnexamineCommand());
					}
					else if (type == GameReference.ADJOURNED_TYPE) {
						connection.send(new ExamineAdjournedCommand(reference.getOwner(), reference.getId()));
						connection.send(new UnexamineCommand());
					}
				}
			};
		};
		examine.setToolTipText(text);
		examine.setDescription(text);
	}
	
	private void createToolbar() {
		IToolBarManager toolbar = getViewSite().getActionBars().getToolBarManager();
		toolbar.add(examine);
	}
	
	private void createMenu() {
		IMenuManager menu = getViewSite().getActionBars().getMenuManager();
		menu.add(examine);
	}
	
	private ImageDescriptor getImageDescriptor(String relativePath) {
		ScrabbleGuiPlugin plugin = ScrabbleGuiPlugin.getDefault();
		URL url = plugin.getBundle().getEntry(relativePath);
		return ImageDescriptor.createFromURL(url);
	}
	
	public void dispose() {
//		winnerIcon.dispose();
		archiveManager.removeArchiveListener(archiveListener);
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
	
	private GameReference getSelectedGameReference() {
		Table table = viewer.getTable();
		int index = table.getSelectionIndex();
		int count = table.getItemCount();
		if (index >= 0 && index < count) {
			TableItem item = table.getItem(table.getSelectionIndex());
			GameReference reference = (GameReference)item.getData();
			return reference;
		}
		return null;
	}
	
	private TableViewer createTableViewer(Table table) {
		TableViewer tableViewer = new TableViewer(table);
		
		TableLayout layout = new TableLayout();
		layout.addColumnData(new ColumnWeightData(15, 80, true));
		layout.addColumnData(new ColumnWeightData(5, 24, true));
		layout.addColumnData(new ColumnWeightData(15, 80, true));
		layout.addColumnData(new ColumnWeightData(10, 64, true));
		layout.addColumnData(new ColumnWeightData(15, 80, true));
		layout.addColumnData(new ColumnWeightData(10, 64, true));
		layout.addColumnData(new ColumnWeightData(10, 64, true));
		layout.addColumnData(new ColumnWeightData(10, 64, true));
		layout.addColumnData(new ColumnWeightData(10, 80, true));
		table.setLayout(layout);
		
		TableColumn column;
		column = new TableColumn(table, SWT.LEFT);
		column.setText("Type");
		column = new TableColumn(table, SWT.LEFT);
		column.setText("#");
		column = new TableColumn(table, SWT.LEFT);
		column.setText("Player 1");
		column = new TableColumn(table, SWT.LEFT);
		column.setText("Rating");
		column = new TableColumn(table, SWT.LEFT);
		column.setText("Player 2");
		column = new TableColumn(table, SWT.LEFT);
		column.setText("Rating");
		column = new TableColumn(table, SWT.LEFT);
		column.setText("Result");
		column = new TableColumn(table, SWT.LEFT);
		column.setText("Dictionary");
		column = new TableColumn(table, SWT.LEFT);
		column.setText("Date");
		
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				examine.run();
			}
		});
		
		table.setHeaderVisible(true);
		configureViewer(tableViewer);
		Util.loadTableViewer(memento, tableViewer);
		return tableViewer;
	}
	
	private void configureViewer(TableViewer viewer) {
		viewer.setContentProvider(new IStructuredContentProvider() {
			public Object[] getElements(Object inputElement) {
				IArchiveManager manager = (IArchiveManager)inputElement;
				List<GameReference> list = manager.getActiveGameReferences();
				GameReference[] result = new GameReference[list.size()];
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
				GameReference entry = (GameReference)element;
				if (entry == null) return null;
				switch (columnIndex) {
					case 0:
						int type = entry.getType();
						if (type == GameReference.HISTORY_TYPE) return "Recent";
						else if (type == GameReference.LIBRARY_TYPE) return "Library";
						else if (type == GameReference.ADJOURNED_TYPE) return "Adjourned";
					case 1:
						return Integer.toString(entry.getId());
					case 2:
						return entry.getHandle1();
					case 3:
						return Integer.toString(entry.getRating1());
					case 4:
						return entry.getHandle2();
					case 5:
						return Integer.toString(entry.getRating2());
					case 6:
						return entry.getResult();
					case 7:
						if (entry.getDictionary() == null) return null;
						return entry.getDictionary().getName();
					case 8:
						if (entry.getDate() == null) return null;
						DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
						return df.format(entry.getDate().getTime());
				}
				return null;
			}
			public Image getColumnImage(Object element, int columnIndex) {
//				GameReference entry = (GameReference)element;
//				if (entry == null) return null;
//				if (entry.getResult().equals("1-0") && columnIndex == 2) {
//					return winnerIcon;
//				}
//				else if (entry.getResult().equals("0-1") && columnIndex == 4) {
//					return winnerIcon;
//				}
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
	
	private IArchiveListener createHistoryListener() {
		return new IArchiveListener() {
			public void archiveUpdated() {
				refresh();
			}
		};
	}

}
