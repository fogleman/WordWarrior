package com.michaelfogleman.wordwarrior.gui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import com.michaelfogleman.wordwarrior.ScrabblePlugin;
import com.michaelfogleman.wordwarrior.model.DictionaryType;
import com.michaelfogleman.wordwarrior.model.Settings;
import com.michaelfogleman.wordwarrior.model.User;
import com.michaelfogleman.wordwarrior.model.UserState;
import com.michaelfogleman.wordwarrior.model.UserType;
import com.michaelfogleman.wordwarrior.protocol.Connection;
import com.michaelfogleman.wordwarrior.protocol.command.ObserveCommand;
import com.michaelfogleman.wordwarrior.protocol.command.WhoCommand;
import com.michaelfogleman.wordwarrior.protocol.response.IResponseHandler;
import com.michaelfogleman.wordwarrior.protocol.response.ResponseAdapter;
import com.michaelfogleman.wordwarrior.protocol.response.WhoListResponse;
import com.michaelfogleman.wordwarrior.protocol.response.WhoResponse;

public class UserSearchDialog extends Dialog {
	
	private Settings settings = ScrabblePlugin.getDefault().getSession().getSettings();
	
	private Group filtersGroup;
	private Label minimumRatingLabel;
	private Label maximumRatingLabel;
	private Text minimumRating;
	private Text maximumRating;
	private Label userStateLabel;
	private Label userTypeLabel;
	private Combo userStateCombo;
	private Combo userTypeCombo;
	private Label dictionaryLabel;
	private Combo dictionaryCombo;
	private Button establishedButton;
	private Button fairplayButton;
	private Label resultsLabel;
	private Button searchButton;
	private TableViewer viewer;
	private IResponseHandler handler;
	private Button matchButton;
	private Button observeButton;
	
	public UserSearchDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}
	
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite)super.createDialogArea(parent);
		
		filtersGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		filtersGroup.setText("Search Filters");
		
		GridLayout layout = new GridLayout(4, true);
		layout.marginWidth = 10;
		layout.marginHeight = 6;
		filtersGroup.setLayout(layout);

		GridData data;
 		
		minimumRatingLabel = new Label(filtersGroup, SWT.LEFT);
		minimumRatingLabel.setText("Minimum Rating:");
		
		maximumRatingLabel = new Label(filtersGroup, SWT.LEFT);
		maximumRatingLabel.setText("Maximum Rating:");
		
		userStateLabel = new Label(filtersGroup, SWT.LEFT);
		userStateLabel.setText("Status:");
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		userStateLabel.setLayoutData(data);
		
		minimumRating = new Text(filtersGroup, SWT.SINGLE | SWT.BORDER);
		minimumRating.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		minimumRating.setText(settings.getString("formula.minimumRating", "0"));
		
		maximumRating = new Text(filtersGroup, SWT.SINGLE | SWT.BORDER);
		maximumRating.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		maximumRating.setText(settings.getString("formula.maximumRating", "9999"));
		
		userStateCombo = new Combo(filtersGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		userStateCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		userStateCombo.setItems(new String[] {
			"Any",
			"Available",
			"Playing",
			"Observing",
			"Examining"
		});
		userStateCombo.select(0);
		
		establishedButton = new Button(filtersGroup, SWT.CHECK);
		establishedButton.setText("Established");
		establishedButton.setSelection(!settings.getBoolean("formula.provisional", true));
		
		dictionaryLabel = new Label(filtersGroup, SWT.LEFT);
		dictionaryLabel.setText("Dictionary:");
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		dictionaryLabel.setLayoutData(data);
		
		userTypeLabel = new Label(filtersGroup, SWT.LEFT);
		userTypeLabel.setText("Type:");
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		userTypeLabel.setLayoutData(data);
		
		dictionaryCombo = new Combo(filtersGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		dictionaryCombo.setLayoutData(data);
		dictionaryCombo.setItems(new String[] {
			"Any",
			"TWL06 (English - American)",
			"SOWPODS (English - Other)",
			"ODS (French)",
			"LOC2000 (Romanian)",
			"MULTI (Aggregate)",
			"PARO (Italian)",
			"SWL (Dutch)"
		});
		dictionaryCombo.select(settings.getInt("user.dictionary", -1) + 1);
		
		userTypeCombo = new Combo(filtersGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		userTypeCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		userTypeCombo.setItems(new String[] {
			"Any",
			"Normal",
			"Computer",
			"Administrator",
			"Bot",
			"Helper"
		});
		userTypeCombo.select(0);
		
		fairplayButton = new Button(filtersGroup, SWT.CHECK);
		fairplayButton.setText("Fairplay");
		fairplayButton.setSelection(settings.getBoolean("formula.fairplay"));
		
		searchButton = new Button(filtersGroup, SWT.PUSH);
		searchButton.setText("Search...");
		searchButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				matchButton.setEnabled(false);
				observeButton.setEnabled(false);
				doSearch();
			}
		});
		getShell().setDefaultButton(searchButton);
		
		resultsLabel = new Label(composite, SWT.LEFT);
		resultsLabel.setText("Search Results:");
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 4;
		resultsLabel.setLayoutData(data);
		
		Table table = new Table(composite, SWT.FULL_SELECTION | SWT.BORDER);
		table.setLinesVisible(true);
		data = new GridData(GridData.FILL_BOTH);
		data.minimumHeight = 128;
		table.setLayoutData(data);
		viewer = createTableViewer(table);
		table.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				checkSelection();
			}
		});
		
		handler = new ResponseAdapter() {
			public void handle(WhoListResponse response) {
				setResults(response);
			}
		};
		getConnection().addResponseHandler(handler);
		
		return composite;
	}
	
	protected void createButtonsForButtonBar(Composite parent) {
		matchButton = createButton(parent, IDialogConstants.YES_ID, "Match...", false);
		observeButton = createButton(parent, IDialogConstants.NO_ID, "Observe", false);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		
		matchButton.setEnabled(false);
		matchButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Table table = viewer.getTable();
				int index = table.getSelectionIndex();
				int count = table.getItemCount();
				if (index >= 0 && index < count) {
					TableItem item = table.getItem(table.getSelectionIndex());
					String handle = item.getText(1);
					if (handle != null) {
						close();
						MatchDialog dialog = new MatchDialog(getShell(), handle);
						dialog.open();
					}
				}
			}
		});
		
		observeButton.setEnabled(false);
		observeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Table table = viewer.getTable();
				int index = table.getSelectionIndex();
				int count = table.getItemCount();
				if (index >= 0 && index < count) {
					TableItem item = table.getItem(table.getSelectionIndex());
					String handle = item.getText(1);
					if (handle != null) {
						getConnection().send(new ObserveCommand(handle));
						close();
					}
				}
			}
		});
	}
	
	private void checkSelection() {
		Table table = viewer.getTable();
		int index = table.getSelectionIndex();
		int count = table.getItemCount();
		if (index >= 0 && index < count) {
			matchButton.setEnabled(true);
			observeButton.setEnabled(true);
		}
		else {
			matchButton.setEnabled(false);
			observeButton.setEnabled(false);
		}
	}
	
	public boolean close() {
		getConnection().removeResponseHandler(handler);
		return super.close();
	}
	
	private TableViewer createTableViewer(Table table) {
		TableViewer tableViewer = new TableViewer(table);

		TableLayout layout = new TableLayout();
		layout.addColumnData(new ColumnWeightData(15, 24, true));
		layout.addColumnData(new ColumnWeightData(25, 64, true));
		layout.addColumnData(new ColumnWeightData(20, 32, true));
		layout.addColumnData(new ColumnWeightData(20, 32, true));
		layout.addColumnData(new ColumnWeightData(20, 32, true));
		table.setLayout(layout);
		
		TableColumn column;
		column = new TableColumn(table, SWT.LEFT);
		column.setText("Rating");
		column = new TableColumn(table, SWT.LEFT);
		column.setText("Handle");
		column = new TableColumn(table, SWT.LEFT);
		column.setText("Dictionary");
		column = new TableColumn(table, SWT.LEFT);
		column.setText("State");
		column = new TableColumn(table, SWT.LEFT);
		column.setText("Type");
		
		table.setHeaderVisible(true);
		configureViewer(tableViewer);
		return tableViewer;
	}
	
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Player Search");
	}
	
	protected void okPressed() {
		super.okPressed();
	}
	
	private void doSearch() {
		String validationMessage = validate();
		if (validationMessage != null) {
			MessageBox msg = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
			msg.setText("Invalid Input");
			msg.setMessage(validationMessage);
			msg.open();
			return;
		}
		
		WhoCommand command = new WhoCommand();
		command.setMinimumRating(Integer.parseInt(minimumRating.getText()));
		command.setMaximumRating(Integer.parseInt(maximumRating.getText()));
		command.setEstablished(establishedButton.getSelection());
		command.setFairplay(fairplayButton.getSelection());
		
		int dictionary = dictionaryCombo.getSelectionIndex() - 1;
		if (dictionary >= 0) {
			command.setDictionaryType(DictionaryType.getInstance(dictionary));
		}
		
		UserState userState = null;
		switch (userStateCombo.getSelectionIndex()) {
			case 1: userState = UserState.AVAILABLE; break;
			case 2: userState = UserState.PLAYING; break;
			case 3: userState = UserState.OBSERVING; break;
			case 4: userState = UserState.EXAMINING; break;
		}
		command.setUserState(userState);
		
		UserType userType = null;
		switch (userTypeCombo.getSelectionIndex()) {
			case 1: userType = UserType.PLAYER; break;
			case 2: userType = UserType.COMPUTER; break;
			case 3: userType = UserType.ADMIN; break;
			case 4: userType = UserType.BOT; break;
			case 5: userType = UserType.HELPER; break;
		}
		command.setUserType(userType);
		
		getConnection().send(command);
	}
	
	private Connection getConnection() {
		return ScrabblePlugin.getDefault().getSession().getConnection();
	}
	
	private String validate() {
		int minimum;
		try {
			minimum = Integer.parseInt(minimumRating.getText());
		} catch (NumberFormatException e) {
			return "The Minimum Rating field must be an integer and may only contain characters 0-9.";
		}
		if (minimum < 0 || minimum > 9999) {
			return "The Minimum Rating field must be an integer between 0 and 9999, inclusive.";
		}
		
		int maximum;
		try {
			maximum = Integer.parseInt(maximumRating.getText());
		} catch (NumberFormatException e) {
			return "The Maximum Rating field must be an integer and may only contain characters 0-9.";
		}
		if (maximum < 0 || maximum > 9999) {
			return "The Maximum Rating field must be an integer between 0 and 9999, inclusive.";
		}
		
		return null;
	}
	
	private void setResults(final WhoListResponse results) {
		exec(new Runnable() {
			public void run() {
				if (!viewer.getTable().isDisposed()) {
					int a = results.getList().size();
					int b = results.getCount();
					String text = "Search Results: (" + a + " of " + b + " players)";
					resultsLabel.setText(text);
					
					viewer.setInput(results);
					viewer.refresh();
					
					Table table = viewer.getTable();
					if (table.getItemCount() > 0) {
						table.showItem(table.getItem(0));
					}
				}
			}
		});
	}
	
	private void configureViewer(TableViewer viewer) {
		viewer.setContentProvider(new IStructuredContentProvider() {
			public Object[] getElements(Object inputElement) {
				WhoListResponse results = (WhoListResponse)inputElement;
				if (results == null) return new WhoResponse[0];
				WhoResponse[] array = new WhoResponse[results.getList().size()];
				results.getList().toArray(array);
				return array;
			}
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}
			public void dispose() {
			}
		});
		viewer.setLabelProvider(new ITableLabelProvider() {
			public String getColumnText(Object element, int columnIndex) {
				WhoResponse who = (WhoResponse)element;
				User user = who.getUser();
				if (who == null) return null;
				switch (columnIndex) {
					case 0:
						return Integer.toString(user.getRating());
					case 1:
						return user.getHandle();
					case 2:
						return user.getDictionaryType().getName();
					case 3:
						return user.getUserState().getName();
					case 4:
						return user.getUserType().getName();
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
	
	private void exec(Runnable runnable) {
		Display display = getShell().getDisplay();
		if (!display.isDisposed()) {
			display.asyncExec(runnable);
		}
	}

}
