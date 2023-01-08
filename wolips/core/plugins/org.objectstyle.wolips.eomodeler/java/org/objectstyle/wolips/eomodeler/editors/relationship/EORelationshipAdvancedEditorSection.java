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
import org.objectstyle.wolips.eomodeler.core.model.EORelationship;
import org.objectstyle.wolips.eomodeler.core.model.EORelationshipPath;
import org.objectstyle.wolips.eomodeler.utils.BooleanUpdateValueStrategy;
import org.objectstyle.wolips.eomodeler.utils.FormUtils;
import org.objectstyle.wolips.eomodeler.utils.UglyFocusHackWorkaroundListener;

public class EORelationshipAdvancedEditorSection extends AbstractPropertySection {
	private EORelationship _relationship;

	private Text _numberOfToManyFaultsToBatchFetchText;

	private Button _ownsDestinationButton;

	private Button _propagatesPrimaryKeyButton;

	private Button _clientClassPropertyButton;

	private Button _commonClassPropertyButton;

	private DataBindingContext _bindingContext;

	private RelationshipPropertyChangeListener _relationshipPropertyChangeListener;

	public EORelationshipAdvancedEditorSection() {
		_relationshipPropertyChangeListener = new RelationshipPropertyChangeListener();
	}

	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);
		Composite form = getWidgetFactory().createFlatFormComposite(parent);
		FormLayout formLayout = new FormLayout();
		form.setLayout(formLayout);

		Composite topForm = FormUtils.createForm(getWidgetFactory(), form);

		getWidgetFactory().createCLabel(topForm, Messages.getString("EORelationship." + EORelationship.NUMBER_OF_TO_MANY_FAULTS_TO_BATCH_FETCH), SWT.NONE);
		_numberOfToManyFaultsToBatchFetchText = new Text(topForm, SWT.BORDER);
		GridData nameFieldLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		_numberOfToManyFaultsToBatchFetchText.setLayoutData(nameFieldLayoutData);
		UglyFocusHackWorkaroundListener.addListener(_numberOfToManyFaultsToBatchFetchText);

		getWidgetFactory().createCLabel(topForm, "", SWT.NONE);
		_ownsDestinationButton = new Button(topForm, SWT.CHECK);
		_ownsDestinationButton.setText(Messages.getString("EORelationship." + EORelationship.OWNS_DESTINATION));

		getWidgetFactory().createCLabel(topForm, "", SWT.NONE);
		_propagatesPrimaryKeyButton = new Button(topForm, SWT.CHECK);
		_propagatesPrimaryKeyButton.setText(Messages.getString("EORelationship." + EORelationship.PROPAGATES_PRIMARY_KEY));

		getWidgetFactory().createCLabel(topForm, "", SWT.NONE);
		_clientClassPropertyButton = new Button(topForm, SWT.CHECK);
		_clientClassPropertyButton.setText(Messages.getString("EORelationship." + EORelationship.CLIENT_CLASS_PROPERTY));

		getWidgetFactory().createCLabel(topForm, "", SWT.NONE);
		_commonClassPropertyButton = new Button(topForm, SWT.CHECK);
		_commonClassPropertyButton.setText(Messages.getString("EORelationship." + EORelationship.COMMON_CLASS_PROPERTY));
	}

	public void setInput(IWorkbenchPart part, ISelection selection) {
		if (ComparisonUtils.equals(selection, getSelection())) {
			return;
		}

		super.setInput(part, selection);
		disposeBindings();

		Object selectedObject = ((IStructuredSelection) selection).getFirstElement();
		if (selectedObject instanceof EORelationship) {
			_relationship = (EORelationship) selectedObject;
		} else if (selectedObject instanceof EORelationshipPath) {
			_relationship = ((EORelationshipPath) selectedObject).getChildRelationship();
		}
		if (_relationship != null) {
			_relationship.addPropertyChangeListener(EORelationship.TO_MANY, _relationshipPropertyChangeListener);
			_bindingContext = new DataBindingContext();
			_bindingContext.bindValue(
					WidgetProperties.text(SWT.Modify).observe(_numberOfToManyFaultsToBatchFetchText),
					BeanProperties.value(EORelationship.class, EORelationship.NUMBER_OF_TO_MANY_FAULTS_TO_BATCH_FETCH, Integer.class).observe(_relationship),
					null, null);
			// new BindSpec(null, null, new RegexStringValidator("^[0-9]*$",
			// "^[0-9]+$", "Please enter a number"), null)
			_bindingContext.bindValue(
					WidgetProperties.buttonSelection().observe(_ownsDestinationButton),
					BeanProperties.value(EORelationship.class, EORelationship.OWNS_DESTINATION, Boolean.class).observe(_relationship),
					null, new BooleanUpdateValueStrategy());
			_bindingContext.bindValue(
					WidgetProperties.buttonSelection().observe(_propagatesPrimaryKeyButton),
					BeanProperties.value(EORelationship.class, EORelationship.PROPAGATES_PRIMARY_KEY, Boolean.class).observe(_relationship),
					null, new BooleanUpdateValueStrategy());
			_bindingContext.bindValue(
					WidgetProperties.buttonSelection().observe(_clientClassPropertyButton),
					BeanProperties.value(EORelationship.class, EORelationship.CLIENT_CLASS_PROPERTY, Boolean.class).observe(_relationship),
					null, new BooleanUpdateValueStrategy());
			_bindingContext.bindValue(
					WidgetProperties.buttonSelection().observe(_commonClassPropertyButton),
					BeanProperties.value(EORelationship.class, EORelationship.COMMON_CLASS_PROPERTY, Boolean.class).observe(_relationship),
					null, new BooleanUpdateValueStrategy());
			updateCardinalityEnabled();
		}
	}

	protected void updateCardinalityEnabled() {
		Boolean isToMany = _relationship.isToMany();
		boolean enabled = (isToMany != null && isToMany.booleanValue());
		_numberOfToManyFaultsToBatchFetchText.setEnabled(enabled);
	}

	protected void removeRelationshipListeners() {
		if (_relationship != null) {
			_relationship.removePropertyChangeListener(EORelationship.TO_MANY, _relationshipPropertyChangeListener);
		}
	}

	protected void disposeBindings() {
		if (_bindingContext != null) {
			_bindingContext.dispose();
		}
		removeRelationshipListeners();
	}

	public void dispose() {
		super.dispose();
		disposeBindings();
	}

	protected class RelationshipPropertyChangeListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent event) {
			EORelationshipAdvancedEditorSection.this.updateCardinalityEnabled();
		}
	}
}
