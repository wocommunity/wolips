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

import org.eclipse.jface.internal.databinding.provisional.DataBindingContext;
import org.eclipse.jface.internal.databinding.provisional.description.Property;
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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.editors.dataType.CustomDataTypePanel;
import org.objectstyle.wolips.eomodeler.editors.dataType.DataDataTypePanel;
import org.objectstyle.wolips.eomodeler.editors.dataType.DateDataTypePanel;
import org.objectstyle.wolips.eomodeler.editors.dataType.DecimalNumberDataTypePanel;
import org.objectstyle.wolips.eomodeler.editors.dataType.DoubleDataTypePanel;
import org.objectstyle.wolips.eomodeler.editors.dataType.IDataTypePanel;
import org.objectstyle.wolips.eomodeler.editors.dataType.IntegerDataTypePanel;
import org.objectstyle.wolips.eomodeler.editors.dataType.StringDataTypePanel;
import org.objectstyle.wolips.eomodeler.model.AbstractEOArgument;
import org.objectstyle.wolips.eomodeler.model.EODataType;
import org.objectstyle.wolips.eomodeler.utils.BindingFactory;
import org.objectstyle.wolips.eomodeler.utils.ComboViewerBinding;
import org.objectstyle.wolips.eomodeler.utils.ComparisonUtils;

public abstract class AbstractEOArgumentBasicEditorSection extends AbstractPropertySection {
	private static String COLUMN = "Column";

	private static String DERIVED = "Derived";

	private AbstractEOArgument myArgument;

	private Text myNameText;

	private ComboViewer myDerivedComboViewer;

	private Text myColumnNameText;

	private Text myDefinitionText;

	private Text myExternalTypeText;

	private Button myAllowNullsButton;

	private ComboViewer myDataTypeComboViewer;

	private StackLayout myColumnNameDefinitionLayout;

	private StackLayout myDataTypeStackLayout;

	private Map<EODataType, Composite> myDataTypeToDataTypePanel;

	private DataBindingContext myBindingContext;

	private ComboViewerBinding myDataTypeBinding;

	private DataTypeChangeListener myDataTypeChangeListener;

	private AttributeNameSyncer myNameColumnNameSyncer;

	private Composite myDataTypePanel;

	private Composite myColumnNameDefinitionComposite;

	public AbstractEOArgumentBasicEditorSection() {
		myDataTypeChangeListener = new DataTypeChangeListener();
		myNameColumnNameSyncer = new AttributeNameSyncer();
	}

	public AbstractEOArgument getArgument() {
		return myArgument;
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

		getWidgetFactory().createCLabel(topForm, Messages.getString("AbstractEOArgument." + AbstractEOArgument.NAME), SWT.NONE);
		myNameText = new Text(topForm, SWT.BORDER);
		GridData nameFieldLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		myNameText.setLayoutData(nameFieldLayoutData);

		Combo derivedCombo = new Combo(topForm, SWT.BORDER | SWT.FLAT | SWT.READ_ONLY);
		myDerivedComboViewer = new ComboViewer(derivedCombo);
		// myDerivedComboViewer.setLabelProvider(new EODerivedLabelProvider());
		myDerivedComboViewer.setContentProvider(new EODerivedContentProvider());
		myDerivedComboViewer.setInput(new String[] { AbstractEOArgumentBasicEditorSection.COLUMN, AbstractEOArgumentBasicEditorSection.DERIVED });
		myDerivedComboViewer.addSelectionChangedListener(new ColumnDerivedChangeListener());
		GridData derivedComboLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		derivedCombo.setLayoutData(derivedComboLayoutData);

		myColumnNameDefinitionComposite = getWidgetFactory().createPlainComposite(topForm, SWT.NONE);
		GridData columnNameDefinitionFieldLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		myColumnNameDefinitionComposite.setLayoutData(columnNameDefinitionFieldLayoutData);
		myColumnNameDefinitionLayout = new StackLayout();
		myColumnNameDefinitionComposite.setLayout(myColumnNameDefinitionLayout);

		getWidgetFactory().createCLabel(myColumnNameDefinitionComposite, Messages.getString("AbstractEOArgument." + AbstractEOArgument.COLUMN_NAME), SWT.NONE);
		myColumnNameText = new Text(myColumnNameDefinitionComposite, SWT.BORDER);
		GridData externalNameFieldLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		myColumnNameText.setLayoutData(externalNameFieldLayoutData);

		getWidgetFactory().createCLabel(myColumnNameDefinitionComposite, Messages.getString("AbstractEOArgument." + AbstractEOArgument.DEFINITION), SWT.NONE);
		myDefinitionText = new Text(myColumnNameDefinitionComposite, SWT.BORDER);
		GridData definitionFieldLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		myDefinitionText.setLayoutData(definitionFieldLayoutData);
		myColumnNameDefinitionLayout.topControl = myColumnNameText;

		_addComponents(topForm);

		getWidgetFactory().createCLabel(topForm, Messages.getString("AbstractEOArgument." + AbstractEOArgument.EXTERNAL_TYPE), SWT.NONE);
		myExternalTypeText = new Text(topForm, SWT.BORDER);
		GridData externalTypeFieldLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		myExternalTypeText.setLayoutData(externalTypeFieldLayoutData);

		getWidgetFactory().createCLabel(topForm, Messages.getString("AbstractEOArgument." + AbstractEOArgument.ALLOWS_NULL), SWT.NONE);
		myAllowNullsButton = new Button(topForm, SWT.CHECK);

		getWidgetFactory().createCLabel(topForm, Messages.getString("AbstractEOArgument." + AbstractEOArgument.DATA_TYPE), SWT.NONE);
		Combo dataTypeCombo = new Combo(topForm, SWT.BORDER | SWT.FLAT | SWT.READ_ONLY);
		myDataTypeComboViewer = new ComboViewer(dataTypeCombo);
		myDataTypeComboViewer.setLabelProvider(new EODataTypeLabelProvider());
		myDataTypeComboViewer.setContentProvider(new EODataTypeContentProvider());
		myDataTypeComboViewer.setSorter(new ViewerSorter());
		myDataTypeComboViewer.setInput(EODataType.DATA_TYPES);
		GridData dataTypeComboLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		dataTypeCombo.setLayoutData(dataTypeComboLayoutData);

		myDataTypePanel = getWidgetFactory().createPlainComposite(form, SWT.NONE);
		FormData dataTypeFormData = new FormData();
		dataTypeFormData.top = new FormAttachment(topForm, 10);
		dataTypeFormData.left = new FormAttachment(0, 5);
		dataTypeFormData.right = new FormAttachment(100, -5);
		myDataTypePanel.setLayoutData(dataTypeFormData);
		myDataTypeStackLayout = new StackLayout();
		myDataTypePanel.setLayout(myDataTypeStackLayout);

		myDataTypeToDataTypePanel = new HashMap<EODataType, Composite>();
		myDataTypeToDataTypePanel.put(EODataType.BIGDECIMAL, new DecimalNumberDataTypePanel(myDataTypePanel, SWT.NONE, getWidgetFactory()));
		myDataTypeToDataTypePanel.put(EODataType.BYTE, new StringDataTypePanel(myDataTypePanel, SWT.NONE, getWidgetFactory()));
		myDataTypeToDataTypePanel.put(EODataType.CUSTOM, new CustomDataTypePanel(myDataTypePanel, SWT.NONE, getWidgetFactory()));
		myDataTypeToDataTypePanel.put(EODataType.DATA, new DataDataTypePanel(myDataTypePanel, SWT.NONE, getWidgetFactory()));
		myDataTypeToDataTypePanel.put(EODataType.DATE, new DateDataTypePanel(myDataTypePanel, SWT.NONE, getWidgetFactory()));
		myDataTypeToDataTypePanel.put(EODataType.DATE_MSSQL, new DateDataTypePanel(myDataTypePanel, SWT.NONE, getWidgetFactory()));
		myDataTypeToDataTypePanel.put(EODataType.DATE_OBJ, new DateDataTypePanel(myDataTypePanel, SWT.NONE, getWidgetFactory()));
		myDataTypeToDataTypePanel.put(EODataType.DECIMAL_NUMBER, new DecimalNumberDataTypePanel(myDataTypePanel, SWT.NONE, getWidgetFactory()));
		myDataTypeToDataTypePanel.put(EODataType.BOOLEAN, new IntegerDataTypePanel(myDataTypePanel, SWT.NONE, getWidgetFactory()));
		myDataTypeToDataTypePanel.put(EODataType.DOUBLE, new DoubleDataTypePanel(myDataTypePanel, SWT.NONE, getWidgetFactory()));
		myDataTypeToDataTypePanel.put(EODataType.FLOAT, new DoubleDataTypePanel(myDataTypePanel, SWT.NONE, getWidgetFactory()));
		myDataTypeToDataTypePanel.put(EODataType.INTEGER, new IntegerDataTypePanel(myDataTypePanel, SWT.NONE, getWidgetFactory()));
		myDataTypeToDataTypePanel.put(EODataType.LONG, new IntegerDataTypePanel(myDataTypePanel, SWT.NONE, getWidgetFactory()));
		myDataTypeToDataTypePanel.put(EODataType.SHORT, new IntegerDataTypePanel(myDataTypePanel, SWT.NONE, getWidgetFactory()));
		myDataTypeToDataTypePanel.put(EODataType.STRING, new StringDataTypePanel(myDataTypePanel, SWT.NONE, getWidgetFactory()));
		myDataTypeToDataTypePanel.put(EODataType.STRING_CHAR, new StringDataTypePanel(myDataTypePanel, SWT.NONE, getWidgetFactory()));
		myDataTypeToDataTypePanel.put(EODataType.STRING_RTRIM, new StringDataTypePanel(myDataTypePanel, SWT.NONE, getWidgetFactory()));
		myDataTypeToDataTypePanel.put(EODataType.STRING_SET, new StringDataTypePanel(myDataTypePanel, SWT.NONE, getWidgetFactory()));
		myDataTypeToDataTypePanel.put(EODataType.STRING_UTF, new StringDataTypePanel(myDataTypePanel, SWT.NONE, getWidgetFactory()));
		myDataTypeToDataTypePanel.put(EODataType.TIME, new DateDataTypePanel(myDataTypePanel, SWT.NONE, getWidgetFactory()));
		myDataTypeToDataTypePanel.put(EODataType.TIMESTAMP, new DateDataTypePanel(myDataTypePanel, SWT.NONE, getWidgetFactory()));

		for (Composite dataTypePanel : myDataTypeToDataTypePanel.values()) {
			dataTypePanel.setBackground(myDataTypePanel.getBackground());
			getWidgetFactory().paintBordersFor(dataTypePanel);
		}
	}

	public void setArgument(AbstractEOArgument _argument) {
		if (!ComparisonUtils.equals(_argument, myArgument)) {
			disposeBindings();

			myArgument = _argument;

			if (myArgument != null) {
				// myArgumentTypeComboViewer.setInput(myAttribute);
				myBindingContext = BindingFactory.createContext();
				myBindingContext.bind(myNameText, new Property(myArgument, AbstractEOArgument.NAME), null);
				myBindingContext.bind(myColumnNameText, new Property(myArgument, AbstractEOArgument.COLUMN_NAME), null);
				myBindingContext.bind(myDefinitionText, new Property(myArgument, AbstractEOArgument.DEFINITION), null);
				myBindingContext.bind(myExternalTypeText, new Property(myArgument, AbstractEOArgument.EXTERNAL_TYPE), null);
				myBindingContext.bind(myAllowNullsButton, new Property(myArgument, AbstractEOArgument.ALLOWS_NULL), null);

				_argumentChanged(_argument);

				myDataTypeBinding = new ComboViewerBinding(myDataTypeComboViewer, myArgument, AbstractEOArgument.DATA_TYPE, null, null, null);
				if (myArgument.getDefinition() == null) {
					myDerivedComboViewer.setSelection(new StructuredSelection(AbstractEOArgumentBasicEditorSection.COLUMN));
				} else {
					myDerivedComboViewer.setSelection(new StructuredSelection(AbstractEOArgumentBasicEditorSection.DERIVED));
				}

				// Iterator dataTypePanelsIter =
				// myDataTypeToDataTypePanel.values().iterator();
				// while (dataTypePanelsIter.hasNext()) {
				// IDataTypePanel dataTypePanel = (IDataTypePanel)
				// dataTypePanelsIter.next();
				// dataTypePanel.setArgument(_argument);
				// }
				updateAttributePanel(null);
				if (myArgument != null) {
					myArgument.addPropertyChangeListener(AbstractEOArgument.DATA_TYPE, myDataTypeChangeListener);
					myArgument.addPropertyChangeListener(AbstractEOArgument.NAME, myNameColumnNameSyncer);
				}
			}
		}
	}

	protected abstract void _addComponents(Composite _parent);

	protected abstract void _argumentChanged(AbstractEOArgument _argument);

	protected void disposeBindings() {
		if (myBindingContext != null) {
			myBindingContext.dispose();
		}
		if (myArgument != null) {
			myArgument.removePropertyChangeListener(AbstractEOArgument.DATA_TYPE, myDataTypeChangeListener);
			myArgument.removePropertyChangeListener(AbstractEOArgument.NAME, myNameColumnNameSyncer);
		}
		if (myDataTypeBinding != null) {
			myDataTypeBinding.dispose();
		}
	}

	public void dispose() {
		disposeBindings();
		for (Composite dataTypePanel : myDataTypeToDataTypePanel.values()) {
			((IDataTypePanel) dataTypePanel).setArgument(null);
		}
		super.dispose();
	}

	protected void updateTextfromDerivedComboViewer() {
		IStructuredSelection selection = (IStructuredSelection) myDerivedComboViewer.getSelection();
		if (selection.getFirstElement() == AbstractEOArgumentBasicEditorSection.COLUMN) {
			myColumnNameDefinitionLayout.topControl = myColumnNameText;
			if (myArgument.getDefinition() != null) {
				myArgument.setDefinition(null);
			}
		} else {
			myColumnNameDefinitionLayout.topControl = myDefinitionText;
		}
		myColumnNameDefinitionComposite.layout();
	}

	protected void updateAttributePanel(EODataType _oldDataType) {
		// System.out.println("AbstractEOArgumentBasicEditorSection.updateAttributePanel:
		// updateAttributePanel");
		if (myArgument != null) {
			EODataType dataType = myArgument.getDataType();
			Composite dataTypePanel = myDataTypeToDataTypePanel.get(dataType);
			if (dataTypePanel == null) {
				dataTypePanel = myDataTypeToDataTypePanel.get(EODataType.CUSTOM);
			}
			if (myDataTypeStackLayout.topControl instanceof IDataTypePanel) {
				((IDataTypePanel) myDataTypeStackLayout.topControl).setArgument(null);
			}
			if (dataTypePanel instanceof IDataTypePanel) {
				((IDataTypePanel) dataTypePanel).setArgument(myArgument);
			}
			myDataTypeStackLayout.topControl = dataTypePanel;
			myDataTypePanel.layout();
		}
	}

	protected class ColumnDerivedChangeListener implements ISelectionChangedListener {
		public void selectionChanged(SelectionChangedEvent _event) {
			AbstractEOArgumentBasicEditorSection.this.updateTextfromDerivedComboViewer();
		}
	}

	protected class DataTypeChangeListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent _event) {
			EODataType oldDataType = (EODataType) _event.getOldValue();
			// System.out.println("DataTypeChangeListener.propertyChange: " +
			// _event.getNewValue());
			AbstractEOArgumentBasicEditorSection.this.updateAttributePanel(oldDataType);
		}
	}
}
