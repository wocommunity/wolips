/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2002 The ObjectStyle Group
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

import java.util.Vector;

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
import org.objectstyle.wolips.core.plugin.IWOLipsPluginConstants;
import org.objectstyle.wolips.core.preferences.ILaunchInfo;
import org.objectstyle.wolips.core.preferences.Preferences;
import org.objectstyle.wolips.core.preferences.PreferencesMessages;

/**
 * @author uli
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LaunchPreferencesPage
	extends PreferencePage
	implements IWorkbenchPreferencePage {
	private Table includeTable;
	private Button addButton;
	private Button removeButton;
	private Button changeButton;
	private String preferencesKey;
	
	private Vector allParameter;
	private Vector allArguments;
	
	public void init(
		IWorkbench workbench) {
		setDescription(PreferencesMessages.getString("LaunchPreferencesPage.description")); //$NON-NLS-1$
		this.preferencesKey = IWOLipsPluginConstants.PREF_LAUNCH_GLOBAL;
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
		l1.setText(PreferencesMessages.getString("LaunchPreferencesPage.label")); //$NON-NLS-1$
		data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 3;
		l1.setLayoutData(data);

		//includeTable = new Table(parent, SWT.CHECK | SWT.BORDER);
		includeTable = new Table(parent, SWT.CHECK | SWT.BORDER);
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
		addButton.setText(PreferencesMessages.getString("LaunchPreferencesPage.add")); //$NON-NLS-1$
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
		removeButton.setText(PreferencesMessages.getString("LaunchPreferencesPage.remove")); //$NON-NLS-1$
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
		changeButton = new Button(buttons, SWT.PUSH);
		changeButton.setText(PreferencesMessages.getString("LaunchPreferencesPage.change")); //$NON-NLS-1$
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
		changeButton.setLayoutData(data);
		changeButton.setEnabled(false);
		changeButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				changeArgument();
			}
		});
		fillTable(Preferences.getLaunchInfoForKey(preferencesKey));
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
		String[] parameter = new String[count];
		String[] arguments = new String[count];
		boolean[] enabled = new boolean[count];
		TableItem[] items = (TableItem[])includeTable.getItems();
		for (int i = 0; i < count; i++) {
			parameter[i] = (String)allParameter.get(i);
			arguments[i] = (String)allArguments.get(i);
			enabled[i] = items[i].getChecked();
		}
		Preferences.setLaunchInfoForKey(parameter, arguments, enabled, preferencesKey);
		//Team.setAllIgnores(patterns, enabled);
		//TeamUIPlugin.broadcastPropertyChange(new PropertyChangeEvent(this, TeamUI.GLOBAL_IGNORES_CHANGED, null, null));
		return true;
	}

	protected void performDefaults() {
		super.performDefaults();
		includeTable.removeAll();
		String string = PreferencesMessages.getString(preferencesKey);
		Preferences.setString(preferencesKey, string);
		fillTable(Preferences.getLaunchInfoForKey(preferencesKey));
	}

	/**
	 * @param ignore
	 */
	private void fillTable(ILaunchInfo[] launchInfoArray) {
		allArguments = new Vector();
		allParameter = new Vector();
		for (int i = 0; i < launchInfoArray.length; i++) {
			ILaunchInfo launchInfo = launchInfoArray[i];
			TableItem item = new TableItem(includeTable, SWT.NONE);
			item.setText(launchInfo.getParameter() + " " + launchInfo.getArgument());
			allParameter.add(launchInfo.getParameter());
			allArguments.add(launchInfo.getArgument());
			item.setChecked(launchInfo.isEnabled());	
		}
	}

	private void addIgnore() {
		InputDialog parameterDialog = new InputDialog(getShell(), PreferencesMessages.getString("LaunchPreferencesPage.enterParameterShort"), Preferences.getString("IgnorePreferencePage.enterPatternLong"), null, null); //$NON-NLS-1$ //$NON-NLS-2$
		parameterDialog.open();
		if (parameterDialog.getReturnCode() != InputDialog.OK)
			return;
		InputDialog argumentDialog = new InputDialog(getShell(), PreferencesMessages.getString("LaunchPreferencesPage.enterArgumentShort"), Preferences.getString("IgnorePreferencePage.enterPatternLong"), null, null); //$NON-NLS-1$ //$NON-NLS-2$
		argumentDialog.open();
				if (argumentDialog.getReturnCode() != InputDialog.OK)
					return;
		String parameter = parameterDialog.getValue();	
		String argument = argumentDialog.getValue();
		if (parameter.equals("") || argument.equals(""))
			return; //$NON-NLS-1$
		// Check if the item already exists
		TableItem[] items = includeTable.getItems();
		for (int i = 0; i < items.length; i++) {
			if (items[i].getText(1).equals(parameter)) {
				MessageDialog.openWarning(getShell(), PreferencesMessages.getString("LaunchPreferencesPage.parameterExistsShort"), Preferences.getString("IgnorePreferencePage.patternExistsLong")); //$NON-NLS-1$ //$NON-NLS-2$
				return;
			}
		}
		TableItem item = new TableItem(includeTable, SWT.NONE);
		item.setText(parameter + " " + argument);
		allParameter.add(parameter);
		allArguments.add(argument);
		item.setChecked(true);
	}

	private void removeIgnore() {
		int[] selection = includeTable.getSelectionIndices();
		includeTable.remove(selection);
		String remove = "WOLips_Remove_request";
		
		for (int i = 0; i < selection.length; i++) {
			allParameter.setElementAt(remove, i);
			allArguments.setElementAt(remove, i);
		}
		for (int i = 0; i < selection.length; i++) {
			allParameter.remove(remove);
			allArguments.remove(remove);
		}
	}

	private void changeArgument() {
		int[] selection = includeTable.getSelectionIndices();
		if (selection.length != 1)
			return;
		int index = selection[0];
		InputDialog argumentDialog = new InputDialog(getShell(), PreferencesMessages.getString("LaunchPreferencesPage.enterArgumentShort"), Preferences.getString("IgnorePreferencePage.enterPatternLong"), null, null); //$NON-NLS-1$ //$NON-NLS-2$
		argumentDialog.open();
		if (argumentDialog.getReturnCode() != InputDialog.OK)
			return;
		String argument = argumentDialog.getValue();
		String parameter = (String) allParameter.elementAt(index);
		TableItem item = includeTable.getItem(index);
		item.setText(parameter + " " + argument);
		allArguments.setElementAt(argument, index);
	}
	
	private void handleSelection() {
		if (includeTable.getSelectionCount() > 0) {
			changeButton.setEnabled(true);
			removeButton.setEnabled(true);
		} else {
			changeButton.setEnabled(false);
			removeButton.setEnabled(false);
		}
	}
}
