package com.michaelfogleman.wordwarrior.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import com.michaelfogleman.wordwarrior.ScrabblePlugin;
import com.michaelfogleman.wordwarrior.model.IEventListener;
import com.michaelfogleman.wordwarrior.model.IEventManager;
import com.michaelfogleman.wordwarrior.protocol.Connection;
import com.michaelfogleman.wordwarrior.protocol.IConnectionListener;
import com.michaelfogleman.wordwarrior.protocol.response.*;

public class ConsoleView extends ViewPart {
	
	public static final String ID = ConsoleView.class.getName();
	
	private CommandInterpreter interpreter;
	private StyledText output;
	private Text input;
	private Color color;
	private Color channelColor;
	private Color gameColor;
	private Color errorColor;
	private Color chatBackground;
	private Color echoColor;
	
	private IEventManager eventManager;
	private IEventListener eventListener;
	private IResponseHandler handler;
	private IConnectionListener listener;
	
	private void init() {
		eventManager = ScrabblePlugin.getDefault().getEventManager();
		eventListener = createEventListener();
		eventManager.addEventListener(eventListener);
		
		color = new Color(null, 0, 0, 0);
		channelColor = new Color(null, 0, 0, 255);
		errorColor = new Color(null, 255, 0, 0);
		chatBackground = new Color(null, 192, 192, 192);
		echoColor = new Color(null, 128, 128, 128);
		gameColor = new Color(null, 0, 128, 0);
	}
	
	public void createPartControl(Composite parent) {
		init();
		
		interpreter = new CommandInterpreter();
		
		GridLayout layout = new GridLayout(1, true);
		parent.setLayout(layout);
		Font font = new Font(null, "Courier New", 10, SWT.BOLD);
		output = new StyledText(parent, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY | SWT.V_SCROLL | SWT.WRAP);
		//output.setBackground(parent.getBackground());
		output.setTextLimit(2048);
		output.setFont(font);
		output.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL));
		
		input = new Text(parent, SWT.SINGLE | SWT.BORDER);
		input.setFont(font);
		input.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL  | GridData.GRAB_HORIZONTAL));
		input.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
					String text = input.getText();
					input.setText("");
					append(text, echoColor, null);
					interpret(text);
				}
			}
		});
		
		handler = new ResponseAdapter() {
			public void handle(AsitisResponse response) {
				String text = response.getText();
				append(text, color, null);
			}
			public void handle(HelpResponse response) {
				String text = response.getText();
				append(text, color, null);
			}
			public void handle(ChannelResponse response) {
				String text = response.toString();
				append(text, channelColor, null);
			}
			public void handle(SayResponse response) {
			}
			public void handle(TellResponse response) {
				String text = response.toString();
				append(text, null, chatBackground);
			}
			public void handle(LoginResponse response) {
				String text = response.getMessage();
				append(text, color, null);
			}
			public void handle(KibitzResponse response) {
				String text = response.toString();
				append(text, gameColor, null);
			}
			public void handle(WhisperResponse response) {
				String text = response.toString();
				append(text, gameColor, null);
			}
		};
		getConnection().addResponseHandler(handler);
		
		listener = new IConnectionListener() {
			public void connectionClosed(Connection connection) {
				String text = "Your connection to the remote server has been closed.";
				append(text, errorColor, null);
			}
		};
		getConnection().addConnectionListener(listener);
	}
	
	private IEventListener createEventListener() {
		return new IEventListener() {
			public void eventPublished(String message) {
				append(message, color, null);
			}
		};
	}
	
	public void dispose() {
		eventManager.removeEventListener(eventListener);
		getConnection().removeConnectionListener(listener);
		getConnection().removeResponseHandler(handler);
		color.dispose();
		channelColor.dispose();
		errorColor.dispose();
		chatBackground.dispose();
		echoColor.dispose();
		gameColor.dispose();
		input.dispose();
		output.dispose();
		super.dispose();
	}
	
	private Connection getConnection() {
		return ScrabblePlugin.getDefault().getSession().getConnection();
	}
	
	private void append(final String text, final Color foreground, final Color background) {
		if (output == null || output.isDisposed()) return;
		Display display = output.getDisplay();
		if (display != null && !display.isDisposed()) {
			display.asyncExec(new Runnable() {
				public void run() {
					if (!output.isDisposed()) {
						StyleRange range = new StyleRange(output.getCharCount(), text.length(), foreground, background);
						output.append(text);
						output.append("\n");
						output.setTopIndex(output.getLineCount());
						output.setStyleRange(range);
					}
				}
			});
		}
	}
	
	private void interpret(String input) {
		interpreter.interpret(input);
	}
	
	public void setFocus() {
		input.setFocus();
		input.forceFocus();
	}

}
