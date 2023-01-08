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

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.typed.BeanProperties;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.objectstyle.wolips.baseforplugins.util.ComparisonUtils;
import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.core.model.EOAttribute;
import org.objectstyle.wolips.eomodeler.core.model.EOAttributePath;
import org.objectstyle.wolips.eomodeler.utils.BooleanUpdateValueStrategy;
import org.objectstyle.wolips.eomodeler.utils.FormUtils;
import org.objectstyle.wolips.eomodeler.utils.UglyFocusHackWorkaroundListener;

public class EOAttributeAdvancedEditorSection extends AbstractPropertySection {
	private EOAttribute _attribute;

	private Button _readOnlyButton;

	private Button _clientClassPropertyButton;

	private Button _commonClassPropertyButton;

	private Button _generateSourceButton;

	private Text _readFormatText;

	private Text _writeFormatText;

	private DataBindingContext _bindingContext;

	public EOAttributeAdvancedEditorSection() {
		// DO NOTHING
	}

	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);
		Composite form = getWidgetFactory().createFlatFormComposite(parent);
		FormLayout formLayout = new FormLayout();
		form.setLayout(formLayout);

		Composite topForm = FormUtils.createForm(getWidgetFactory(), form);

		getWidgetFactory().createCLabel(topForm, Messages.getString("EOAttribute." + EOAttribute.READ_FORMAT), SWT.NONE);
		_readFormatText = new Text(topForm, SWT.BORDER);
		GridData readFormatFieldLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		_readFormatText.setLayoutData(readFormatFieldLayoutData);
		UglyFocusHackWorkaroundListener.addListener(_readFormatText);

		getWidgetFactory().createCLabel(topForm, Messages.getString("EOAttribute." + EOAttribute.WRITE_FORMAT), SWT.NONE);
		_writeFormatText = new Text(topForm, SWT.BORDER);
		GridData writeFormatFieldLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		_writeFormatText.setLayoutData(writeFormatFieldLayoutData);
		UglyFocusHackWorkaroundListener.addListener(_writeFormatText);

		getWidgetFactory().createCLabel(topForm, "", SWT.NONE);
		_readOnlyButton = new Button(topForm, SWT.CHECK);
		_readOnlyButton.setText(Messages.getString("EOAttribute." + EOAttribute.READ_ONLY));

		getWidgetFactory().createCLabel(topForm, "", SWT.NONE);
		_clientClassPropertyButton = new Button(topForm, SWT.CHECK);
		_clientClassPropertyButton.setText(Messages.getString("EOAttribute." + EOAttribute.CLIENT_CLASS_PROPERTY));

		getWidgetFactory().createCLabel(topForm, "", SWT.NONE);
		_commonClassPropertyButton = new Button(topForm, SWT.CHECK);
		_commonClassPropertyButton.setText(Messages.getString("EOAttribute." + EOAttribute.COMMON_CLASS_PROPERTY));

		getWidgetFactory().createCLabel(topForm, "", SWT.NONE);
		_generateSourceButton = new Button(topForm, SWT.CHECK);
		_generateSourceButton.setText(Messages.getString("EOAttribute." + EOAttribute.GENERATE_SOURCE));
	}

	public void setInput(IWorkbenchPart part, ISelection selection) {
		if (ComparisonUtils.equals(selection, getSelection())) {
			return;
		}
		
		super.setInput(part, selection);
		disposeBindings();

		Object selectedObject = ((IStructuredSelection) selection).getFirstElement();
		if (selectedObject instanceof EOAttribute) {
			_attribute = (EOAttribute) selectedObject;
		} else if (selectedObject instanceof EOAttributePath) {
			_attribute = ((EOAttributePath) selectedObject).getChildAttribute();
		}

		if (_attribute != null) {
			_bindingContext = new DataBindingContext();
			_bindingContext.bindValue(
					WidgetProperties.buttonSelection().observe(_readOnlyButton),
					BeanProperties.value(EOAttribute.class, EOAttribute.READ_ONLY, Boolean.class).observe(_attribute),
					null, new BooleanUpdateValueStrategy());
			_bindingContext.bindValue(
					WidgetProperties.buttonSelection().observe(_clientClassPropertyButton),
					BeanProperties.value(EOAttribute.class, EOAttribute.CLIENT_CLASS_PROPERTY, Boolean.class).observe(_attribute),
					null, new BooleanUpdateValueStrategy());
			_bindingContext.bindValue(
					WidgetProperties.buttonSelection().observe(_commonClassPropertyButton),
					BeanProperties.value(EOAttribute.class, EOAttribute.COMMON_CLASS_PROPERTY, Boolean.class).observe(_attribute),
					null, new BooleanUpdateValueStrategy());
			_bindingContext.bindValue(
					WidgetProperties.buttonSelection().observe(_generateSourceButton),
					BeanProperties.value(EOAttribute.class, EOAttribute.GENERATE_SOURCE, Boolean.class).observe(_attribute),
					null, new BooleanUpdateValueStrategy());
			_bindingContext.bindValue(
					WidgetProperties.text(SWT.Modify).observe(_readFormatText),
					BeanProperties.value(EOAttribute.class, EOAttribute.READ_FORMAT, String.class).observe(_attribute),
					null, null);
			_bindingContext.bindValue(
					WidgetProperties.text(SWT.Modify).observe(_writeFormatText),
					BeanProperties.value(EOAttribute.class, EOAttribute.WRITE_FORMAT, String.class).observe(_attribute),
					null, null);
		}
	}

	protected void disposeBindings() {
		if (_bindingContext != null) {
			_bindingContext.dispose();
		}
	}

	public void dispose() {
		super.dispose();
		disposeBindings();
	}
}
