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
package org.objectstyle.wolips.eomodeler.editors.dataType;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.typed.BeanProperties;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.core.model.AbstractEOArgument;

public class StringDataTypePanel extends Composite implements IDataTypePanel {
	private Text myExternalWidthText;

	private DataBindingContext myBindingContext;

	public StringDataTypePanel(Composite _parent, int _style, TabbedPropertySheetWidgetFactory _widgetFactory) {
		super(_parent, _style);
		setBackground(_parent.getBackground());
		GridLayout layout = new GridLayout(2, false);
		layout.verticalSpacing = 10;
		layout.marginLeft = 7;
		layout.marginRight = 7;
		layout.marginBottom = 13;
		setLayout(layout);
		_widgetFactory.createCLabel(this, Messages.getString("AbstractEOArgument." + AbstractEOArgument.WIDTH), SWT.NONE);
		myExternalWidthText = new Text(this, SWT.BORDER);
		GridData externalWidthFieldLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		myExternalWidthText.setLayoutData(externalWidthFieldLayoutData);
	}

	public void setArgument(AbstractEOArgument _argument) {
		if (myBindingContext != null) {
			myBindingContext.dispose();
		}
		if (_argument != null) {
			myBindingContext = new DataBindingContext();
			myBindingContext.bindValue(
					//SWTObservables.observeText(myExternalWidthText, SWT.Modify),
					WidgetProperties.text(SWT.Modify).observe(myExternalWidthText), 
					//BeansObservables.observeValue(_argument, AbstractEOArgument.WIDTH),
					BeanProperties.value(AbstractEOArgument.WIDTH).observe(_argument), 
					null, null);
		}
	}

	public void dispose() {
		setArgument(null);
		super.dispose();
	}
}
