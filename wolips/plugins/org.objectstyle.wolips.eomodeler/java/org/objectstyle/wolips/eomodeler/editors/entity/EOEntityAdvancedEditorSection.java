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

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.objectstyle.wolips.baseforplugins.util.ComparisonUtils;
import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.utils.BooleanUpdateValueStrategy;
import org.objectstyle.wolips.eomodeler.utils.ComboViewerBinding;
import org.objectstyle.wolips.eomodeler.utils.FormUtils;

public class EOEntityAdvancedEditorSection extends AbstractPropertySection {
	private EOEntity _entity;

	private Text _maxNumberOfInstancesToBatchFetchText;

	private Button _cacheInMemoryButton;

	private Button _readOnlyButton;
	
	private Button _immutableButton;

	private Button _generateSourceButton;

	private Button _rawRowsOnlyButton;

	private Text _externalQueryText;

	private Text _clientClassNameText;

	private Text _parentClassNameText;

	private ComboViewer _partialEntityComboViewer;

	private ComboViewerBinding _partialEntityBinding;

	private DataBindingContext _bindingContext;

	public EOEntityAdvancedEditorSection() {
		// DO NOTHING
	}

	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);
		Composite form = getWidgetFactory().createFlatFormComposite(parent);
		FormLayout formLayout = new FormLayout();
		form.setLayout(formLayout);

		Composite topForm = FormUtils.createForm(getWidgetFactory(), form);

		getWidgetFactory().createCLabel(topForm, Messages.getString("EOEntity." + EOEntity.MAX_NUMBER_OF_INSTANCES_TO_BATCH_FETCH), SWT.NONE);
		_maxNumberOfInstancesToBatchFetchText = new Text(topForm, SWT.BORDER);
		GridData maxNumberOfInstancesToBatchFetchFieldLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		_maxNumberOfInstancesToBatchFetchText.setLayoutData(maxNumberOfInstancesToBatchFetchFieldLayoutData);

		getWidgetFactory().createCLabel(topForm, "", SWT.NONE);
		_cacheInMemoryButton = new Button(topForm, SWT.CHECK);
		_cacheInMemoryButton.setText(Messages.getString("EOEntity." + EOEntity.CACHES_OBJECTS));

		getWidgetFactory().createCLabel(topForm, "", SWT.NONE);
		_readOnlyButton = new Button(topForm, SWT.CHECK);
		_readOnlyButton.setText(Messages.getString("EOEntity." + EOEntity.READ_ONLY));

		getWidgetFactory().createCLabel(topForm, "", SWT.NONE);
		_immutableButton = new Button(topForm, SWT.CHECK);
		_immutableButton.setText(Messages.getString("EOEntity." + EOEntity.IMMUTABLE));

		getWidgetFactory().createCLabel(topForm, "", SWT.NONE);
		_generateSourceButton = new Button(topForm, SWT.CHECK);
		_generateSourceButton.setText(Messages.getString("EOEntity." + EOEntity.GENERATE_SOURCE));

		getWidgetFactory().createCLabel(topForm, "", SWT.NONE);
		_rawRowsOnlyButton = new Button(topForm, SWT.CHECK);
		_rawRowsOnlyButton.setText(Messages.getString("EOEntity." + EOEntity.RAW_ROWS_ONLY));

		getWidgetFactory().createCLabel(topForm, Messages.getString("EOEntity." + EOEntity.EXTERNAL_QUERY), SWT.NONE);
		_externalQueryText = new Text(topForm, SWT.BORDER);
		GridData externalQueryFieldLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		_externalQueryText.setLayoutData(externalQueryFieldLayoutData);

		getWidgetFactory().createCLabel(topForm, Messages.getString("EOEntity." + EOEntity.CLIENT_CLASS_NAME), SWT.NONE);
		_clientClassNameText = new Text(topForm, SWT.BORDER);
		GridData clientClassNameLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		_clientClassNameText.setLayoutData(clientClassNameLayoutData);

		getWidgetFactory().createCLabel(topForm, Messages.getString("EOEntity." + EOEntity.PARENT_CLASS_NAME), SWT.NONE);
		_parentClassNameText = new Text(topForm, SWT.BORDER);
		GridData parentClassNameLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		_parentClassNameText.setLayoutData(parentClassNameLayoutData);

		getWidgetFactory().createCLabel(topForm, Messages.getString("EOEntity." + EOEntity.PARTIAL_ENTITY), SWT.NONE);
		Combo partialEntityCombo = new Combo(topForm, SWT.BORDER | SWT.FLAT | SWT.READ_ONLY);
		_partialEntityComboViewer = new ComboViewer(partialEntityCombo);
		_partialEntityComboViewer.setLabelProvider(new EOEntityLabelProvider());
		_partialEntityComboViewer.setContentProvider(new EOEntityListContentProvider(true, false, false));
		GridData entityComboLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		partialEntityCombo.setLayoutData(entityComboLayoutData);
	}

	public void setInput(IWorkbenchPart part, ISelection selection) {
		if (ComparisonUtils.equals(selection, getSelection())) {
			return;
		}
		
		super.setInput(part, selection);
		disposeBindings();

		Object selectedObject = ((IStructuredSelection) selection).getFirstElement();
		_entity = (EOEntity) selectedObject;
		if (_entity != null) {
			_bindingContext = new DataBindingContext();
			_bindingContext.bindValue(SWTObservables.observeText(_maxNumberOfInstancesToBatchFetchText, SWT.Modify), BeansObservables.observeValue(_entity, EOEntity.MAX_NUMBER_OF_INSTANCES_TO_BATCH_FETCH), null, null);
			// new BindSpec(null, null, new RegexStringValidator("^[0-9]*$",
			// "^[0-9]+$", "Please enter a number"), null));
			_bindingContext.bindValue(SWTObservables.observeSelection(_cacheInMemoryButton), BeansObservables.observeValue(_entity, EOEntity.CACHES_OBJECTS), null, new BooleanUpdateValueStrategy());
			_bindingContext.bindValue(SWTObservables.observeSelection(_readOnlyButton), BeansObservables.observeValue(_entity, EOEntity.READ_ONLY), null, new BooleanUpdateValueStrategy());
			_bindingContext.bindValue(SWTObservables.observeSelection(_immutableButton), BeansObservables.observeValue(_entity, EOEntity.IMMUTABLE), null, new BooleanUpdateValueStrategy());
			_bindingContext.bindValue(SWTObservables.observeSelection(_generateSourceButton), BeansObservables.observeValue(_entity, EOEntity.GENERATE_SOURCE), null, new BooleanUpdateValueStrategy());
			_bindingContext.bindValue(SWTObservables.observeSelection(_rawRowsOnlyButton), BeansObservables.observeValue(_entity, EOEntity.RAW_ROWS_ONLY), null, new BooleanUpdateValueStrategy());
			_bindingContext.bindValue(SWTObservables.observeText(_externalQueryText, SWT.Modify), BeansObservables.observeValue(_entity, EOEntity.EXTERNAL_QUERY), null, null);
			_bindingContext.bindValue(SWTObservables.observeText(_clientClassNameText, SWT.Modify), BeansObservables.observeValue(_entity, EOEntity.CLIENT_CLASS_NAME), null, null);
			_bindingContext.bindValue(SWTObservables.observeText(_parentClassNameText, SWT.Modify), BeansObservables.observeValue(_entity, EOEntity.PARENT_CLASS_NAME), null, null);

			_partialEntityComboViewer.setInput(_entity);
			_partialEntityBinding = new ComboViewerBinding(_partialEntityComboViewer, _entity, EOEntity.PARTIAL_ENTITY, _entity.getModel(), EOModel.ENTITIES, EOEntityListContentProvider.BLANK_ENTITY);
		}
	}

	protected void disposeBindings() {
		if (_bindingContext != null) {
			_bindingContext.dispose();
			_partialEntityBinding.dispose();
		}
	}

	public void dispose() {
		super.dispose();
		disposeBindings();
	}
}
