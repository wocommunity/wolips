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
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.objectstyle.wolips.eomodeler.model.EORelationship;
import org.objectstyle.wolips.eomodeler.model.EORelationshipPath;
import org.objectstyle.wolips.eomodeler.utils.BindingFactory;

public class EORelationshipSection extends AbstractPropertySection {
  private EORelationship myRelationship;
  private Text myNameField;
  private DataBindingContext myContext;
  private Composite myComposite;
  private CCombo myModelField;
  private CCombo myEntityField;
  private List myJoinsField;
  private CCombo mySourceAttributeField;
  private CCombo myDestinationAttributeField;
  private Button myAddJoinButton;
  private Button myRemoveJoinButton;

  public void createControls(Composite _parent, TabbedPropertySheetPage _tabbedPropertySheetPage) {
    super.createControls(_parent, _tabbedPropertySheetPage);
    myComposite = getWidgetFactory().createFlatFormComposite(_parent);

    //    Composite 

    myNameField = createText(myComposite);
    createLabelFor(myComposite, myNameField, "Name");

    myModelField = createCombo(myComposite);
    createLabelFor(myComposite, myModelField, "Model");
    myEntityField = createCombo(myComposite);
    createLabelFor(myComposite, myEntityField, "Entity");
    myJoinsField = createList(myComposite, SWT.NONE);
    createLabelFor(myComposite, myJoinsField, "Joins");
    mySourceAttributeField = createCombo(myComposite);
    createLabelFor(myComposite, mySourceAttributeField, "Source");
    myDestinationAttributeField = createCombo(myComposite);
    createLabelFor(myComposite, myDestinationAttributeField, "Dest");
    myAddJoinButton = getWidgetFactory().createButton(myComposite, " + ", SWT.PUSH);
    myAddJoinButton.setLayoutData(createControlFormData());
    createLabelFor(myComposite, myAddJoinButton, "Add");
    myRemoveJoinButton = getWidgetFactory().createButton(myComposite, " - ", SWT.PUSH);
    myRemoveJoinButton.setLayoutData(createControlFormData());
    createLabelFor(myComposite, myRemoveJoinButton, "Remove");
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
    if (myContext != null) {
      myContext.dispose();
    }
    myContext = BindingFactory.createContext(myComposite);
    //myContext.bind(myNameField, new Property(myRelationship, EORelationship.NAME), null);
  }

  public void dispose() {
    super.dispose();
  }

  protected FormData createControlFormData() {
    FormData data = new FormData();
    data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
    return data;
  }

  protected Text createText(Composite _parent) {
    Text text = getWidgetFactory().createText(_parent, ""); //$NON-NLS-1$
    //text.setLayoutData(createControlFormData());
    return text;
  }

  protected CCombo createCombo(Composite _parent) {
    CCombo combo = getWidgetFactory().createCCombo(_parent);
    //combo.setLayoutData(createControlFormData());
    return combo;
  }

  protected List createList(Composite _parent, int _style) {
    List list = getWidgetFactory().createList(_parent, _style);
    //list.setLayoutData(createControlFormData());
    return list;
  }

  protected CLabel createLabelFor(Composite _parent, Control _control, String _labelText) {
    CLabel label = getWidgetFactory().createCLabel(_parent, _labelText);
    FormData data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(_control, -ITabbedPropertyConstants.HSPACE);
    data.top = new FormAttachment(_control, 0, SWT.CENTER);
    label.setLayoutData(data);
    return label;
  }
}
