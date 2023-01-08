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
package org.objectstyle.wolips.eomodeler.editors.entityIndex;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.typed.BeanProperties;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.objectstyle.wolips.baseforplugins.util.ComparisonUtils;
import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.core.model.EOEntityIndex;
import org.objectstyle.wolips.eomodeler.utils.ComboViewerBinding;
import org.objectstyle.wolips.eomodeler.utils.FormUtils;
import org.objectstyle.wolips.eomodeler.utils.UglyFocusHackWorkaroundListener;

public class EOEntityIndexBasicEditorSection extends AbstractPropertySection implements ISelectionChangedListener {
	private EOEntityIndex _entityIndex;

	private Text _nameText;

	private ComboViewer _constraintCombo;

	private ComboViewer _indexTypeCombo;

	private ComboViewer _orderCombo;

	private EOEntityIndexAttributesEditor _attributesEditor;

	private DataBindingContext _bindingContext;

	private ComboViewerBinding _constraintBinding;

	private ComboViewerBinding _indexTypeBinding;

	private ComboViewerBinding _orderBinding;

	public EOEntityIndexBasicEditorSection() {
		// DO NOTHING
	}
	
	@Override
	public boolean shouldUseExtraSpace() {
		return true;
	}

	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);
		Composite form = getWidgetFactory().createFlatFormComposite(parent);
		FormLayout formLayout = new FormLayout();
		form.setLayout(formLayout);

		Composite topForm = FormUtils.createForm(getWidgetFactory(), form);

		getWidgetFactory().createCLabel(topForm, Messages.getString("EOEntityIndex." + EOEntityIndex.NAME), SWT.NONE);
		_nameText = new Text(topForm, SWT.BORDER);
		GridData nameLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		_nameText.setLayoutData(nameLayoutData);
		UglyFocusHackWorkaroundListener.addListener(_nameText);

		getWidgetFactory().createCLabel(topForm, Messages.getString("EOEntityIndex." + EOEntityIndex.CONSTRAINT), SWT.NONE);
		Combo constraintCombo = new Combo(topForm, SWT.BORDER | SWT.FLAT | SWT.READ_ONLY);
		_constraintCombo = new ComboViewer(constraintCombo);
		_constraintCombo.setLabelProvider(new EOEntityIndexConstraintLabelProvider());
		_constraintCombo.setContentProvider(new EOEntityIndexConstraintContentProvider());
		GridData constraintComboLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		constraintCombo.setLayoutData(constraintComboLayoutData);

		getWidgetFactory().createCLabel(topForm, Messages.getString("EOEntityIndex." + EOEntityIndex.INDEX_TYPE), SWT.NONE);
		Combo indexTypeCombo = new Combo(topForm, SWT.BORDER | SWT.FLAT | SWT.READ_ONLY);
		_indexTypeCombo = new ComboViewer(indexTypeCombo);
		_indexTypeCombo.setLabelProvider(new EOEntityIndexIndexTypeLabelProvider());
		_indexTypeCombo.setContentProvider(new EOEntityIndexIndexTypeContentProvider());
		GridData indexTypeComboLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		indexTypeCombo.setLayoutData(indexTypeComboLayoutData);

		getWidgetFactory().createCLabel(topForm, Messages.getString("EOEntityIndex." + EOEntityIndex.ORDER), SWT.NONE);
		Combo orderCombo = new Combo(topForm, SWT.BORDER | SWT.FLAT | SWT.READ_ONLY);
		_orderCombo = new ComboViewer(orderCombo);
		_orderCombo.setLabelProvider(new EOEntityIndexOrderLabelProvider());
		_orderCombo.setContentProvider(new EOEntityIndexOrderContentProvider());
		GridData orderComboLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		orderCombo.setLayoutData(orderComboLayoutData);

		CLabel attributesLabel = getWidgetFactory().createCLabel(topForm, Messages.getString("EOEntityIndex." + EOEntityIndex.ATTRIBUTES), SWT.NONE);
		attributesLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		_attributesEditor = new EOEntityIndexAttributesEditor(topForm, SWT.NONE);
		GridData attributesLayoutData = new GridData(GridData.FILL_BOTH);
		_attributesEditor.setLayoutData(attributesLayoutData);
	}

	public void setInput(IWorkbenchPart part, ISelection selection) {
		if (ComparisonUtils.equals(selection, getSelection())) {
			return;
		}
		
		super.setInput(part, selection);
		disposeBindings();

		Object selectedObject = ((IStructuredSelection) selection).getFirstElement();
		_entityIndex = (EOEntityIndex) selectedObject;
		if (_entityIndex != null) {
			_constraintCombo.setInput(_entityIndex);
			_indexTypeCombo.setInput(_entityIndex);
			_orderCombo.setInput(_entityIndex);
			_attributesEditor.setEntityIndex(_entityIndex);

			_bindingContext = new DataBindingContext();
			_bindingContext.bindValue(
					WidgetProperties.text(SWT.Modify).observe(_nameText),
					BeanProperties.value(EOEntityIndex.class, EOEntityIndex.NAME, String.class).observe(_entityIndex),
					null, null);
			// _bindingContext.bindValue(ViewersObservables.observeSingleSelection(_constraintCombo),
			// BeansObservables.observeValue(_entityIndex,
			// EOEntityIndex.CONSTRAINT), null, null);
			// _bindingContext.bindValue(ViewersObservables.observeSingleSelection(_indexTypeCombo),
			// BeansObservables.observeValue(_entityIndex,
			// EOEntityIndex.INDEX_TYPE), null, null);
			// _bindingContext.bindValue(ViewersObservables.observeSingleSelection(_orderCombo),
			// BeansObservables.observeValue(_entityIndex, EOEntityIndex.ORDER),
			// null, null);

			_constraintBinding = new ComboViewerBinding(_constraintCombo, _entityIndex, EOEntityIndex.CONSTRAINT, null, null, null);
			_indexTypeBinding = new ComboViewerBinding(_indexTypeCombo, _entityIndex, EOEntityIndex.INDEX_TYPE, null, null, null);
			_orderBinding = new ComboViewerBinding(_orderCombo, _entityIndex, EOEntityIndex.ORDER, null, null, null);
		}
	}

	protected void disposeBindings() {
		if (_bindingContext != null) {
			_bindingContext.dispose();
			_constraintBinding.dispose();
			_indexTypeBinding.dispose();
			_orderBinding.dispose();
		}
	}

	public void dispose() {
		super.dispose();
		disposeBindings();
	}

	public void selectionChanged(SelectionChangedEvent event) {
		// DO NOTHING
	}
}
