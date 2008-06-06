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
/*Portions of this code are Copyright Apple Inc. 2008 and licensed under the
ObjectStyle Group Software License, version 1.0.  This license from Apple
applies solely to the actual code contributed by Apple and to no other code.
No other license or rights are granted by Apple, explicitly, by implication,
by estoppel, or otherwise.  All rights reserved.*/
package org.objectstyle.wolips.wizards;

import java.io.File;
import java.util.HashMap;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.dialogs.WizardDataTransferPage;
import org.eclipse.ui.model.WorkbenchContentProvider;


public class EOModelResourceImportPage extends WizardDataTransferPage {
	HashMap <String,String> _modelPaths; //key = file name, value = full path to file including file name
	Table _table;
	static Image _eomodelIcon;
	static boolean _requiresModel;

	public EOModelResourceImportPage(String name) {
		this(name, false);
	}

	public EOModelResourceImportPage(String name, boolean requiresModel) {
		super(name);
		_modelPaths = new HashMap <String, String>();
		_table = null;
		ImageDescriptor id = WizardsPlugin.EOMODEL_ICON();
		_eomodelIcon = id.createImage();
		_requiresModel = requiresModel;
	}

	//key = file name, value = full path to file including file name
	public HashMap <String,String> getModelPaths() {
		return _modelPaths;
	}

	protected void createSourceGroup(Composite parent) {

		//Selected models table
		_table = new Table (parent, SWT.VIRTUAL | SWT.FULL_SELECTION | SWT.BORDER | SWT.MULTI);
		_table.setFont(parent.getFont());
		_table.setHeaderVisible(true);
		_table.setLinesVisible(true);

		GridLayout tablelayout = new GridLayout();
		tablelayout.numColumns = 1;
		_table.setLayout(tablelayout);
		GridData data= new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL);
		data.grabExcessHorizontalSpace = true;
		_table.setLayoutData(data);

		//layout for buttons
		Composite containerGroup = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		containerGroup.setLayout(layout);
		containerGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		containerGroup.setFont(parent.getFont());

		//Browse button
		Button browseButton = new Button(containerGroup, SWT.PUSH);
		browseButton.setText("Browse...");
		browseButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		browseButton.addSelectionListener(new EOModelSelectionListener());
		browseButton.setFont(parent.getFont());
		setButtonLayoutData(browseButton);

		//Delete button
		Button tableRemoveButton = new Button(containerGroup, SWT.PUSH);
		tableRemoveButton.setText("Delete");
		tableRemoveButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		tableRemoveButton.addSelectionListener(new TableRemoveSelectionListener());
		tableRemoveButton.setFont(parent.getFont());
		setButtonLayoutData(tableRemoveButton);


	}

	@Override
	protected void createOptionsGroup(Composite parent) {
		//Override default options group and display nothing
	}

	/** (non-Javadoc)
	 * Method declared on IDialogPage.
	 */
	public void createControl(Composite parent) {

		initializeDialogUnits(parent);

		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
				| GridData.HORIZONTAL_ALIGN_FILL));
		composite.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		composite.setFont(parent.getFont());

		createSourceGroup(composite);

		createOptionsGroup(composite);

		if (_requiresModel) {
			setPageComplete(false);
		}

		restoreWidgetValues();
		updateWidgetEnablements();
		setTitle(Messages.getString("EOModelResourceImportPage.title"));
		setMessage(Messages.getString("EOModelResourceImportPage.description"));
		setErrorMessage(null);	// should not initially have error message

		setControl(composite);
	}

	protected ITreeContentProvider getFileProvider() {
		return new WorkbenchContentProvider();
	}

	protected ITreeContentProvider getFolderProvider() {
		return new WorkbenchContentProvider();
	}

	public void updatePageComplete() {
		if (_requiresModel && getModelPaths().size() > 1) {
			setMessage(null);
			setErrorMessage("EOModel not selected");
			setPageComplete(false);
		}

		setPageComplete(true);
		setMessage(Messages.getString("EOModelResourceImportPage.description"));
		setErrorMessage(null);
	}

	//Responder for EOModel browse button
	class EOModelSelectionListener extends SelectionAdapter {
		@SuppressWarnings("synthetic-access")
		public void widgetSelected(SelectionEvent e) {
			DirectoryDialog ddialog = new DirectoryDialog(getShell(), SWT.OPEN | SWT.MULTI);
			ddialog.setMessage("Select EOModel bundle");
			ddialog.open();
			String rootPath = ddialog.getFilterPath();
			System.out.println("rootPath: "+rootPath);
			if (!rootPath.endsWith(".eomodeld")){
				displayErrorDialog(rootPath+" is not an EOModel file and will not be copied");
			} else  {
				if (!_modelPaths.values().contains(rootPath)) {
					_modelPaths.put(rootPath.substring(rootPath.lastIndexOf(File.separator)), rootPath);
					updatePageComplete();
					if (_table != null) {
						TableItem item = new TableItem(_table, SWT.NATIVE);
						item.setImage(_eomodelIcon);
						item.setText(rootPath);
					}
				}
			}
		}
	}

	class TableRemoveSelectionListener extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
			if (_table != null) {
				int[] selectedIndexes = _table.getSelectionIndices();
				for (int anIndex : selectedIndexes) {
					TableItem anItem = _table.getItem(anIndex);
					//key is file name, so extract from path
					String key = anItem.getText().substring(anItem.getText().lastIndexOf(File.separator)+1);
					_modelPaths.remove(key);
				}
				_table.remove(selectedIndexes);
			}
		}
	}

	@Override
	protected boolean allowNewContainerName() {
		return false;
	}

	public void handleEvent(Event arg0) {
		//handle our own events
	}

}
