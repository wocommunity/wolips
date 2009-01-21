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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.objectstyle.wolips.baseforplugins.util.ComparisonUtils;
import org.objectstyle.wolips.eomodeler.Activator;
import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.core.model.AbstractEOArgument;
import org.objectstyle.wolips.eomodeler.core.model.EODataType;
import org.objectstyle.wolips.eomodeler.editors.dataType.CustomDataTypePanel;
import org.objectstyle.wolips.eomodeler.editors.dataType.DataDataTypePanel;
import org.objectstyle.wolips.eomodeler.editors.dataType.DateDataTypePanel;
import org.objectstyle.wolips.eomodeler.editors.dataType.DecimalNumberDataTypePanel;
import org.objectstyle.wolips.eomodeler.editors.dataType.DoubleDataTypePanel;
import org.objectstyle.wolips.eomodeler.editors.dataType.IDataTypePanel;
import org.objectstyle.wolips.eomodeler.editors.dataType.IntegerDataTypePanel;
import org.objectstyle.wolips.eomodeler.editors.dataType.StringDataTypePanel;
import org.objectstyle.wolips.eomodeler.utils.BooleanUpdateValueStrategy;
import org.objectstyle.wolips.eomodeler.utils.ComboViewerBinding;

public abstract class AbstractEOArgumentBasicEditorSection extends AbstractPropertySection {
	private static String COLUMN = "Column";

	private static String DERIVED = "Derived";

	private AbstractEOArgument _argument;

	private Text _nameText;

	private ComboViewer _derivedComboViewer;

	private Text _columnNameText;

	private Text _definitionText;

	private Text _externalTypeText;

	private Text _classNameText;

	private Button _allowNullsButton;

	private ComboViewer _dataTypeComboViewer;

	private StackLayout _columnNameDefinitionLayout;

	private StackLayout _dataTypeStackLayout;

	private Map<EODataType, Composite> _dataTypeToDataTypePanel;

	private DataBindingContext _bindingContext;

	private ComboViewerBinding _dataTypeBinding;

	private DataTypeChangeListener _dataTypeChangeListener;

	private Composite _dataTypePanel;

	private Composite _columnNameDefinitionComposite;

	public AbstractEOArgumentBasicEditorSection() {
		_dataTypeChangeListener = new DataTypeChangeListener();
	}

	public AbstractEOArgument getArgument() {
		return _argument;
	}
	
	public DataBindingContext getBindingContext() {
		return _bindingContext;
	}

	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);
		Composite form = getWidgetFactory().createFlatFormComposite(parent);
		FormLayout formLayout = new FormLayout();
		form.setLayout(formLayout);

		Composite topForm = getWidgetFactory().createPlainComposite(form, SWT.NONE);
		topForm.setBackgroundMode(SWT.INHERIT_DEFAULT);
		FormData topFormData = new FormData();
		topFormData.top = new FormAttachment(0, 5);
		topFormData.left = new FormAttachment(0, 5);
		topFormData.right = new FormAttachment(100, -5);
		topForm.setLayoutData(topFormData);

		GridLayout topFormLayout = new GridLayout();
		topFormLayout.numColumns = 2;
		topForm.setLayout(topFormLayout);

		getWidgetFactory().createCLabel(topForm, Messages.getString("AbstractEOArgument." + AbstractEOArgument.NAME), SWT.NONE);
		_nameText = new Text(topForm, SWT.BORDER);
		GridData nameFieldLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		_nameText.setLayoutData(nameFieldLayoutData);

		Combo derivedCombo = new Combo(topForm, SWT.BORDER | SWT.FLAT | SWT.READ_ONLY);
		_derivedComboViewer = new ComboViewer(derivedCombo);
		// myDerivedComboViewer.setLabelProvider(new EODerivedLabelProvider());
		_derivedComboViewer.setContentProvider(new EODerivedContentProvider());
		_derivedComboViewer.setInput(new String[] { AbstractEOArgumentBasicEditorSection.COLUMN, AbstractEOArgumentBasicEditorSection.DERIVED });
		_derivedComboViewer.addSelectionChangedListener(new ColumnDerivedChangeListener());
		GridData derivedComboLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		derivedCombo.setLayoutData(derivedComboLayoutData);

		_columnNameDefinitionComposite = getWidgetFactory().createPlainComposite(topForm, SWT.NONE);
		GridData columnNameDefinitionFieldLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		_columnNameDefinitionComposite.setLayoutData(columnNameDefinitionFieldLayoutData);
		_columnNameDefinitionLayout = new StackLayout();
		_columnNameDefinitionComposite.setLayout(_columnNameDefinitionLayout);

		getWidgetFactory().createCLabel(_columnNameDefinitionComposite, Messages.getString("AbstractEOArgument." + AbstractEOArgument.COLUMN_NAME), SWT.NONE);
		_columnNameText = new Text(_columnNameDefinitionComposite, SWT.BORDER);
		GridData externalNameFieldLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		_columnNameText.setLayoutData(externalNameFieldLayoutData);

		getWidgetFactory().createCLabel(_columnNameDefinitionComposite, Messages.getString("AbstractEOArgument." + AbstractEOArgument.DEFINITION), SWT.NONE);
		_definitionText = new Text(_columnNameDefinitionComposite, SWT.BORDER);
		GridData definitionFieldLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		_definitionText.setLayoutData(definitionFieldLayoutData);
		_columnNameDefinitionLayout.topControl = _columnNameText;

		getWidgetFactory().createCLabel(topForm, Messages.getString("AbstractEOArgument.settings"), SWT.NONE);
		
		Composite settingsComposite = new Composite(topForm, SWT.NONE);
		settingsComposite.setLayout(new RowLayout(SWT.HORIZONTAL));

		_addSettings(settingsComposite);

		_allowNullsButton = new Button(settingsComposite, SWT.TOGGLE);
		_allowNullsButton.setToolTipText(Messages.getString("AbstractEOArgument." + AbstractEOArgument.ALLOWS_NULL));
		_allowNullsButton.setImage(Activator.getDefault().getImageRegistry().get(Activator.ALLOW_NULL_ICON));

		_addComponents(topForm);

		getWidgetFactory().createCLabel(topForm, Messages.getString("AbstractEOArgument." + AbstractEOArgument.EXTERNAL_TYPE), SWT.NONE);
		_externalTypeText = new Text(topForm, SWT.BORDER);
		GridData externalTypeFieldLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		_externalTypeText.setLayoutData(externalTypeFieldLayoutData);

		getWidgetFactory().createCLabel(topForm, Messages.getString("AbstractEOArgument." + AbstractEOArgument.CLASS_NAME), SWT.NONE);
		_classNameText = new Text(topForm, SWT.BORDER);
		GridData classNameFieldLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		_classNameText.setLayoutData(classNameFieldLayoutData);

		getWidgetFactory().createCLabel(topForm, Messages.getString("AbstractEOArgument." + AbstractEOArgument.DATA_TYPE), SWT.NONE);
		Combo dataTypeCombo = new Combo(topForm, SWT.BORDER | SWT.FLAT | SWT.READ_ONLY);
		_dataTypeComboViewer = new ComboViewer(dataTypeCombo);
		_dataTypeComboViewer.setLabelProvider(new EODataTypeLabelProvider());
		_dataTypeComboViewer.setContentProvider(new EODataTypeContentProvider());
		_dataTypeComboViewer.setSorter(new ViewerSorter());
		_dataTypeComboViewer.setInput(EODataType.DATA_TYPES);
		GridData dataTypeComboLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		dataTypeCombo.setLayoutData(dataTypeComboLayoutData);

		_dataTypePanel = getWidgetFactory().createPlainComposite(form, SWT.NONE);
		FormData dataTypeFormData = new FormData();
		dataTypeFormData.top = new FormAttachment(topForm, 0);
		dataTypeFormData.left = new FormAttachment(0, 6);
		dataTypeFormData.right = new FormAttachment(100, -5);
		_dataTypePanel.setLayoutData(dataTypeFormData);
		_dataTypeStackLayout = new StackLayout();
		_dataTypePanel.setLayout(_dataTypeStackLayout);

		_dataTypeToDataTypePanel = new HashMap<EODataType, Composite>();
		_dataTypeToDataTypePanel.put(EODataType.BIGDECIMAL, new DecimalNumberDataTypePanel(_dataTypePanel, SWT.NONE, getWidgetFactory()));
		_dataTypeToDataTypePanel.put(EODataType.BYTE, new StringDataTypePanel(_dataTypePanel, SWT.NONE, getWidgetFactory()));
		_dataTypeToDataTypePanel.put(EODataType.CUSTOM, new CustomDataTypePanel(_dataTypePanel, SWT.NONE, getWidgetFactory()));
		_dataTypeToDataTypePanel.put(EODataType.DATA, new DataDataTypePanel(_dataTypePanel, SWT.NONE, getWidgetFactory()));
		_dataTypeToDataTypePanel.put(EODataType.DATE, new DateDataTypePanel(_dataTypePanel, SWT.NONE, getWidgetFactory()));
		_dataTypeToDataTypePanel.put(EODataType.DATE_MSSQL, new DateDataTypePanel(_dataTypePanel, SWT.NONE, getWidgetFactory()));
		_dataTypeToDataTypePanel.put(EODataType.DATE_OBJ, new DateDataTypePanel(_dataTypePanel, SWT.NONE, getWidgetFactory()));
		_dataTypeToDataTypePanel.put(EODataType.DECIMAL_NUMBER, new DecimalNumberDataTypePanel(_dataTypePanel, SWT.NONE, getWidgetFactory()));
		_dataTypeToDataTypePanel.put(EODataType.BOOLEAN, new IntegerDataTypePanel(_dataTypePanel, SWT.NONE, getWidgetFactory()));
		_dataTypeToDataTypePanel.put(EODataType.DOUBLE, new DoubleDataTypePanel(_dataTypePanel, SWT.NONE, getWidgetFactory()));
		_dataTypeToDataTypePanel.put(EODataType.FLOAT, new DoubleDataTypePanel(_dataTypePanel, SWT.NONE, getWidgetFactory()));
		_dataTypeToDataTypePanel.put(EODataType.INTEGER, new IntegerDataTypePanel(_dataTypePanel, SWT.NONE, getWidgetFactory()));
		_dataTypeToDataTypePanel.put(EODataType.LONG, new IntegerDataTypePanel(_dataTypePanel, SWT.NONE, getWidgetFactory()));
		_dataTypeToDataTypePanel.put(EODataType.SHORT, new IntegerDataTypePanel(_dataTypePanel, SWT.NONE, getWidgetFactory()));
		_dataTypeToDataTypePanel.put(EODataType.STRING, new StringDataTypePanel(_dataTypePanel, SWT.NONE, getWidgetFactory()));
		_dataTypeToDataTypePanel.put(EODataType.STRING_CHAR, new StringDataTypePanel(_dataTypePanel, SWT.NONE, getWidgetFactory()));
		_dataTypeToDataTypePanel.put(EODataType.STRING_RTRIM, new StringDataTypePanel(_dataTypePanel, SWT.NONE, getWidgetFactory()));
		_dataTypeToDataTypePanel.put(EODataType.STRING_SET, new StringDataTypePanel(_dataTypePanel, SWT.NONE, getWidgetFactory()));
		_dataTypeToDataTypePanel.put(EODataType.STRING_UTF, new StringDataTypePanel(_dataTypePanel, SWT.NONE, getWidgetFactory()));
		_dataTypeToDataTypePanel.put(EODataType.TIME, new DateDataTypePanel(_dataTypePanel, SWT.NONE, getWidgetFactory()));
		_dataTypeToDataTypePanel.put(EODataType.TIMESTAMP, new DateDataTypePanel(_dataTypePanel, SWT.NONE, getWidgetFactory()));

		for (Composite dataTypePanel : _dataTypeToDataTypePanel.values()) {
			dataTypePanel.setBackground(_dataTypePanel.getBackground());
			getWidgetFactory().paintBordersFor(dataTypePanel);
		}
	}

	public void setArgument(AbstractEOArgument argument) {
		if (!ComparisonUtils.equals(argument, _argument)) {
			disposeBindings();

			_argument = argument;

			if (_argument != null) {
				// myArgumentTypeComboViewer.setInput(myAttribute);
				_bindingContext = new DataBindingContext();
				_bindingContext.bindValue(SWTObservables.observeText(_nameText, SWT.Modify), BeansObservables.observeValue(_argument, AbstractEOArgument.NAME), null, null);
				_bindingContext.bindValue(SWTObservables.observeText(_columnNameText, SWT.Modify), BeansObservables.observeValue(_argument, AbstractEOArgument.COLUMN_NAME), null, null);
				_bindingContext.bindValue(SWTObservables.observeText(_definitionText, SWT.Modify), BeansObservables.observeValue(_argument, AbstractEOArgument.DEFINITION), null, null);
				_bindingContext.bindValue(SWTObservables.observeText(_externalTypeText, SWT.Modify), BeansObservables.observeValue(_argument, AbstractEOArgument.EXTERNAL_TYPE), null, null);
				_bindingContext.bindValue(SWTObservables.observeText(_classNameText, SWT.Modify), BeansObservables.observeValue(_argument, AbstractEOArgument.CLASS_NAME), null, null);
				_bindingContext.bindValue(SWTObservables.observeSelection(_allowNullsButton), BeansObservables.observeValue(_argument, AbstractEOArgument.ALLOWS_NULL), null, new BooleanUpdateValueStrategy());

				_argumentChanged(argument);

				_dataTypeBinding = new ComboViewerBinding(_dataTypeComboViewer, _argument, AbstractEOArgument.DATA_TYPE, null, null, null);
				if (_argument.getDefinition() == null) {
					_derivedComboViewer.setSelection(new StructuredSelection(AbstractEOArgumentBasicEditorSection.COLUMN));
				} else {
					_derivedComboViewer.setSelection(new StructuredSelection(AbstractEOArgumentBasicEditorSection.DERIVED));
				}

				// Iterator dataTypePanelsIter =
				// myDataTypeToDataTypePanel.values().iterator();
				// while (dataTypePanelsIter.hasNext()) {
				// IDataTypePanel dataTypePanel = (IDataTypePanel)
				// dataTypePanelsIter.next();
				// dataTypePanel.setArgument(_argument);
				// }
				updateAttributePanel(null);
				if (_argument != null) {
					_argument.addPropertyChangeListener(AbstractEOArgument.DATA_TYPE, _dataTypeChangeListener);
				}
			}
		}
	}

	protected abstract void _addSettings(Composite settings);
	
	protected abstract void _addComponents(Composite parent);

	protected abstract void _argumentChanged(AbstractEOArgument argument);

	protected void disposeBindings() {
		if (_bindingContext != null) {
			_bindingContext.dispose();
		}
		if (_argument != null) {
			_argument.removePropertyChangeListener(AbstractEOArgument.DATA_TYPE, _dataTypeChangeListener);
		}
		if (_dataTypeBinding != null) {
			_dataTypeBinding.dispose();
		}
	}

	public void dispose() {
		disposeBindings();
		if (_dataTypeToDataTypePanel != null) {
			for (Composite dataTypePanel : _dataTypeToDataTypePanel.values()) {
				((IDataTypePanel) dataTypePanel).setArgument(null);
			}
		}
		super.dispose();
	}

	protected void updateTextfromDerivedComboViewer() {
		IStructuredSelection selection = (IStructuredSelection) _derivedComboViewer.getSelection();
		if (AbstractEOArgumentBasicEditorSection.COLUMN.equals(selection.getFirstElement())) {
			_columnNameDefinitionLayout.topControl = _columnNameText;
			if (_argument.getDefinition() != null) {
				_argument.setDefinition(null);
			}
		} else {
			_columnNameDefinitionLayout.topControl = _definitionText;
		}
		_columnNameDefinitionComposite.layout();
	}

	@SuppressWarnings("unused")
	protected void updateAttributePanel(EODataType oldDataType) {
		// System.out.println(
		// "AbstractEOArgumentBasicEditorSection.updateAttributePanel:
		// updateAttributePanel");
		if (_argument != null) {
			EODataType dataType = _argument.getDataType();
			Composite dataTypePanel = _dataTypeToDataTypePanel.get(dataType);
			if (dataTypePanel == null) {
				dataTypePanel = _dataTypeToDataTypePanel.get(EODataType.CUSTOM);
			}
			if (_dataTypeStackLayout.topControl instanceof IDataTypePanel) {
				((IDataTypePanel) _dataTypeStackLayout.topControl).setArgument(null);
			}
			if (dataTypePanel instanceof IDataTypePanel) {
				((IDataTypePanel) dataTypePanel).setArgument(_argument);
			}
			_dataTypeStackLayout.topControl = dataTypePanel;
			_dataTypePanel.layout();
		}
	}

	protected class ColumnDerivedChangeListener implements ISelectionChangedListener {
		public void selectionChanged(SelectionChangedEvent event) {
			AbstractEOArgumentBasicEditorSection.this.updateTextfromDerivedComboViewer();
		}
	}

	protected class DataTypeChangeListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent event) {
			EODataType oldDataType = (EODataType) event.getOldValue();
			// System.out.println("DataTypeChangeListener.propertyChange: " +
			// _event.getNewValue());
			AbstractEOArgumentBasicEditorSection.this.updateAttributePanel(oldDataType);
		}
	}
}
