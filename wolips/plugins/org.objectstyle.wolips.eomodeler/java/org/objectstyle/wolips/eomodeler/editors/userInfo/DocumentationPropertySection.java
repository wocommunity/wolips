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
package org.objectstyle.wolips.eomodeler.editors.userInfo;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.objectstyle.wolips.eomodeler.core.model.IUserInfoable;
import org.objectstyle.wolips.eomodeler.core.model.UserInfoableEOModelObject;

public class DocumentationPropertySection extends AbstractPropertySection {
	private Text _documentationText;

	private DataBindingContext _bindingContext;

	private UserInfoableEOModelObject _userInfoable;

	private Browser _browser;

	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);

		Label documentationLabel = new Label(composite, SWT.NONE);
		documentationLabel.setBackground(composite.getBackground());
		documentationLabel.setText("Documentation");
		FormData documentationLabelFormData = new FormData();
		documentationLabelFormData.left = new FormAttachment(0, 8);
		documentationLabelFormData.right = new FormAttachment(100, -6);
		documentationLabelFormData.top = new FormAttachment(0, 5);
		documentationLabel.setLayoutData(documentationLabelFormData);

		_documentationText = getWidgetFactory().createText(composite, "", SWT.BORDER | SWT.WRAP | SWT.MULTI | SWT.V_SCROLL);
		FormData textFormData = new FormData();
		textFormData.left = new FormAttachment(0, 5);
		textFormData.right = new FormAttachment(100, -3);
		textFormData.top = new FormAttachment(documentationLabel, -3);
		textFormData.bottom = new FormAttachment(50, 0);
		textFormData.width = 100;
		textFormData.height = 50;
		_documentationText.setLayoutData(textFormData);

		Label previewLabel = new Label(composite, SWT.NONE);
		previewLabel.setBackground(composite.getBackground());
		previewLabel.setText("HTML Preview");
		FormData labelFormData = new FormData();
		labelFormData.left = new FormAttachment(0, 8);
		labelFormData.right = new FormAttachment(100, -6);
		labelFormData.top = new FormAttachment(_documentationText, 5);
		previewLabel.setLayoutData(labelFormData);

		_browser = new Browser(composite, SWT.NONE);
		FormData browserFormData = new FormData();
		browserFormData.left = new FormAttachment(0, 8);
		browserFormData.right = new FormAttachment(100, -6);
		browserFormData.top = new FormAttachment(previewLabel, -3);
		browserFormData.bottom = new FormAttachment(100, -5);
		browserFormData.width = 100;
		_browser.setLayoutData(browserFormData);
	}

	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		removeListeners();
		if (selection instanceof IStructuredSelection) {
			_userInfoable = (UserInfoableEOModelObject) ((IStructuredSelection) selection).getFirstElement();

			_bindingContext = new DataBindingContext();
			_bindingContext.bindValue(SWTObservables.observeText(_documentationText, SWT.Modify), BeansObservables.observeValue(_userInfoable, UserInfoableEOModelObject.DOCUMENTATION_KEY), null, null);
			_bindingContext.bindValue(new BrowserTextObservableValue(_browser, "body { margin: 0px; margin-right: 10px; font-size: 0.8em; }"), BeansObservables.observeValue(_userInfoable, UserInfoableEOModelObject.DOCUMENTATION_KEY), null, null);
		} else {
			_userInfoable = null;
		}
	}

	protected void removeListeners() {
		if (_userInfoable != null) {
			_bindingContext.dispose();
			_bindingContext = null;
		}
	}

	public void dispose() {
		super.dispose();
		removeListeners();
	}

	public boolean shouldUseExtraSpace() {
		return true;
	}

	public IUserInfoable getUserInfoable() {
		return _userInfoable;
	}
}
