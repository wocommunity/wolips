package org.objectstyle.wolips.eomodeler.editors.entity;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.core.model.EOModelGroup;
import org.objectstyle.wolips.eomodeler.editors.relationship.EOModelLabelProvider;
import org.objectstyle.wolips.eomodeler.editors.relationship.EOModelListContentProvider;

public class EntityPicker extends Composite {
	private ComboViewer _modelComboViewer;

	private ComboViewer _entityComboViewer;

	public EntityPicker(Composite parent, int style) {
		super(parent, style);

		GridLayout layout = new GridLayout(2, false);
		setLayout(layout);

		Combo modelCombo = new Combo(this, SWT.BORDER | SWT.FLAT | SWT.READ_ONLY);
		_modelComboViewer = new ComboViewer(modelCombo);
		_modelComboViewer.setLabelProvider(new EOModelLabelProvider());
		_modelComboViewer.setContentProvider(new EOModelListContentProvider());
		// GridData modelComboLayoutData = new
		// GridData(GridData.FILL_HORIZONTAL);
		// modelCombo.setLayoutData(modelComboLayoutData);
		_modelComboViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				EOModel selectedModel = (EOModel) selection.getFirstElement();
				_entityComboViewer.setInput(selectedModel);
				if (_entityComboViewer.getCombo().getItemCount() > 0) {
				  _entityComboViewer.setSelection(new StructuredSelection(_entityComboViewer.getElementAt(0)));
				}
			}
		});

		Combo entityCombo = new Combo(this, SWT.BORDER | SWT.FLAT | SWT.READ_ONLY);
		_entityComboViewer = new ComboViewer(entityCombo);
		_entityComboViewer.setLabelProvider(new EOEntityLabelProvider());
		_entityComboViewer.setContentProvider(new EOEntityListContentProvider(false, true));
		GridData entityComboLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		entityCombo.setLayoutData(entityComboLayoutData);
	}

	public void setModelGroup(EOModelGroup modelGroup) {
		_modelComboViewer.setInput(modelGroup);
	}
	
	public void setEntity(EOEntity entity) {
		if (entity != null) {
			_modelComboViewer.setSelection(new StructuredSelection(entity.getModel()), true);
			_entityComboViewer.setSelection(new StructuredSelection(entity), true);
		}
		else {
			_entityComboViewer.setSelection(new StructuredSelection());
		}
	}

	public EOEntity getEntity() {
		return (EOEntity) ((IStructuredSelection) _entityComboViewer.getSelection()).getFirstElement();
	}

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		_entityComboViewer.addSelectionChangedListener(listener);
	}

	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		_entityComboViewer.removeSelectionChangedListener(listener);
	}
}
