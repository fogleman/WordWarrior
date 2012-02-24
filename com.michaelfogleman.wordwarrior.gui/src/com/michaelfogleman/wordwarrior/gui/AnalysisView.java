package com.michaelfogleman.wordwarrior.gui;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import com.michaelfogleman.wordwarrior.Convert;
import com.michaelfogleman.wordwarrior.model.GameAdapter;
import com.michaelfogleman.wordwarrior.model.IGame;
import com.michaelfogleman.wordwarrior.model.IGameListener;
import com.michaelfogleman.wordwarrior.model.IPlayer;
import com.michaelfogleman.wordwarrior.model.MoveResult;
import com.michaelfogleman.wordwarrior.model.Tile;

public class AnalysisView extends ViewPart {
	
	public static final String ID = AnalysisView.class.getName();
	
	private IGame game;
	private IGameListener gameListener;
	private IPartListener partListener;
	private TableViewer viewer;
	private IMemento memento;
	
	public void createPartControl(Composite parent) {
		gameListener = createGameListener();
		partListener = createPartListener();
		
		getSite().getWorkbenchWindow().getPartService().addPartListener(partListener);
		
		Table table = new Table(parent, SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		viewer = createTableViewer(table);
		
		viewer.setContentProvider(new IStructuredContentProvider() {
			public Object[] getElements(Object inputElement) {
				IGame game = getGame();
				if (game == null) return new MoveResult[0];
				return game.getAnalysis();
			}
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}
			public void dispose() {
			}
		});
		viewer.setLabelProvider(new ITableLabelProvider() {
			public String getColumnText(Object element, int columnIndex) {
				MoveResult move = (MoveResult)element;
				switch (columnIndex) {
					case 0:
						if (move != null) {
							return Integer.toString(move.getScore());
						}
					case 1:
						if (move != null) {
							return move.getMove().getCoordinateString();
						}
					case 2:
						if (move != null) {
							return Convert.toString(move.getMove().getWord());
						}
					case 3:
						if (move != null) {
							StringBuffer b = new StringBuffer();
							String[] words = move.getWords();
							for (int i = 0; i < words.length; i++) {
								String word = words[i];
								if (i > 0) b.append(", ");
								b.append(word);
							}
							return b.toString();
						}
					case 4:
						if (move != null) {
							return Convert.toString(move.getTilesUsed());
						}
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
	
	public void dispose() {
		setGame(null);
		getSite().getWorkbenchWindow().getPartService().removePartListener(partListener);
		super.dispose();
	}
	
	private void checkPart(IWorkbenchPart part) {
		if (part instanceof GameView) {
			GameView view = (GameView)part;
			setGame(view.getGame());
		}
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
	
	private TableViewer createTableViewer(final Table table) {
		TableViewer tableViewer = new TableViewer(table);

		TableLayout layout = new TableLayout();
		layout.addColumnData(new ColumnWeightData(15, 48, true));
		layout.addColumnData(new ColumnWeightData(15, 32, true));
		layout.addColumnData(new ColumnWeightData(20, 64, true));
		layout.addColumnData(new ColumnWeightData(30, 128, true));
		layout.addColumnData(new ColumnWeightData(20, 64, true));
		table.setLayout(layout);
		
		TableColumn column;
		column = new TableColumn(table, SWT.LEFT);
		column.setText("Score");
		column = new TableColumn(table, SWT.LEFT);
		column.setText("Pos");
		column = new TableColumn(table, SWT.LEFT);
		column.setText("Word");
		column = new TableColumn(table, SWT.LEFT);
		column.setText("Words Formed");
		column = new TableColumn(table, SWT.LEFT);
		column.setText("Tiles Used");
		
		table.setHeaderVisible(true);
		Util.loadTableViewer(memento, tableViewer);
		return tableViewer;
	}
	
	public void setGame(IGame game) {
		if (this.game != null) {
			this.game.removeGameListener(gameListener);
		}
		this.game = game;
		if (game != null) {
			game.addGameListener(gameListener);
		}
		if (game != null) {
			viewer.setInput(game);
			refresh();
		}
	}
	
	private IGame getGame() {
		return game;
	}
	
	private IGameListener createGameListener() {
		return new GameAdapter() {
			public void playerMoved(IGame game, IPlayer player, MoveResult moveResult) {
				refresh();
			}
			public void playerPassedTurn(IGame game, IPlayer player) {
				refresh();
			}
			public void playerExchangedTiles(IGame game, IPlayer player, Tile[] tiles, Tile[] newRack) {
				refresh();
			}
			public void playerChallenged(IGame game, IPlayer player, boolean successful, String[] words) {
				refresh();
			}
		};
	}
	
	private IPartListener createPartListener() {
		return new IPartListener() {
			public void partActivated(IWorkbenchPart part) {
				checkPart(part);
			}
			public void partBroughtToTop(IWorkbenchPart part) {
				checkPart(part);
			}
			public void partClosed(IWorkbenchPart part) {
			}
			public void partDeactivated(IWorkbenchPart part) {
			}
			public void partOpened(IWorkbenchPart part) {
				checkPart(part);
			}
		};
	}

}
