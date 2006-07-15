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
package org.objectstyle.wolips.eomodeler.editors.relationship;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.internal.databinding.provisional.DataBindingContext;
import org.eclipse.jface.internal.databinding.provisional.description.Property;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
import org.objectstyle.wolips.eomodeler.editors.entity.EOEntityLabelProvider;
import org.objectstyle.wolips.eomodeler.editors.entity.EOEntityListContentProvider;
import org.objectstyle.wolips.eomodeler.model.EODeleteRule;
import org.objectstyle.wolips.eomodeler.model.EOEntity;
import org.objectstyle.wolips.eomodeler.model.EOJoin;
import org.objectstyle.wolips.eomodeler.model.EOJoinSemantic;
import org.objectstyle.wolips.eomodeler.model.EOModel;
import org.objectstyle.wolips.eomodeler.model.EOModelGroup;
import org.objectstyle.wolips.eomodeler.model.EORelationship;
import org.objectstyle.wolips.eomodeler.model.EORelationshipPath;
import org.objectstyle.wolips.eomodeler.utils.BindingFactory;
import org.objectstyle.wolips.eomodeler.utils.ComboViewerBinding;
import org.objectstyle.wolips.eomodeler.utils.KeyComboBoxCellEditor;
import org.objectstyle.wolips.eomodeler.utils.TablePropertyViewerSorter;
import org.objectstyle.wolips.eomodeler.utils.TableUtils;

public class EORelationshipBasicEditorSection extends AbstractPropertySection {
  private EORelationship myRelationship;

  private Text myNameText;
  private Button myToOneButton;
  private Button myToManyButton;
  private Button myOptionalButton;
  private Button myMandatoryButton;
  private ComboViewer myDeleteRuleComboViewer;
  private ComboViewer myModelComboViewer;
  private ComboViewer myJoinSemanticComboViewer;
  private ComboViewer myEntityComboViewer;
  private TableViewer myJoinsTableViewer;
  private Button myRemoveButton;
  private Button myAddButton;

  private DataBindingContext myBindingContext;
  private ComboViewerBinding myDeleteRuleBinding;
  private ComboViewerBinding myJoinSemanticBinding;
  private ComboViewerBinding myModelBinding;
  private ComboViewerBinding myEntityBinding;
  private AttributesListener myAttributesListener;
  private RelationshipListener myRelationshipListener;
  private ButtonUpdateListener myButtonUpdateListener;

  public EORelationshipBasicEditorSection() {
    myAttributesListener = new AttributesListener();
    myRelationshipListener = new RelationshipListener();
    myButtonUpdateListener = new ButtonUpdateListener();
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

    getWidgetFactory().createCLabel(topForm, Messages.getString("EORelationship." + EORelationship.NAME), SWT.NONE); //$NON-NLS-1$
    myNameText = new Text(topForm, SWT.BORDER);
    GridData nameFieldLayoutData = new GridData(GridData.FILL_HORIZONTAL);
    myNameText.setLayoutData(nameFieldLayoutData);

    getWidgetFactory().createCLabel(topForm, Messages.getString("EORelationship.cardinality"), SWT.NONE); //$NON-NLS-1$
    Composite cardinalityComposite = getWidgetFactory().createPlainComposite(topForm, SWT.NONE);
    GridLayout cardinalityLayout = new GridLayout();
    cardinalityLayout.numColumns = 2;
    cardinalityLayout.makeColumnsEqualWidth = true;
    cardinalityComposite.setLayout(cardinalityLayout);
    myToOneButton = new Button(cardinalityComposite, SWT.RADIO);
    myToOneButton.setText(Messages.getString("EORelationship.toOne")); //$NON-NLS-1$
    GridData toOneButtonLayoutData = new GridData(GridData.FILL_HORIZONTAL);
    myToOneButton.setLayoutData(toOneButtonLayoutData);
    myToManyButton = new Button(cardinalityComposite, SWT.RADIO);
    myToManyButton.setText(Messages.getString("EORelationship.toMany")); //$NON-NLS-1$
    GridData toManyButtonLayoutData = new GridData(GridData.FILL_HORIZONTAL);
    myToManyButton.setLayoutData(toManyButtonLayoutData);
    GridData cardinalityCompositeLayoutData = new GridData(GridData.FILL_HORIZONTAL);
    cardinalityComposite.setLayoutData(cardinalityCompositeLayoutData);

    getWidgetFactory().createCLabel(topForm, Messages.getString("EORelationship.optionality"), SWT.NONE); //$NON-NLS-1$
    Composite optionalityComposite = getWidgetFactory().createPlainComposite(topForm, SWT.NONE);
    GridLayout optionalityLayout = new GridLayout();
    optionalityLayout.numColumns = 2;
    optionalityLayout.makeColumnsEqualWidth = true;
    optionalityComposite.setLayout(optionalityLayout);
    myOptionalButton = new Button(optionalityComposite, SWT.RADIO);
    myOptionalButton.setText(Messages.getString("EORelationship.optional")); //$NON-NLS-1$
    GridData optionalButtonLayoutData = new GridData(GridData.FILL_HORIZONTAL);
    myOptionalButton.setLayoutData(optionalButtonLayoutData);
    myMandatoryButton = new Button(optionalityComposite, SWT.RADIO);
    myMandatoryButton.setText(Messages.getString("EORelationship.mandatory")); //$NON-NLS-1$
    GridData mandatoryButtonLayoutData = new GridData(GridData.FILL_HORIZONTAL);
    myMandatoryButton.setLayoutData(mandatoryButtonLayoutData);
    GridData optioanlityCompositeLayoutData = new GridData(GridData.FILL_HORIZONTAL);
    optionalityComposite.setLayoutData(optioanlityCompositeLayoutData);

    getWidgetFactory().createCLabel(topForm, Messages.getString("EORelationship." + EORelationship.DELETE_RULE), SWT.NONE); //$NON-NLS-1$
    Combo deleteRuleCombo = new Combo(topForm, SWT.BORDER | SWT.FLAT | SWT.READ_ONLY);
    myDeleteRuleComboViewer = new ComboViewer(deleteRuleCombo);
    myDeleteRuleComboViewer.setLabelProvider(new EODeleteRuleLabelProvider());
    myDeleteRuleComboViewer.setContentProvider(new EODeleteRuleContentProvider());
    myDeleteRuleComboViewer.setInput(EODeleteRule.DELETE_RULES);
    myDeleteRuleComboViewer.setSelection(new StructuredSelection(EODeleteRule.NULLIFY));
    GridData deleteRuleComboLayoutData = new GridData(GridData.FILL_HORIZONTAL);
    deleteRuleCombo.setLayoutData(deleteRuleComboLayoutData);

    getWidgetFactory().createCLabel(topForm, Messages.getString("EORelationship.model"), SWT.NONE); //$NON-NLS-1$
    Combo modelCombo = new Combo(topForm, SWT.BORDER | SWT.FLAT | SWT.READ_ONLY);
    myModelComboViewer = new ComboViewer(modelCombo);
    myModelComboViewer.setLabelProvider(new EOModelLabelProvider());
    myModelComboViewer.setContentProvider(new EOModelListContentProvider());
    myModelComboViewer.addSelectionChangedListener(new ModelSelectionListener());
    GridData modelRuleComboLayoutData = new GridData(GridData.FILL_HORIZONTAL);
    modelCombo.setLayoutData(modelRuleComboLayoutData);

    getWidgetFactory().createCLabel(topForm, Messages.getString("EORelationship." + EORelationship.DESTINATION), SWT.NONE); //$NON-NLS-1$
    Combo entityCombo = new Combo(topForm, SWT.BORDER | SWT.FLAT | SWT.READ_ONLY);
    myEntityComboViewer = new ComboViewer(entityCombo);
    myEntityComboViewer.setLabelProvider(new EOEntityLabelProvider());
    myEntityComboViewer.setContentProvider(new EOEntityListContentProvider(false, true));
    GridData entityComboLayoutData = new GridData(GridData.FILL_HORIZONTAL);
    entityCombo.setLayoutData(entityComboLayoutData);

    Combo joinSemanticCombo = new Combo(topForm, SWT.BORDER | SWT.FLAT | SWT.READ_ONLY);
    myJoinSemanticComboViewer = new ComboViewer(joinSemanticCombo);
    myJoinSemanticComboViewer.setLabelProvider(new EOJoinSemanticLabelProvider());
    myJoinSemanticComboViewer.setContentProvider(new EOJoinSemanticContentProvider());
    myJoinSemanticComboViewer.setInput(EOJoinSemantic.JOIN_SEMANTICS);
    myJoinSemanticComboViewer.setSelection(new StructuredSelection(EOJoinSemantic.INNER));
    GridData joinSemanticLayoutData = new GridData(GridData.FILL_HORIZONTAL);
    joinSemanticLayoutData.verticalAlignment = SWT.TOP;
    joinSemanticCombo.setLayoutData(joinSemanticLayoutData);

    myJoinsTableViewer = new TableViewer(topForm, SWT.BORDER | SWT.FLAT | SWT.MULTI | SWT.FULL_SELECTION);
    myJoinsTableViewer.getTable().setHeaderVisible(true);
    myJoinsTableViewer.getTable().setLinesVisible(true);
    TableUtils.createTableColumns(myJoinsTableViewer, "EOJoin", EOJoinsConstants.COLUMNS); //$NON-NLS-1$
    myJoinsTableViewer.setContentProvider(new EOJoinsContentProvider());
    myJoinsTableViewer.setLabelProvider(new EOJoinsLabelProvider(EOJoinsConstants.COLUMNS));
    myJoinsTableViewer.setSorter(new TablePropertyViewerSorter(myJoinsTableViewer, EOJoinsConstants.COLUMNS));
    myJoinsTableViewer.setColumnProperties(EOJoinsConstants.COLUMNS);

    CellEditor[] cellEditors = new CellEditor[EOJoinsConstants.COLUMNS.length];
    cellEditors[TableUtils.getColumnNumber(EOJoinsConstants.COLUMNS, EOJoin.SOURCE_ATTRIBUTE_NAME)] = new KeyComboBoxCellEditor(myJoinsTableViewer.getTable(), new String[0], SWT.READ_ONLY);
    cellEditors[TableUtils.getColumnNumber(EOJoinsConstants.COLUMNS, EOJoin.DESTINATION_ATTRIBUTE_NAME)] = new KeyComboBoxCellEditor(myJoinsTableViewer.getTable(), new String[0], SWT.READ_ONLY);
    myJoinsTableViewer.setCellModifier(new EOJoinsCellModifier(myJoinsTableViewer));
    myJoinsTableViewer.setCellEditors(cellEditors);

    GridData joinsTableLayoutData = new GridData(GridData.FILL_HORIZONTAL);
    joinsTableLayoutData.heightHint = 100;
    myJoinsTableViewer.getTable().setLayoutData(joinsTableLayoutData);
    myJoinsTableViewer.addSelectionChangedListener(myButtonUpdateListener);

    Composite buttonGroup = getWidgetFactory().createPlainComposite(form, SWT.NONE);
    FormData buttonGroupFormData = new FormData();
    buttonGroupFormData.top = new FormAttachment(topForm, 5);
    buttonGroupFormData.left = new FormAttachment(0, 5);
    buttonGroupFormData.right = new FormAttachment(100, -5);
    buttonGroup.setLayoutData(buttonGroupFormData);
    FormLayout layout = new FormLayout();
    buttonGroup.setLayout(layout);

    myAddButton = new Button(buttonGroup, SWT.PUSH);
    myAddButton.setText(Messages.getString("button.add")); //$NON-NLS-1$
    FormData addButtonData = new FormData();
    addButtonData.right = new FormAttachment(100, 0);
    myAddButton.setLayoutData(addButtonData);
    myAddButton.addSelectionListener(new AddJoinHandler());

    myRemoveButton = new Button(buttonGroup, SWT.PUSH);
    myRemoveButton.setText(Messages.getString("button.remove")); //$NON-NLS-1$
    FormData remoteButtonData = new FormData();
    remoteButtonData.right = new FormAttachment(myAddButton, 0);
    myRemoveButton.setLayoutData(remoteButtonData);
    myRemoveButton.addSelectionListener(new RemoveJoinsHandler());
  }

  public void setInput(IWorkbenchPart _part, ISelection _selection) {
    super.setInput(_part, _selection);
    disposeBindings();

    Object selectedObject = ((IStructuredSelection) _selection).getFirstElement();
    if (selectedObject instanceof EORelationship) {
      myRelationship = (EORelationship) selectedObject;
    }
    else if (selectedObject instanceof EORelationshipPath) {
      myRelationship = ((EORelationshipPath) selectedObject).getChildRelationship();
    }
    myRelationship.addPropertyChangeListener(EORelationship.DESTINATION, myRelationshipListener);
    myRelationship.addPropertyChangeListener(EORelationship.JOINS, myRelationshipListener);

    myModelComboViewer.setInput(myRelationship);
    myEntityComboViewer.setInput(myRelationship);
    myModelComboViewer.setSelection(new StructuredSelection(myRelationship.getEntity().getModel()));
    myJoinsTableViewer.setInput(myRelationship);
    ((TablePropertyViewerSorter) myJoinsTableViewer.getSorter()).sort(EOJoin.SOURCE_ATTRIBUTE);

    myBindingContext = BindingFactory.createContext();
    myBindingContext.bind(myNameText, new Property(myRelationship, EORelationship.NAME), null);
    myBindingContext.bind(myToOneButton, new Property(myRelationship, EORelationship.TO_ONE), null);
    myBindingContext.bind(myToManyButton, new Property(myRelationship, EORelationship.TO_MANY), null);
    myBindingContext.bind(myOptionalButton, new Property(myRelationship, EORelationship.OPTIONAL), null);
    myBindingContext.bind(myMandatoryButton, new Property(myRelationship, EORelationship.MANDATORY), null);

    myDeleteRuleBinding = new ComboViewerBinding(myDeleteRuleComboViewer, myRelationship, EORelationship.DELETE_RULE, null, null, null);
    myJoinSemanticBinding = new ComboViewerBinding(myJoinSemanticComboViewer, myRelationship, EORelationship.JOIN_SEMANTIC, myRelationship.getEntity().getModel().getModelGroup(), EOModelGroup.MODELS, null);
    myEntityBinding = new ComboViewerBinding(myEntityComboViewer, myRelationship, EORelationship.DESTINATION, myRelationship.getEntity().getModel(), EOModel.ENTITIES, null);

    boolean enabled = !myRelationship.isFlattened();
    myModelComboViewer.getCombo().setEnabled(enabled);
    myEntityComboViewer.getCombo().setEnabled(enabled);
    myJoinSemanticComboViewer.getCombo().setEnabled(enabled);
    myJoinsTableViewer.getTable().setEnabled(enabled);
    myAddButton.setEnabled(enabled);
    myRemoveButton.setEnabled(enabled);

    updateModelAndEntityCombosEnabled();
    updateJoins();
    updateButtons();
  }

  protected void updateButtons() {
    boolean buttonsEnabled = myRelationship.getDestination() != null;
    boolean removeEnabled = buttonsEnabled && !myJoinsTableViewer.getSelection().isEmpty() && !myRelationship.isFlattened();
    boolean addEnabled = buttonsEnabled;
    myRemoveButton.setEnabled(removeEnabled);
    myAddButton.setEnabled(addEnabled);
  }

  protected void updateModelAndEntityCombosEnabled() {
    boolean enabled = myRelationship.getJoins().size() == 0 && !myRelationship.isFlattened();
    myModelComboViewer.getCombo().setEnabled(enabled);
    myEntityComboViewer.getCombo().setEnabled(enabled);
  }

  protected void updateEntityCombo() {
    IStructuredSelection selection = (IStructuredSelection) myModelComboViewer.getSelection();
    EOModel selectedModel = (EOModel) selection.getFirstElement();
    myEntityComboViewer.setInput(selectedModel);
  }

  protected void updateJoins() {
    if (myJoinsTableViewer != null) {
      myJoinsTableViewer.setInput(myRelationship);
      KeyComboBoxCellEditor sourceCellEditor = (KeyComboBoxCellEditor) myJoinsTableViewer.getCellEditors()[TableUtils.getColumnNumber(EOJoinsConstants.COLUMNS, EOJoin.SOURCE_ATTRIBUTE_NAME)];
      EOEntity source = myRelationship.getEntity();
      if (source != null) {
        sourceCellEditor.setItems(source.getAttributeNames());
      }
      KeyComboBoxCellEditor destinationCellEditor = (KeyComboBoxCellEditor) myJoinsTableViewer.getCellEditors()[TableUtils.getColumnNumber(EOJoinsConstants.COLUMNS, EOJoin.DESTINATION_ATTRIBUTE_NAME)];
      EOEntity destination = myRelationship.getDestination();
      if (destination != null) {
        destinationCellEditor.setItems(destination.getAttributeNames());
      }
      TableUtils.packTableColumns(myJoinsTableViewer);
    }
    updateModelAndEntityCombosEnabled();
  }

  protected void addSelectedJoin() {
    EOJoin newJoin = new EOJoin(myRelationship);
    myRelationship.addJoin(newJoin);
    myJoinsTableViewer.setSelection(new StructuredSelection(newJoin));
  }

  protected void removeSelectedJoins() {
    Object[] selectedJoins = ((IStructuredSelection) myJoinsTableViewer.getSelection()).toArray();
    if (selectedJoins.length > 0) {
      boolean confirmed = MessageDialog.openConfirm(getPart().getSite().getShell(), Messages.getString("EORelationshipBasicEditorSection.removeJoinsTitle"), Messages.getString("EORelationshipBasicEditorSection.removeJoinsMessage")); //$NON-NLS-1$ //$NON-NLS-2$
      if (confirmed) {
        for (int joinNum = 0; joinNum < selectedJoins.length; joinNum++) {
          EOJoin join = (EOJoin) selectedJoins[joinNum];
          myRelationship.removeJoin(join);
        }
      }
    }
  }

  protected void disposeRelationshipListener() {
    if (myRelationship != null) {
      myRelationship.removePropertyChangeListener(EORelationship.DESTINATION, myRelationshipListener);
      myRelationship.removePropertyChangeListener(EORelationship.JOINS, myRelationshipListener);
      EOEntity destination = myRelationship.getDestination();
      if (destination != null) {
        destination.removePropertyChangeListener(EOEntity.ATTRIBUTE, myAttributesListener);
        destination.removePropertyChangeListener(EOEntity.ATTRIBUTES, myAttributesListener);
      }
    }
  }

  protected void disposeBindings() {
    if (myBindingContext != null) {
      myBindingContext.dispose();
    }
    if (myDeleteRuleBinding != null) {
      myDeleteRuleBinding.dispose();
    }
    if (myJoinSemanticBinding != null) {
      myJoinSemanticBinding.dispose();
    }
    if (myModelBinding != null) {
      myModelBinding.dispose();
    }
    if (myEntityBinding != null) {
      myEntityBinding.dispose();
    }
    disposeRelationshipListener();
  }

  public void dispose() {
    super.dispose();
    disposeBindings();
  }

  protected void destinationChanged(EOEntity _oldDestination, EOEntity _newDestination) {
    if (_oldDestination != null) {
      _oldDestination.removePropertyChangeListener(EOEntity.ATTRIBUTE, myAttributesListener);
      _oldDestination.removePropertyChangeListener(EOEntity.ATTRIBUTES, myAttributesListener);
    }
    updateJoins();
    updateButtons();
    if (_newDestination != null) {
      _newDestination.addPropertyChangeListener(EOEntity.ATTRIBUTE, myAttributesListener);
      _newDestination.addPropertyChangeListener(EOEntity.ATTRIBUTES, myAttributesListener);
    }
  }

  protected class ModelSelectionListener implements ISelectionChangedListener {
    public void selectionChanged(SelectionChangedEvent _event) {
      EORelationshipBasicEditorSection.this.updateEntityCombo();
    }
  }

  protected class AttributesListener implements PropertyChangeListener {
    public void propertyChange(PropertyChangeEvent _event) {
      String propertyName = _event.getPropertyName();
      if (propertyName.equals(EOEntity.ATTRIBUTE)) {
        EORelationshipBasicEditorSection.this.updateJoins();
      }
      else if (propertyName.equals(EOEntity.ATTRIBUTES)) {
        EORelationshipBasicEditorSection.this.updateJoins();
      }
    }
  }

  protected class RelationshipListener implements PropertyChangeListener {
    public void propertyChange(PropertyChangeEvent _event) {
      String propertyName = _event.getPropertyName();
      if (propertyName.equals(EORelationship.DESTINATION)) {
        EOEntity oldDestination = (EOEntity) _event.getOldValue();
        EOEntity newDestination = (EOEntity) _event.getNewValue();
        EORelationshipBasicEditorSection.this.destinationChanged(oldDestination, newDestination);
      }
      else if (propertyName.equals(EORelationship.JOINS)) {
        EORelationshipBasicEditorSection.this.updateJoins();
      }
    }
  }

  protected class ButtonUpdateListener implements ISelectionChangedListener {
    public void selectionChanged(SelectionChangedEvent _event) {
      EORelationshipBasicEditorSection.this.updateButtons();
    }
  }

  protected class AddJoinHandler implements SelectionListener {
    public void widgetDefaultSelected(SelectionEvent _e) {
      widgetSelected(_e);
    }

    public void widgetSelected(SelectionEvent _e) {
      EORelationshipBasicEditorSection.this.addSelectedJoin();
    }
  }

  protected class RemoveJoinsHandler implements SelectionListener {
    public void widgetDefaultSelected(SelectionEvent _e) {
      widgetSelected(_e);
    }

    public void widgetSelected(SelectionEvent _e) {
      EORelationshipBasicEditorSection.this.removeSelectedJoins();
    }
  }
}
