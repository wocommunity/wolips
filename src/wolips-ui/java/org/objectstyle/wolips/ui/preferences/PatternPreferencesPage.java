/*
 * Created on 23.07.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.objectstyle.wolips.ui.preferences;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.objectstyle.wolips.core.preferences.IIncludeInfo;
import org.objectstyle.wolips.core.preferences.Preferences;
import org.objectstyle.wolips.core.preferences.PreferencesMessages;
/**
 * @author ulrich
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class PatternPreferencesPage
	extends PreferencePage
	implements IWorkbenchPreferencePage {
	private Table includeTable;
	private Button addButton;
	private Button removeButton;
	private String preferencesKey;
	public void init(
		IWorkbench workbench,
		String description,
		String preferencesKey) {
		setDescription(description); //$NON-NLS-1$
		this.preferencesKey = preferencesKey;
	}

	/**
	 * Creates preference page controls on demand.
	 *
	 * @param parent  the parent for the preference page
	 */
	protected Control createContents(Composite ancestor) {

		Composite parent = new Composite(ancestor, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.numColumns = 2;
		parent.setLayout(layout);
		GridData data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		parent.setLayoutData(data);

		// set F1 help
		//WorkbenchHelp.setHelp(parent, IHelpContextIds.IGNORE_PREFERENCE_PAGE);

		Label l1 = new Label(parent, SWT.NULL);
		l1.setText(PreferencesMessages.getString("PatternPreferencesPage.includePatterns")); //$NON-NLS-1$
		data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 2;
		l1.setLayoutData(data);

		//includeTable = new Table(parent, SWT.CHECK | SWT.BORDER);
		includeTable = new Table(parent, SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_BOTH);
		//gd.widthHint = convertWidthInCharsToPixels(30);
		gd.heightHint = 300;
		includeTable.setLayoutData(gd);
		includeTable.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				handleSelection();
			}
		});

		Composite buttons = new Composite(parent, SWT.NULL);
		buttons.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		buttons.setLayout(layout);

		addButton = new Button(buttons, SWT.PUSH);
		addButton.setText(PreferencesMessages.getString("PatternPreferencesPage.add")); //$NON-NLS-1$
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.heightHint =
			convertVerticalDLUsToPixels(IDialogConstants.BUTTON_HEIGHT);
		int widthHint =
			convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
		data.widthHint =
			Math.max(
				widthHint,
				addButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
		addButton.setLayoutData(data);
		addButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				addIgnore();
			}
		});

		removeButton = new Button(buttons, SWT.PUSH);
		removeButton.setText(PreferencesMessages.getString("PatternPreferencesPage.remove")); //$NON-NLS-1$
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.heightHint =
			convertVerticalDLUsToPixels(IDialogConstants.BUTTON_HEIGHT);
		widthHint =
			convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
		data.widthHint =
			Math.max(
				widthHint,
				removeButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
		removeButton.setLayoutData(data);
		removeButton.setEnabled(false);
		removeButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				removeIgnore();
			}
		});
		fillTable(Preferences.getIncludeInfoForKey(preferencesKey));
		Dialog.applyDialogFont(ancestor);
		return parent;
	}
	/**
	 * Do anything necessary because the OK button has been pressed.
	 *
	 * @return whether it is okay to close the preference page
	 */
	public boolean performOk() {
		int count = includeTable.getItemCount();
		String[] patterns = new String[count];
		//boolean[] enabled = new boolean[count];
		TableItem[] items = includeTable.getItems();
		for (int i = 0; i < count; i++) {
			patterns[i] = items[i].getText();
			//enabled[i] = items[i].getChecked();
		}
		Preferences.setIncludeInfoForKey(patterns, preferencesKey);
		//Team.setAllIgnores(patterns, enabled);
		//TeamUIPlugin.broadcastPropertyChange(new PropertyChangeEvent(this, TeamUI.GLOBAL_IGNORES_CHANGED, null, null));
		return true;
	}

	protected void performDefaults() {
		super.performDefaults();
		includeTable.removeAll();
		String string = PreferencesMessages.getString(preferencesKey);
		Preferences.setString(preferencesKey, string);
		fillTable(Preferences.getIncludeInfoForKey(preferencesKey));
	}

	/**
	 * @param ignore
	 */
	private void fillTable(IIncludeInfo[] include) {
		for (int i = 0; i < include.length; i++) {
			IIncludeInfo info = include[i];
			TableItem item = new TableItem(includeTable, SWT.NONE);
			item.setText(info.getPattern());
			//item.setChecked(info.getEnabled());
		}
	}

	private void addIgnore() {
		InputDialog dialog = new InputDialog(getShell(), PreferencesMessages.getString("PatternPreferencesPage.enterPatternShort"), Preferences.getString("IgnorePreferencePage.enterPatternLong"), null, null); //$NON-NLS-1$ //$NON-NLS-2$
		dialog.open();
		if (dialog.getReturnCode() != InputDialog.OK)
			return;
		String pattern = dialog.getValue();
		if (pattern.equals(""))
			return; //$NON-NLS-1$
		// Check if the item already exists
		TableItem[] items = includeTable.getItems();
		for (int i = 0; i < items.length; i++) {
			if (items[i].getText().equals(pattern)) {
				MessageDialog.openWarning(getShell(), PreferencesMessages.getString("PatternPreferencesPage.patternExistsShort"), Preferences.getString("IgnorePreferencePage.patternExistsLong")); //$NON-NLS-1$ //$NON-NLS-2$
				return;
			}
		}
		TableItem item = new TableItem(includeTable, SWT.NONE);
		item.setText(pattern);
		item.setChecked(true);
	}

	private void removeIgnore() {
		int[] selection = includeTable.getSelectionIndices();
		includeTable.remove(selection);
	}
	private void handleSelection() {
		if (includeTable.getSelectionCount() > 0) {
			removeButton.setEnabled(true);
		} else {
			removeButton.setEnabled(false);
		}
	}
}