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

package org.objectstyle.wolips.ui.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.objectstyle.wolips.core.plugin.WOLipsPlugin;
import org.objectstyle.wolips.core.project.IClasspathVariablesAccessor;
import org.objectstyle.wolips.core.project.WOLipsCore;
import org.objectstyle.wolips.core.util.WorkbenchUtilities;
import org.objectstyle.wolips.ui.view.WOFrameworkDialogWrapper;

/**
 * Adding WOFrameworks
 * 
 * @author mnolte
 *
 */
public class WOFrameworkAction extends ActionOnIProject {

	private static String WOSystemFrameworkAddID = "WOSystemFramework.Add.ID";
	private static String WOLocalFrameworkAddID = "WOLocalFramework.Add.ID";
	private static String WOUserHomeFrameworkAddID =
		"WOUserHomeFramework.Add.ID";
	private static String ProjectWonderHomeFrameworkAddID =
		"ProjectWonderHomeFramework.Add.ID";
	private static String WOOtherFrameworkAddID = "WOOtherFramework.Add.ID";
	private static String SelectOneClasspathVariableKey =
		"SelectOneClasspathVariable";

	/**
	 * Constructor for WOFrameworkAction.
	 */
	public WOFrameworkAction() {
		super();
	}
	/**
	 * Method dispose.
	 */
	public void dispose() {
		super.dispose();
	}
	/**
	 * Runs the action.
	 */
	public void run(IAction action) {
		WOLipsPlugin.informUser(WorkbenchUtilities.getShell(), "Please use the WOLips Classpath Container instead.");
		if (true)
			return;
		if (project() == null)
			return;
		IJavaProject javaProject = JavaCore.create(project());
		if (action.getId().equals(WOSystemFrameworkAddID)) {
			WOFrameworkAction.frameworkDialog(
				this.part,
				javaProject,
				WOLipsPlugin
					.getDefault()
					.getWOEnvironment()
					.getNEXT_SYSTEM_ROOT(),
				false,
				true);
			return;
		} else if (action.getId().equals(WOLocalFrameworkAddID)) {
			WOFrameworkAction.frameworkDialog(
				this.part,
				javaProject,
				WOLipsPlugin
					.getDefault()
					.getWOEnvironment()
					.getNEXT_LOCAL_ROOT(),
				true,
				true);
			return;
		} else if (action.getId().equals(WOUserHomeFrameworkAddID)) {
			WOFrameworkAction.frameworkDialog(
				this.part,
				javaProject,
				IClasspathVariablesAccessor.UserHomeClasspathVariable,
				false,
				true);
			return;
		} else if (action.getId().equals(ProjectWonderHomeFrameworkAddID)) {
			WOFrameworkAction.frameworkDialog(
				this.part,
				javaProject,
				IClasspathVariablesAccessor.ProjectWonderHomeClasspathVariable,
				false,
				true);
			return;
		} else if (action.getId().equals(WOOtherFrameworkAddID)) {
			Object[] classpathVariables = this.selectClasspath();
			if (classpathVariables == null)
				return;
			for (int i = 0; i < classpathVariables.length; i++) {
				String classpathVariable = (String) classpathVariables[i];
				WOFrameworkAction.frameworkDialog(
					this.part,
					javaProject,
					classpathVariable,
					false,
					false);
				return;
			}
		}
		MessageDialog.openInformation(
			this.part.getSite().getShell(),
			Messages.getString("ErrorDialog.title"),
			Messages.getString("ErrorDialog.invalid.selection"));
	}

	/**
	 * @return
	 */
	private Object[] selectClasspath() {
		SelectionDialog dialog =
			new SelectionDialog(part.getSite().getShell()) {
			private Table includeTable;

			protected Control createDialogArea(Composite parentComposite) {
				Composite composite =
					(Composite) super.createDialogArea(parentComposite);
				Composite parent = new Composite(composite, SWT.NULL);
				GridLayout layout = new GridLayout();
				layout.marginWidth = 0;
				layout.marginHeight = 0;
				layout.numColumns = 2;
				parent.setLayout(layout);
				GridData data = new GridData();
				data.verticalAlignment = GridData.FILL;
				data.horizontalAlignment = GridData.FILL;
				parent.setLayoutData(data);

				//includeTable = new Table(parent, SWT.CHECK | SWT.BORDER);
				includeTable = new Table(parent, SWT.CHECK | SWT.BORDER);
				GridData gd = new GridData(GridData.FILL_BOTH);
				gd.widthHint = this.convertWidthInCharsToPixels(20);
				//gd.widthHint = 150;
				gd.heightHint = 200;
				includeTable.setLayoutData(gd);
				List list = this.getInitialElementSelections();
				for (int i = 0; i < list.size(); i++) {
					String string = list.get(i).toString();
					if (WOLipsCore
						.getClasspathVariablesAccessor()
						.isUnderWOLipsControl(string))
						continue;
					TableItem item = new TableItem(includeTable, SWT.NONE);
					item.setText(string);
					item.setChecked(false);
				}
				Dialog.applyDialogFont(parent);
				return parent;
			}

			protected void okPressed() {
				TableItem[] items = includeTable.getItems();
				ArrayList arrayList = new ArrayList();
				for (int i = 0; i < items.length; i++) {
					TableItem item = items[i];
					if (item.getChecked())
						arrayList.add(item.getText());
				}
				this.setResult(arrayList);
				super.okPressed();
			}
		};
		String[] classpathVariables = JavaCore.getClasspathVariableNames();
		dialog.setInitialSelections(classpathVariables);
		dialog.setTitle(
			Messages.getString(
				WOFrameworkAction.SelectOneClasspathVariableKey));
		dialog.open();

		if (dialog.getReturnCode() != Window.OK)
			return null;
		Object[] result = dialog.getResult();
		return result;
	}

	private static void frameworkDialog(
		IWorkbenchPart aPart,
		IJavaProject aProject,
		String classPathVariableName,
		boolean addLocalFrameworkSectionToPBProject,
		boolean addLibraryFrameworks) {
		WOFrameworkDialogWrapper frameworkDialog =
			new WOFrameworkDialogWrapper(
				aPart,
				aProject,
				classPathVariableName,
				addLocalFrameworkSectionToPBProject,
				addLibraryFrameworks);
		frameworkDialog.executeDialog();
	}
}
