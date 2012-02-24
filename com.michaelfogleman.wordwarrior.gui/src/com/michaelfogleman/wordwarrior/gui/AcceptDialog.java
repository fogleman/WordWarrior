package com.michaelfogleman.wordwarrior.gui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.michaelfogleman.wordwarrior.ScrabblePlugin;
import com.michaelfogleman.wordwarrior.model.Match;
import com.michaelfogleman.wordwarrior.model.Session;
import com.michaelfogleman.wordwarrior.protocol.command.DeclineMatchCommand;

public class AcceptDialog extends Dialog {
	
	private Match match;
	
	private Label messageLabel;
	private Label handleLabel;
	private Label handle;
	private Label ratingLabel;
	private Label rating;
	private Label dictionaryLabel;
	private Label dictionary;
	private Label challengeLabel;
	private Label challenge;
	private Label timeLabel;
	private Label time;
	private Label incrementLabel;
	private Label increment;
	private Label ratedLabel;
	private Label rated;
	private Label noescapeLabel;
	private Label noescape;
	
	private Button acceptButton;
	private Button declineButton;
	
	public AcceptDialog(Shell parentShell, Match match) {
		super(parentShell);
		this.match = match;
	}
	
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite)super.createDialogArea(parent);
		
		GridLayout layout = new GridLayout(4, true);
		layout.marginWidth = 10;
		layout.marginHeight = 6;
		composite.setLayout(layout);

		GridData data;
		
		String message = "You have received the following match request:";
		
		messageLabel = new Label(composite, SWT.LEFT | SWT.WRAP);
		messageLabel.setText(message);
		data = new GridData();
		data.horizontalSpan = 4;
		data.widthHint = 256;
		messageLabel.setLayoutData(data);
		
		handleLabel = new Label(composite, SWT.LEFT);
		handleLabel.setText("Handle:");
		handle = new Label(composite, SWT.LEFT);
		handle.setText(match.getHandle());
		
		ratingLabel = new Label(composite, SWT.LEFT);
		ratingLabel.setText("Rating:");
		rating = new Label(composite, SWT.LEFT);
		rating.setText(Integer.toString(match.getRating()));
		
		dictionaryLabel = new Label(composite, SWT.LEFT);
		dictionaryLabel.setText("Dictionary:");
		dictionary = new Label(composite, SWT.LEFT);
		dictionary.setText(match.getDictionary().getName());
		
		challengeLabel = new Label(composite, SWT.LEFT);
		challengeLabel.setText("Challenge:");
		challenge = new Label(composite, SWT.LEFT);
		challenge.setText(match.getChallengeMode().getName());
		
		timeLabel = new Label(composite, SWT.LEFT);
		timeLabel.setText("Time:");
		time = new Label(composite, SWT.LEFT);
		time.setText(Integer.toString(match.getTime()));
		
		incrementLabel = new Label(composite, SWT.LEFT);
		incrementLabel.setText("Increment:");
		increment = new Label(composite, SWT.LEFT);
		increment.setText(Integer.toString(match.getIncrement()));
		
		ratedLabel = new Label(composite, SWT.LEFT);
		ratedLabel.setText("Rated:");
		rated = new Label(composite, SWT.LEFT);
		rated.setText(match.isRated() ? "Yes" : "No");
		
		noescapeLabel = new Label(composite, SWT.LEFT);
		noescapeLabel.setText("No Escape:");
		noescape = new Label(composite, SWT.LEFT);
		noescape.setText(match.isNoescape() ? "Yes" : "No");
		
		return composite;
	}
	
	protected void createButtonsForButtonBar(Composite parent) {
		final Session session = ScrabblePlugin.getDefault().getSession();
		
		acceptButton = createButton(parent, IDialogConstants.OK_ID, "Accept", true);
		declineButton = createButton(parent, IDialogConstants.CANCEL_ID, "Decline", false);
		
		acceptButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				session.startGame(match);
			}
		});
		
		declineButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				session.getConnection().send(new DeclineMatchCommand(match));
			}
		});
	}
	
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Match Request");
	}

}
