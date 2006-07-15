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
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.internal.databinding.provisional.DataBindingContext;
import org.eclipse.jface.internal.databinding.provisional.description.Property;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.model.EOAttribute;
import org.objectstyle.wolips.eomodeler.model.EODataType;
import org.objectstyle.wolips.eomodeler.utils.BindingFactory;
import org.objectstyle.wolips.eomodeler.utils.ComboViewerBinding;
import org.objectstyle.wolips.eomodeler.utils.ComparisonUtils;

public class EOAttributeBasicEditorSection extends AbstractPropertySection {
  private static String COLUMN = "Column";
  private static String DERIVED = "Derived";
  private EOAttribute myAttribute;

  private Text myNameText;
  private ComboViewer myDerivedComboViewer;
  private Text myColumnNameText;
  private Text myDefinitionText;
  private Text myExternalTypeText;
  private Button myAllowNullsButton;
  private ComboViewer myDataTypeComboViewer;
  private StackLayout myColumnNameDefinitionLayout;
  private StackLayout myDataTypeStackLayout;
  private Map myDataTypeToDataTypePanel;

  private DataBindingContext myBindingContext;
  private ComboViewerBinding myDataTypeBinding;
  private DataTypeChangeListener myDataTypeChangeListener;

  private Composite myDataTypePanel;
  private Composite myColumnNameDefinitionComposite;

  public EOAttributeBasicEditorSection() {
    myDataTypeChangeListener = new DataTypeChangeListener();
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

    getWidgetFactory().createCLabel(topForm, Messages.getString("EOAttribute." + EOAttribute.NAME), SWT.NONE); //$NON-NLS-1$
    myNameText = new Text(topForm, SWT.BORDER);
    GridData nameFieldLayoutData = new GridData(GridData.FILL_HORIZONTAL);
    myNameText.setLayoutData(nameFieldLayoutData);

    Combo derivedCombo = new Combo(topForm, SWT.BORDER | SWT.FLAT | SWT.READ_ONLY);
    myDerivedComboViewer = new ComboViewer(derivedCombo);
    //myDerivedComboViewer.setLabelProvider(new EODerivedLabelProvider());
    myDerivedComboViewer.setContentProvider(new EODerivedContentProvider());
    myDerivedComboViewer.setInput(new String[] { EOAttributeBasicEditorSection.COLUMN, EOAttributeBasicEditorSection.DERIVED });
    myDerivedComboViewer.addSelectionChangedListener(new ColumnDerivedChangeListener());
    GridData derivedComboLayoutData = new GridData(GridData.FILL_HORIZONTAL);
    derivedCombo.setLayoutData(derivedComboLayoutData);

    myColumnNameDefinitionComposite = getWidgetFactory().createPlainComposite(topForm, SWT.NONE);
    GridData columnNameDefinitionFieldLayoutData = new GridData(GridData.FILL_HORIZONTAL);
    myColumnNameDefinitionComposite.setLayoutData(columnNameDefinitionFieldLayoutData);
    myColumnNameDefinitionLayout = new StackLayout();
    myColumnNameDefinitionComposite.setLayout(myColumnNameDefinitionLayout);

    getWidgetFactory().createCLabel(myColumnNameDefinitionComposite, Messages.getString("EOAttribute." + EOAttribute.COLUMN_NAME), SWT.NONE); //$NON-NLS-1$
    myColumnNameText = new Text(myColumnNameDefinitionComposite, SWT.BORDER);
    GridData externalNameFieldLayoutData = new GridData(GridData.FILL_HORIZONTAL);
    myColumnNameText.setLayoutData(externalNameFieldLayoutData);

    getWidgetFactory().createCLabel(myColumnNameDefinitionComposite, Messages.getString("EOAttribute." + EOAttribute.DEFINITION), SWT.NONE); //$NON-NLS-1$
    myDefinitionText = new Text(myColumnNameDefinitionComposite, SWT.BORDER);
    GridData definitionFieldLayoutData = new GridData(GridData.FILL_HORIZONTAL);
    myDefinitionText.setLayoutData(definitionFieldLayoutData);
    myColumnNameDefinitionLayout.topControl = myColumnNameText;

    getWidgetFactory().createCLabel(topForm, Messages.getString("EOAttribute." + EOAttribute.EXTERNAL_TYPE), SWT.NONE); //$NON-NLS-1$
    myExternalTypeText = new Text(topForm, SWT.BORDER);
    GridData externalTypeFieldLayoutData = new GridData(GridData.FILL_HORIZONTAL);
    myExternalTypeText.setLayoutData(externalTypeFieldLayoutData);

    getWidgetFactory().createCLabel(topForm, Messages.getString("EOAttribute." + EOAttribute.ALLOWS_NULL), SWT.NONE); //$NON-NLS-1$
    myAllowNullsButton = new Button(topForm, SWT.CHECK);

    getWidgetFactory().createCLabel(topForm, Messages.getString("EOAttribute." + EOAttribute.DATA_TYPE), SWT.NONE); //$NON-NLS-1$
    Combo dataTypeCombo = new Combo(topForm, SWT.BORDER | SWT.FLAT | SWT.READ_ONLY);
    myDataTypeComboViewer = new ComboViewer(dataTypeCombo);
    myDataTypeComboViewer.setLabelProvider(new EODataTypeLabelProvider());
    myDataTypeComboViewer.setContentProvider(new EODataTypeContentProvider());
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

    myDataTypeToDataTypePanel = new HashMap();
    myDataTypeToDataTypePanel.put(EODataType.CUSTOM, new CustomDataTypePanel(myDataTypePanel, SWT.NONE, getWidgetFactory()));
    myDataTypeToDataTypePanel.put(EODataType.DATA, new DataDataTypePanel(myDataTypePanel, SWT.NONE, getWidgetFactory()));
    myDataTypeToDataTypePanel.put(EODataType.DATE, new DateDataTypePanel(myDataTypePanel, SWT.NONE, getWidgetFactory()));
    myDataTypeToDataTypePanel.put(EODataType.DECIMAL_NUMBER, new DecimalNumberDataTypePanel(myDataTypePanel, SWT.NONE, getWidgetFactory()));
    myDataTypeToDataTypePanel.put(EODataType.DOUBLE, new DoubleDataTypePanel(myDataTypePanel, SWT.NONE, getWidgetFactory()));
    myDataTypeToDataTypePanel.put(EODataType.INTEGER, new IntegerDataTypePanel(myDataTypePanel, SWT.NONE, getWidgetFactory()));
    myDataTypeToDataTypePanel.put(EODataType.STRING, new StringDataTypePanel(myDataTypePanel, SWT.NONE, getWidgetFactory()));

    Iterator dataTypePanelsIter = myDataTypeToDataTypePanel.values().iterator();
    while (dataTypePanelsIter.hasNext()) {
      Composite dataTypePanel = (Composite) dataTypePanelsIter.next();
      dataTypePanel.setBackground(myDataTypePanel.getBackground());
      getWidgetFactory().paintBordersFor(dataTypePanel);
    }

  }

  public void setInput(IWorkbenchPart _part, ISelection _selection) {
    super.setInput(_part, _selection);
    EOAttribute attribute = (EOAttribute) ((IStructuredSelection) _selection).getFirstElement();
    setAttribute(attribute);
  }

  public void setAttribute(EOAttribute _attribute) {
    if (!ComparisonUtils.equals(_attribute, myAttribute)) {
      if (myBindingContext != null) {
        myBindingContext.dispose();
      }
      if (myAttribute != null) {
        myAttribute.removePropertyChangeListener(EOAttribute.DATA_TYPE, myDataTypeChangeListener);
      }
      if (myDataTypeBinding != null) {
        myDataTypeBinding.dispose();
      }

      myAttribute = _attribute;

      if (myAttribute != null) {
        //myArgumentTypeComboViewer.setInput(myAttribute);
        myBindingContext = BindingFactory.createContext();
        myBindingContext.bind(myNameText, new Property(myAttribute, EOAttribute.NAME), null);
        myBindingContext.bind(myColumnNameText, new Property(myAttribute, EOAttribute.COLUMN_NAME), null);
        myBindingContext.bind(myDefinitionText, new Property(myAttribute, EOAttribute.DEFINITION), null);
        myBindingContext.bind(myExternalTypeText, new Property(myAttribute, EOAttribute.EXTERNAL_TYPE), null);
        myBindingContext.bind(myAllowNullsButton, new Property(myAttribute, EOAttribute.ALLOWS_NULL), null);

        myDataTypeBinding = new ComboViewerBinding(myDataTypeComboViewer, myAttribute, EOAttribute.DATA_TYPE, null, null, null);
        if (myAttribute.getDefinition() == null) {
          myDerivedComboViewer.setSelection(new StructuredSelection(EOAttributeBasicEditorSection.COLUMN));
        }
        else {
          myDerivedComboViewer.setSelection(new StructuredSelection(EOAttributeBasicEditorSection.DERIVED));
        }
      }

      Iterator dataTypePanelsIter = myDataTypeToDataTypePanel.values().iterator();
      while (dataTypePanelsIter.hasNext()) {
        IDataTypePanel dataTypePanel = (IDataTypePanel) dataTypePanelsIter.next();
        dataTypePanel.setAttribute(_attribute);
      }
      updateAttributePanel();
      if (myAttribute != null) {
        myAttribute.addPropertyChangeListener(EOAttribute.DATA_TYPE, myDataTypeChangeListener);
      }
    }
  }

  public void dispose() {
    super.dispose();
    setAttribute(null);
  }

  protected void updateTextfromDerivedComboViewer() {
    IStructuredSelection selection = (IStructuredSelection) myDerivedComboViewer.getSelection();
    if (selection.getFirstElement() == EOAttributeBasicEditorSection.COLUMN) {
      myColumnNameDefinitionLayout.topControl = myColumnNameText;
      myAttribute.setDefinition(null);
    }
    else {
      myColumnNameDefinitionLayout.topControl = myDefinitionText;
    }
    myColumnNameDefinitionComposite.layout();
  }

  protected void updateAttributePanel() {
    if (myAttribute != null) {
      EODataType dataType = myAttribute.getDataType();
      Control dataTypePanel = (Control) myDataTypeToDataTypePanel.get(dataType);
      myDataTypeStackLayout.topControl = dataTypePanel;
      myDataTypePanel.layout();
    }
  }

  protected class ColumnDerivedChangeListener implements ISelectionChangedListener {
    public void selectionChanged(SelectionChangedEvent _event) {
      EOAttributeBasicEditorSection.this.updateTextfromDerivedComboViewer();
    }
  }

  protected class DataTypeChangeListener implements PropertyChangeListener {
    public void propertyChange(PropertyChangeEvent _event) {
      EOAttributeBasicEditorSection.this.updateAttributePanel();
    }
  }
}
