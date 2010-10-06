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

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.objectstyle.wolips.baseforplugins.util.ComparisonUtils;
import org.objectstyle.wolips.eomodeler.Activator;
import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.core.model.AbstractEOArgument;
import org.objectstyle.wolips.eomodeler.core.model.EOAttribute;
import org.objectstyle.wolips.eomodeler.core.model.EOAttributePath;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.editors.entity.EOEntityListContentProvider;
import org.objectstyle.wolips.eomodeler.utils.BooleanUpdateValueStrategy;
import org.objectstyle.wolips.eomodeler.utils.ComboViewerBinding;

public class EOAttributeBasicEditorSection extends AbstractEOArgumentBasicEditorSection {
	private ComboViewer _prototypeComboViewer;

	private ComboViewerBinding _prototypeBinding;

	private Button _primaryKeyButton;

	private Button _classPropertyButton;

	private Button _lockingButton;

	@Override
	protected void _addSettings(Composite settings) {
		_primaryKeyButton = new Button(settings, SWT.TOGGLE | SWT.FLAT);
		_primaryKeyButton.setToolTipText(Messages.getString("EOAttribute." + EOAttribute.PRIMARY_KEY));
		_primaryKeyButton.setImage(Activator.getDefault().getImageRegistry().get(Activator.PRIMARY_KEY_ICON));

		_classPropertyButton = new Button(settings, SWT.TOGGLE | SWT.FLAT);
		_classPropertyButton.setToolTipText(Messages.getString("EOAttribute." + EOAttribute.CLASS_PROPERTY));
		_classPropertyButton.setImage(Activator.getDefault().getImageRegistry().get(Activator.CLASS_PROPERTY_ICON));

		_lockingButton = new Button(settings, SWT.TOGGLE | SWT.FLAT);
		_lockingButton.setToolTipText(Messages.getString("EOAttribute." + EOAttribute.USED_FOR_LOCKING));
		_lockingButton.setImage(Activator.getDefault().getImageRegistry().get(Activator.LOCKING_ICON));
	}

	protected void _addComponents(Composite parent) {
		getWidgetFactory().createCLabel(parent, Messages.getString("EOAttribute." + EOAttribute.PROTOTYPE), SWT.NONE);
		Combo prototypeCombo = new Combo(parent, SWT.BORDER | SWT.FLAT | SWT.READ_ONLY);
		_prototypeComboViewer = new ComboViewer(prototypeCombo);
		_prototypeComboViewer.setLabelProvider(new EOPrototypeListLabelProvider());
		_prototypeComboViewer.setContentProvider(new EOPrototypeListContentProvider());
		GridData prototypeComboLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		prototypeCombo.setLayoutData(prototypeComboLayoutData);
	}

	protected void _argumentChanged(AbstractEOArgument argument) {
		EOAttribute attribute = (EOAttribute) argument;
		if (attribute != null) {
			_prototypeComboViewer.setInput(attribute);
			_prototypeBinding = new ComboViewerBinding(_prototypeComboViewer, attribute, EOAttribute.PROTOTYPE, attribute.getEntity().getModel(), EOModel.ENTITIES, EOEntityListContentProvider.BLANK_ENTITY);

			getBindingContext().bindValue(SWTObservables.observeSelection(_primaryKeyButton), BeansObservables.observeValue(attribute, EOAttribute.PRIMARY_KEY), null, new BooleanUpdateValueStrategy());
			getBindingContext().bindValue(SWTObservables.observeSelection(_classPropertyButton), BeansObservables.observeValue(attribute, EOAttribute.CLASS_PROPERTY), null, new BooleanUpdateValueStrategy());
			getBindingContext().bindValue(SWTObservables.observeSelection(_lockingButton), BeansObservables.observeValue(attribute, EOAttribute.USED_FOR_LOCKING), null, new BooleanUpdateValueStrategy());
		}
	}

	protected void disposeBindings() {
		if (_prototypeBinding != null) {
			_prototypeBinding.dispose();
		}
		super.disposeBindings();
	}

	public void setInput(IWorkbenchPart part, ISelection selection) {
		if (!ComparisonUtils.equals(selection, getSelection())) {
			EOAttribute attribute = null;
			Object selectedObject = ((IStructuredSelection) selection).getFirstElement();
			super.setInput(part, selection);
			if (selectedObject instanceof EOAttribute) {
				attribute = (EOAttribute) selectedObject;
			} else if (selectedObject instanceof EOAttributePath) {
				attribute = ((EOAttributePath) selectedObject).getChildAttribute();
			}
			setArgument(attribute);
		}
	}
}
