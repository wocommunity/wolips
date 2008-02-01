/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2002 - 2006 The ObjectStyle Group
 * and individual authors of the software.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        ObjectStyle Group (http://objectstyle.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "ObjectStyle Group" and "Cayenne"
 *    must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact andrus@objectstyle.org.
 *
 * 5. Products derived from this software may not be called "ObjectStyle"
 *    nor may "ObjectStyle" appear in their names without prior written
 *    permission of the ObjectStyle Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE OBJECTSTYLE GROUP OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the ObjectStyle Group.  For more
 * information on the ObjectStyle Group, please see
 * <http://objectstyle.org/>.
 *
 */

package org.objectstyle.wolips.ui.preferences;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.window.Window;
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
import org.objectstyle.wolips.baseforplugins.util.ArrayUtilities;
import org.objectstyle.wolips.baseforplugins.util.StringUtilities;
import org.objectstyle.wolips.preferences.ILaunchInfo;
import org.objectstyle.wolips.preferences.Preferences;
import org.objectstyle.wolips.preferences.PreferencesMessages;

/**
 * @author uli
 */
public class LaunchPreferencesPage extends PreferencePage implements IWorkbenchPreferencePage {
	private Table includeTable;

	private Button addButton;

	private Button removeButton;

	private Button changeButton;

	private String preferencesKey;

	private List<String> allParameter;

	private List<String> allArguments;

	public void init(IWorkbench workbench) {
		setDescription(PreferencesMessages.getString("LaunchPreferencesPage.description")); //$NON-NLS-1$
		this.preferencesKey = Preferences.PREF_LAUNCH_GLOBAL;
	}

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
		// WorkbenchHelp.setHelp(parent,
		// IHelpContextIds.IGNORE_PREFERENCE_PAGE);

		Label l1 = new Label(parent, SWT.NULL);
		l1.setText(PreferencesMessages.getString("LaunchPreferencesPage.label")); //$NON-NLS-1$
		data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 3;
		l1.setLayoutData(data);

		// includeTable = new Table(parent, SWT.CHECK | SWT.BORDER);
		this.includeTable = new Table(parent, SWT.CHECK | SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_BOTH);
		// gd.widthHint = convertWidthInCharsToPixels(30);
		gd.heightHint = 300;
		this.includeTable.setLayoutData(gd);
		this.includeTable.addListener(SWT.Selection, new Listener() {
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

		this.addButton = new Button(buttons, SWT.PUSH);
		this.addButton.setText(PreferencesMessages.getString("LaunchPreferencesPage.add")); //$NON-NLS-1$
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.heightHint = Math.max(data.heightHint, this.addButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).y);
		int widthHint = convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
		data.widthHint = Math.max(widthHint, this.addButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
		this.addButton.setLayoutData(data);
		this.addButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				addIgnore();
			}
		});

		this.removeButton = new Button(buttons, SWT.PUSH);
		this.removeButton.setText(PreferencesMessages.getString("LaunchPreferencesPage.remove")); //$NON-NLS-1$
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.heightHint = Math.max(data.heightHint, this.removeButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).y);
		widthHint = convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
		data.widthHint = Math.max(widthHint, this.removeButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
		this.removeButton.setLayoutData(data);
		this.removeButton.setEnabled(false);
		this.removeButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				removeIgnore();
			}
		});
		this.changeButton = new Button(buttons, SWT.PUSH);
		this.changeButton.setText(PreferencesMessages.getString("LaunchPreferencesPage.change")); //$NON-NLS-1$
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.heightHint = Math.max(data.heightHint, this.changeButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).y);
		widthHint = convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
		data.widthHint = Math.max(widthHint, this.removeButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
		this.changeButton.setLayoutData(data);
		this.changeButton.setEnabled(false);
		this.changeButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				changeArgument();
			}
		});
		fillTable(Preferences.getLaunchInfoForKey(this.preferencesKey));
		Dialog.applyDialogFont(ancestor);
		return parent;
	}

	/**
	 * Do anything necessary because the OK button has been pressed.
	 * 
	 * @return whether it is okay to close the preference page
	 */
	public boolean performOk() {
		int count = this.includeTable.getItemCount();
		String[] parameter = new String[count];
		String[] arguments = new String[count];
		boolean[] enabled = new boolean[count];
		TableItem[] items = this.includeTable.getItems();
		for (int i = 0; i < count; i++) {
			parameter[i] = this.allParameter.get(i);
			arguments[i] = this.allArguments.get(i);
			enabled[i] = items[i].getChecked();
		}
		Preferences.setLaunchInfoForKey(parameter, arguments, enabled, this.preferencesKey);
		// Team.setAllIgnores(patterns, enabled);
		// TeamUIPlugin.broadcastPropertyChange(new PropertyChangeEvent(this,
		// TeamUI.GLOBAL_IGNORES_CHANGED, null, null));
		return true;
	}

	protected void performDefaults() {
		super.performDefaults();
		this.includeTable.removeAll();
		String string = PreferencesMessages.getString(this.preferencesKey);
		Preferences.setString(this.preferencesKey, string);
		fillTable(Preferences.getLaunchInfoForKey(this.preferencesKey));
	}

	private void fillTable(ILaunchInfo[] launchInfoArray) {
		this.allArguments = new LinkedList<String>();
		this.allParameter = new LinkedList<String>();
		for (int i = 0; i < launchInfoArray.length; i++) {
			ILaunchInfo launchInfo = launchInfoArray[i];
			TableItem item = new TableItem(this.includeTable, SWT.NONE);
			item.setText(StringUtilities.toCommandlineParameterFormat(launchInfo.getParameter(), launchInfo.getArgument(), false));
			this.allParameter.add(launchInfo.getParameter());
			this.allArguments.add(launchInfo.getArgument());
			item.setChecked(launchInfo.isEnabled());
		}
	}

	void addIgnore() {
		InputDialog parameterDialog = new InputDialog(getShell(), PreferencesMessages.getString("LaunchPreferencesPage.enterParameterShort"), PreferencesMessages.getString("IgnorePreferencePage.enterPatternLong"), null, null); //$NON-NLS-1$ //$NON-NLS-2$
		parameterDialog.open();
		if (parameterDialog.getReturnCode() != Window.OK)
			return;
		InputDialog argumentDialog = new InputDialog(getShell(), PreferencesMessages.getString("LaunchPreferencesPage.enterArgumentShort"), PreferencesMessages.getString("IgnorePreferencePage.enterPatternLong"), null, null); //$NON-NLS-1$ //$NON-NLS-2$
		argumentDialog.open();
		if (argumentDialog.getReturnCode() != Window.OK)
			return;
		String parameter = parameterDialog.getValue();
		String argument = argumentDialog.getValue();
		if (parameter.equals("") || argument.equals(""))
			return; //$NON-NLS-1$
		// Check if the item already exists
		TableItem[] items = this.includeTable.getItems();
		for (int i = 0; i < items.length; i++) {
			if (items[i].getText(1).equals(parameter)) {
				MessageDialog.openWarning(getShell(), PreferencesMessages.getString("LaunchPreferencesPage.parameterExistsShort"), PreferencesMessages.getString("IgnorePreferencePage.patternExistsLong")); //$NON-NLS-1$ //$NON-NLS-2$
				return;
			}
		}
		TableItem item = new TableItem(this.includeTable, SWT.NONE);
		item.setText(StringUtilities.toCommandlineParameterFormat(parameter, argument, false));
		this.allParameter.add(parameter);
		this.allArguments.add(argument);
		item.setChecked(true);
	}

	void removeIgnore() {
		int[] selection = this.includeTable.getSelectionIndices();
		this.includeTable.remove(selection);
		if (selection == null)
			return;
		int[] newIndices = new int[selection.length];
		System.arraycopy(selection, 0, newIndices, 0, selection.length);
		ArrayUtilities.sort(selection);
		int last = -1;
		for (int i = 0; i < newIndices.length; i++) {
			int index = newIndices[i];
			if (index != last || i == 0) {
				this.allParameter.remove(index);
				this.allArguments.remove(index);
			}

			last = index;
		}
	}

	void changeArgument() {
		int[] selection = this.includeTable.getSelectionIndices();
		if (selection.length != 1)
			return;
		int index = selection[0];
		InputDialog argumentDialog = new InputDialog(getShell(), PreferencesMessages.getString("LaunchPreferencesPage.enterArgumentShort"), PreferencesMessages.getString("IgnorePreferencePage.enterPatternLong"), this.allArguments.get(index), null); //$NON-NLS-1$ //$NON-NLS-2$
		argumentDialog.open();
		if (argumentDialog.getReturnCode() != Window.OK)
			return;
		String argument = argumentDialog.getValue();
		String parameter = this.allParameter.get(index);
		TableItem item = this.includeTable.getItem(index);
		item.setText(StringUtilities.toCommandlineParameterFormat(parameter, argument, false));
		this.allArguments.set(index, argument);
	}

	void handleSelection() {
		if (this.includeTable.getSelectionCount() > 0) {
			this.changeButton.setEnabled(true);
			this.removeButton.setEnabled(true);
		} else {
			this.changeButton.setEnabled(false);
			this.removeButton.setEnabled(false);
		}
	}
}
