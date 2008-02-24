/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2005 The ObjectStyle Group,
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
package org.objectstyle.wolips.apieditor.editor;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.objectstyle.wolips.bindings.api.ApiModel;
import org.objectstyle.wolips.bindings.api.Binding;
import org.objectstyle.wolips.bindings.api.IApiBinding;

/**
 * @author uli
 */
public class BindingDetailsPage implements IDetailsPage {
	IManagedForm managedForm;

	TableViewer viewer;

	Binding binding;

	Text name;

	Button requiredFlag;

	Button willSetFlag;

	Button[] defaults;

	ApiModel apiModel;

	public BindingDetailsPage() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.IDetailsPage#initialize(org.eclipse.ui.forms.IManagedForm)
	 */
	public void initialize(IManagedForm form) {
		this.managedForm = form;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.IDetailsPage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	public void createContents(Composite parent) {
		TableWrapLayout layout = new TableWrapLayout();
		layout.topMargin = 5;
		layout.leftMargin = 5;
		layout.rightMargin = 2;
		layout.bottomMargin = 2;
		parent.setLayout(layout);

		FormToolkit toolkit = managedForm.getToolkit();
		Section s1 = toolkit.createSection(parent, Section.DESCRIPTION);
		s1.marginWidth = 10;
		s1.setText("Binding Details");
		s1.setDescription("Set the properties of the selected binding.");
		TableWrapData td = new TableWrapData(TableWrapData.FILL, TableWrapData.TOP);
		td.grabHorizontal = true;
		s1.setLayoutData(td);
		toolkit.createCompositeSeparator(s1);
		Composite client = toolkit.createComposite(s1);
		GridLayout glayout = new GridLayout();
		glayout.marginWidth = glayout.marginHeight = 0;
		glayout.numColumns = 2;
		client.setLayout(glayout);

		SelectionListener choiceListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Integer value = (Integer) e.widget.getData();
				if (binding != null) {
					binding.setDefaults(value.intValue());
					managedForm.dirtyStateChanged();
				}
			}
		};

		GridData gd;
		toolkit.createLabel(client, "Name:");
		name = toolkit.createText(client, "", SWT.SINGLE);
		name.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (binding != null) {
					binding.setName(name.getText());
					managedForm.dirtyStateChanged();
				}
			}
		});
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		gd.widthHint = 10;
		name.setLayoutData(gd);

		createSpacer(toolkit, client, 2);
		requiredFlag = toolkit.createButton(client, "Required", SWT.CHECK);
		requiredFlag.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (binding != null) {
					binding.setIsRequired(requiredFlag.getSelection());
					managedForm.dirtyStateChanged();
				}
			}
		});
		gd = new GridData();
		gd.horizontalSpan = 2;

		requiredFlag.setLayoutData(gd);

		willSetFlag = toolkit.createButton(client, "Will Set", SWT.CHECK);
		willSetFlag.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (binding != null) {
					binding.setIsWillSet(willSetFlag.getSelection());
					managedForm.dirtyStateChanged();
				}
			}
		});
		gd = new GridData();
		gd.horizontalSpan = 2;
		willSetFlag.setLayoutData(gd);

		createSpacer(toolkit, client, 2);

		toolkit.createLabel(client, "Value Set:");
		defaults = new Button[IApiBinding.ALL_DEFAULTS.length];
		for (int i = 0; i < IApiBinding.ALL_DEFAULTS.length; i++) {
			defaults[i] = toolkit.createButton(client, IApiBinding.ALL_DEFAULTS[i], SWT.RADIO);
			defaults[i].setData(new Integer(i));
			defaults[i].addSelectionListener(choiceListener);
			gd = new GridData();
			gd.horizontalSpan = 2;
			defaults[i].setLayoutData(gd);
		}

		toolkit.paintBordersFor(s1);
		s1.setClient(client);
	}

	private void createSpacer(FormToolkit toolkit, Composite parent, int span) {
		Label spacer = toolkit.createLabel(parent, "");
		GridData gd = new GridData();
		gd.horizontalSpan = span;
		spacer.setLayoutData(gd);
	}

	private void update() {
		int selectedDefaults = binding.getSelectedDefaults();
		for (int i = 0; i < defaults.length; i++) {
			if (i == selectedDefaults) {
				defaults[i].setSelection(true);
			} else {
				defaults[i].setSelection(false);
			}
		}
		requiredFlag.setSelection(binding != null && binding.isRequired());
		willSetFlag.setSelection(binding != null && binding.isWillSet());
		name.setText(binding != null && binding.getName() != null ? binding.getName() : "");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.IDetailsPage#inputChanged(org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void selectionChanged(IFormPart part, ISelection selection) {
		IStructuredSelection ssel = (IStructuredSelection) selection;
		if (ssel.size() == 1) {
			binding = (Binding) ssel.getFirstElement();
		} else
			binding = null;
		update();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.IDetailsPage#commit()
	 */
	public void commit(boolean onSave) {
		// nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.IDetailsPage#setFocus()
	 */
	public void setFocus() {
		name.setFocus();
		// choices[0].setFocus();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.IDetailsPage#dispose()
	 */
	public void dispose() {
		// nothing to dispose
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.IDetailsPage#isDirty()
	 */
	public boolean isDirty() {
		if (binding == null) {
			return false;
		}
		return binding.apiModel.isDirty();
	}

	public boolean isStale() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.IDetailsPage#refresh()
	 */
	public void refresh() {
		update();
	}

	public boolean setFormInput(Object input) {
		return false;
	}
}
