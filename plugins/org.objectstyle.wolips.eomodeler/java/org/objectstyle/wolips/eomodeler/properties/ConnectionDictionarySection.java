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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.model.EOModel;
import org.objectstyle.wolips.eomodeler.model.IConnectionDictionaryOwner;
import org.objectstyle.wolips.eomodeler.utils.BindingFactory;

public class ConnectionDictionarySection extends AbstractPropertySection {
  private IConnectionDictionaryOwner myConnectionDictionaryOwner;

  private Text myUsernameText;
  private Text myPasswordText;
  private Text myURLText;
  private Text myDriverText;
  private Text myPluginText;

  private DataBindingContext myBindingContext;

  public ConnectionDictionarySection() {
    // DO NOTHING
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

    addFormEntriesAbove(topForm);

    getWidgetFactory().createCLabel(topForm, Messages.getString("EOModel." + IConnectionDictionaryOwner.USERNAME), SWT.NONE);
    myUsernameText = new Text(topForm, SWT.BORDER);
    myUsernameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    getWidgetFactory().createCLabel(topForm, Messages.getString("EOModel." + IConnectionDictionaryOwner.PASSWORD), SWT.NONE);
    myPasswordText = new Text(topForm, SWT.BORDER);
    myPasswordText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    getWidgetFactory().createCLabel(topForm, Messages.getString("EOModel." + IConnectionDictionaryOwner.URL), SWT.NONE);
    myURLText = new Text(topForm, SWT.BORDER);
    myURLText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    getWidgetFactory().createCLabel(topForm, Messages.getString("EOModel." + IConnectionDictionaryOwner.DRIVER), SWT.NONE);
    myDriverText = new Text(topForm, SWT.BORDER);
    myDriverText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    getWidgetFactory().createCLabel(topForm, Messages.getString("EOModel." + IConnectionDictionaryOwner.PLUGIN), SWT.NONE);
    myPluginText = new Text(topForm, SWT.BORDER);
    myPluginText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    
    addFormEntriesBelow(topForm);
  }
  
  protected void addFormEntriesAbove(Composite _form) {
    // DO NOTHING
  }
  
  protected void addFormEntriesBelow(Composite _form) {
    // DO NOTHING
  }

  public void setInput(IWorkbenchPart _part, ISelection _selection) {
    super.setInput(_part, _selection);
    disposeBindings();

    Object selectedObject = ((IStructuredSelection) _selection).getFirstElement();
    if (selectedObject instanceof IConnectionDictionaryOwner) {
      myConnectionDictionaryOwner = (IConnectionDictionaryOwner) selectedObject;
    }

    if (myConnectionDictionaryOwner != null) {
      myBindingContext = BindingFactory.createContext();
      addBindings(myBindingContext);
    }
  }
  
  public IConnectionDictionaryOwner getConnectionDictionaryOwner() {
    return myConnectionDictionaryOwner;
  }
  
  protected void addBindings(DataBindingContext _context) {
    _context.bind(myUsernameText, new Property(myConnectionDictionaryOwner, IConnectionDictionaryOwner.USERNAME), null);
    _context.bind(myPasswordText, new Property(myConnectionDictionaryOwner, IConnectionDictionaryOwner.PASSWORD), null);
    _context.bind(myURLText, new Property(myConnectionDictionaryOwner, IConnectionDictionaryOwner.URL), null);
    _context.bind(myDriverText, new Property(myConnectionDictionaryOwner, IConnectionDictionaryOwner.DRIVER), null);
    _context.bind(myPluginText, new Property(myConnectionDictionaryOwner, IConnectionDictionaryOwner.PLUGIN), null);
  }

  protected void disposeBindings() {
    if (myBindingContext != null) {
      myBindingContext.dispose();
    }
  }

  public void dispose() {
    super.dispose();
    disposeBindings();
  }
}
