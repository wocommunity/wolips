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

import java.util.StringTokenizer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.objectstyle.wolips.launching.LaunchingMessages;
import org.objectstyle.wolips.launching.LaunchingPlugin;
import org.objectstyle.wolips.launching.delegates.WOJavaLocalApplicationLaunchConfigurationDelegate;
import org.objectstyle.wolips.preferences.PreferencesMessages;

/**
 * @author uli 
 */
public class LogTab extends AbstractWOArgumentsTab {

	private Table debugGroupsTable;

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
		l1
				.setText(PreferencesMessages
						.getString("LaunchPreferencesPage.label")); //$NON-NLS-1$
		data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 3;
		l1.setLayoutData(data);

		this.debugGroupsTable = new Table(parent, SWT.CHECK | SWT.BORDER);
		GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
		// gd.widthHint = convertWidthInCharsToPixels(30);
		gd2.widthHint = 150;
		gd2.heightHint = 250;
		this.debugGroupsTable.setLayoutData(gd2);

		this.debugGroupsTable.addListener(SWT.CHECK, new Listener() {
			public void handleEvent(Event e) {
				setDirty(true);
				updateLaunchConfigurationDialog();
			}
		});

		Dialog.applyDialogFont(parent);
		this.setControl(parent);
	}

	private void fillDebugGroupsTable(String aString) {
		String debugGroups = LaunchingMessages
				.getString(WOJavaLocalApplicationLaunchConfigurationDelegate.ATTR_WOLIPS_LAUNCH_DEBUG_GROUPS);
		StringTokenizer stringTokenizer = new StringTokenizer(debugGroups, ",");
		this.debugGroupsTable.removeAll();
		while (stringTokenizer.hasMoreTokens()) {
			TableItem item = new TableItem(this.debugGroupsTable, SWT.NONE);
			String token = stringTokenizer.nextToken();
			item.setText(token);
			if (aString != null && aString.indexOf(token) >= 0)
				item.setChecked(true);
			else
				item.setChecked(false);
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
		config
				.setAttribute(
						WOJavaLocalApplicationLaunchConfigurationDelegate.ATTR_WOLIPS_LAUNCH_DEBUG_GROUPS,
						"");
	}

	/**
	 * @see ILaunchConfigurationTab#initializeFrom(ILaunchConfiguration)
	 */
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			this
					.fillDebugGroupsTable(configuration
							.getAttribute(
									WOJavaLocalApplicationLaunchConfigurationDelegate.ATTR_WOLIPS_LAUNCH_DEBUG_GROUPS,
									""));
		} catch (CoreException e) {
			setErrorMessage(LaunchingMessages
					.getString("WOArgumentsTab.Exception_occurred_reading_configuration___15") + e.getStatus().getMessage()); //$NON-NLS-1$
			LaunchingPlugin.getDefault().log(e);
		}
	}

	/**
	 * @see ILaunchConfigurationTab#performApply(ILaunchConfigurationWorkingCopy)
	 */
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		String aString = "";
		TableItem[] debugGrouspItems = this.debugGroupsTable.getItems();
		for (int i = 0; i < debugGrouspItems.length; i++) {
			if (debugGrouspItems[i].getChecked()) {
				if (aString.length() > 0)
					aString = aString + ",";
				aString = aString + debugGrouspItems[i].getText();
			}
		}
		configuration
				.setAttribute(
						WOJavaLocalApplicationLaunchConfigurationDelegate.ATTR_WOLIPS_LAUNCH_DEBUG_GROUPS,
						aString);
	}

	/**
	 * @see ILaunchConfigurationTab#getName()
	 */
	public String getName() {
		return LaunchingMessages.getString("LoggingTab.Name"); //$NON-NLS-1$
	}

	/**
	 * @see ILaunchConfigurationTab#getImage()
	 */
	public Image getImage() {
		return LaunchingPlugin.getImageDescriptor(
				"icons/launching/logging-tab.gif").createImage();
	}

	protected void updateLaunchConfigurationDialog() {
		super.updateLaunchConfigurationDialog();
		this.getControl().update();
	}

}
