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

package org.objectstyle.wolips.launching.ui;

import java.util.Arrays;
import java.util.Vector;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.objectstyle.wolips.baseforplugins.util.ArrayUtilities;
import org.objectstyle.wolips.baseforplugins.util.StringUtilities;
import org.objectstyle.wolips.launching.LaunchingMessages;
import org.objectstyle.wolips.launching.LaunchingPlugin;
import org.objectstyle.wolips.launching.delegates.WOJavaLocalApplicationLaunchConfigurationDelegate;
import org.objectstyle.wolips.preferences.ILaunchInfo;
import org.objectstyle.wolips.preferences.Preferences;
import org.objectstyle.wolips.preferences.PreferencesMessages;

/**
 * @author uli
 */
public class CommonWOArgumentsTab extends AbstractWOArgumentsTab {

	private Table includeTable;

	private Button addButton;

	private Button removeButton;

	private Button changeButton;

	private Vector allParameter;

	private Vector allArguments;

	protected static final String EMPTY_STRING = ""; //$NON-NLS-1$

	/**
	 * @see ILaunchConfigurationTab#createControl(Composite)
	 */
	public void createControl(Composite parentComposite) {

		Composite parent = new Composite(parentComposite, SWT.NULL);
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
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		// gd.widthHint = convertWidthInCharsToPixels(30);
		gd.widthHint = 150;
		gd.heightHint = 250;
		this.includeTable.setLayoutData(gd);
		this.includeTable.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				handleSelection();
				updateLaunchConfigurationDialog();
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
		data.heightHint = 20;
		data.heightHint = Math.max(data.heightHint, this.addButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).y);
		// convertVerticalDLUsToPixels(IDialogConstants.BUTTON_HEIGHT);
		int widthHint = 100;
		// convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
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
		data.heightHint = 20;
		data.heightHint = Math.max(data.heightHint, this.removeButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).y);
		// Dialog.convertVerticalDLUsToPixels(new FontMetrics(),
		// IDialogConstants.BUTTON_HEIGHT);
		widthHint = 100;
		// Dialog.convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
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
		data.heightHint = 20;
		data.heightHint = Math.max(data.heightHint, this.changeButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).y);

		// Dialog.convertVerticalDLUsToPixels(new FontMetrics(),
		// IDialogConstants.BUTTON_HEIGHT);
		widthHint = 100;
		// Dialog.convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
		data.widthHint = Math.max(widthHint, this.changeButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
		this.changeButton.setLayoutData(data);
		this.changeButton.setEnabled(false);
		this.changeButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				changeArgument();
			}
		});

		Dialog.applyDialogFont(parent);
		this.setControl(parent);
	}

	private void fillTable(ILaunchInfo[] launchInfoArray) {
		this.allArguments = new Vector();
		this.allParameter = new Vector();
		this.includeTable.removeAll();
		Arrays.sort(launchInfoArray);
		for (int i = 0; i < launchInfoArray.length; i++) {
			ILaunchInfo launchInfo = launchInfoArray[i];
			TableItem item = new TableItem(this.includeTable, SWT.NONE);
			item.setText(StringUtilities.toCommandlineParameterFormat(launchInfo.getParameter(), launchInfo.getArgument(), false));
			this.allParameter.add(launchInfo.getParameter());
			this.allArguments.add(launchInfo.getArgument());
			item.setChecked(launchInfo.isEnabled());
		}
	}

	protected void addIgnore() {
		InputDialog parameterDialog = new InputDialog(getShell(), PreferencesMessages.getString("LaunchPreferencesPage.enterParameterShort"), Preferences.getString("IgnorePreferencePage.enterPatternLong"), null, null); //$NON-NLS-1$ //$NON-NLS-2$
		parameterDialog.open();
		if (parameterDialog.getReturnCode() != Window.OK)
			return;
		InputDialog argumentDialog = new InputDialog(getShell(), PreferencesMessages.getString("LaunchPreferencesPage.enterArgumentShort"), Preferences.getString("IgnorePreferencePage.enterPatternLong"), null, null); //$NON-NLS-1$ //$NON-NLS-2$
		argumentDialog.open();
		if (argumentDialog.getReturnCode() != Window.OK)
			return;
		String parameter = parameterDialog.getValue();
		String argument = argumentDialog.getValue();
		if (parameter.equals("") || argument.equals(""))
			return; //$NON-NLS-1$
		// Check if the item already exists
		if (this.itemExist(parameter)) {
			return;
		}
		TableItem item = new TableItem(this.includeTable, SWT.NONE);
		item.setText(StringUtilities.toCommandlineParameterFormat(parameter, argument, false));
		this.allParameter.add(parameter);
		this.allArguments.add(argument);
		item.setChecked(true);
		setDirty(true);
		this.updateLaunchConfigurationDialog();
	}

	private boolean itemExist(String item) {
		TableItem[] items = this.includeTable.getItems();
		for (int i = 0; i < items.length; i++) {
			if (items[i].getText(1).equals(item)) {
				MessageDialog.openWarning(getShell(), PreferencesMessages.getString("LaunchPreferencesPage.parameterExistsShort"), Preferences.getString("IgnorePreferencePage.patternExistsLong")); //$NON-NLS-1$ //$NON-NLS-2$
				return true;
			}
		}
		return false;
	}

	protected void removeIgnore() {
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
		setDirty(true);
		this.updateLaunchConfigurationDialog();
	}

	protected void changeArgument() {
		int[] selection = this.includeTable.getSelectionIndices();
		if (selection.length != 1)
			return;
		int index = selection[0];
		String parameter = (String) this.allParameter.elementAt(index);
		InputDialog argumentDialog = new InputDialog(getShell(), parameter + " " + PreferencesMessages.getString("LaunchPreferencesPage.enterArgumentShort"), Preferences.getString("IgnorePreferencePage.enterPatternLong"), (String) this.allArguments.elementAt(index), null);
		argumentDialog.open();
		if (argumentDialog.getReturnCode() != Window.OK)
			return;
		String argument = argumentDialog.getValue();
		TableItem item = this.includeTable.getItem(index);
		item.setText(StringUtilities.toCommandlineParameterFormat(parameter, argument, false));
		this.allArguments.setElementAt(argument, index);
		setDirty(true);
		this.updateLaunchConfigurationDialog();
	}

	protected void handleSelection() {
		if (this.includeTable.getSelectionCount() > 0) {
			this.removeButton.setEnabled(true);
			this.changeButton.setEnabled(true);
		} else {
			this.removeButton.setEnabled(false);
			this.changeButton.setEnabled(false);
		}
	}

	/**
	 * @see ILaunchConfigurationTab#dispose()
	 */
	public void dispose() {
		super.dispose();
	}

	/**
	 * @see ILaunchConfigurationTab#isValid(ILaunchConfiguration)
	 */
	public boolean isValid(ILaunchConfiguration config) {
		return true;
	}

	/**
	 * Defaults are empty.
	 * 
	 * @see ILaunchConfigurationTab#setDefaults(ILaunchConfigurationWorkingCopy)
	 */
	public void setDefaults(ILaunchConfigurationWorkingCopy config) {
		String string = this.getDefaultArguments(config);
		config.setAttribute(WOJavaLocalApplicationLaunchConfigurationDelegate.ATTR_WOLIPS_LAUNCH_WOARGUMENTS, string);
	}

	/**
	 * @see ILaunchConfigurationTab#initializeFrom(ILaunchConfiguration)
	 */
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			String string = configuration.getAttribute(WOJavaLocalApplicationLaunchConfigurationDelegate.ATTR_WOLIPS_LAUNCH_WOARGUMENTS, Preferences.getLaunchGlobal()); //$NON-NLS-1$
			this.fillTable(Preferences.getLaunchInfoFrom(string));
		} catch (CoreException e) {
			setErrorMessage(LaunchingMessages.getString("WOArgumentsTab.Exception_occurred_reading_configuration___15") + e.getStatus().getMessage()); //$NON-NLS-1$
			LaunchingPlugin.getDefault().log(e);
		}
	}

	/**
	 * @see ILaunchConfigurationTab#performApply(ILaunchConfigurationWorkingCopy)
	 */
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		int count = this.includeTable.getItemCount();
		String[] parameter = new String[count];
		String[] arguments = new String[count];
		boolean[] enabled = new boolean[count];
		TableItem[] items = this.includeTable.getItems();
		for (int i = 0; i < count; i++) {
			parameter[i] = (String) this.allParameter.get(i);
			arguments[i] = (String) this.allArguments.get(i);
			enabled[i] = items[i].getChecked();
		}
		String string = Preferences.LaunchInfoToString(parameter, arguments, enabled);
		configuration.setAttribute(WOJavaLocalApplicationLaunchConfigurationDelegate.ATTR_WOLIPS_LAUNCH_WOARGUMENTS, string);
	}

	/**
	 * Retuns the string in the text widget, or <code>null</code> if empty.
	 * 
	 * @return text or <code>null</code>
	 */
	/*
	 * protected String getAttributeValueFrom(Text text) { String content =
	 * text.getText().trim(); if (content.length() > 0) { return content; }
	 * return null; }
	 */

	/**
	 * @see ILaunchConfigurationTab#getName()
	 */
	public String getName() {
		return LaunchingMessages.getString("CommonWOArgumentsTab.Name"); //$NON-NLS-1$
	}

	/**
	 * @see ILaunchConfigurationTab#getImage()
	 */
	public Image getImage() {
		return LaunchingPlugin.getImageDescriptor("icons/launching/arguments-tab.gif").createImage();
	}

	private String getDefaultArguments(ILaunchConfigurationWorkingCopy config) {
		try {
			String path = config.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, (String) null);
			IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
			if (resource != null && resource instanceof IContainer && resource.exists()) {
				IResource distFolder = ((IContainer) resource).findMember("dist");
				IResource product;
				if (distFolder instanceof IContainer && distFolder.exists())
					product = ((IContainer) distFolder).findMember(path.toString() + ".woa");
				else
					product = ((IContainer) resource).findMember(path.toString() + ".woa");
				if (product != null) {
					if (product instanceof IContainer && product.exists()) {
						config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY, ((IContainer) product).getFullPath().toString().substring(1));
					}
				}
			}

		} catch (Exception anException) {
			LaunchingPlugin.getDefault().log(anException);
		}
		return Preferences.getLaunchGlobal();
	}

	protected void updateLaunchConfigurationDialog() {
		super.updateLaunchConfigurationDialog();
		this.getControl().update();
		this.includeTable.update();
	}

}
