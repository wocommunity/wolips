/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2006 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * 3. The end-user documentation included with the redistribution, if any, must
 * include the following acknowlegement: "This product includes software
 * developed by the ObjectStyle Group (http://objectstyle.org/)." Alternately,
 * this acknowlegement may appear in the software itself, if and wherever such
 * third-party acknowlegements normally appear.
 * 
 * 4. The names "ObjectStyle Group" and "Cayenne" must not be used to endorse or
 * promote products derived from this software without prior written permission.
 * For written permission, please contact andrus@objectstyle.org.
 * 
 * 5. Products derived from this software may not be called "ObjectStyle" nor
 * may "ObjectStyle" appear in their names without prior written permission of
 * the ObjectStyle Group.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * OBJECTSTYLE GROUP OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals on
 * behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/>.
 *  
 */
package org.objectstyle.wolips.eomodeler.editors.entity;

import org.eclipse.jface.internal.databinding.provisional.DataBindingContext;
import org.eclipse.jface.internal.databinding.provisional.description.Property;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.core.utils.ComparisonUtils;
import org.objectstyle.wolips.eomodeler.utils.BindingFactory;
import org.objectstyle.wolips.eomodeler.utils.ComboViewerBinding;

public class EOEntityBasicEditorSection extends AbstractPropertySection {
	private EOEntity myEntity;

	private Text myNameText;

	private Text myExternalNameText;

	private Text myClassNameText;

	private ComboViewer myParentEntityComboViewer;

	private Text myRestrictingQualifierText;

	private Button myAbstractButton;

	private DataBindingContext myBindingContext;

	private ComboViewerBinding myParentEntityBinding;

	private EntityNameSyncer myNameSyncer;

	public EOEntityBasicEditorSection() {
		myNameSyncer = new EntityNameSyncer();
	}

	public void createControls(Composite _parent, TabbedPropertySheetPage _tabbedPropertySheetPage) {
		super.createControls(_parent, _tabbedPropertySheetPage);
		Composite form = getWidgetFactory().createFlatFormComposite(_parent);
		FormLayout formLayout = new FormLayout();
		form.setLayout(formLayout);

		Composite topForm = getWidgetFactory().createPlainComposite(form, SWT.NONE);
		FormData topFormData = new FormData();
		topFormData.top = new FormAttachment(0, 5);
		topFormData.left = new FormAttachment(0, 5);
		topFormData.right = new FormAttachment(100, -5);
		topForm.setLayoutData(topFormData);

		GridLayout topFormLayout = new GridLayout();
		topFormLayout.numColumns = 2;
		topForm.setLayout(topFormLayout);

		getWidgetFactory().createCLabel(topForm, Messages.getString("EOEntity." + EOEntity.NAME), SWT.NONE);
		myNameText = new Text(topForm, SWT.BORDER);
		GridData nameFieldLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		myNameText.setLayoutData(nameFieldLayoutData);

		getWidgetFactory().createCLabel(topForm, Messages.getString("EOEntity." + EOEntity.EXTERNAL_NAME), SWT.NONE);
		myExternalNameText = new Text(topForm, SWT.BORDER);
		GridData externalNameFieldLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		myExternalNameText.setLayoutData(externalNameFieldLayoutData);

		getWidgetFactory().createCLabel(topForm, Messages.getString("EOEntity." + EOEntity.CLASS_NAME), SWT.NONE);
		myClassNameText = new Text(topForm, SWT.BORDER);
		GridData classNameFieldLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		myClassNameText.setLayoutData(classNameFieldLayoutData);

		getWidgetFactory().createCLabel(topForm, Messages.getString("EOEntity." + EOEntity.PARENT), SWT.NONE);
		Combo parentEntityCombo = new Combo(topForm, SWT.BORDER | SWT.FLAT | SWT.READ_ONLY);
		myParentEntityComboViewer = new ComboViewer(parentEntityCombo);
		myParentEntityComboViewer.setLabelProvider(new EOEntityLabelProvider());
		myParentEntityComboViewer.setContentProvider(new EOEntityListContentProvider(true, false));
		GridData entityComboLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		parentEntityCombo.setLayoutData(entityComboLayoutData);

		getWidgetFactory().createCLabel(topForm, Messages.getString("EOEntity." + EOEntity.RESTRICTING_QUALIFIER), SWT.NONE);
		myRestrictingQualifierText = new Text(topForm, SWT.BORDER);
		GridData restrictingQualifierFieldLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		myRestrictingQualifierText.setLayoutData(restrictingQualifierFieldLayoutData);

		getWidgetFactory().createCLabel(topForm, Messages.getString("EOEntity." + EOEntity.ABSTRACT_ENTITY), SWT.NONE);
		myAbstractButton = new Button(topForm, SWT.CHECK);
	}

	public void setInput(IWorkbenchPart _part, ISelection _selection) {
		super.setInput(_part, _selection);
		Object selectedObject = ((IStructuredSelection) _selection).getFirstElement();
		EOEntity entity = (EOEntity) selectedObject;
		if (!ComparisonUtils.equals(entity, myEntity)) {
			disposeBindings();

			myEntity = entity;
			if (myEntity != null) {
				myBindingContext = BindingFactory.createContext();
				myBindingContext.bind(myNameText, new Property(myEntity, EOEntity.NAME), null);
				myBindingContext.bind(myExternalNameText, new Property(myEntity, EOEntity.EXTERNAL_NAME), null);
				myBindingContext.bind(myClassNameText, new Property(myEntity, EOEntity.CLASS_NAME), null);
				myBindingContext.bind(myRestrictingQualifierText, new Property(myEntity, EOEntity.RESTRICTING_QUALIFIER), null);
				myBindingContext.bind(myAbstractButton, new Property(myEntity, EOEntity.ABSTRACT_ENTITY), null);

				myParentEntityComboViewer.setInput(myEntity);
				myParentEntityBinding = new ComboViewerBinding(myParentEntityComboViewer, myEntity, EOEntity.PARENT, myEntity.getModel(), EOModel.ENTITIES, EOEntityListContentProvider.BLANK_ENTITY);
				myEntity.addPropertyChangeListener(EOEntity.NAME, myNameSyncer);
			}
		}
	}

	protected void disposeBindings() {
		if (myBindingContext != null) {
			myBindingContext.dispose();
		}
		if (myParentEntityBinding != null) {
			myParentEntityBinding.dispose();
		}
		if (myEntity != null) {
			myEntity.removePropertyChangeListener(EOEntity.NAME, myNameSyncer);
		}
	}

	public void dispose() {
		super.dispose();
		disposeBindings();
	}
}
