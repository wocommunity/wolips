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
package org.objectstyle.wolips.eomodeler.properties;

import org.eclipse.jface.internal.databinding.provisional.DataBindingContext;
import org.eclipse.jface.internal.databinding.provisional.description.Property;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.objectstyle.wolips.eomodeler.model.EODeleteRule;
import org.objectstyle.wolips.eomodeler.model.EOJoinSemantic;
import org.objectstyle.wolips.eomodeler.model.EOModel;
import org.objectstyle.wolips.eomodeler.model.EOModelGroup;
import org.objectstyle.wolips.eomodeler.model.EORelationship;
import org.objectstyle.wolips.eomodeler.model.EORelationshipPath;
import org.objectstyle.wolips.eomodeler.utils.BindingFactory;

public class EORelationshipSection extends AbstractPropertySection {
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
  private Composite myForm;

  private DataBindingContext myBindingContext;
  private ComboViewerBinding myDeleteRuleBinding;
  private ComboViewerBinding myJoinSemanticBinding;
  private ComboViewerBinding myModelBinding;
  private ComboViewerBinding myEntityBinding;

  public void createControls(Composite _parent, TabbedPropertySheetPage _tabbedPropertySheetPage) {
    super.createControls(_parent, _tabbedPropertySheetPage);
    myForm = getWidgetFactory().createFlatFormComposite(_parent);
    FormLayout formLayout = new FormLayout();
    myForm.setLayout(formLayout);

    Composite topForm = getWidgetFactory().createPlainComposite(myForm, SWT.NONE);
    FormData topFormData = new FormData();
    topFormData.top = new FormAttachment(0, 10);
    topFormData.left = new FormAttachment(0, 10);
    topFormData.bottom = new FormAttachment(100, 0);
    topFormData.right = new FormAttachment(100, 0);
    topForm.setLayoutData(topFormData);

    GridLayout topFormLayout = new GridLayout();
    topFormLayout.numColumns = 2;
    topForm.setLayout(topFormLayout);

    getWidgetFactory().createCLabel(topForm, "Name", SWT.NONE);
    myNameText = new Text(topForm, SWT.BORDER);
    GridData nameFieldLayoutData = new GridData(GridData.FILL_HORIZONTAL);
    myNameText.setLayoutData(nameFieldLayoutData);

    getWidgetFactory().createCLabel(topForm, "Cardinality", SWT.NONE);
    Composite cardinalityComposite = getWidgetFactory().createPlainComposite(topForm, SWT.NONE);
    GridLayout cardinalityLayout = new GridLayout();
    cardinalityLayout.numColumns = 2;
    cardinalityLayout.makeColumnsEqualWidth = true;
    cardinalityComposite.setLayout(cardinalityLayout);
    myToOneButton = new Button(cardinalityComposite, SWT.RADIO);
    myToOneButton.setText("To One");
    GridData toOneButtonLayoutData = new GridData(GridData.FILL_HORIZONTAL);
    myToOneButton.setLayoutData(toOneButtonLayoutData);
    myToManyButton = new Button(cardinalityComposite, SWT.RADIO);
    myToManyButton.setText("To Many");
    GridData toManyButtonLayoutData = new GridData(GridData.FILL_HORIZONTAL);
    myToManyButton.setLayoutData(toManyButtonLayoutData);
    GridData cardinalityCompositeLayoutData = new GridData(GridData.FILL_HORIZONTAL);
    cardinalityComposite.setLayoutData(cardinalityCompositeLayoutData);

    getWidgetFactory().createCLabel(topForm, "Optionality", SWT.NONE);
    Composite optionalityComposite = getWidgetFactory().createPlainComposite(topForm, SWT.NONE);
    GridLayout optionalityLayout = new GridLayout();
    optionalityLayout.numColumns = 2;
    optionalityLayout.makeColumnsEqualWidth = true;
    optionalityComposite.setLayout(optionalityLayout);
    myOptionalButton = new Button(optionalityComposite, SWT.RADIO);
    myOptionalButton.setText("Optional");
    GridData optionalButtonLayoutData = new GridData(GridData.FILL_HORIZONTAL);
    myOptionalButton.setLayoutData(optionalButtonLayoutData);
    myMandatoryButton = new Button(optionalityComposite, SWT.RADIO);
    myMandatoryButton.setText("Mandatory");
    GridData mandatoryButtonLayoutData = new GridData(GridData.FILL_HORIZONTAL);
    myMandatoryButton.setLayoutData(mandatoryButtonLayoutData);
    GridData optioanlityCompositeLayoutData = new GridData(GridData.FILL_HORIZONTAL);
    optionalityComposite.setLayoutData(optioanlityCompositeLayoutData);

    getWidgetFactory().createCLabel(topForm, "Delete Rule", SWT.NONE);
    Combo deleteRuleCombo = new Combo(topForm, SWT.BORDER | SWT.FLAT | SWT.READ_ONLY);
    myDeleteRuleComboViewer = new ComboViewer(deleteRuleCombo);
    myDeleteRuleComboViewer.setLabelProvider(new EODeleteRuleLabelProvider());
    myDeleteRuleComboViewer.setContentProvider(new EODeleteRuleContentProvider());
    myDeleteRuleComboViewer.setInput(EODeleteRule.DELETE_RULES);
    myDeleteRuleComboViewer.setSelection(new StructuredSelection(EODeleteRule.NULLIFY));
    GridData deleteRuleComboLayoutData = new GridData(GridData.FILL_HORIZONTAL);
    deleteRuleCombo.setLayoutData(deleteRuleComboLayoutData);

    getWidgetFactory().createCLabel(topForm, "Model", SWT.NONE);
    Combo modelCombo = new Combo(topForm, SWT.BORDER | SWT.FLAT | SWT.READ_ONLY);
    myModelComboViewer = new ComboViewer(modelCombo);
    myModelComboViewer.setLabelProvider(new EOModelLabelProvider());
    myModelComboViewer.setContentProvider(new EOModelListContentProvider());
    myModelComboViewer.addSelectionChangedListener(new ModelSelectionListener());
    GridData modelRuleComboLayoutData = new GridData(GridData.FILL_HORIZONTAL);
    modelCombo.setLayoutData(modelRuleComboLayoutData);

    getWidgetFactory().createCLabel(topForm, "Entity", SWT.NONE);
    Combo entityCombo = new Combo(topForm, SWT.BORDER | SWT.FLAT | SWT.READ_ONLY);
    myEntityComboViewer = new ComboViewer(entityCombo);
    myEntityComboViewer.setLabelProvider(new EOEntityLabelProvider());
    myEntityComboViewer.setContentProvider(new EOEntityListContentProvider());
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

    Table joinsTable = new Table(topForm, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION | SWT.FLAT);
    myJoinsTableViewer = new TableViewer(joinsTable);
    GridData joinsTableLayoutData = new GridData(GridData.FILL_HORIZONTAL);
    joinsTableLayoutData.heightHint = 100;
    joinsTable.setLayoutData(joinsTableLayoutData);
  }

  protected void updateModelAndEntityCombosEnabled() {
    boolean hasJoins = myRelationship.getJoins().size() > 0;
    myModelComboViewer.getCombo().setEnabled(!hasJoins);
    myEntityComboViewer.getCombo().setEnabled(!hasJoins);
  }

  protected void updateEntityCombo() {
    IStructuredSelection selection = (IStructuredSelection) myModelComboViewer.getSelection();
    EOModel selectedModel = (EOModel) selection.getFirstElement();
    myEntityComboViewer.setInput(selectedModel);
  }

  protected class ModelSelectionListener implements ISelectionChangedListener {
    public void selectionChanged(SelectionChangedEvent _event) {
      EORelationshipSection.this.updateEntityCombo();
    }
  }

  public void setInput(IWorkbenchPart _part, ISelection _selection) {
    super.setInput(_part, _selection);
    Object selectedObject = ((IStructuredSelection) _selection).getFirstElement();
    if (selectedObject instanceof EORelationship) {
      myRelationship = (EORelationship) selectedObject;
    }
    else if (selectedObject instanceof EORelationshipPath) {
      myRelationship = ((EORelationshipPath) selectedObject).getChildRelationship();
    }
    disposeBindings();

    myModelComboViewer.setInput(myRelationship);
    myEntityComboViewer.setInput(myRelationship);
    myModelComboViewer.setSelection(new StructuredSelection(myRelationship.getEntity().getModel()));

    myBindingContext = BindingFactory.createContext();
    myBindingContext.bind(myNameText, new Property(myRelationship, EORelationship.NAME), null);
    myBindingContext.bind(myToOneButton, new Property(myRelationship, EORelationship.TO_ONE), null);
    myBindingContext.bind(myToManyButton, new Property(myRelationship, EORelationship.TO_MANY), null);
    myBindingContext.bind(myOptionalButton, new Property(myRelationship, EORelationship.OPTIONAL), null);
    myBindingContext.bind(myMandatoryButton, new Property(myRelationship, EORelationship.MANDATORY), null);

    myDeleteRuleBinding = new ComboViewerBinding(myDeleteRuleComboViewer, myRelationship, EORelationship.DELETE_RULE, null, null);
    myJoinSemanticBinding = new ComboViewerBinding(myJoinSemanticComboViewer, myRelationship, EORelationship.JOIN_SEMANTIC, myRelationship.getEntity().getModel().getModelGroup(), EOModelGroup.MODELS);
    myEntityBinding = new ComboViewerBinding(myEntityComboViewer, myRelationship, EORelationship.DESTINATION, myRelationship.getEntity().getModel(), EOModel.ENTITIES);
    //myBindingContext.bind(myJoinsTableViewer, new Property(myRelationship, EORelationship.JOINS), null);

    updateModelAndEntityCombosEnabled();
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
  }

  public void dispose() {
    super.dispose();
    disposeBindings();
  }

  //  protected FormData createControlFormData() {
  //    FormData data = new FormData();
  //    data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
  //    data.right = new FormAttachment(100, 0);
  //    data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
  //    return data;
  //  }
  //
  //  protected CLabel createLabelFor(Composite _parent, Control _control, String _labelText) {
  //    CLabel label = getWidgetFactory().createCLabel(_parent, _labelText);
  //    FormData data = new FormData();
  //    data.left = new FormAttachment(0, 0);
  //    data.right = new FormAttachment(_control, -ITabbedPropertyConstants.HSPACE);
  //    data.top = new FormAttachment(_control, 0, SWT.CENTER);
  //    label.setLayoutData(data);
  //    return label;
  //  }
}
