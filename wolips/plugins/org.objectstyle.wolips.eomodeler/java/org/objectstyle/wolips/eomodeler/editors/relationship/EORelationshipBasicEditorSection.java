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

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
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
import org.objectstyle.wolips.eomodeler.Activator;
import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.core.model.EODeleteRule;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOJoinSemantic;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.core.model.EOModelGroup;
import org.objectstyle.wolips.eomodeler.core.model.EORelationship;
import org.objectstyle.wolips.eomodeler.core.model.EORelationshipPath;
import org.objectstyle.wolips.eomodeler.editors.entity.EOEntityLabelProvider;
import org.objectstyle.wolips.eomodeler.editors.entity.EOEntityListContentProvider;
import org.objectstyle.wolips.eomodeler.utils.BooleanUpdateValueStrategy;
import org.objectstyle.wolips.eomodeler.utils.ComboViewerBinding;
import org.objectstyle.wolips.eomodeler.utils.FormUtils;

public class EORelationshipBasicEditorSection extends AbstractPropertySection {
	private EORelationship _relationship;

	private Text _nameText;

	private Text _definitionText;

	private Button _toOneButton;

	private Button _toManyButton;

	private Button _optionalButton;

	private Button _mandatoryButton;

	private JoinsTableEditor _joinsTableEditor;

	private ComboViewer _deleteRuleComboViewer;

	private ComboViewer _modelComboViewer;

	private ComboViewer _joinSemanticComboViewer;

	private ComboViewer _entityComboViewer;

	private JoinsListener _joinsListener;

	private DataBindingContext _bindingContext;

	private ComboViewerBinding _deleteRuleBinding;

	private ComboViewerBinding _joinSemanticBinding;

	private ComboViewerBinding _modelBinding;

	private ComboViewerBinding _entityBinding;

	private Button _classPropertyButton;

	public EORelationshipBasicEditorSection() {
		_joinsListener = new JoinsListener();
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

		getWidgetFactory().createCLabel(topForm, Messages.getString("EORelationship." + EORelationship.NAME), SWT.NONE);
		_nameText = new Text(topForm, SWT.BORDER);
		GridData nameFieldLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		_nameText.setLayoutData(nameFieldLayoutData);

		getWidgetFactory().createCLabel(topForm, Messages.getString("EORelationship." + EORelationship.DEFINITION), SWT.NONE);
		_definitionText = new Text(topForm, SWT.BORDER);
		GridData definitionFieldLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		_definitionText.setLayoutData(definitionFieldLayoutData);

		getWidgetFactory().createCLabel(topForm, Messages.getString("EORelationship.settings"), SWT.NONE);
		
		Composite settingsComposite = new Composite(topForm, SWT.NONE);
		settingsComposite.setBackground(topForm.getBackground());
		FillLayout settingsLayout = new FillLayout(SWT.HORIZONTAL);
		settingsLayout.spacing = 10;
		settingsComposite.setLayout(settingsLayout);
		GridData settingsLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		settingsLayoutData.heightHint = 25;
		settingsComposite.setLayoutData(settingsLayoutData);

		_toManyButton = new Button(settingsComposite, SWT.TOGGLE | SWT.FLAT);
		_toManyButton.setToolTipText(Messages.getString("EORelationship." + EORelationship.TO_MANY));
		_toManyButton.setImage(Activator.getDefault().getImageRegistry().get(Activator.TO_MANY_ICON));

		_classPropertyButton = new Button(settingsComposite, SWT.TOGGLE | SWT.FLAT);
		_classPropertyButton.setToolTipText(Messages.getString("EORelationship." + EORelationship.CLASS_PROPERTY));
		_classPropertyButton.setImage(Activator.getDefault().getImageRegistry().get(Activator.CLASS_PROPERTY_ICON));

		_optionalButton = new Button(settingsComposite, SWT.TOGGLE | SWT.FLAT);
		_optionalButton.setToolTipText(Messages.getString("EORelationship." + EORelationship.OPTIONAL));
		_optionalButton.setImage(Activator.getDefault().getImageRegistry().get(Activator.ALLOW_NULL_ICON));

//		getWidgetFactory().createCLabel(topForm, Messages.getString("EORelationship.cardinality"), SWT.NONE);
//		Composite cardinalityComposite = getWidgetFactory().createPlainComposite(topForm, SWT.NONE);
//		GridLayout cardinalityLayout = new GridLayout();
//		cardinalityLayout.numColumns = 2;
//		cardinalityLayout.makeColumnsEqualWidth = true;
//		cardinalityComposite.setLayout(cardinalityLayout);
//		_toOneButton = new Button(cardinalityComposite, SWT.RADIO);
//		_toOneButton.setText(Messages.getString("EORelationship.toOne"));
//		GridData toOneButtonLayoutData = new GridData(GridData.FILL_HORIZONTAL);
//		_toOneButton.setLayoutData(toOneButtonLayoutData);
//		_toManyButton = new Button(cardinalityComposite, SWT.RADIO);
//		_toManyButton.setText(Messages.getString("EORelationship.toMany"));
//		GridData toManyButtonLayoutData = new GridData(GridData.FILL_HORIZONTAL);
//		_toManyButton.setLayoutData(toManyButtonLayoutData);
//		GridData cardinalityCompositeLayoutData = new GridData(GridData.FILL_HORIZONTAL);
//		cardinalityComposite.setLayoutData(cardinalityCompositeLayoutData);

//		getWidgetFactory().createCLabel(topForm, Messages.getString("EORelationship.optionality"), SWT.NONE);
//		Composite optionalityComposite = getWidgetFactory().createPlainComposite(topForm, SWT.NONE);
//		GridLayout optionalityLayout = new GridLayout();
//		optionalityLayout.numColumns = 2;
//		optionalityLayout.makeColumnsEqualWidth = true;
//		optionalityComposite.setLayout(optionalityLayout);
//		_optionalButton = new Button(optionalityComposite, SWT.RADIO);
//		_optionalButton.setText(Messages.getString("EORelationship.optional"));
//		GridData optionalButtonLayoutData = new GridData(GridData.FILL_HORIZONTAL);
//		_optionalButton.setLayoutData(optionalButtonLayoutData);
//		_mandatoryButton = new Button(optionalityComposite, SWT.RADIO);
//		_mandatoryButton.setText(Messages.getString("EORelationship.mandatory"));
//		GridData mandatoryButtonLayoutData = new GridData(GridData.FILL_HORIZONTAL);
//		_mandatoryButton.setLayoutData(mandatoryButtonLayoutData);
//		GridData optioanlityCompositeLayoutData = new GridData(GridData.FILL_HORIZONTAL);
//		optionalityComposite.setLayoutData(optioanlityCompositeLayoutData);

		getWidgetFactory().createCLabel(topForm, Messages.getString("EORelationship." + EORelationship.DELETE_RULE), SWT.NONE);
		Combo deleteRuleCombo = new Combo(topForm, SWT.BORDER | SWT.FLAT | SWT.READ_ONLY);
		_deleteRuleComboViewer = new ComboViewer(deleteRuleCombo);
		_deleteRuleComboViewer.setLabelProvider(new EODeleteRuleLabelProvider());
		_deleteRuleComboViewer.setContentProvider(new EODeleteRuleContentProvider());
		_deleteRuleComboViewer.setInput(EODeleteRule.DELETE_RULES);
		_deleteRuleComboViewer.setSelection(new StructuredSelection(EODeleteRule.NULLIFY));
		GridData deleteRuleComboLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		deleteRuleCombo.setLayoutData(deleteRuleComboLayoutData);

		getWidgetFactory().createCLabel(topForm, Messages.getString("EORelationship.model"), SWT.NONE);
		Combo modelCombo = new Combo(topForm, SWT.BORDER | SWT.FLAT | SWT.READ_ONLY);
		_modelComboViewer = new ComboViewer(modelCombo);
		_modelComboViewer.setLabelProvider(new EOModelLabelProvider());
		_modelComboViewer.setContentProvider(new EOModelListContentProvider());
		_modelComboViewer.addSelectionChangedListener(new ModelSelectionListener());
		GridData modelRuleComboLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		modelCombo.setLayoutData(modelRuleComboLayoutData);

		getWidgetFactory().createCLabel(topForm, Messages.getString("EORelationship." + EORelationship.DESTINATION), SWT.NONE);
		Combo entityCombo = new Combo(topForm, SWT.BORDER | SWT.FLAT | SWT.READ_ONLY);
		_entityComboViewer = new ComboViewer(entityCombo);
		_entityComboViewer.setLabelProvider(new EOEntityLabelProvider());
		_entityComboViewer.setContentProvider(new EOEntityListContentProvider(false, true, false));
		GridData entityComboLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		entityCombo.setLayoutData(entityComboLayoutData);

		Combo joinSemanticCombo = new Combo(topForm, SWT.BORDER | SWT.FLAT | SWT.READ_ONLY);
		_joinSemanticComboViewer = new ComboViewer(joinSemanticCombo);
		_joinSemanticComboViewer.setLabelProvider(new EOJoinSemanticLabelProvider());
		_joinSemanticComboViewer.setContentProvider(new EOJoinSemanticContentProvider());
		_joinSemanticComboViewer.setInput(EOJoinSemantic.JOIN_SEMANTICS);
		_joinSemanticComboViewer.setSelection(new StructuredSelection(EOJoinSemantic.INNER));
		GridData joinSemanticLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		joinSemanticLayoutData.verticalAlignment = SWT.TOP;
		joinSemanticCombo.setLayoutData(joinSemanticLayoutData);

		_joinsTableEditor = new JoinsTableEditor(topForm, SWT.NONE);
		_joinsTableEditor.setBackground(topForm.getBackground());

		GridData joinsTableLayoutData = new GridData(GridData.FILL_BOTH);
		_joinsTableEditor.setLayoutData(joinsTableLayoutData);
	}

	public void setInput(IWorkbenchPart part, ISelection selection) {
		if (ComparisonUtils.equals(selection, getSelection())) {
			return;
		}
		
		super.setInput(part, selection);
		EORelationship relationship = null;
		Object selectedObject = ((IStructuredSelection) selection).getFirstElement();
		if (selectedObject instanceof EORelationship) {
			relationship = (EORelationship) selectedObject;
		} else if (selectedObject instanceof EORelationshipPath) {
			relationship = ((EORelationshipPath) selectedObject).getChildRelationship();
		}
		if (!ComparisonUtils.equals(relationship, _relationship)) {
			disposeBindings();

			_relationship = relationship;
			if (_relationship != null) {
				_relationship.addPropertyChangeListener(EORelationship.JOINS, _joinsListener);
				_relationship.addPropertyChangeListener(EORelationship.DEFINITION, _joinsListener);
				_joinsTableEditor.setRelationship(_relationship);
				_modelComboViewer.setInput(_relationship);
				_entityComboViewer.setInput(_relationship);
				EOEntity destinationEntity = _relationship.getDestination();
				if (destinationEntity != null) {
					_modelComboViewer.setSelection(new StructuredSelection(destinationEntity.getModel()));
				}

				_bindingContext = new DataBindingContext();
				_bindingContext.bindValue(SWTObservables.observeText(_nameText, SWT.Modify), BeansObservables.observeValue(_relationship, EORelationship.NAME), null, null);
				_bindingContext.bindValue(SWTObservables.observeText(_definitionText, SWT.Modify), BeansObservables.observeValue(_relationship, EORelationship.DEFINITION), null, null);
				//_bindingContext.bindValue(SWTObservables.observeSelection(_toOneButton), BeansObservables.observeValue(_relationship, EORelationship.TO_ONE), null, new BooleanUpdateValueStrategy());
				_bindingContext.bindValue(SWTObservables.observeSelection(_toManyButton), BeansObservables.observeValue(_relationship, EORelationship.TO_MANY), null, new BooleanUpdateValueStrategy());
				_bindingContext.bindValue(SWTObservables.observeSelection(_optionalButton), BeansObservables.observeValue(_relationship, EORelationship.OPTIONAL), null, new BooleanUpdateValueStrategy());
				//_bindingContext.bindValue(SWTObservables.observeSelection(_mandatoryButton), BeansObservables.observeValue(_relationship, EORelationship.MANDATORY), null, new BooleanUpdateValueStrategy());
				_bindingContext.bindValue(SWTObservables.observeSelection(_classPropertyButton), BeansObservables.observeValue(_relationship, EORelationship.CLASS_PROPERTY), null, new BooleanUpdateValueStrategy());

				_deleteRuleBinding = new ComboViewerBinding(_deleteRuleComboViewer, _relationship, EORelationship.DELETE_RULE, null, null, null);
				_joinSemanticBinding = new ComboViewerBinding(_joinSemanticComboViewer, _relationship, EORelationship.JOIN_SEMANTIC, _relationship.getEntity().getModel().getModelGroup(), EOModelGroup.MODELS, null);
				_entityBinding = new ComboViewerBinding(_entityComboViewer, _relationship, EORelationship.DESTINATION, _relationship.getEntity().getModel(), EOModel.ENTITIES, null);

				// boolean flattened = myRelationship.isFlattened();
				// myDefinitionLabel.setVisible(flattened);
				// myDefinitionText.setVisible(flattened);

				updateModelAndEntityCombosEnabled();
			}
		}
	}

	protected void updateModelAndEntityCombosEnabled() {
		boolean joinsEnabled = !_relationship.isFlattened();
		boolean hasJoins = _relationship.getJoins().size() != 0;
		boolean enabled = !hasJoins && joinsEnabled;
		_modelComboViewer.getCombo().setEnabled(enabled);
		_entityComboViewer.getCombo().setEnabled(enabled);
		//_joinSemanticComboViewer.getCombo().setEnabled(enabled);
		_definitionText.setEnabled(!hasJoins);
	}

	protected void updateEntityCombo() {
		IStructuredSelection selection = (IStructuredSelection) _modelComboViewer.getSelection();
		EOModel selectedModel = (EOModel) selection.getFirstElement();
		_entityComboViewer.setInput(selectedModel);
	}

	protected void disposeBindings() {
		if (_relationship != null) {
			_relationship.removePropertyChangeListener(EORelationship.JOINS, _joinsListener);
			_relationship.removePropertyChangeListener(EORelationship.DEFINITION, _joinsListener);
		}
		if (_bindingContext != null) {
			_bindingContext.dispose();
		}
		if (_deleteRuleBinding != null) {
			_deleteRuleBinding.dispose();
		}
		if (_joinSemanticBinding != null) {
			_joinSemanticBinding.dispose();
		}
		if (_modelBinding != null) {
			_modelBinding.dispose();
		}
		if (_entityBinding != null) {
			_entityBinding.dispose();
		}
		if (_joinsTableEditor != null) {
			_joinsTableEditor.disposeBindings();
		}
	}

	public void dispose() {
		super.dispose();
		disposeBindings();
	}

	protected class JoinsListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent event) {
			EORelationshipBasicEditorSection.this.updateModelAndEntityCombosEnabled();
		}
	}

	protected class ModelSelectionListener implements ISelectionChangedListener {
		public void selectionChanged(SelectionChangedEvent event) {
			EORelationshipBasicEditorSection.this.updateEntityCombo();
		}
	}
}
