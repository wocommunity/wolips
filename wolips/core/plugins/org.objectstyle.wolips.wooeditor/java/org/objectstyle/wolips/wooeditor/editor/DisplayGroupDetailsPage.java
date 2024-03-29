/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2005 The ObjectStyle Group,
 * and individual authors of the software.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        ObjectStyle Group (http://objectstyle.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "ObjectStyle Group" and "Cayenne"
 *    must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact andrus@objectstyle.org.
 *
 * 5. Products derived from this software may not be called "ObjectStyle"
 *    nor may "ObjectStyle" appear in their names without prior written
 *    permission of the ObjectStyle Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE OBJECTSTYLE GROUP OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the ObjectStyle Group.  For more
 * information on the ObjectStyle Group, please see
 * <http://objectstyle.org/>.
 *
 */
package org.objectstyle.wolips.wooeditor.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.typed.BeanProperties;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.objectstyle.wolips.wodclipse.core.woo.DisplayGroup;
import org.objectstyle.wolips.wooeditor.databinding.observable.CustomSWTObservables;
import org.objectstyle.wolips.wooeditor.widgets.RadioGroup;

public class DisplayGroupDetailsPage implements IDetailsPage {

	private IManagedForm myManagedForm;

	private DisplayGroup myDisplayGroup;

	private Group myMasterDetailGroup;

	private Text myNameText;

	private Combo myClassNameCombo;

	private Spinner myEntriesPerBatchSpinner;

	private Combo myEntityCombo;

	private Text myEditingContextText;

	private Combo myMasterEntityCombo;

	private Combo mySortAttributeCombo;

	private Combo myFetchSpecCombo;

	private Combo myDetailKeyCombo;

	private Combo myQualificationCombo;

	private Button myHasDetailButton;

	private Button myFetchOnLoadButton;

	private Button mySelectsFirstObjectButton;

	private RadioGroup mySortRadioGroup;

	private DataBindingContext myBindingContext;

	private PropertyChangeListener myDirtyStateListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(final PropertyChangeEvent evt) {
			myManagedForm.dirtyStateChanged();
		}
	};

	private final ModifyListener myEntityModifyListener = new ModifyListener() {
		@Override
		public void modifyText(final ModifyEvent e) {
			Object selection = mySortRadioGroup.getSelection();
			if (!DisplayGroup.NOT_SORTED.equals(selection)) {
				mySortRadioGroup.setSelection(DisplayGroup.NOT_SORTED);
			}
			if (!myHasDetailButton.getSelection()) {
				myDetailKeyCombo.setText("");
			}
		}
	};

	private final ModifyListener myMasterEntityListener = new ModifyListener() {
		@Override
		public void modifyText(final ModifyEvent e) {
			myDetailKeyCombo.setText("");
		}
	};

	private final SelectionListener mySortEntityListener = new SelectionListener() {
		@Override
		public void widgetDefaultSelected(final SelectionEvent e) {
			widgetSelected(e);
		}

		@Override
		public void widgetSelected(final SelectionEvent e) {
			Object selection = mySortRadioGroup.getSelection();
			if (DisplayGroup.NOT_SORTED.equals(selection)) {
				mySortRadioGroup.setSelection(DisplayGroup.ASCENDING);
			}
		}
	};

	public DisplayGroupDetailsPage() {
		super();
	}

	private void bind() {
		// XXX This method is too long
		if (myDisplayGroup == null) {
			return;
		}
		myBindingContext = new DataBindingContext();

		myBindingContext.bindValue(
				//SWTObservables.observeText(myNameText, SWT.Modify),
				WidgetProperties.text(SWT.Modify).observe(myNameText),
				//BeansObservables.observeValue(myDisplayGroup, DisplayGroup.NAME),
				BeanProperties.value(DisplayGroup.NAME).observe(myDisplayGroup),
				null, null);

		myBindingContext.bindList(
				//SWTObservables.observeItems(myClassNameCombo),
				WidgetProperties.items().observe(myClassNameCombo),
				//BeansObservables.observeList(Realm.getDefault(), myDisplayGroup, DisplayGroup.CLASS_NAME_LIST),
				BeanProperties.list(DisplayGroup.CLASS_NAME_LIST).observe(Realm.getDefault(), myDisplayGroup),
				null, null);
		myBindingContext.bindValue(
				//SWTObservables.observeSingleSelectionIndex(myClassNameCombo),
				WidgetProperties.singleSelectionIndex().observe(myClassNameCombo),
				//BeansObservables.observeValue(myDisplayGroup, DisplayGroup.CLASS_NAME_INDEX),
				BeanProperties.value(DisplayGroup.CLASS_NAME_INDEX).observe(myDisplayGroup),
				null, null);

		myBindingContext.bindList(
				//SWTObservables.observeItems(myEntityCombo),
				WidgetProperties.items().observe(myEntityCombo),
				//BeansObservables.observeList(Realm.getDefault(), myDisplayGroup, DisplayGroup.ENTITY_LIST),
				BeanProperties.list(DisplayGroup.ENTITY_LIST).observe(Realm.getDefault(), myDisplayGroup),
				null, null);
		myBindingContext.bindValue(
				//SWTObservables.observeEnabled(myMasterDetailGroup),
				WidgetProperties.enabled().observe(myMasterDetailGroup),
				//BeansObservables.observeValue(myDisplayGroup, DisplayGroup.HAS_MASTER_DETAIL),
				BeanProperties.value(DisplayGroup.HAS_MASTER_DETAIL).observe(myDisplayGroup),
				new UpdateValueStrategy<Object, Boolean>(UpdateValueStrategy.POLICY_NEVER), null);

		UpdateValueStrategy<Object, Boolean> booleanInverse = new UpdateValueStrategy<Object, Boolean>(UpdateValueStrategy.POLICY_UPDATE) {
			@Override
			protected IStatus doSet(final IObservableValue<? super Boolean> observableValue, final Boolean value) {
				Boolean newValue = !(Boolean) value;
				return super.doSet(observableValue, newValue);
			}
		};

		myBindingContext.bindValue(
				//SWTObservables.observeEnabled(myEntityCombo),
				WidgetProperties.enabled().observe(myEntityCombo), 
				//BeansObservables.observeValue(myDisplayGroup, DisplayGroup.HAS_MASTER_DETAIL),
				BeanProperties.value(DisplayGroup.HAS_MASTER_DETAIL).observe(myDisplayGroup),
				new UpdateValueStrategy<Object, Boolean>(UpdateValueStrategy.POLICY_NEVER), booleanInverse);
		myBindingContext.bindValue(
				CustomSWTObservables.observeText(myEntityCombo), 
				//BeansObservables.observeValue(myDisplayGroup, DisplayGroup.ENTITY_NAME),
				BeanProperties.value(DisplayGroup.ENTITY_NAME).observe(myDisplayGroup), 
				null, null);

		myBindingContext.bindValue(
				//SWTObservables.observeEnabled((myEditingContextText)),
				WidgetProperties.enabled().observe(myEditingContextText), 
				//BeansObservables.observeValue(myDisplayGroup, DisplayGroup.HAS_MASTER_DETAIL),
				BeanProperties.value(DisplayGroup.HAS_MASTER_DETAIL).observe(myDisplayGroup), 
				new UpdateValueStrategy<Object, Boolean>(UpdateValueStrategy.POLICY_NEVER), booleanInverse);
		myBindingContext.bindValue(
				//SWTObservables.observeText(myEditingContextText, SWT.Modify),
				WidgetProperties.text(SWT.Modify).observe(myEditingContextText), 
				//BeansObservables.observeValue(myDisplayGroup, DisplayGroup.EDITING_CONTEXT),
				BeanProperties.value(DisplayGroup.EDITING_CONTEXT).observe(myDisplayGroup), 
				null, null);

		myBindingContext.bindValue(
				//SWTObservables.observeSelection(myHasDetailButton),
				WidgetProperties.buttonSelection().observe(myHasDetailButton),
				//BeansObservables.observeValue(myDisplayGroup, DisplayGroup.HAS_MASTER_DETAIL),
				BeanProperties.value(DisplayGroup.HAS_MASTER_DETAIL).observe(myDisplayGroup), 
				null, null);

		myBindingContext.bindList(
				//SWTObservables.observeItems(myMasterEntityCombo),
				WidgetProperties.items().observe(myMasterEntityCombo),
				//BeansObservables.observeList(Realm.getDefault(), myDisplayGroup, DisplayGroup.ENTITY_LIST),
				BeanProperties.list(DisplayGroup.ENTITY_LIST).observe(Realm.getDefault(), myDisplayGroup),
				null, null);
		myBindingContext.bindValue(
				CustomSWTObservables.observeText(myMasterEntityCombo), 
				//BeansObservables.observeValue(myDisplayGroup, DisplayGroup.MASTER_ENTITY_NAME),
				BeanProperties.value(DisplayGroup.MASTER_ENTITY_NAME).observe(myDisplayGroup), 
				null, null);

		myBindingContext.bindList(
				//SWTObservables.observeItems(myDetailKeyCombo),
				WidgetProperties.items().observe(myDetailKeyCombo), 
				//BeansObservables.observeList(Realm.getDefault(), myDisplayGroup, DisplayGroup.DETAIL_KEY_LIST),
				BeanProperties.list(DisplayGroup.DETAIL_KEY_LIST).observe(Realm.getDefault(), myDisplayGroup), 
				null, null);

		myBindingContext.bindValue(
				CustomSWTObservables.observeText(myDetailKeyCombo), 
				//BeansObservables.observeValue(myDisplayGroup, DisplayGroup.DETAIL_KEY_NAME),
				BeanProperties.value(DisplayGroup.DETAIL_KEY_NAME).observe(myDisplayGroup),
				null, null);

		myBindingContext.bindValue(
				//SWTObservables.observeSelection(myEntriesPerBatchSpinner),
				WidgetProperties.spinnerSelection().observe(myEntriesPerBatchSpinner), 
				//BeansObservables.observeValue(myDisplayGroup, DisplayGroup.ENTRIES_PER_BATCH),
				BeanProperties.value(DisplayGroup.ENTRIES_PER_BATCH).observe(myDisplayGroup), 
				null, null);

		myBindingContext.bindValue(
				//SWTObservables.observeSelection(mySelectsFirstObjectButton),
				WidgetProperties.buttonSelection().observe(mySelectsFirstObjectButton), 
				//BeansObservables.observeValue(myDisplayGroup, DisplayGroup.SELECTS_FIRST_OBJECT),
				BeanProperties.value(DisplayGroup.SELECTS_FIRST_OBJECT).observe(myDisplayGroup), 
				null, null);

		myBindingContext.bindList(
				//SWTObservables.observeItems(myQualificationCombo),
				WidgetProperties.items().observe(myQualificationCombo), 
				//BeansObservables.observeList(Realm.getDefault(), myDisplayGroup, DisplayGroup.QUALIFICATION_LIST),
				BeanProperties.list(DisplayGroup.QUALIFICATION_LIST).observe(Realm.getDefault(), myDisplayGroup), 
				null, null);
		myBindingContext.bindValue(
				//SWTObservables.observeSingleSelectionIndex(myQualificationCombo),
				WidgetProperties.singleSelectionIndex().observe(myQualificationCombo),
				//BeansObservables.observeValue(myDisplayGroup, DisplayGroup.QUALIFICATION_INDEX),
				BeanProperties.value(DisplayGroup.QUALIFICATION_INDEX).observe(myDisplayGroup), 
				null, null);

		myBindingContext.bindValue(
				//SWTObservables.observeSelection(myFetchOnLoadButton),
				WidgetProperties.buttonSelection().observe(myFetchOnLoadButton), 
				//BeansObservables.observeValue(myDisplayGroup, DisplayGroup.FETCHES_ON_LOAD),
				BeanProperties.value(DisplayGroup.FETCHES_ON_LOAD).observe(myDisplayGroup), 
				null, null);

		myBindingContext.bindList(
				//SWTObservables.observeItems(mySortAttributeCombo),
				WidgetProperties.items().observe(mySortAttributeCombo), 
				//BeansObservables.observeList(Realm.getDefault(), myDisplayGroup, DisplayGroup.SORT_LIST),
				BeanProperties.list(DisplayGroup.SORT_LIST).observe(Realm.getDefault(), myDisplayGroup), 
				null, null);

		myBindingContext.bindValue(
				CustomSWTObservables.observeSelection(mySortAttributeCombo), 
				//BeansObservables.observeValue(myDisplayGroup, DisplayGroup.SORT_ORDER_KEY),
				BeanProperties.value(DisplayGroup.SORT_ORDER_KEY).observe(myDisplayGroup), 
				null, null);

		myBindingContext.bindValue(
				CustomSWTObservables.observeSelection(mySortRadioGroup), 
				//BeansObservables.observeValue(myDisplayGroup, DisplayGroup.SORT_ORDER),
				BeanProperties.value(DisplayGroup.SORT_ORDER).observe(myDisplayGroup),
				null, null);

		final UpdateValueStrategy<Object, Boolean> fetchSpecEmpty = new UpdateValueStrategy<>();
		fetchSpecEmpty.setConverter(new IConverter<Object, Boolean>() {
			@Override
			public Boolean convert(final Object fromObject) {
				boolean result = fromObject != null && ((List<?>) fromObject).size() > 0;
				if (!myHasDetailButton.getSelection()) {
					return result;
				}
				return false;
			}

			@Override
			public Object getFromType() {
				return new ArrayList<String>();
			}

			@Override
			public Object getToType() {
				return true;
			}
		});

		final UpdateValueStrategy<Object, Boolean> fetchSpecEnabled = new UpdateValueStrategy<>(UpdateValueStrategy.POLICY_UPDATE) {
			@Override
			protected IStatus doSet(final IObservableValue<? super Boolean> observableValue, final Boolean value) {
				Boolean newValue = false;
				if (myFetchSpecCombo.getItemCount() > 0) {
					newValue = !(Boolean) value;
				}
				return super.doSet(observableValue, newValue);
			}
		};
		myBindingContext.bindValue(
				//SWTObservables.observeEnabled((myFetchSpecCombo)),
				WidgetProperties.enabled().observe(myFetchSpecCombo), 
				//BeansObservables.observeValue(myDisplayGroup, DisplayGroup.HAS_MASTER_DETAIL),
				BeanProperties.value(DisplayGroup.HAS_MASTER_DETAIL).observe(myDisplayGroup),
				new UpdateValueStrategy<Boolean, Object>(UpdateValueStrategy.POLICY_NEVER), fetchSpecEnabled);

		myBindingContext.bindValue(
				//SWTObservables.observeEnabled((myFetchSpecCombo)),
				WidgetProperties.enabled().observe(myFetchSpecCombo), 
				//BeansObservables.observeValue(myDisplayGroup, DisplayGroup.FETCH_SPEC_LIST),
				BeanProperties.value(DisplayGroup.class, DisplayGroup.FETCH_SPEC_LIST).observe(myDisplayGroup),
				new UpdateValueStrategy<Boolean, List<String>>(UpdateValueStrategy.POLICY_NEVER), fetchSpecEmpty);

		myBindingContext.bindList(
				//SWTObservables.observeItems(myFetchSpecCombo),
				WidgetProperties.items().observe(myFetchSpecCombo), 
				//BeansObservables.observeList(Realm.getDefault(), myDisplayGroup, DisplayGroup.FETCH_SPEC_LIST),
				BeanProperties.list(DisplayGroup.FETCH_SPEC_LIST).observe(Realm.getDefault(), myDisplayGroup), 
				null, null);

		UpdateValueStrategy<Object, String> fetchSpecSelection = new UpdateValueStrategy<>() {
			@Override
			protected IStatus doSet(IObservableValue<? super String> observableValue, String value) {
				if (value == null || value.equals("")) {
					value = DisplayGroup.FETCH_SPEC_NONE;
				}
				return super.doSet(observableValue, value);
			}
		};
		myBindingContext.bindValue(
				CustomSWTObservables.observeSelection(myFetchSpecCombo), 
				//BeansObservables.observeValue(myDisplayGroup, DisplayGroup.FETCH_SPEC_NAME),
				BeanProperties.value(DisplayGroup.FETCH_SPEC_NAME).observe(myDisplayGroup), 
				null, fetchSpecSelection);

		myEntityCombo.addModifyListener(myEntityModifyListener);
		myMasterEntityCombo.addModifyListener(myMasterEntityListener);
		mySortAttributeCombo.addSelectionListener(mySortEntityListener);

	}

	@Override
	public void commit(final boolean onSave) {
		// nothing to do
	}

	@Override
	public void createContents(final Composite parent) {
		// XXX: Q - This method is too long

		TableWrapLayout pageLayout = new TableWrapLayout();
		pageLayout.topMargin = 5;
		pageLayout.leftMargin = 5;
		pageLayout.rightMargin = 2;
		pageLayout.bottomMargin = 2;
		parent.setLayout(pageLayout);

		// Create form for layout
		FormToolkit form = myManagedForm.getToolkit();
		Section displayGroupSection = form.createSection(parent, Section.TITLE_BAR);
		displayGroupSection.marginWidth = 10;
		displayGroupSection.setText("Display Group Details");
		displayGroupSection.setDescription("Set the properties of the " + "selected display group.");

		TableWrapData tableWrapData = new TableWrapData(TableWrapData.FILL, TableWrapData.TOP);
		tableWrapData.grabHorizontal = true;
		displayGroupSection.setLayoutData(tableWrapData);

		// form.createCompositeSeparator(displayGroupSection);
		Composite displayGroupComposite = form.createComposite(displayGroupSection);

		GridLayout displayGroupLayout = new GridLayout();
		displayGroupLayout.marginWidth = 0;
		displayGroupLayout.marginHeight = 0;
		displayGroupLayout.numColumns = 2;
		displayGroupComposite.setLayout(displayGroupLayout);

		// Name
		form.createLabel(displayGroupComposite, "Name:");
		myNameText = form.createText(displayGroupComposite, "", SWT.SINGLE);
		GridData nameFieldLayoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
		myNameText.setLayoutData(nameFieldLayoutData);

		// Class Name
		form.createLabel(displayGroupComposite, "Class Type:");
		myClassNameCombo = new Combo(displayGroupComposite, SWT.POP_UP);
		GridData classNameFieldLayoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
		myNameText.setLayoutData(classNameFieldLayoutData);

		// Entity
		form.createLabel(displayGroupComposite, "Entity:");
		myEntityCombo = new Combo(displayGroupComposite, SWT.DROP_DOWN);
		GridData entityFieldLayoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
		myEntityCombo.setLayoutData(entityFieldLayoutData);

		// Editing Context
		form.createLabel(displayGroupComposite, "Editing Context:");
		myEditingContextText = form.createText(displayGroupComposite, "", SWT.SINGLE);
		GridData editingContextFieldLayoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
		myEditingContextText.setLayoutData(editingContextFieldLayoutData);

		// HasDetail
		createSpacer(form, displayGroupComposite, 1);
		myHasDetailButton = form.createButton(displayGroupComposite, "Has detail data source", SWT.CHECK);
		GridData hasDetailLayoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
		hasDetailLayoutData.horizontalIndent = 5;
		myHasDetailButton.setLayoutData(hasDetailLayoutData);

		// Master/Detail Group
		myMasterDetailGroup = new Group(displayGroupComposite, SWT.NONE);
		myMasterDetailGroup.setBackground(displayGroupComposite.getBackground());
		myMasterDetailGroup.setText("Master / Detail");
		GridLayout masterDetailLayout = new GridLayout();
		masterDetailLayout.numColumns = 2;
		myMasterDetailGroup.setEnabled(false);
		myMasterDetailGroup.setLayout(masterDetailLayout);

		GridData masterDetailLayoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
		masterDetailLayoutData.horizontalSpan = 2;
		myMasterDetailGroup.setLayoutData(masterDetailLayoutData);

		// Master Entity
		form.createLabel(myMasterDetailGroup, "Master Entity:");
		myMasterEntityCombo = new Combo(myMasterDetailGroup, SWT.DROP_DOWN);
		GridData masterEntityFieldLayoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
		myMasterEntityCombo.setLayoutData(masterEntityFieldLayoutData);
		// XXX For debugging
		myMasterEntityCombo.setData("myMasterEntityCombo");

		// Detail Key
		Label label = form.createLabel(myMasterDetailGroup, "Detail Key:");
		label.setAlignment(SWT.RIGHT);
		myDetailKeyCombo = new Combo(myMasterDetailGroup, SWT.DROP_DOWN);
		GridData detailKeyFieldLayoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
		myDetailKeyCombo.setLayoutData(detailKeyFieldLayoutData);

		// Entries per batch group (fixes layout of label and control)
		Composite batchGroupComposite = new Composite(displayGroupComposite, SWT.NONE);
		batchGroupComposite.setBackground(displayGroupComposite.getBackground());
		GridLayout batchLayout = new GridLayout();
		batchLayout.numColumns = 2;
		batchGroupComposite.setLayout(batchLayout);
		GridData batchGroupLayoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
		batchGroupLayoutData.horizontalSpan = 2;
		batchGroupComposite.setLayoutData(batchGroupLayoutData);

		// Entries per batch
		form.createLabel(batchGroupComposite, "Entities per batch:");
		myEntriesPerBatchSpinner = new Spinner(batchGroupComposite, SWT.NULL);
		myEntriesPerBatchSpinner.setIncrement(5);
		GridData entriesPerBatchLayoutData = new GridData(SWT.FILL, SWT.FILL, false, false);
		myEntriesPerBatchSpinner.setLayoutData(entriesPerBatchLayoutData);

		// Selects first object
		createSpacer(form, displayGroupComposite, 1);
		mySelectsFirstObjectButton = form.createButton(displayGroupComposite, "Selects first object on load", SWT.CHECK);
		GridData selectsFirstObjectLayoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
		selectsFirstObjectLayoutData.horizontalIndent = 5;
		mySelectsFirstObjectButton.setLayoutData(selectsFirstObjectLayoutData);

		// Fetches on load
		createSpacer(form, displayGroupComposite, 1);
		myFetchOnLoadButton = form.createButton(displayGroupComposite, "Fetches on load", SWT.CHECK);
		GridData fetchOnLoadLayoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
		fetchOnLoadLayoutData.horizontalIndent = 5;
		myFetchOnLoadButton.setLayoutData(fetchOnLoadLayoutData);

		// Qualification type
		form.createLabel(displayGroupComposite, "Qualification:");
		myQualificationCombo = new Combo(displayGroupComposite, SWT.POP_UP);
		GridData qualificationFieldLayoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
		myQualificationCombo.setLayoutData(qualificationFieldLayoutData);

		// Sorting attribute
		form.createLabel(displayGroupComposite, "Sorting:");
		mySortAttributeCombo = new Combo(displayGroupComposite, SWT.POP_UP);
		GridData sortingFieldLayoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
		mySortAttributeCombo.setLayoutData(sortingFieldLayoutData);

		// Sort order radio group
		createSpacer(form, displayGroupComposite, 1);
		Composite radioGroupComposite = new Composite(displayGroupComposite, SWT.NO_BACKGROUND);
		radioGroupComposite.setLayout(new GridLayout());

		// Sort order radio buttons
		mySortRadioGroup = new RadioGroup();
		for (String s : DisplayGroup.SORT_OPTIONS) {
			Button sortButton = form.createButton(radioGroupComposite, s, SWT.RADIO);
			GridData sortButtonLayoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
			sortButton.setLayoutData(sortButtonLayoutData);
			mySortRadioGroup.add(sortButton);
		}

		// Fetch spec
		form.createLabel(displayGroupComposite, "Fetch Spec:");
		myFetchSpecCombo = new Combo(displayGroupComposite, SWT.POP_UP);
		GridData fetchSpecFieldLayoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
		myFetchSpecCombo.setLayoutData(fetchSpecFieldLayoutData);

		form.paintBordersFor(displayGroupSection);
		displayGroupSection.setClient(displayGroupComposite);
	}

	private void createSpacer(final FormToolkit toolkit, final Composite parent, final int span) {
		Label spacer = toolkit.createLabel(parent, "");
		GridData gd = new GridData();
		gd.horizontalSpan = span;
		spacer.setLayoutData(gd);
	}

	@Override
	public void dispose() {
		disposeBindings();
	}

	protected void disposeBindings() {
		if (myBindingContext != null) {
			myBindingContext.dispose();
		}
		if (myEntityCombo != null && !myEntityCombo.isDisposed()) {
			myEntityCombo.removeModifyListener(myEntityModifyListener);
		}
		if (myMasterEntityCombo != null && !myMasterEntityCombo.isDisposed()) {
			myMasterEntityCombo.removeModifyListener(myMasterEntityListener);
		}
		if (mySortAttributeCombo != null && !mySortAttributeCombo.isDisposed()) {
			mySortAttributeCombo.removeSelectionListener(mySortEntityListener);
		}
	}

	@Override
	public void initialize(final IManagedForm form) {
		this.myManagedForm = form;
	}

	@Override
	public boolean isDirty() {
		if (myDisplayGroup == null) {
			return false;
		}
		return myDisplayGroup.getWooModel().isDirty();
	}

	@Override
	public boolean isStale() {
		return false;
	}

	@Override
	public void refresh() {
	}

	@Override
	public void selectionChanged(final IFormPart part, final ISelection selection) {
		disposeBindings();
		IStructuredSelection ssel = (IStructuredSelection) selection;
		if (ssel.size() > 0) {
			setDisplayGroup((DisplayGroup) ssel.getFirstElement());
		} else {
			setDisplayGroup(null);
		}
		bind();
	}

	public void setDisplayGroup(final DisplayGroup displayGroup) {
		if (myDisplayGroup != null) {
			myDisplayGroup.removePropertyChangeListener(myDirtyStateListener);
		}
		myDisplayGroup = displayGroup;
		if (myDisplayGroup != null) {
			myDisplayGroup.addPropertyChangeListener(myDirtyStateListener);
		}

	}

	@Override
	public void setFocus() {
		myNameText.setFocus();
	}

	@Override
	public boolean setFormInput(final Object input) {
		return false;
	}

}
