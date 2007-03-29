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
package org.objectstyle.wolips.eomodeler.editors.entityIndex;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.internal.databinding.provisional.DataBindingContext;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.editors.attributes.EOAttributesContentProvider;
import org.objectstyle.wolips.eomodeler.editors.attributes.EOAttributesLabelProvider;
import org.objectstyle.wolips.eomodeler.model.AbstractEOArgument;
import org.objectstyle.wolips.eomodeler.model.EOAttribute;
import org.objectstyle.wolips.eomodeler.model.EOEntity;
import org.objectstyle.wolips.eomodeler.model.EOEntityIndex;
import org.objectstyle.wolips.eomodeler.utils.AddRemoveButtonGroup;
import org.objectstyle.wolips.eomodeler.utils.BindingFactory;
import org.objectstyle.wolips.eomodeler.utils.ComboViewerBinding;
import org.objectstyle.wolips.eomodeler.utils.ComparisonUtils;
import org.objectstyle.wolips.eomodeler.utils.IPropertyChangeSource;

public class EOEntityIndexAttributesEditor extends Composite implements IPropertyChangeSource {
	private EOEntityIndex _entityIndex;

	private ComboViewer _attributesComboViewer;

	private ListViewer _entityIndexListViewer;

	private AddRemoveButtonGroup _addRemoveButtonGroup;

	//
	// private AttributesListener _attributesListener;

	//
	// private EOEntityIndexAttributesListener _entityIndexAttributesListener;

	private ButtonUpdateListener _buttonUpdateListener;

	private EOAttribute _selectedAttribute;

	private DataBindingContext _dataBindingContext;

	private ComboViewerBinding _selectedAttributeBinding;

	private PropertyChangeSupport _propertyChangeSupport;

	public EOEntityIndexAttributesEditor(Composite parent, int style) {
		super(parent, style);
		_propertyChangeSupport = new PropertyChangeSupport(this);
		setBackground(parent.getBackground());

		// _attributesListener = new AttributesListener();
		_buttonUpdateListener = new ButtonUpdateListener();

		GridLayout layout = new GridLayout();
		setLayout(layout);

		_entityIndexListViewer = new ListViewer(this, SWT.BORDER | SWT.FLAT | SWT.MULTI);
		_entityIndexListViewer.setContentProvider(new EOEntityIndexAttributesContentProvider());
		_entityIndexListViewer.setLabelProvider(new EOAttributesLabelProvider(new String[] { AbstractEOArgument.NAME }));

		GridData attributeListLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		attributeListLayoutData.heightHint = 100;
		_entityIndexListViewer.getList().setLayoutData(attributeListLayoutData);
		_entityIndexListViewer.addSelectionChangedListener(_buttonUpdateListener);

		Combo attributesCombo = new Combo(this, SWT.BORDER | SWT.FLAT | SWT.READ_ONLY);
		_attributesComboViewer = new ComboViewer(attributesCombo);
		_attributesComboViewer.setLabelProvider(new EOAttributesLabelProvider(new String[] { AbstractEOArgument.NAME }));
		_attributesComboViewer.setContentProvider(new EOAttributesContentProvider());
		GridData attributesComboLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		attributesCombo.setLayoutData(attributesComboLayoutData);

		_addRemoveButtonGroup = new AddRemoveButtonGroup(this, new AddAttributeHandler(), new RemoveAttributesHandler());
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		_propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		_propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		_propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		_propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}

	public void setSelectedAttribute(EOAttribute selectedAttribute) {
		EOAttribute oldAttribute = _selectedAttribute;
		_selectedAttribute = selectedAttribute;
		_propertyChangeSupport.firePropertyChange("selectedAttribute", oldAttribute, selectedAttribute);
	}

	public EOAttribute getSelectedAttribute() {
		return _selectedAttribute;
	}

	public void setEntityIndex(EOEntityIndex entityIndex) {
		if (!ComparisonUtils.equals(entityIndex, _entityIndex)) {
			disposeBindings();

			_entityIndex = entityIndex;
			if (_entityIndex != null) {
				_selectedAttribute = null;
				_dataBindingContext = BindingFactory.createContext();
				_selectedAttributeBinding = new ComboViewerBinding(_attributesComboViewer, this, "selectedAttribute", null, null, null);

				updateAttributes();
				updateButtons();
			}
		}
	}

	protected void updateButtons() {
		boolean removeEnabled = !_entityIndexListViewer.getSelection().isEmpty();
		_addRemoveButtonGroup.setRemoveEnabled(removeEnabled);
	}

	protected void updateAttributes() {
		if (_entityIndexListViewer != null) {
			_entityIndexListViewer.setInput(_entityIndex);
			_attributesComboViewer.setInput(_entityIndex.getEntity());
		}
	}

	protected void addSelectedAttribute() {
		if (_selectedAttribute != null) {
			_entityIndex.addAttribute(_selectedAttribute);
			_entityIndexListViewer.setSelection(new StructuredSelection(_selectedAttribute));
			updateAttributes();
		}
	}

	protected void removeSelectedAttributes() {
		Object[] selectedAttributes = ((IStructuredSelection) _entityIndexListViewer.getSelection()).toArray();
		if (selectedAttributes.length > 0) {
			boolean confirmed = MessageDialog.openConfirm(getShell(), Messages.getString("EOEntityIndexBasicEditorSection.removeAttributesTitle"), Messages.getString("EOEntityIndexBasicEditorSection.removeAttributesMessage"));
			if (confirmed) {
				for (int attributeNum = 0; attributeNum < selectedAttributes.length; attributeNum++) {
					EOAttribute attribute = (EOAttribute) selectedAttributes[attributeNum];
					_entityIndex.removeAttribute(attribute, true);
				}
				updateAttributes();
			}
		}
	}

	public void disposeBindings() {
		if (_entityIndex != null) {
			_dataBindingContext.dispose();
			_selectedAttributeBinding.dispose();
		}
	}

	public void setEnabled(boolean _enabled) {
		super.setEnabled(_enabled);
		_entityIndexListViewer.getList().setEnabled(_enabled);
		updateButtons();
	}

	public void dispose() {
		super.dispose();
		disposeBindings();
	}

	protected class AttributesListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent _event) {
			String propertyName = _event.getPropertyName();
			if (propertyName.equals(EOEntity.ATTRIBUTE)) {
				EOEntityIndexAttributesEditor.this.updateAttributes();
			} else if (propertyName.equals(EOEntity.ATTRIBUTES)) {
				EOEntityIndexAttributesEditor.this.updateAttributes();
			}
		}
	}

	protected class EntityIndexAttributesListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent _event) {
			String propertyName = _event.getPropertyName();
			if (propertyName.equals(EOEntityIndex.ATTRIBUTES)) {
				EOEntityIndexAttributesEditor.this.updateAttributes();
			}
		}
	}

	protected class ButtonUpdateListener implements ISelectionChangedListener {
		public void selectionChanged(SelectionChangedEvent _event) {
			EOEntityIndexAttributesEditor.this.updateButtons();
		}
	}

	protected class AddAttributeHandler implements SelectionListener {
		public void widgetDefaultSelected(SelectionEvent _e) {
			widgetSelected(_e);
		}

		public void widgetSelected(SelectionEvent _e) {
			EOEntityIndexAttributesEditor.this.addSelectedAttribute();
		}
	}

	protected class RemoveAttributesHandler implements SelectionListener {
		public void widgetDefaultSelected(SelectionEvent _e) {
			widgetSelected(_e);
		}

		public void widgetSelected(SelectionEvent _e) {
			EOEntityIndexAttributesEditor.this.removeSelectedAttributes();
		}
	}
}
