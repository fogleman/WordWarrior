package com.michaelfogleman.wordwarrior.gui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import com.michaelfogleman.wordwarrior.ScrabblePlugin;
import com.michaelfogleman.wordwarrior.model.Settings;
import com.michaelfogleman.wordwarrior.protocol.Connection;
import com.michaelfogleman.wordwarrior.protocol.command.MatchCommand;
import com.michaelfogleman.wordwarrior.protocol.command.SetAllCommand;

public class MatchDialog extends Dialog {
	
	private Settings settings = ScrabblePlugin.getDefault().getSession().getSettings();
	
	private Label handleLabel;
	private Text handleText;
	private Button searchButton;
	private Label timeLabel;
	private Label incrementLabel;
	private Text timeText;
	private Text incrementText;
	private Label challengeLabel;
	private Label dictionaryLabel;
	private Combo challengeCombo;
	private Combo dictionaryCombo;
	private Button ratedButton;
	private Button privateButton;
	private Button noescapeButton;
	private Button saveButton;
	private String handle;
	
	public MatchDialog(Shell parentShell) {
		this(parentShell, null);
	}
	
	public MatchDialog(Shell parentShell, String handle) {
		super(parentShell);
		this.handle = handle;
	}
	
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite)super.createDialogArea(parent);
		
		GridLayout layout = new GridLayout(4, true);
		layout.marginWidth = 10;
		layout.marginHeight = 6;
		composite.setLayout(layout);

		GridData data;
 		
		handleLabel = new Label(composite, SWT.LEFT);
		handleLabel.setText("Handle:");
		data = new GridData();
		data.horizontalSpan = 4;
		handleLabel.setLayoutData(data);
		
		handleText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		data = new GridData();
		data.horizontalSpan = 3;
		data.horizontalAlignment = SWT.FILL;
		handleText.setLayoutData(data);
		if (handle != null) {
			handleText.setText(handle);
		}
		
		searchButton = new Button(composite, SWT.PUSH);
		searchButton.setText("Search...");
		data = new GridData();
		data.horizontalSpan = 1;
		searchButton.setLayoutData(data);
		
		searchButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				close();
				UserSearchDialog dialog = new UserSearchDialog(getShell());
				dialog.open();
			}
		});
		
		timeLabel = new Label(composite, SWT.LEFT);
		timeLabel.setText("Initial Time:");
		data = new GridData();
		data.horizontalSpan = 2;
		timeLabel.setLayoutData(data);
		
		incrementLabel = new Label(composite, SWT.LEFT);
		incrementLabel.setText("Time Increment:");
		data = new GridData();
		data.horizontalSpan = 2;
		incrementLabel.setLayoutData(data);
		
		timeText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		data = new GridData();
		data.horizontalSpan = 2;
		data.horizontalAlignment = SWT.FILL;
		timeText.setLayoutData(data);
		timeText.setText(settings.getString("user.time", ""));
		
		incrementText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		data = new GridData();
		data.horizontalSpan = 2;
		data.horizontalAlignment = SWT.FILL;
		incrementText.setLayoutData(data);
		incrementText.setText(settings.getString("user.increment", ""));
		
		challengeLabel = new Label(composite, SWT.LEFT);
		challengeLabel.setText("Challenge Mode:");
		data = new GridData();
		data.horizontalSpan = 2;
		challengeLabel.setLayoutData(data);
		
		dictionaryLabel = new Label(composite, SWT.LEFT);
		dictionaryLabel.setText("Dictionary:");
		data = new GridData();
		data.horizontalSpan = 2;
		dictionaryLabel.setLayoutData(data);
		
		challengeCombo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		data = new GridData();
		data.horizontalSpan = 2;
		data.horizontalAlignment = SWT.FILL;
		challengeCombo.setLayoutData(data);
		challengeCombo.setItems(new String[] {
			"SINGLE (No Penalty)",
			"DOUBLE (Lose Turn)",
			"FIVE POINTS (Lose 5 Points)",
			"VOID (No Challenges)"
		});
		challengeCombo.select(settings.getInt("user.challenge", 0));
		
		dictionaryCombo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		data = new GridData();
		data.horizontalSpan = 2;
		data.horizontalAlignment = SWT.FILL;
		dictionaryCombo.setLayoutData(data);
		dictionaryCombo.setItems(new String[] {
			"TWL06 (English - American)",
			"SOWPODS (English - Other)",
			"ODS (French)",
			"LOC2000 (Romanian)",
			"MULTI (Aggregate)",
			"PARO (Italian)",
			"SWL (Dutch)"
		});
		dictionaryCombo.select(settings.getInt("user.dictionary", 0));
		
		ratedButton = new Button(composite, SWT.CHECK);
		ratedButton.setText("Rated");
		ratedButton.setSelection(settings.getBoolean("user.rated"));
		
		privateButton = new Button(composite, SWT.CHECK);
		privateButton.setText("Private");
		privateButton.setSelection(settings.getBoolean("user.private"));
		
		noescapeButton = new Button(composite, SWT.CHECK);
		noescapeButton.setText("No Escape");
		data = new GridData();
		data.horizontalSpan = 2;
		noescapeButton.setLayoutData(data);
		noescapeButton.setSelection(settings.getBoolean("user.noescape"));
		
		saveButton = new Button(composite, SWT.CHECK);
		saveButton.setText("Save these settings as my defaults");
		data = new GridData();
		data.horizontalSpan = 4;
		saveButton.setLayoutData(data);
	
		return composite;
	}
	
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Match a Player");
	}
	
	protected void okPressed() {
		Settings oldSettings = new Settings();
		Settings newSettings = new Settings();
		
		String validationMessage = validate();
		if (validationMessage != null) {
			MessageBox msg = new MessageBox(this.getShell(), SWT.ICON_ERROR | SWT.OK);
			msg.setText("Invalid Input");
			msg.setMessage(validationMessage);
			msg.open();
			return;
		}
		
		oldSettings.set("user.time", settings.getString("user.time"));
		oldSettings.set("user.increment", settings.getString("user.increment"));
		oldSettings.set("user.challenge", settings.getString("user.challenge"));
		oldSettings.set("user.dictionary", settings.getString("user.dictionary"));
		oldSettings.set("user.rated", settings.getString("user.rated"));
		oldSettings.set("user.private", settings.getString("user.private"));
		oldSettings.set("user.noescape", settings.getString("user.noescape"));
		
		newSettings.set("user.time", timeText.getText());
		newSettings.set("user.increment", incrementText.getText());
		newSettings.set("user.challenge", challengeCombo.getSelectionIndex());
		newSettings.set("user.dictionary", dictionaryCombo.getSelectionIndex());
		newSettings.set("user.rated", ratedButton.getSelection());
		newSettings.set("user.private", privateButton.getSelection());
		newSettings.set("user.noescape", noescapeButton.getSelection());
		
		Connection connection = ScrabblePlugin.getDefault().getSession().getConnection();
		if (connection != null) {
			if (!newSettings.equals(oldSettings)) {
				settings.setAll(newSettings);
				connection.send(new SetAllCommand(newSettings));
			}
			connection.send(new MatchCommand(handleText.getText()));
			if (!saveButton.getSelection() && !oldSettings.equals(newSettings)) {
				settings.setAll(oldSettings);
				connection.send(new SetAllCommand(oldSettings));
			}
		}
		
		super.okPressed();
	}
	
	private String validate() {
		String handle = handleText.getText();
		if (handle == null || handle.length() == 0) {
			return "The Handle field must not be blank.";
		}
		
		int time;
		try {
			time = Integer.parseInt(timeText.getText());
		} catch (NumberFormatException e) {
			return "The Initial Time field must be an integer and may only contain characters 0-9.";
		}
		if (time < 1 || time > 60) {
			return "The Initial Time field must be an integer between 1 and 60, inclusive.";
		}
		
		int increment;
		try {
			increment = Integer.parseInt(incrementText.getText());
		} catch (NumberFormatException e) {
			return "The Time Increment field must be an integer and may only contain characters 0-9.";
		}
		if (increment < 0 || increment > 10) {
			return "The Time Increment field must be an integer between 0 and 10, inclusive.";
		}
		
		return null;
	}

}
