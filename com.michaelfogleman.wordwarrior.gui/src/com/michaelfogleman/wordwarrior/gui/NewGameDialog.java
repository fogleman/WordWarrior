package com.michaelfogleman.wordwarrior.gui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.michaelfogleman.wordwarrior.ScrabblePlugin;
import com.michaelfogleman.wordwarrior.engine.BasicMoveMaker;
import com.michaelfogleman.wordwarrior.model.ChallengeMode;
import com.michaelfogleman.wordwarrior.model.DictionaryType;
import com.michaelfogleman.wordwarrior.model.IGame;
import com.michaelfogleman.wordwarrior.model.IPlayer;
import com.michaelfogleman.wordwarrior.model.LocalPlayer;
import com.michaelfogleman.wordwarrior.model.StandardTileRack;

public class NewGameDialog extends Dialog {
	
	private IDialogSettings settings = ScrabbleGuiPlugin.getDefault().getDialogSettings();
	
	private Label name1Label;
	private Label type1Label;
	private Label name2Label;
	private Label type2Label;
	private Text name1Text;
	private Combo type1Combo;
	private Text name2Text;
	private Combo type2Combo;
	private Label timeLabel;
	private Label incrementLabel;
	private Text timeText;
	private Text incrementText;
	private Label challengeLabel;
	private Label dictionaryLabel;
	private Combo challengeCombo;
	private Combo dictionaryCombo;
	private Button saveButton;
	
	public NewGameDialog(Shell parentShell) {
		super(parentShell);
	}
	
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite)super.createDialogArea(parent);
		
		GridLayout layout = new GridLayout(2, true);
		layout.marginWidth = 10;
		layout.marginHeight = 6;
		composite.setLayout(layout);

		GridData data;
 		
		name1Label = new Label(composite, SWT.LEFT);
		name1Label.setText("Player 1:");
		
		name2Label = new Label(composite, SWT.LEFT);
		name2Label.setText("Player 2:");
		
		name1Text = new Text(composite, SWT.SINGLE | SWT.BORDER);
		name1Text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		name2Text = new Text(composite, SWT.SINGLE | SWT.BORDER);
		name2Text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		type1Label = new Label(composite, SWT.LEFT);
		type1Label.setText("Player Type:");
		
		type2Label = new Label(composite, SWT.LEFT);
		type2Label.setText("Player Type:");
		
		type1Combo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		type1Combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		type1Combo.setItems(new String[] {
			"Human",
			"Computer"
		});
		type1Combo.select(0);
		
		type2Combo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		type2Combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		type2Combo.setItems(new String[] {
			"Human",
			"Computer"
		});
		type2Combo.select(0);
		
		timeLabel = new Label(composite, SWT.LEFT);
		timeLabel.setText("Initial Time:");
		
		incrementLabel = new Label(composite, SWT.LEFT);
		incrementLabel.setText("Time Increment:");
		
		timeText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		timeText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		incrementText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		incrementText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		challengeLabel = new Label(composite, SWT.LEFT);
		challengeLabel.setText("Challenge Mode:");
		
		dictionaryLabel = new Label(composite, SWT.LEFT);
		dictionaryLabel.setText("Dictionary:");
		
		challengeCombo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		challengeCombo.setItems(new String[] {
			"SINGLE (No Penalty)",
			"DOUBLE (Lose Turn)",
			"FIVE POINTS (Lose 5 Points)",
			"VOID (No Challenges)"
		});
		challengeCombo.select(0);
		
		dictionaryCombo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		dictionaryCombo.setItems(new String[] {
			"TWL06 (English - American)",
			"SOWPODS (English - Other)",
			"ODS (French)",
			"LOC2000 (Romanian)",
			"MULTI (Aggregate)",
			"PARO (Italian)",
			"SWL (Dutch)",
			"TWL06 (Clabbers Mode)"
		});
		dictionaryCombo.select(0);
		
		saveButton = new Button(composite, SWT.CHECK);
		saveButton.setText("Save these settings as my defaults");
		data = new GridData();
		data.horizontalSpan = 2;
		saveButton.setLayoutData(data);
		
		String key;
		String name1 = settings.get("game.name1");
		if (name1 == null) name1 = "Player-1";
		String name2 = settings.get("game.name2");
		if (name2 == null) name2 = "Player-2";
		int type1 = 0;
		int type2 = 1;
		key = "game.type1";
		if (settings.get(key) != null) type1 = settings.getInt(key);
		key = "game.type2";
		if (settings.get(key) != null) type2 = settings.getInt(key);
		String time = settings.get("game.time");
		if (time == null) time = "60";
		String increment = settings.get("game.increment");
		if (increment == null) increment = "0";
		int dictionary = 0;
		int challenge = 3;
		key = "game.dictionary";
		if (settings.get(key) != null) dictionary = settings.getInt(key);
		key = "game.challenge";
		if (settings.get(key) != null) challenge = settings.getInt(key);
		
		name1Text.setText(name1);
		type1Combo.select(type1);
		name2Text.setText(name2);
		type2Combo.select(type2);
		timeText.setText(time);
		incrementText.setText(increment);
		dictionaryCombo.select(dictionary);
		challengeCombo.select(challenge);
		
		return composite;
	}
	
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("New Offline Game");
	}
	
	protected void okPressed() {
		String validationMessage = validate();
		if (validationMessage != null) {
			MessageBox msg = new MessageBox(this.getShell(), SWT.ICON_ERROR | SWT.OK);
			msg.setText("Invalid Input");
			msg.setMessage(validationMessage);
			msg.open();
			return;
		}
		
		DictionaryType dictionaryType = DictionaryType.getInstance(dictionaryCombo.getSelectionIndex());
		ChallengeMode challengeMode = ChallengeMode.getInstance(challengeCombo.getSelectionIndex());
		
		int time = 60;
		int increment = 0;
		
		try {
			time = Integer.parseInt(timeText.getText());
		} catch (NumberFormatException e) {
		}
		
		try {
			increment = Integer.parseInt(incrementText.getText());
		} catch (NumberFormatException e) {
		}
		
		if (saveButton.getSelection()) {
			settings.put("game.name1", name1Text.getText());
			settings.put("game.name2", name2Text.getText());
			settings.put("game.type1", type1Combo.getSelectionIndex());
			settings.put("game.type2", type2Combo.getSelectionIndex());
			settings.put("game.time", time);
			settings.put("game.increment", increment);
			settings.put("game.dictionary", dictionaryCombo.getSelectionIndex());
			settings.put("game.challenge", challengeCombo.getSelectionIndex());
		}
		
		IGame game = ScrabblePlugin.getDefault().getSession().createLocalGame(dictionaryType, challengeMode, 2);
		game.setTimeLimit(time);
		game.setTimeIncrement(increment);
		
		IPlayer player1 = new LocalPlayer(game, 1, name1Text.getText(), new StandardTileRack());
		IPlayer player2 = new LocalPlayer(game, 2, name2Text.getText(), new StandardTileRack());
		
		if (type1Combo.getSelectionIndex() == 1) {
			player1.setMoveMaker(new BasicMoveMaker());
		}
		if (type2Combo.getSelectionIndex() == 1) {
			player2.setMoveMaker(new BasicMoveMaker());
		}
		
		game.start();
		
		super.okPressed();
	}
	
	private String validate() {
		String name1 = name1Text.getText();
		if (name1 == null || name1.trim().length() == 0) {
			return "The name for Player 1 must not be blank.";
		}
		
		String name2 = name2Text.getText();
		if (name2 == null || name2.trim().length() == 0) {
			return "The name for Player 2 must not be blank.";
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
