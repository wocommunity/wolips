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
package org.objectstyle.wolips.eomodeler.editors.attribute;

import org.eclipse.jface.internal.databinding.provisional.DataBindingContext;
import org.eclipse.jface.internal.databinding.provisional.description.Property;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.core.model.EOAttribute;
import org.objectstyle.wolips.eomodeler.core.model.EOAttributePath;
import org.objectstyle.wolips.eomodeler.utils.BindingFactory;

public class EOAttributeAdvancedEditorSection extends AbstractPropertySection {
	private EOAttribute myAttribute;

	private Button myReadOnlyButton;

	private Button myClientClassPropertyButton;

	// private Button myIndexedButton;
	private Text myReadFormatText;

	private Text myWriteFormatText;

	private DataBindingContext myBindingContext;

	public EOAttributeAdvancedEditorSection() {
		// DO NOTHING
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

		getWidgetFactory().createCLabel(topForm, Messages.getString("EOAttribute." + EOAttribute.READ_ONLY), SWT.NONE);
		myReadOnlyButton = new Button(topForm, SWT.CHECK);

		// getWidgetFactory().createCLabel(topForm,
		// Messages.getString("EOAttribute." + EOAttribute.INDEXED), SWT.NONE);
		// myIndexedButton = new Button(topForm, SWT.CHECK);

		getWidgetFactory().createCLabel(topForm, Messages.getString("EOAttribute." + EOAttribute.CLIENT_CLASS_PROPERTY), SWT.NONE);
		myClientClassPropertyButton = new Button(topForm, SWT.CHECK);

		getWidgetFactory().createCLabel(topForm, Messages.getString("EOAttribute." + EOAttribute.READ_FORMAT), SWT.NONE);
		myReadFormatText = new Text(topForm, SWT.BORDER);
		GridData readFormatFieldLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		myReadFormatText.setLayoutData(readFormatFieldLayoutData);

		getWidgetFactory().createCLabel(topForm, Messages.getString("EOAttribute." + EOAttribute.WRITE_FORMAT), SWT.NONE);
		myWriteFormatText = new Text(topForm, SWT.BORDER);
		GridData writeFormatFieldLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		myWriteFormatText.setLayoutData(writeFormatFieldLayoutData);
	}

	public void setInput(IWorkbenchPart _part, ISelection _selection) {
		super.setInput(_part, _selection);
		disposeBindings();

		Object selectedObject = ((IStructuredSelection) _selection).getFirstElement();
		if (selectedObject instanceof EOAttribute) {
			myAttribute = (EOAttribute) selectedObject;
		} else if (selectedObject instanceof EOAttributePath) {
			myAttribute = ((EOAttributePath) selectedObject).getChildAttribute();
		}

		if (myAttribute != null) {
			myBindingContext = BindingFactory.createContext();
			myBindingContext.bind(myReadOnlyButton, new Property(myAttribute, EOAttribute.READ_ONLY), null);
			// myBindingContext.bind(myIndexedButton, new Property(myAttribute,
			// EOAttribute.INDEXED), null);
			myBindingContext.bind(myClientClassPropertyButton, new Property(myAttribute, EOAttribute.CLIENT_CLASS_PROPERTY), null);
			myBindingContext.bind(myReadFormatText, new Property(myAttribute, EOAttribute.READ_FORMAT), null);
			myBindingContext.bind(myWriteFormatText, new Property(myAttribute, EOAttribute.WRITE_FORMAT), null);
		}
	}

	protected void disposeBindings() {
		if (myBindingContext != null) {
			myBindingContext.dispose();
		}
	}

	public void dispose() {
		super.dispose();
		disposeBindings();
	}
}
