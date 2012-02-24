package com.michaelfogleman.wordwarrior.gui;

import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import com.michaelfogleman.wordwarrior.model.GameAdapter;
import com.michaelfogleman.wordwarrior.model.IGame;
import com.michaelfogleman.wordwarrior.model.IGameListener;
import com.michaelfogleman.wordwarrior.model.IPlayer;
import com.michaelfogleman.wordwarrior.model.ITilePool;
import com.michaelfogleman.wordwarrior.model.MoveResult;
import com.michaelfogleman.wordwarrior.model.Tile;

public class TilePoolView extends ViewPart {
	
	public static final String ID = TilePoolView.class.getName();
	
	private IGame game;
	private IGameListener gameListener;
	private Canvas canvas;
	private Color background;
	private Color tileLight;
	private Color tileDark;
	private Color tileLightHighlight;
	private Color tileDarkHighlight;
	private Color tileFill;
	private Color textColor;
	private String tileFont = "Courier New";
	
	public void createPartControl(Composite parent) {
		init();
		
		gameListener = createGameListener();
		
		getSite().getWorkbenchWindow().getPartService().addPartListener(new IPartListener() {
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
		});
		
		canvas = new Canvas(parent, SWT.NO_BACKGROUND);
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				TilePoolView.this.paintControl(e);
			}
		});
	}
	
	private void checkPart(IWorkbenchPart part) {
		if (part instanceof GameView) {
			GameView view = (GameView)part;
			setGame(view.getGame());
		}
	}
	
	private void refresh() {
		if (canvas != null && !canvas.isDisposed()) {
			Display display = canvas.getDisplay();
			if (!display.isDisposed()) {
				display.asyncExec(new Runnable() {
					public void run() {
						if (!canvas.isDisposed()) {
							canvas.redraw();
						}
					};
				});
			}
		}
	}

	public void setFocus() {
		canvas.setFocus();
	}
	
	private void init() {
		background = getViewSite().getShell().getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
		
		// standard
//		textColor = new Color(null, 0, 0, 0);
//		tileFill = new Color(null, 210, 200, 185);
//		tileLight = new Color(null, 250, 245, 240);
//		tileDark = new Color(null, 170, 150, 110);
		
		// charcoal
		tileDark = new Color(null, 0, 0, 0);
		textColor = new Color(null, 255, 255, 255);
		tileLight = new Color(null, 92, 92, 92);
		tileFill = new Color(null, 48, 48, 48);
		
		tileLightHighlight = new Color(null, 255, 255, 0);
		tileDarkHighlight = new Color(null, 128, 128, 0);
	}
	
	public void dispose() {
		background.dispose();
		tileDark.dispose();
		textColor.dispose();
		tileLight.dispose();
		tileFill.dispose();
		tileLightHighlight.dispose();
		tileDarkHighlight.dispose();
		super.dispose();
	}
	
	public void setGame(IGame game) {
		if (this.game != null) {
			this.game.removeGameListener(gameListener);
		}
		this.game = game;
		if (game != null) {
			game.addGameListener(gameListener);
		}
		refresh();
	}
	
//	private IGame getGame() {
//		return game;
//	}
	
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
	
	private List<Tile> getTiles() {
		ITilePool pool = game.getTilePool();
		List<Tile> tiles = pool.getRemainingTiles();
		IPlayer local = game.getLocalPlayer();
		for (IPlayer player : game.getPlayers()) {
			if (player == local) continue;
			Tile[] rack = player.getTileRack().getTiles();
			for (Tile tile : rack) {
				tiles.add(tile);
			}
		}
		Collections.sort(tiles);
		return tiles;
	}
	
	private void paintControl(PaintEvent e) {
		Point size = canvas.getSize();
		Image buffer = new Image(null, size.x, size.y);
		GC gc = new GC(buffer);
		
		gc.setBackground(background);
		gc.fillRectangle(canvas.getBounds());
		
		int w = canvas.getBounds().width;
		int h = canvas.getBounds().height;
		
		if (game != null) {
			List<Tile> tiles = getTiles();
			int n = tiles.size();
			if (n > 0) {
				Point d = getDimensions(w, h, n);
				int c = d.x;
				int r = d.y;
				int p = 0;
				int m = 0;
				int s = Math.min((w - m * 2 - (c - 1) * p) / c, (h - m * 2 - (r - 1) * p) / r);
				int xo = (w - c * s - (c - 1) * p) / 2;
				int yo = (h - r * s - (r - 1) * p) / 2;
				
				int i = 0;
				for (int y = 0; y < r; y++) {
					for (int x = 0; x < c; x++) {
						if (i >= n) break;
						Tile tile = tiles.get(i++);
						int px = xo + (s + p) * x;
						int py = yo + (s + p) * y;
						Rectangle bounds = new Rectangle(px, py, s, s);
						drawTile(gc, bounds, tile);
					}
				}
			}
		}
		
		e.gc.drawImage(buffer, 0, 0);
		gc.dispose();
		buffer.dispose();
	}
	
	private Point getDimensions(int w, int h, int n) {
		int c, r;
		
		if (w > h) {
			r = (int)Math.round(Math.sqrt(n * h / (double)w));
			if (r < 1) r = 1;
			c = (int)Math.ceil(n / (double)r);
			if (c < 1) c = 1;
		}
		else {
			c = (int)Math.round(Math.sqrt(n * w / (double)h));
			if (c < 1) c = 1;
			r = (int)Math.ceil(n / (double)c);
			if (r < 1) r = 1;
		}
		
		return new Point(c, r);
	}
	
	private void drawTile(GC gc, Rectangle bounds, Tile tile) {
		drawTile(gc, bounds, tile, false);
	}
	
	private void drawTile(GC gc, Rectangle bounds, Tile tile, boolean highlight) {
		if (tile == null || tile.equals(Tile.NONE)) return;
		
		gc.setClipping(bounds);
		
		int x = bounds.x;
		int y = bounds.y;
		int w = bounds.width;
		int h = bounds.height;
		
		int s = Math.min(w, h);
		int n = Math.max(s / 12, 1);
		int x1 = x;
		int x2 = x1 + n;
		int x3 = x + s - n;
		int x4 = x + s;
		int y1 = y;
		int y2 = y1 + n;
		int y3 = y + s - n;
		int y4 = y + s;
		
		int[] p1 = new int[] {x1,y1,x4,y1,x3,y2,x2,y2,x2,y3,x1,y4};
		int[] p2 = new int[] {x4,y4,x1,y4,x2,y3,x3,y3,x3,y2,x4,y1};
		
		gc.setBackground(tileFill);
		gc.fillRectangle(x, y, w, h);
		gc.setBackground(highlight ? tileLightHighlight : tileLight);
		gc.fillPolygon(p1);
		gc.setBackground(highlight ? tileDarkHighlight : tileDark);
		gc.fillPolygon(p2);
		
		Font font = new Font(null, tileFont, s/2+1, SWT.BOLD);
		String string = Character.toString(tile.getLetter()).toUpperCase();
		gc.setFont(font);
		Point extent = gc.stringExtent(string);
		int sx = x + w / 2 - extent.x / 2 - n;
		int sy = y + h / 2 - extent.y / 2;
		gc.setForeground(textColor);
		gc.drawString(string, sx, sy, true);
		font.dispose();
		
		font = new Font(null, tileFont, s/5, SWT.BOLD);
		string = Integer.toString(game.getTileValues().getValue(tile));
		gc.setFont(font);
		extent = gc.stringExtent(string);
		sx = x + w - n - extent.x - 1;
		sy = y + h - n - extent.y;
		gc.setForeground(textColor);
		gc.drawString(string, sx, sy, true);
		font.dispose();
	}

}

