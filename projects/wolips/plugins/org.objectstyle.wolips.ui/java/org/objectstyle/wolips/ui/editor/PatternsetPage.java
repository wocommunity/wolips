/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2004 The ObjectStyle Group 
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

package org.objectstyle.wolips.ui.editor;

import java.util.ArrayList;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.objectstyle.wolips.commons.util.ArrayUtilities;
/**
 * @author ulrich
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class PatternsetPage extends FormPage {
	private ArrayList patternList;
	private Table table;
	private Button changeButton;
	private Button removeButton;

	/**
	 * @param editor
	 * @param patternList
	 */
	public PatternsetPage(PatternsetEditor editor, ArrayList patternList) {
		super(editor, "Pattern", "Pattern");
		this.patternList = patternList;
	}
	protected void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		form.setText("Pattern");
		//form.setBackgroundImage(ExamplesPlugin.getDefault().getImage(ExamplesPlugin.IMG_FORM_BG));
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		form.getBody().setLayout(layout);
		createTableSection(form, toolkit, "Pattern");
	}
	
	private void createTableSection(final ScrolledForm form, FormToolkit toolkit, String title) {
		Section section =
			toolkit.createSection(
				form.getBody(),
				Section.TWISTIE | Section.DESCRIPTION);
		section.setActiveToggleColor(
			toolkit.getHyperlinkGroup().getActiveForeground());
		section.setToggleColor(
			toolkit.getColors().getColor(FormColors.SEPARATOR));
		toolkit.createCompositeSeparator(section);
		Composite client = toolkit.createComposite(section, SWT.WRAP);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;

		client.setLayout(layout);
		this.table = toolkit.createTable(client, SWT.NULL);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 200;
		gd.widthHint = 100;
		this.table.setLayoutData(gd);
		for(int i = 0; i < this.patternList.size(); i++) {
			TableItem item = new TableItem(this.table, SWT.NONE);
			item.setText((String)this.patternList.get(i));
		}
		this.table.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				handleSelection();
			}
		});
		toolkit.paintBordersFor(client);
		Button b = toolkit.createButton(client, PatternsetEditorMessages.getString("PaternsetEditor.add"), SWT.PUSH);
		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		b.setLayoutData(gd);
		b.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				addPattern();
			}
		});
		this.removeButton = toolkit.createButton(client, PatternsetEditorMessages.getString("PaternsetEditor.remove"), SWT.PUSH);
		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		this.removeButton.setLayoutData(gd);
		this.removeButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				removePattern();
			}
		});
		this.changeButton = toolkit.createButton(client, PatternsetEditorMessages.getString("PaternsetEditor.change"), SWT.PUSH);
		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		this.changeButton.setLayoutData(gd);
		this.changeButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				changePattern();
			}
		});
		section.setText(title);
		section.setDescription("");
		section.setClient(client);
		section.setExpanded(true);
		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				form.reflow(false);
			}
		});
		gd = new GridData(GridData.FILL_BOTH);
		section.setLayoutData(gd);
		this.changeButton.setEnabled(false);
		this.removeButton.setEnabled(false);
	}
	
	void addPattern() {
		InputDialog patternDialog = new InputDialog(this.getEditorSite().getShell(), PatternsetEditorMessages.getString("PaternsetEditor.enterPatternShort"), PatternsetEditorMessages.getString("PaternsetEditor.enterPatternLong"), null, null); //$NON-NLS-1$ //$NON-NLS-2$
		patternDialog.open();
		if (patternDialog.getReturnCode() != Window.OK)
			return;
		String pattern = patternDialog.getValue();
		if (pattern.equals(""))
			return; //$NON-NLS-1$
		// Check if the item already exists
		TableItem[] items = this.table.getItems();
		for (int i = 0; i < items.length; i++) {
			if (items[i].getText().equals(pattern)) {
				MessageDialog.openWarning(this.getEditorSite().getShell(), PatternsetEditorMessages.getString("PaternsetEditor.patternExistsShort"), PatternsetEditorMessages.getString("PaternsetEditor.patternExistsLong")); //$NON-NLS-1$ //$NON-NLS-2$
				return;
			}
		}
		TableItem item = new TableItem(this.table, SWT.NONE);
		item.setText(pattern);
		this.patternList.add(pattern);
		this.markDirty();
	}

	void removePattern() {
		int[] selection = this.table.getSelectionIndices();
		this.table.remove(selection);
		if (selection == null)
			return;
		int[] newIndices = new int[selection.length];
		System.arraycopy(selection, 0, newIndices, 0, selection.length);
		ArrayUtilities.sort(selection);
		int last = -1;
		for (int i = 0; i < newIndices.length; i++) {
			int index = newIndices[i];
			if (index != last || i == 0) {
				this.patternList.remove(index);
			}

			last = index;
		}
		this.markDirty();
	}

	void changePattern() {
		int[] selection = this.table.getSelectionIndices();
		if (selection.length != 1)
			return;
		int index = selection[0];
		InputDialog patternDialog = new InputDialog(this.getEditorSite().getShell(), PatternsetEditorMessages.getString("PaternsetEditor.enterPatternShort"), PatternsetEditorMessages.getString("PaternsetEditor.enterPatternLong"), (String)this.patternList.get(index), null); //$NON-NLS-1$ //$NON-NLS-2$
		patternDialog.open();
		if (patternDialog.getReturnCode() != Window.OK)
			return;
		String pattern = patternDialog.getValue();
		TableItem item = this.table.getItem(index);
		item.setText(pattern);
		this.patternList.set(index, pattern);
		this.markDirty();
	}

	void handleSelection() {
		if (this.table.getSelectionCount() > 0) {
			this.changeButton.setEnabled(true);
			this.removeButton.setEnabled(true);
		} else {
			this.changeButton.setEnabled(false);
			this.removeButton.setEnabled(false);
		}
	}
	
	private void markDirty() {
		((PatternsetEditor)this.getEditor()).setDirty(true);
	}
}
