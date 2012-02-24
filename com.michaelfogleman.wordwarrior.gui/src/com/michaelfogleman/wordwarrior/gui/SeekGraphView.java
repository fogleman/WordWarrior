package com.michaelfogleman.wordwarrior.gui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
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
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

import com.michaelfogleman.wordwarrior.ScrabblePlugin;
import com.michaelfogleman.wordwarrior.model.Seek;
import com.michaelfogleman.wordwarrior.model.Session;
import com.michaelfogleman.wordwarrior.protocol.Connection;
import com.michaelfogleman.wordwarrior.protocol.IConnectionListener;
import com.michaelfogleman.wordwarrior.protocol.command.SoughtCommand;
import com.michaelfogleman.wordwarrior.protocol.response.ClearSeeksResponse;
import com.michaelfogleman.wordwarrior.protocol.response.IResponseHandler;
import com.michaelfogleman.wordwarrior.protocol.response.ResponseAdapter;
import com.michaelfogleman.wordwarrior.protocol.response.SeekResponse;
import com.michaelfogleman.wordwarrior.protocol.response.UnseekResponse;

public class SeekGraphView extends ViewPart {
	
	public static final String ID = SeekGraphView.class.getName();
	
	private Canvas canvas;
	private Color background;
	private Color color;
	private Color seekColor;
	private Font textFont;
	private Font numberFont;
	private Map<String, Seek> seeks;
	private Map<Seek, Rectangle> positions;
	private Seek hoveredSeek;
	
	private IResponseHandler handler;
	private IConnectionListener listener;
	
	public void createPartControl(Composite parent) {
		init();
		
		canvas = new Canvas(parent, SWT.NO_BACKGROUND);
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				SeekGraphView.this.paintControl(e);
			}
		});
		canvas.addMouseMoveListener(new MouseMoveListener() {
			public void mouseMove(MouseEvent e) {
				SeekGraphView.this.mouseMove(e);
			}
		});
		canvas.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
				SeekGraphView.this.mouseClick(e);
			}
		});
		positions = new HashMap<Seek, Rectangle>();
		seeks = new HashMap<String, Seek>();
		
		Connection connection = getConnection();
		
		handler = new ResponseAdapter() {
			public void handle(SeekResponse response) {
				Seek seek = response.getSeek();
				synchronized (seeks) {
					seeks.put(seek.getHandle(), seek);
				}
				redraw();
			}
			public void handle(UnseekResponse response) {
				synchronized (seeks) {
					seeks.remove(response.getHandle());
				}
				redraw();
			}
			public void handle(ClearSeeksResponse response) {
				synchronized (seeks) {
					seeks.clear();
				}
				redraw();
			}
		};
		connection.addResponseHandler(handler);
		
		listener = new IConnectionListener() {
			public void connectionClosed(Connection connection) {
				synchronized (seeks) {
					seeks.clear();
				}
				redraw();
			}
		};
		connection.addConnectionListener(listener);
		
		connection.send(new SoughtCommand());
	}
	
	private Connection getConnection() {
		return ScrabblePlugin.getDefault().getSession().getConnection();
	}
	
	private void redraw() {
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
	
	private Seek getSeek(int x, int y) {
		Seek result = null;
		for (Seek seek : positions.keySet()) {
			Rectangle r = positions.get(seek);
			if (r.contains(x, y)) {
				result = seek;
			}
		}
		return result;
	}
	
	private void mouseClick(MouseEvent e) {
		Seek seek = getSeek(e.x, e.y);
		if (seek != null) {
			Session session = ScrabblePlugin.getDefault().getSession();
			session.startGame(seek);
		}
	}
	
	private void mouseMove(MouseEvent e) {
		Seek seek = getSeek(e.x, e.y);
		if (seek != hoveredSeek) {
			hoveredSeek = seek;
			String text = seek == null ? null : seek.toString();
			canvas.setToolTipText(text);
		}
	}
	
	public void setFocus() {
		canvas.setFocus();
	}
	
	private void init() {
		color = new Color(null, 0, 0, 0);
		background = getViewSite().getShell().getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
		seekColor = new Color(null, 0, 0, 96);
		textFont = new Font(null, "Arial", 10, SWT.BOLD);
		numberFont = new Font(null, "Arial", 9, SWT.NORMAL);
	}
	
	public void dispose() {
		getConnection().removeConnectionListener(listener);
		getConnection().removeResponseHandler(handler);
		canvas.dispose();
		color.dispose();
		seekColor.dispose();
		background.dispose();
		textFont.dispose();
		numberFont.dispose();
		super.dispose();
	}
	
	private void paintControl(PaintEvent e) {
		Point size = canvas.getSize();
		Image buffer = new Image(null, size.x, size.y);
		GC gc = new GC(buffer);
		
		gc.setBackground(background);
		gc.fillRectangle(canvas.getBounds());
		
		int w = canvas.getBounds().width;
		int h = canvas.getBounds().height;
		
		int xo = 50;//w / 10;
		int yo = 50;//h / 10;
		int x1 = xo;
		int y1 = h - yo;
		int x2 = w - xo / 2;
		int y2 = yo / 2;
		int pad = 5;
		
		gc.setAdvanced(true);
		gc.setAntialias(SWT.ON);
		gc.setForeground(color);
		gc.setLineWidth(1);
		gc.setLineCap(SWT.CAP_FLAT);
		gc.drawLine(x1,y1,x1,y2);
		gc.drawLine(x1,y2,x1-pad,y2+pad);
		gc.drawLine(x1,y1,x2,y1);
		gc.drawLine(x2,y1,x2-pad,y1-pad);
		
		gc.setFont(textFont);
		String s = "rating";
		Point p = gc.stringExtent(s);
		gc.drawString(s, x1-p.x/2, y2-p.y-pad);
		s = "time";
		p = gc.stringExtent(s);
		gc.drawString(s, x2-p.x, y1+pad);
		gc.setFont(numberFont);
		
		int y = y1;
		for (int r = 0; r <= 2000; r += 500) {
			if (r > 0) {
				gc.drawLine(x1-pad,y,x1,y);
				s = Integer.toString(r);
				p = gc.stringExtent(s);
				gc.drawString(s, x1-pad-pad-p.x, y-p.y/2);
			}
			y -= (y1-y2) / 5;
		}
		
		int x = x1;
		for (int t = 0; t <= 50; t += 10) {
			if (t > 0) {
				gc.drawLine(x,y1+pad,x,y1);
				s = Integer.toString(t);
				p = gc.stringExtent(s);
				gc.drawString(s, x-p.x/2, y1+pad+pad);
			}
			x += (x2-x1) / 7;
		}
		
		positions.clear();
		gc.setBackground(seekColor);
		gc.setForeground(seekColor);
		int cw = 10;//Math.min(w,h) / 60;
		
		synchronized (seeks) {
			for (Seek seek : seeks.values()) {
				int time = seek.getTime();
				int rating = seek.getRating();
				x = (int)((x2-x1) * (time / 70.0)) + x1;
				y = (int)((y2-y1) * (rating / 2500.0)) + y1;
				
				if (seek.isRated()) {
					gc.fillOval(x-cw/2,y-cw/2,cw,cw);
				}
				else {
					gc.drawOval(x-cw/2,y-cw/2,cw,cw);
				}
				
				Rectangle r = new Rectangle(x-cw/2,y-cw/2,cw,cw);
				positions.put(seek, r);
			}
		}
		
		e.gc.drawImage(buffer, 0, 0);
		gc.dispose();
		buffer.dispose();
	}

}
