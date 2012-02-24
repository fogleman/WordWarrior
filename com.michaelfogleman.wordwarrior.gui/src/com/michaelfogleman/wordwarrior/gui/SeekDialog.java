package com.michaelfogleman.wordwarrior.gui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import com.michaelfogleman.wordwarrior.ScrabblePlugin;
import com.michaelfogleman.wordwarrior.model.Settings;
import com.michaelfogleman.wordwarrior.protocol.Connection;
import com.michaelfogleman.wordwarrior.protocol.command.SeekCommand;
import com.michaelfogleman.wordwarrior.protocol.command.SetAllCommand;

public class SeekDialog extends Dialog {
	
	private Settings settings = ScrabblePlugin.getDefault().getSession().getSettings();
	
	private Label timeLabel;
	private Label incrementLabel;
	private Text timeText;
	private Text incrementText;
	private Label challengeLabel;
	private Label dictionaryLabel;
	private Combo challengeCombo;
	private Combo dictionaryCombo;
	private Label minimumRatingLabel;
	private Label maximumRatingLabel;
	private Text minimumRating;
	private Text maximumRating;
	private Button ratedButton;
	private Button provisionalButton;
	private Button fairPlayButton;
	private Button noescapeButton;
	private Button saveButton;
	
	public SeekDialog(Shell parentShell) {
		super(parentShell);
	}
	
	protected Control createDialogArea(Composite parent) {
		System.out.println(settings);
		Composite composite = (Composite)super.createDialogArea(parent);
		
		GridLayout layout = new GridLayout(4, true);
		layout.marginWidth = 10;
		layout.marginHeight = 6;
		composite.setLayout(layout);

		GridData data;
		
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
		
		minimumRatingLabel = new Label(composite, SWT.LEFT);
		minimumRatingLabel.setText("Minimum Rating:");
		data = new GridData();
		data.horizontalSpan = 2;
		minimumRatingLabel.setLayoutData(data);
		
		maximumRatingLabel = new Label(composite, SWT.LEFT);
		maximumRatingLabel.setText("Maximum Rating:");
		data = new GridData();
		data.horizontalSpan = 2;
		maximumRatingLabel.setLayoutData(data);
		
		minimumRating = new Text(composite, SWT.SINGLE | SWT.BORDER);
		data = new GridData();
		data.horizontalSpan = 2;
		data.horizontalAlignment = SWT.FILL;
		minimumRating.setLayoutData(data);
		minimumRating.setText(settings.getString("formula.minimumRating", ""));
		minimumRating.setEnabled(false);
		
		maximumRating = new Text(composite, SWT.SINGLE | SWT.BORDER);
		data = new GridData();
		data.horizontalSpan = 2;
		data.horizontalAlignment = SWT.FILL;
		maximumRating.setLayoutData(data);
		maximumRating.setText(settings.getString("formula.maximumRating", ""));
		maximumRating.setEnabled(false);
		
		ratedButton = new Button(composite, SWT.CHECK);
		ratedButton.setText("Rated");
		ratedButton.setSelection(settings.getBoolean("user.rated"));
		
		noescapeButton = new Button(composite, SWT.CHECK);
		noescapeButton.setText("No Escape");
		noescapeButton.setSelection(settings.getBoolean("user.noescape"));
		
		provisionalButton = new Button(composite, SWT.CHECK);
		provisionalButton.setText("Provisional");
		provisionalButton.setSelection(settings.getBoolean("formula.provisional"));
		provisionalButton.setEnabled(false);
		
		fairPlayButton = new Button(composite, SWT.CHECK);
		fairPlayButton.setText("Fair Play");
		fairPlayButton.setSelection(settings.getBoolean("formula.fairplay"));
		fairPlayButton.setEnabled(false);
		
		saveButton = new Button(composite, SWT.CHECK);
		saveButton.setText("Save these settings as my defaults");
		data = new GridData();
		data.horizontalSpan = 4;
		saveButton.setLayoutData(data);
	
		return composite;
	}
	
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Seek a Game");
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
		oldSettings.set("user.noescape", settings.getString("user.noescape"));
		
		newSettings.set("user.time", timeText.getText());
		newSettings.set("user.increment", incrementText.getText());
		newSettings.set("user.challenge", challengeCombo.getSelectionIndex());
		newSettings.set("user.dictionary", dictionaryCombo.getSelectionIndex());
		newSettings.set("user.rated", ratedButton.getSelection());
		newSettings.set("user.noescape", noescapeButton.getSelection());
		
		Connection connection = ScrabblePlugin.getDefault().getSession().getConnection();
		if (connection != null) {
			if (!newSettings.equals(oldSettings)) {
				settings.setAll(newSettings);
				connection.send(new SetAllCommand(newSettings));
			}
			connection.send(new SeekCommand());
			if (!saveButton.getSelection() && !oldSettings.equals(newSettings)) {
				settings.setAll(oldSettings);
				connection.send(new SetAllCommand(oldSettings));
			}
		}
		
		super.okPressed();
	}
	
	private String validate() {
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
