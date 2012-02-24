package com.michaelfogleman.wordwarrior.gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.part.ViewPart;

import com.michaelfogleman.wordwarrior.Convert;
import com.michaelfogleman.wordwarrior.ScrabblePlugin;
import com.michaelfogleman.wordwarrior.model.*;
import com.michaelfogleman.wordwarrior.protocol.command.AbortCommand;
import com.michaelfogleman.wordwarrior.protocol.command.AdjournCommand;
import com.michaelfogleman.wordwarrior.protocol.command.ResignCommand;

public class GameView extends ViewPart {

	public static final String ID = GameView.class.getName();

	private Session session;
	private IGame game;
	private IGameListener gameListener;
	private ITileRack tileRack;
	
	private Canvas canvas;
	private Runnable redrawRunnable;
	private Action done;
	private Action pass;
	private Action change;
	private Action challenge;
	private Action abort;
	private Action resign;
	private Action adjourn;
	
	private Color background;
	private Color light;
	private Color dark;
	private Color tileLight;
	private Color tileDark;
	private Color tileLightHighlight;
	private Color tileDarkHighlight;
	private Color tileFill;
	private Color quadWord;
	private Color quadWordLight;
	private Color tripleWord;
	private Color tripleWordLight;
	private Color doubleWord;
	private Color doubleWordLight;
	private Color quadLetter;
	private Color quadLetterLight;
	private Color tripleLetter;
	private Color tripleLetterLight;
	private Color doubleLetter;
	private Color doubleLetterLight;
	private Color textColor;
	private Color coordinateColor;
	private Color arrowForeground;
	private Color arrowBackground;
	private Font coordinateFont;
	private Color tileRackBorder;
	private Color tileRackFill;
	private Color boardBorder;
	
	private int padding = 16;
	private int border = 4;
	private int coordinates = 16;
	private int tileSize;
	
	private String tileFont = "Courier New";
	private boolean showCoordinates;
	
	private List<Square> squareLocations; // for mouse click -> square resolution
	private Map<Point, Tile> placedTiles; // maps Point to Tile
	private Arrow arrow;
	private int mousex;
	private int mousey;
	
	private Map<Rectangle, DragSource> dragSources;
	private List<DragTarget> dragTargets;
	private DragSource drag;
	
	public void createPartControl(Composite parent) {
		init();
		
		session = ScrabblePlugin.getDefault().getSession();
		
		GridLayout layout = new GridLayout(1, true);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		parent.setLayout(layout);
		
//		Composite bar = new Composite(parent, SWT.NONE);
//		bar.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
//		bar.setLayout(new RowLayout());
//		
//		Button button = new Button(bar, SWT.PUSH);
//		button.setText("Test Button");
		
		canvas = new Canvas(parent, SWT.NO_BACKGROUND);
		canvas.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL));
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				paintCanvas(e);
			}
		});
		canvas.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				GameView.this.keyPressed(e);
			}
		});
		canvas.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent e) {
				GameView.this.mouseDown(e);
			}
			public void mouseUp(MouseEvent e) {
				GameView.this.mouseUp(e);
			}
		});
		canvas.addMouseMoveListener(new MouseMoveListener() {
			public void mouseMove(MouseEvent e) {
				GameView.this.mouseMove(e);
			};
		});
		
		createActions();
		createToolbar();
		createMenu();
	}
	
	public void setFocus() {
		canvas.setFocus();
	}
	
	private void setStatusMessage(String message) {
		getViewSite().getActionBars().getStatusLineManager().setMessage(message);
	}

	private void init() {
		SoundClip.loadSoundClips();
		
		redrawRunnable = createRedrawRunnable();
		gameListener = createGameListener();
		
		tileRack = new StandardTileRack();
		placedTiles = new HashMap<Point, Tile>();
		squareLocations = new ArrayList<Square>();
		
		dragSources = new HashMap<Rectangle, DragSource>();
		dragTargets = new ArrayList<DragTarget>();
		
		background = new Color(null, 165, 155, 145);
		light = new Color(null, 205, 200, 195);
		dark = new Color(null, 100, 90, 80);
		
		quadWord = new Color(null, 220, 140, 15);
		tripleWord = new Color(null, 165, 45, 65);
		doubleWord = new Color(null, 210, 160, 180);
		quadLetter = new Color(null, 50, 120, 75);
		tripleLetter = new Color(null, 0, 70, 120);
		doubleLetter = new Color(null, 125, 175, 205);
		
		quadWordLight = new Color(null, 250, 170, 25);
		tripleWordLight = new Color(null, 205, 85, 105);
		doubleWordLight = new Color(null, 250, 200, 220);
		quadLetterLight = new Color(null, 90, 170, 120);
		tripleLetterLight = new Color(null, 20, 105, 160);
		doubleLetterLight = new Color(null, 155, 215, 255);
		
		// standard
//		textColor = new Color(null, 0, 0, 0);
//		tileFill = new Color(null, 210, 200, 185);
//		tileLight = new Color(null, 250, 245, 240);
//		tileDark = new Color(null, 170, 150, 110);
//		tileRackFill = new Color(null, 165, 155, 145);
		
		// charcoal
		tileDark = new Color(null, 0, 0, 0);
		textColor = new Color(null, 255, 255, 255);
		tileLight = new Color(null, 92, 92, 92);
		tileFill = new Color(null, 48, 48, 48);
		tileRackFill = new Color(null, 128, 128, 128);
		
		tileLightHighlight = new Color(null, 255, 255, 0);
		tileDarkHighlight = new Color(null, 128, 128, 0);
		
		showCoordinates = false;
		coordinateColor = new Color(null, 92, 92, 92);
		coordinateFont = new Font(null, "Courier New", 10, SWT.BOLD);
		
		arrowForeground = new Color(null, 0, 0, 0);
		arrowBackground = new Color(null, 255,255,128);
		
		tileRackBorder = new Color(null, 64, 64, 64);
		boardBorder = new Color(null, 0, 0, 0);
	}
	
	public void dispose() {
		if (session != null && game != null) {
			session.closeGame(game);
		}
		background.dispose();
		light.dispose();
		dark.dispose();
		quadWord.dispose();
		tripleWord.dispose();
		doubleWord.dispose();
		quadLetter.dispose();
		tripleLetter.dispose();
		doubleLetter.dispose();
		quadWordLight.dispose();
		tripleWordLight.dispose();
		doubleWordLight.dispose();
		quadLetterLight.dispose();
		tripleLetterLight.dispose();
		doubleLetterLight.dispose();
		tileDark.dispose();
		textColor.dispose();
		tileLight.dispose();
		tileFill.dispose();
		tileRackFill.dispose();
		tileLightHighlight.dispose();
		tileDarkHighlight.dispose();
		coordinateColor.dispose();
		coordinateFont.dispose();
		arrowForeground.dispose();
		arrowBackground.dispose();
		tileRackBorder.dispose();
		boardBorder.dispose();
		super.dispose();
	}
	
	private void keyPressed(KeyEvent e) {
		if (game == null) return;
		if (game.getGameState() != GameState.IN_PROGRESS) return;
		char c = Character.toLowerCase(e.character);
		if (c >= 'a' && c <= 'z') {
			// type letters on the board
			if (arrow != null) {
				IBoard board = game.getBoard();
				if (arrow.x >= board.getWidth()) return;
				if (arrow.y >= board.getHeight()) return;
				Tile boardTile = game.getBoard().getTile(arrow.y, arrow.x);
				if (!boardTile.equals(Tile.NONE)) {
					if (c == boardTile.getLetter()) {
						arrow.advance();
						redraw();
					}
					return;
				}
				Tile tile = new Tile(c);
				boolean wild = false;
				if (!tileRack.contains(tile)) {
					tile = new Tile('?');
					if (!tileRack.contains(tile)) {
						return;
					}
					wild = true;
				}
				tileRack.remove(tile);
				tile = new Tile(c, wild);
				placedTiles.put(new Point(arrow.x, arrow.y), tile);
				arrow.advance();
			}
			updateMoveString();
			redraw();
		}
		else if (c == SWT.DEL || c == SWT.BS) {
			// backspace a typed character on the board
			if (placedTiles.size() > 0) {
				arrow.backup();
				Point point = new Point(arrow.x, arrow.y);
				Tile tile = placedTiles.get(point);
				if (tile != null) {
					if (tile.isWild()) tile = new Tile('?');
					tileRack.add(tile);
					placedTiles.remove(point);
				}
				else {
					arrow.advance();
				}
				updateMoveString();
				redraw();
			}
		}
		else if (c == '1') {
			// toggle coordinates
			showCoordinates = !showCoordinates;
			redraw();
		}
		else if (e.keyCode == SWT.ESC) {
			// clear typed characters
			resetInput(true);
			redraw();
		}
		else if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
			// submit move on enter key
			done.run();
		}
		else if (c == ' ') {
			getLocalTileRack().shuffle();
			resetInput(true);
			redraw();
		}
	}
	
	private void mouseDown(MouseEvent e) {
		if (game == null) return;
		if (game.getGameState() != GameState.IN_PROGRESS) return;
		if (e.button == 1) {
			// check for drag event
			if (beginDrag(e)) {
				redraw();
				return;
			}
			
			// update arrow on left mouse click
			int x = e.x;
			int y = e.y;
			boolean del = true;
			for (Iterator i = squareLocations.iterator(); i.hasNext();) {
				Square s = (Square) i.next();
				Rectangle r = s.r;
				if (r.contains(x, y)) {
					int n = s.x;
					int m = s.y;
					del = false;
					if (arrow == null || n != arrow.x || m != arrow.y) {
						arrow = new Arrow(n, m, Orientation.HORIZONTAL);
					}
					else {
						arrow.flip();
					}
					redraw();
				}
			}
			if (del) {
				getLocalTileRack().shuffle();
			}
			resetInput(del);
			redraw();
		}
		else {
			// stop drag
			doDrop(e);
			
			// clear typed characters on right mouse click
			resetInput(true);
			redraw();
		}
	}
	
	private void mouseUp(MouseEvent e) {
		doDrop(e);
//		updateTileRack();
		redraw();
	}
	
	private void mouseMove(MouseEvent e) {
		mousex = e.x;
		mousey = e.y;
		
		if (drag != null) {
			emulateDrop(e);
			redraw();
		}
	}
	
	private boolean beginDrag(MouseEvent e) {
		if (drag != null) {
			doDrop(e);
		}
		for (Rectangle rectangle : dragSources.keySet()) {
			if (rectangle.contains(e.x, e.y)) {
				drag = dragSources.get(rectangle);
				if (drag.y == -1) {
					tileRack.remove(drag.x);
				}
				else {
					placedTiles.remove(new Point(drag.x, drag.y));
				}
				emulateDrop(e);
				return true;
			}
		}
		return false;
	}
	
	private void doDrop(MouseEvent e) {
		DragSource drag = this.drag;
		this.drag = null;
		if (drag == null) {
			return;
		}
		while (tileRack.remove(Tile.NONE));
		for (DragTarget target : dragTargets) {
			if (target.rectangle.contains(e.x, e.y)) {
				if (target.y == -1) {
					int x = target.x;
					Tile[] tiles = tileRack.getTiles();
					if (x < tiles.length && !tiles[x].equals(Tile.NONE)) {
						tileRack.add(x, drag.tile);
						pushRack();
						return;
					}
				}
				else {
					IBoard board = game.getBoard();
					Tile current = board.getTile(target.y, target.x);
					if (current.equals(Tile.NONE)) {
						Point point = new Point(target.x, target.y);
						if (!placedTiles.containsKey(point)) {
							placedTiles.put(point, drag.tile);
							updateMoveString();
							return;
						}
					}
				}
			}
		}
		// return to source
		if (drag.y == -1) {
			tileRack.add(drag.tile);
			pushRack();
		}
		else {
			placedTiles.put(new Point(drag.x, drag.y), drag.tile);
		}
	}
	
	private void emulateDrop(MouseEvent e) {
		while (tileRack.remove(Tile.NONE));
		for (DragTarget target : dragTargets) {
			if (target.rectangle.contains(e.x, e.y)) {
				if (target.y == -1) {
					int x = target.x;
					Tile[] tiles = tileRack.getTiles();
					if (x < tiles.length && !tiles[x].equals(Tile.NONE)) {
						tileRack.add(x, Tile.NONE);
						return;
					}
				}
			}
		}
	}
	
	private void pushRack() {
		if (!validateRacks()) return;
		ITileRack rack = getLocalTileRack();
		if (rack != null) {
			rack.clear();
			rack.add(tileRack.getTiles());
		}
		updateTileRack();
	}
	
	private boolean validateRacks() {
		ITileRack r1 = tileRack;
		ITileRack r2 = getLocalTileRack();
		if (r2 == null) return false;
		Tile[] t1 = r1.getTiles();
		Tile[] t2 = r2.getTiles();
		Arrays.sort(t1);
		Arrays.sort(t2);
		if (t1.length != t2.length) return false;
		for (int i = 0; i < t1.length; i++) {
			if (!t1[i].equals(t2[i])) {
				return false;
			}
		}
		return true;
	}
	
	private boolean validatePlacedTiles() {
		List<Tile> availableTiles = new ArrayList<Tile>();
		ITileRack rack = getLocalTileRack();
		if (rack != null) {
			for (Tile tile : rack.getTiles()) {
				availableTiles.add(tile);
			}
		}
		for (Tile tile : placedTiles.values()) {
			if (tile.isWild()) tile = new Tile('?');
			if (!availableTiles.remove(tile)) {
				return false;
			}
		}
		return true;
	}
	
	private void checkTiles() {
		if (!validatePlacedTiles()) {
			resetInput(true);
			redraw();
		}
	}
	
	private Shell getShell() {
		return getViewSite().getShell();
	}
	
	private IGameListener createGameListener() {
		return new GameAdapter() {
			public void playerMoved(IGame game, IPlayer player, MoveResult moveResult) {
				eventOccurred(player);
			}
			public void playerPassedTurn(IGame game, IPlayer player) {
				eventOccurred(player);
			}
			public void playerExchangedTiles(IGame game, IPlayer player, Tile[] tiles, Tile[] newRack) {
				eventOccurred(player);
			}
			public void playerChallenged(IGame game, IPlayer player, boolean successful, String[] words) {
				eventOccurred(player);
			}
			public void playerClockExpired(IGame game, IPlayer player, boolean penalty) {
				eventOccurred(player);
			}
			public void gameStateChanged(IGame game, GameState previous, GameState current) {
				if (current == GameState.CONFIRMATION) {
					IPlayer player = game.getCurrentPlayer();
					if (player.isLocal() && player.isHuman()) {
						if (game.getChallengeMode() == ChallengeMode.VOID) {
							game.doPassTurn();
						}
						else {
							getConfirmation();
						}
					}
				}
			}
		};
	}
	
	private void getConfirmation() {
		exec(new Runnable() {
			public void run() {
				MessageBox msg = new MessageBox(canvas.getShell(), SWT.ICON_QUESTION | SWT.NO | SWT.YES);
				msg.setText("Game Over");
				msg.setMessage("Your opponent has run out of tiles.  If you accept the last move, click Yes to pass.  Otherwise, click No to challenge the last move.");
				int result = msg.open();
				if (result == SWT.YES) {
					game.doPassTurn();
				}
				else {
					game.doChallenge(game.getCurrentPlayer());
				}
			}
		});
	}
	
	private void eventOccurred(final IPlayer player) {
		exec(new Runnable() {
			public void run() {
				//updateTileRack();
				// TODO support keeping tiles on board
				resetInput(true);
				redraw();
				if (player.isLocal() && player.isHuman()) {
					SoundClip.LOCAL_MOVE.play();
				}
				else {
					SoundClip.REMOTE_MOVE.play();
				}
			}
		});
	}
	
	private Runnable createRedrawRunnable() {
		return new Runnable() {
			public void run() {
				if (canvas != null && !canvas.isDisposed()) {
					canvas.redraw();
				}
			};
		};
	}

	private void createActions() {
		String text;
		
		text = "Submit Move";
		done = new Action(text, getImageDescriptor("images/done.gif")) {
			public void run() {
				if (game == null) return;
				if (game.getGameState() != GameState.IN_PROGRESS) return;
				checkTiles();
				IPlayer player = game.getCurrentPlayer();
				if (!player.isLocal()) return;
				Move move = createMove();
				if (move != null) {
					//System.out.println(move);
					
					if (game.getChallengeMode() == ChallengeMode.VOID) {
						session.validateMove(game, move);
					}
					else {
						game.doMove(move);
					}
					resetInput(true);
					redraw();
				}
			};
		};
		done.setToolTipText(text);
		done.setDescription(text);
		
		text = "Exchange Tiles";
		change = new Action(text, getImageDescriptor("images/exchange.gif")) {
			public void run() {
				if (game == null) return;
				if (game.getGameState() != GameState.IN_PROGRESS) return;
				IPlayer player = game.getCurrentPlayer();
				if (!player.isLocal()) return;
				if (game.getTilePool().size() < tileRack.capacity()) {
					MessageBox msg = new MessageBox(getShell(), SWT.ICON_INFORMATION | SWT.OK);
					msg.setText("Too Few Tiles Remaining");
					msg.setMessage("You may not exchange tiles now because there are not enough tiles left in the tile pool.");
					msg.open();
					return;
				}

				IInputValidator validator = new IInputValidator() {
					public String isValid(String text) {
						Tile[] tiles = Convert.toTiles(text);
						String rack = Convert.toString(getLocalTileRack().getTiles());
						if (tiles.length == 0) {
							return "Enter at least one letter to exchange.";
						}
						if (tiles.length > rack.length()) {
							return "Error: You have entered too many characters.";
						}
						for (int i = 0; i < tiles.length; i++) {
							Tile tile = tiles[i];
							char letter = tile.getLetter();
							String string = (letter == '?') ? "\\?" : Character.toString(letter);
							int len = rack.length();
							rack = rack.replaceFirst(string, "");
							if (rack.length() == len) {
								return "Error: One or more of the letters entered are not in your tile rack.";
							}
						}
						return null;
					}
				};
				
				InputDialog in = new InputDialog(getShell(), "Exchange Tiles", "Enter the tiles you would like to exchange.  Use a question mark, '?',  to indicate a blank tile.", "", validator);
				if (in.open() == InputDialog.OK) {
					String result = in.getValue().toLowerCase();
					if (result != null && result.length() > 0) {
						Tile[] tiles = Convert.toTiles(result);
						game.doExchangeTiles(tiles);
						resetInput(true);
					}
				}
			};
		};
		change.setToolTipText(text);
		change.setDescription(text);
		
		text = "Pass This Turn";
		pass = new Action(text, getImageDescriptor("images/pass.gif")) {
			public void run() {
				if (game == null) return;
				if (game.getGameState() == GameState.ENDED) return;
				IPlayer player = game.getCurrentPlayer();
				if (!player.isLocal()) return;
				MessageBox msg = new MessageBox(getShell(), SWT.ICON_QUESTION | SWT.NO | SWT.YES);
				msg.setText("Pass Turn");
				msg.setMessage("Are you sure you want to pass this turn?");
				int result = msg.open();
				if (result == SWT.YES) {
					game.doPassTurn();
				}
			};
		};
		pass.setToolTipText(text);
		pass.setDescription(text);
		
		text = "Challenge Last Move";
		challenge = new Action(text, getImageDescriptor("images/challenge.gif")) {
			public void run() {
				if (game == null) return;
				if (game.getGameState() == GameState.ENDED) return;
				IPlayer player = game.getCurrentPlayer();
				if (!player.isLocal()) return;
				MessageBox msg = new MessageBox(getShell(), SWT.ICON_QUESTION | SWT.NO | SWT.YES);
				msg.setText("Challenge");
				msg.setMessage("Are you sure you want to challenge the previous move?");
				int result = msg.open();
				if (result == SWT.YES) {
					game.doChallenge(player);
				}
			};
		};
		challenge.setToolTipText(text);
		challenge.setDescription(text);
		
		text = "Resign";
		resign = new Action(text, getImageDescriptor("images/resign.gif")) {
			public void run() {
				if (game == null) return;
				if (game.getGameState() == GameState.ENDED) return;
				MessageBox msg = new MessageBox(getShell(), SWT.ICON_QUESTION | SWT.NO | SWT.YES);
				msg.setText("Resign");
				msg.setMessage("Are you sure you want to resign?");
				int result = msg.open();
				if (result == SWT.YES) {
					session.getConnection().send(new ResignCommand());
				}
			};
		};
		
		text = "Abort Game";
		abort = new Action(text, getImageDescriptor("images/abort.gif")) {
			public void run() {
				if (game == null) return;
				if (game.getGameState() == GameState.ENDED) return;
				MessageBox msg = new MessageBox(getShell(), SWT.ICON_QUESTION | SWT.NO | SWT.YES);
				msg.setText("Abort Game");
				msg.setMessage("Are you sure you want to abort this game?");
				int result = msg.open();
				if (result == SWT.YES) {
					session.getConnection().send(new AbortCommand());
				}
			};
		};
		
		text = "Adjourn Game";
		adjourn = new Action(text, getImageDescriptor("images/adjourn.gif")) {
			public void run() {
				if (game == null) return;
				if (game.getGameState() == GameState.ENDED) return;
				MessageBox msg = new MessageBox(getShell(), SWT.ICON_QUESTION | SWT.NO | SWT.YES);
				msg.setText("Adjourn Game");
				msg.setMessage("Are you sure you want to adjourn this game?");
				int result = msg.open();
				if (result == SWT.YES) {
					session.getConnection().send(new AdjournCommand());
				}
			};
		};
	}
	
	private ImageDescriptor getImageDescriptor(String relativePath) {
		ScrabbleGuiPlugin plugin = ScrabbleGuiPlugin.getDefault();
		URL url = plugin.getBundle().getEntry(relativePath);
		return ImageDescriptor.createFromURL(url);
	}
	
	private void createToolbar() {
		IToolBarManager toolbar = getViewSite().getActionBars().getToolBarManager();
		toolbar.add(done);
		toolbar.add(change);
		toolbar.add(pass);
		toolbar.add(challenge);
	}
	
	private void createMenu() {
		IMenuManager menu = getViewSite().getActionBars().getMenuManager();
		menu.add(done);
		menu.add(change);
		menu.add(pass);
		menu.add(challenge);
		menu.add(abort);
		menu.add(adjourn);
		menu.add(resign);
	}

	private void updateMoveString() {
		Move move = createMove();
		String message = "";
		if (placedTiles.size() > 0) {
			message = "Invalid Move";
		}
		if (move != null) {
			MoveResult result = game.getMoveEngine().testMove(game.getBoard(), move);
			if (result != null) {
				message = result.toString();
			}
		}
		setStatusMessage(message);
	}
	
	private ITileRack getLocalTileRack() {
		IPlayer player = game.getLocalPlayer();
		if (player == null) return null;
		return player.getTileRack();
	}
	
	private void updateTileRack() {
		tileRack.clear();
		ITileRack current = getLocalTileRack();
		if (current != null) {
			tileRack.add(current.getTiles());
		}
	}
	
	public IGame getGame() {
		return game;
	}
	
	public void setGame(IGame game) {
		if (this.game != null) {
			this.game.removeGameListener(gameListener);
		}
		this.game = game;
		if (game != null) {
			game.addGameListener(gameListener);
			
			StringBuffer b = new StringBuffer();
			IPlayer[] players = game.getPlayers();
			for (int i = 0; i < players.length; i++) {
				IPlayer player = players[i];
				if (i > 0) b.append (" vs ");
				b.append(player.getName());
			}
			final String title = b.toString();
			
			// TODO unnecessary thread sync?
			exec(new Runnable() {
				public void run() {
					setPartName(title);
				};
			});
		}
		updateTileRack();
		
		MoveListView moveListView = (MoveListView)getSite().getPage().findView(MoveListView.ID);
		if (moveListView != null) {
			moveListView.setGame(game);
		}
		
		TilePoolView tilePoolView = (TilePoolView)getSite().getPage().findView(TilePoolView.ID);
		if (tilePoolView != null) {
			tilePoolView.setGame(game);
		}
		
		AnalysisView analysisView = (AnalysisView)getSite().getPage().findView(AnalysisView.ID);
		if (analysisView != null) {
			analysisView.setGame(game);
		}
	}
	
	private void resetInput(boolean clearArrow) {
		if (clearArrow) {
			arrow = null;
			drag = null;
			updateTileRack();
			placedTiles.clear();
		}
		updateMoveString();
	}
	
	// create move based on placed tiles
	private Move createMove() {
		if (game == null) return null;
		if (placedTiles.size() < 1) return null;
		
		Point[] points = new Point[placedTiles.size()];
		placedTiles.keySet().toArray(points);
		
		IBoard board = game.getBoard();
		boolean horizontal = true;
		boolean vertical = true;
		
		// vertical?
		for (int i = 1; i < points.length; i++) {
			Point point = points[i];
			if (point.x != points[i-1].x) {
				vertical = false;
				break;
			}
		}
		
		// horizontal?
		for (int i = 1; i < points.length; i++) {
			Point point = points[i];
			if (point.y != points[i-1].y) {
				horizontal = false;
				break;
			}
		}
		
		// scattered and invalid?
		if (!horizontal && !vertical) {
			return null;
		}
		
		// ambiguous?
		if (horizontal && vertical) {
			int x = points[0].x;
			int y = points[0].y;
			if (board.getTile(y,x-1).equals(Tile.NONE) && board.getTile(y,x+1).equals(Tile.NONE)) {
				horizontal = false;
			}
			if (board.getTile(y-1,x).equals(Tile.NONE) && board.getTile(y+1,x).equals(Tile.NONE)) {
				vertical = false;
			}
			// not adjacent to anything?
			if (!horizontal && !vertical) {
				return null;
			}
			// still ambiguous?
			if (horizontal && vertical) {
				// just pick one
				horizontal = false;
			}
		}
		
		Orientation orientation = vertical ? Orientation.VERTICAL : Orientation.HORIZONTAL;
		int dx = orientation.getDx();
		int dy = orientation.getDy();
		
		// find first placed tile
		int x = points[0].x;
		int y = points[0].y;
		for (int i = 1; i < points.length; i++) {
			Point point = points[i];
			if (point.x < x) x = point.x;
			if (point.y < y) y = point.y;
		}
		
		// traverse on-board tiles backwards
		while (!board.getTile(y-dy,x-dx).equals(Tile.NONE)) {
			x -= dx;
			y -= dy;
		}
		
		int row = y;
		int column = x;
		
		// build word
		StringBuffer b = new StringBuffer();
		while (true) {
			Point point = new Point(x,y);
			Tile tile = board.getTile(y,x);
			if (tile == null || tile.equals(Tile.NONE)) {
				tile = placedTiles.get(point);
			}
			if (tile == null || tile.equals(Tile.NONE)) {
				break;
			}
			b.append(tile);
			x += dx;
			y += dy;
		}
		
		Move move = new Move();
		move.setColumn(column);
		move.setRow(row);
		move.setOrientation(orientation);
		move.setWord(Convert.toTiles(b.toString()));
		return move;
	}
	
	
	
	private void redraw() {
		exec(redrawRunnable);
	}

	private void paintCanvas(PaintEvent e) {
		Point p = canvas.getSize();
		Image buffer = new Image(null, p.x, p.y);
		GC gc = new GC(buffer);
		
		int n = Math.min(p.x, p.y);
		padding = Math.max(n / 64, 1);
		border = Math.max(n / 256, 1);
		
		drawContainer(gc, new Rectangle(0, 0, p.x, p.y));
		drawDraggedTile(gc);

		e.gc.drawImage(buffer, 0, 0);
		gc.dispose();
		buffer.dispose();
		
		canvas.getDisplay().timerExec(1000, redrawRunnable);
	}
	
	private void drawDraggedTile(GC gc) {
		if (drag != null) {
			Rectangle rectangle = drag.rectangle;
			int w = rectangle.width;
			int h = rectangle.height;
			int x = mousex - w / 2;
			int y = mousey - h / 2;
			rectangle = new Rectangle(x, y, w, h);
			drawTile(gc, rectangle, drag.tile);
		}
	}
	
	private void drawContainer(GC gc, Rectangle bounds) {
		gc.setClipping(bounds);
		
		dragSources.clear();
		dragTargets.clear();
		
//		gc.setTextAntialias(SWT.ON);
//		gc.setAntialias(SWT.OFF);
//		gc.setAdvanced(false);
		
		gc.setBackground(light);
		gc.fillRectangle(bounds);
		
		if (game == null) {
			return;
		}
		
		IBoard board = game.getBoard();
		
		int x = bounds.x;
		int y = bounds.y;
		int w = bounds.width;
		int h = bounds.height;
		
		int coord = showCoordinates ? coordinates : 0;
		
		// compute tile size
		int height = bounds.height - (4 * border + 3 * padding + 1 * coord);
		int width = bounds.width - (2 * border + 2 * padding + 1 * coord);
		tileSize = Math.min(width / board.getWidth(), height / (board.getHeight() + 1));
		
		// compute header size and position
		int hh = tileSize + border * 2;
		int hw = tileSize * board.getWidth() + border * 2;
		int hx = (w - hw + coord) / 2;
		int hy = y + padding;
		drawHeader(gc, new Rectangle(hx, hy, hw, hh));
		
		// compute board area size and position
		int cx = x + padding;
		int cy = y + hh + padding * 2;
		int cw = w - padding * 2;
		int ch = h - hh - padding * 3;
		drawCenter(gc, new Rectangle(cx, cy, cw, ch));
	}
	
	private void drawHeader(GC gc, Rectangle bounds) {
		gc.setClipping(bounds);
		
		int x = bounds.x;
		int y = bounds.y;
		int w = bounds.width;
		int h = bounds.height;
		
		int rw = 7 * tileSize + border * 2;
		int rh = tileSize + border * 2;
		int rx = (w - rw) / 2 + x;
		drawTileRack(gc, new Rectangle(rx, y, rw, rh));
		
		IPlayer[] players = game.getPlayers();
		if (players.length >= 1) {
			IPlayer player = players[0];
			int sw = rx - x - padding;
			drawPlayerStats(gc, new Rectangle(x, y, sw, h), player, SWT.LEFT);
		}
		if (players.length >= 2) {
			IPlayer player = players[1];
			int sw = rx - x - padding;
			int sx = w - sw + x;
			drawPlayerStats(gc, new Rectangle(sx, y, sw, h), player, SWT.RIGHT);
		}
	}
	
	private void drawPlayerStats(GC gc, Rectangle bounds, IPlayer player, int align) {
		gc.setClipping(bounds);
		
		int x = bounds.x;
		int y = bounds.y;
		int w = bounds.width;
		int h = bounds.height;
		int p = w/32;
		
		Color fillColor = null;
		Color handleColor = null;
		Color scoreColor = null;
		Color clockColor = null;
		
		if (game.getGameState() == GameState.ENDED) {
			if (player == game.getWinner()) {
				fillColor = new Color(null,0,164,0);
				handleColor = new Color(null,255,255,255);
				scoreColor = new Color(null,255,255,255);
				clockColor = new Color(null,255,255,255);
			}
			else {
				fillColor = new Color(null,164,0,0);
				handleColor = new Color(null,255,255,255);
				scoreColor = new Color(null,255,255,255);
				clockColor = new Color(null,255,255,255);
			}
		}
		else {
			if (player == game.getCurrentPlayer()) {
				fillColor = new Color(null,0,0,192);
				handleColor = new Color(null,205,205,255);
				scoreColor = new Color(null,255,255,255);
				clockColor = new Color(null,215,215,215);
			}
			else {
				fillColor = new Color(null,153,153,153);
				handleColor = new Color(null, 0, 0, 128);
				scoreColor = new Color(null,0,0,0);
				clockColor = new Color(null,0,0,0);
			}
		}
		
		// fill
		gc.setBackground(fillColor);
		gc.fillRoundRectangle(x, y, w, h, border+1, border+1);
		
		// draw handle
		int n = h / 4;
		Font font = new Font(null, "Arial", n, SWT.BOLD);
		String s = player.getName();
		gc.setFont(font);
		gc.setForeground(handleColor);
		Point extent = gc.stringExtent(s);
		if (align == SWT.LEFT) {
			gc.drawString(s, x+p, y, true);
		}
		else {
			gc.drawString(s, x + w - p - extent.x, y, true);
		}
		font.dispose();
		
		// save width
		int i = extent.x + p;
		
		// draw clock
		font = new Font(null, "Courier New", n, SWT.BOLD);
		s = player.getClock().toString();
		gc.setFont(font);
		gc.setForeground(clockColor);
		extent = gc.stringExtent(s);
		if (align == SWT.LEFT) {
			gc.drawString(s, x+p, y + h - extent.y, true);
		}
		else {
			gc.drawString(s, x + w - p - extent.x, y + h - extent.y, true);
		}
		font.dispose();
		
		// draw score
		font = new Font(null, "Arial", h/2, SWT.BOLD);
		s = Integer.toString(player.getScore());
		gc.setFont(font);
		gc.setForeground(scoreColor);
		extent = gc.stringExtent(s);
		if (align == SWT.LEFT) {
			gc.drawString(s, x + i + (w-i)/2 - extent.x/2, y + h/2 - extent.y/2, true);
		}
		else {
			gc.drawString(s, x + (w-i)/2 - extent.x/2, y + h/2 - extent.y/2, true);
		}
		font.dispose();
		
		fillColor.dispose();
		handleColor.dispose();
		scoreColor.dispose();
		clockColor.dispose();
	}
	
	private void drawTileRack(GC gc, Rectangle bounds) {
		gc.setClipping(bounds);
		
		int x = bounds.x;
		int y = bounds.y;
		int w = bounds.width;
		int h = bounds.height;
		
		gc.setBackground(tileRackBorder);
		gc.fillRoundRectangle(x, y, w, h, border+1, border+1);
		gc.setBackground(tileRackFill);
		gc.fillRectangle(x + border, y + border, w - border * 2, h - border * 2);
		
		//TileRack tileRack = getLocalTileRack();
		if (tileRack == null) return;
		Tile[] tiles = tileRack.getTiles();
		
		x += border;
		y += border;
		for (int i = 0; i < tiles.length; i++) {
			Tile tile = tiles[i];
			Rectangle rectangle = new Rectangle(x, y, tileSize, tileSize);
			
			DragSource source = new DragSource();
			source.rectangle = rectangle;
			source.x = i;
			source.y = -1;
			source.tile = tile;
			dragSources.put(rectangle, source);
			
			DragTarget target = new DragTarget();
			target.rectangle = rectangle;
			target.x = i;
			target.y = -1;
			dragTargets.add(target);
			
			drawTile(gc, rectangle, tile);
			x += tileSize;
		}
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
		
		if (tile.isWild()) {
			if (!bounds.contains(mousex, mousey)) {
				return;
			}
		}
		
		Font font = new Font(null, tileFont, s/2+1, SWT.BOLD);
		String string = Character.toString(tile.getLetter()).toUpperCase();
		gc.setFont(font);
		Point extent = gc.stringExtent(string);
		int sx = x + w / 2 - extent.x / 2 - n; //n * 2; //w / 2 - extent.x / 2 - s/8;
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
	
	private void drawCenter(GC gc, Rectangle bounds) {
		gc.setClipping(bounds);
		
		int x = bounds.x;
		int y = bounds.y;
		int w = bounds.width;
		int h = bounds.height;
		
		gc.setFont(coordinateFont);
		int coord = showCoordinates ? coordinates : 0;
		int n = coord + border * 2;
		
		IBoard board = game.getBoard();
		int t = Math.min((w - n) / board.getWidth(), (h - n) / board.getHeight());
		int bw = t * board.getWidth() + n;
		int bh = t * board.getHeight() + n;
		int px = (w - bw) / 2;
		int py = (h - bh) / 2;
		int rx = (w - bw) % 2;
		int ry = (h - bh) % 2;
		
		drawBoardArea(gc, new Rectangle(x+px, y+0, w-px*2-rx, h-py*2-ry), coord);
	}
	
	private void drawBoardArea(GC gc, Rectangle bounds, int coord) {
		gc.setClipping(bounds);
		
		int x = bounds.x;
		int y = bounds.y;
		int w = bounds.width;
		int h = bounds.height;
		if (showCoordinates) {
			drawCoordinates(gc, bounds, coord);
		}
		drawBoard(gc, new Rectangle(x+coord,y+coord,w-coord,h-coord));
	}
	
	private void drawCoordinates(GC gc, Rectangle bounds, int coord) {
		gc.setClipping(bounds);
		
		drawCoordinates(game.getBoard(), gc, bounds.x + border + coord, bounds.y + border + coord, bounds.width - border*2 - coord, 0);
	}
	
	private void drawBoard(GC gc, Rectangle bounds) {
		gc.setClipping(bounds);
		
		int padding = border;
		int x = bounds.x + padding;
		int y = bounds.y + padding;
		int w = bounds.width - padding * 2;
		int h = bounds.height - padding * 2;
		drawBoardBorder(gc, bounds);
		drawBoardGrid(gc, new Rectangle(x, y, w, h));
	}
	
	private void drawBoardBorder(GC gc, Rectangle bounds) {
		gc.setClipping(bounds);
		
		int x = bounds.x;
		int y = bounds.y;
		int w = bounds.width;
		int h = bounds.height;
		gc.setBackground(boardBorder);
		gc.fillRoundRectangle(x, y, w, h, border+1, border+1);
	}
	
	private void drawBoardGrid(GC gc, Rectangle bounds) {
		gc.setClipping(bounds);
		
		drawBoard(game.getBoard(), gc, bounds.x, bounds.y, bounds.width);
	}
	
	private void drawCoordinates(IBoard b, GC gc, int xo, int yo, int size, int csize) {
		gc.setBackground(light);
		gc.setForeground(coordinateColor);
		gc.setFont(coordinateFont);
		
		int xs = size / b.getWidth();
		int ys = size / b.getHeight();
		int pdx = 4 + gc.stringExtent("X").x;
		int pdy = 4;
		
		for (int y = 0; y < b.getHeight(); y++) {
			String s = Character.toString((char)(y + 'A'));
			int x = 0;
			int px = xo + x * xs - gc.stringExtent(s).x/2 - pdx;
			int py = yo + y * ys + ys/2 - gc.stringExtent(s).y/2;
			gc.drawString(s, px, py, true);
		}
		
		for (int x = 0; x < b.getWidth(); x++) {
			String s = Integer.toString(x+1);
			int y = 0;
			int px = xo + x * xs + xs/2 - gc.stringExtent(s).x/2;
			int py = yo + y * ys - gc.stringExtent(s).y - pdy;
			gc.drawString(s, px, py, true);
		}
	}
	
	private void drawArrow(GC gc, int x, int y, int s) {
		s += (s%2==0) ? 1 : 0;
		Orientation o = arrow.o;
		int[] a;
		
		if (o == Orientation.VERTICAL) {
			int d = s / 4;
			int t = s / 10;
			int x3 = x + s / 2;
			int x2 = x3 - t;
			int x4 = x3 + t;
			int x1 = x3 - d;
			int x5 = x3 + d;
			
			int y1 = y + s / 5;
			int y3 = y + s - s / 5;
			int y2 = y3 - d;
			
			a = new int[] {
				x2,y1,
				x4,y1,
				x4,y2,
				x5,y2,
				x3,y3,
				x1,y2,
				x2,y2,
				x2,y1
			};
		}
		else {
			int d = s / 4;
			int t = s / 10;
			int y3 = y + s / 2;
			int y2 = y3 - t;
			int y4 = y3 + t;
			int y1 = y3 - d;
			int y5 = y3 + d;
			
			int x1 = x + s / 5;
			int x3 = x + s - s / 5;
			int x2 = x3 - d;
			
			a = new int[] {
				x1,y2,
				x1,y4,
				x2,y4,
				x2,y5,
				x3,y3,
				x2,y1,
				x2,y2,
				x1,y2
			};
		}
		
		gc.setForeground(arrowForeground);
		gc.setBackground(arrowBackground);
		gc.fillPolygon(a);
		gc.drawPolygon(a);
	}
	
	private void drawBoard(IBoard b, GC gc, int xo, int yo, int size) {
		squareLocations.clear();
		int tileSize = size / b.getWidth(); // TODO handle non-square boards
		for (int y = 0; y < b.getHeight(); y++) {
			for (int x = 0; x < b.getWidth(); x++) {
				int px = xo + x * tileSize;
				int py = yo + y * tileSize;
				Rectangle rectangle = new Rectangle(px, py, tileSize, tileSize);
				
				DragTarget target = new DragTarget();
				target.rectangle = rectangle;
				target.x = x;
				target.y = y;
				dragTargets.add(target);
				
				squareLocations.add(new Square(x, y, rectangle));
				drawTile(b, gc, x, y, px, py, tileSize-1);
			}
		}
	}
	
	private void drawTile(IBoard b, GC gc, int x, int y, int xo, int yo, int size) {
		gc.setClipping(xo, yo, size+1, size+1);
		
		Color c = background;
		if (b.getLetterMultiplier(y, x) == 2) c = doubleLetter;
		else if (b.getLetterMultiplier(y, x) == 3) c = tripleLetter;
		else if (b.getLetterMultiplier(y, x) == 4) c = quadLetter;
		else if (b.getWordMultiplier(y, x) == 2) c = doubleWord;
		else if (b.getWordMultiplier(y, x) == 3) c = tripleWord;
		else if (b.getWordMultiplier(y, x) == 4) c = quadWord;
		gc.setBackground(c);
		gc.fillRectangle(xo, yo, size, size);
		
		c = light;
		if (b.getLetterMultiplier(y, x) == 2) c = doubleLetterLight;
		else if (b.getLetterMultiplier(y, x) == 3) c = tripleLetterLight;
		else if (b.getLetterMultiplier(y, x) == 4) c = quadLetterLight;
		else if (b.getWordMultiplier(y, x) == 2) c = doubleWordLight;
		else if (b.getWordMultiplier(y, x) == 3) c = tripleWordLight;
		else if (b.getWordMultiplier(y, x) == 4) c = quadWordLight;
		gc.setForeground(c);
		gc.drawLine(xo, yo, xo+size, yo);
		gc.drawLine(xo, yo, xo, yo+size);
		
		gc.setForeground(dark);
		gc.drawLine(xo+size, yo+size, xo+size, yo);
		gc.drawLine(xo+size, yo+size, xo, yo+size);
		
		Tile tile = b.getTile(y, x);
		
		if (tile.equals(Tile.NONE)) {
			for (Iterator i = placedTiles.entrySet().iterator(); i.hasNext();) {
				Map.Entry entry = (Map.Entry) i.next();
				Point point = (Point)entry.getKey();
				if (point.x == x && point.y == y) {
					tile = (Tile)entry.getValue();
					
					DragSource source = new DragSource();
					source.rectangle = new Rectangle(xo, yo, size+1, size+1);
					source.x = x;
					source.y = y;
					source.tile = tile;
					dragSources.put(source.rectangle, source);
				}
			}
		}
		
		boolean highlight = false;
		IGameAction action = game.getLastAction();
		if (action != null && action instanceof MoveAction) {
			MoveAction moveAction = (MoveAction)action;
			MoveResult result = moveAction.getMoveResult();
			Tile[] tiles = result.getPreviousBoardState();
			Move move = result.getMove();
			Orientation orientation = move.getOrientation();
			int dx = orientation.getDx();
			int dy = orientation.getDy();
			int mx = move.getColumn();
			int my = move.getRow();
			for (int i = 0; i < tiles.length; i++) {
				Tile t = tiles[i];
				if (mx == x && my == y && t.equals(Tile.NONE)) {
					highlight = true;
				}
				mx += dx;
				my += dy;
			}
		}
		
		drawTile(gc, new Rectangle(xo, yo, size+1, size+1), tile, highlight);
		
		if (arrow != null && arrow.x == x && arrow.y == y) {
			drawArrow(gc, xo, yo, size);
		}
	}
		

	
	private static class Square {
		int x;
		int y;
		Rectangle r;
		Square(int x, int y, Rectangle r) {
			this.x = x;
			this.y = y;
			this.r = r;
		}
	}
	
	private class Arrow {
		int x;
		int y;
		Orientation o;
		Arrow(int x, int y, Orientation o) {
			this.x = x;
			this.y = y;
			this.o = o;
		}
		void flip() {
			o = (o == Orientation.VERTICAL) ? Orientation.HORIZONTAL : Orientation.VERTICAL;
		}
		void advance() {
			do {
				x += o.getDx();
				y += o.getDy();
			} while (!game.getBoard().getTile(y,x).equals(Tile.NONE) || placedTiles.get(new Point(x,y)) != null);
		}
		void backup() {
			do {
				x -= o.getDx();
				y -= o.getDy();
			} while (!game.getBoard().getTile(y,x).equals(Tile.NONE));
		}
	}
	
	private static class DragSource {
		Rectangle rectangle;
		Tile tile;
		int x;
		int y;
	}
	
	private static class DragTarget {
		Rectangle rectangle;
		int x;
		int y;
	}
	
	private void exec(Runnable runnable) {
		if (canvas == null) return;
		if (canvas.isDisposed()) return;
		Display display = canvas.getDisplay();
		if (!display.isDisposed()) {
			display.asyncExec(runnable);
		}
	}
	
}
