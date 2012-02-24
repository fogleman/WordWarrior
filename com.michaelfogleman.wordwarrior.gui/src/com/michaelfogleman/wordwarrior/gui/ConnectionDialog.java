package com.michaelfogleman.wordwarrior.gui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.michaelfogleman.wordwarrior.ScrabblePlugin;

public class ConnectionDialog extends Dialog {
	
	private Text username;
	private Text password;
	
	private IDialogSettings settings = ScrabbleGuiPlugin.getDefault().getDialogSettings();
	
	public ConnectionDialog(Shell parentShell) {
		super(parentShell);
	}
	
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite)super.createDialogArea(parent);
		
		GridLayout layout = new GridLayout();
 		layout.numColumns = 2;
 		composite.setLayout(layout);
		
 		(new Label(composite, SWT.LEFT)).setText("Username:");
		username = new Text(composite, SWT.SINGLE | SWT.BORDER);
		
		(new Label(composite, SWT.LEFT)).setText("Password:");
		password = new Text(composite, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD);
		
		String handle = settings.get("connect.handle");
		String pass = settings.get("connect.password");
		
		username.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		password.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		
		if (handle != null) username.setText(handle);
		if (pass != null) password.setText(pass);
		
		return composite;
	}
	
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Connect to a Server");
	}
	
	protected void okPressed() {
		settings.put("connect.handle", getUsername());
		settings.put("connect.password", getPassword());
		
		ScrabblePlugin.getDefault().connect(getUsername(), getPassword());
		super.okPressed();
	}
	
	public String getUsername() {
		return username.getText();
	}
	
	public String getPassword() {
		return password.getText();
	}

}
