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
package org.objectstyle.wolips.eomodeler.editors.databaseConfig;

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
import org.objectstyle.wolips.eomodeler.core.model.EODatabaseConfig;
import org.objectstyle.wolips.eomodeler.utils.UglyFocusHackWorkaroundListener;

public class JDBCConnectionDictionarySection extends Composite implements IConnectionDictionarySection {
	private EODatabaseConfig _databaseConfig;

	private Text _usernameText;

	private Text _passwordText;

	private Text _urlText;

	private Text _driverText;

	private Text _pluginText;

	private DataBindingContext _bindingContext;

	public JDBCConnectionDictionarySection(Composite parent, int style, TabbedPropertySheetWidgetFactory widgetFactory) {
		super(parent, style);
		GridLayout layout = new GridLayout(2, false);
		layout.verticalSpacing = 10;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginBottom = 0;
		layout.marginWidth = 0;
		setLayout(layout);
		setBackground(parent.getBackground());
		widgetFactory.createCLabel(this, Messages.getString("EOModel." + EODatabaseConfig.URL), SWT.NONE);
		_urlText = new Text(this, SWT.BORDER);
		_urlText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		UglyFocusHackWorkaroundListener.addListener(_urlText);

		widgetFactory.createCLabel(this, Messages.getString("EOModel." + EODatabaseConfig.USERNAME), SWT.NONE);
		_usernameText = new Text(this, SWT.BORDER);
		_usernameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		UglyFocusHackWorkaroundListener.addListener(_usernameText);

		widgetFactory.createCLabel(this, Messages.getString("EOModel." + EODatabaseConfig.PASSWORD), SWT.NONE);
		_passwordText = new Text(this, SWT.BORDER | SWT.PASSWORD);
		_passwordText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		UglyFocusHackWorkaroundListener.addListener(_passwordText);

		widgetFactory.createCLabel(this, Messages.getString("EOModel." + EODatabaseConfig.DRIVER), SWT.NONE);
		_driverText = new Text(this, SWT.BORDER);
		_driverText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		UglyFocusHackWorkaroundListener.addListener(_driverText);

		widgetFactory.createCLabel(this, Messages.getString("EOModel." + EODatabaseConfig.PLUGIN), SWT.NONE);
		_pluginText = new Text(this, SWT.BORDER);
		_pluginText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		UglyFocusHackWorkaroundListener.addListener(_pluginText);
	}

	public void setInput(EODatabaseConfig databaseContext) {
		disposeBindings();

		_databaseConfig = databaseContext;

		if (_databaseConfig != null) {
			_bindingContext = new DataBindingContext();
			_bindingContext.bindValue(
					WidgetProperties.text(SWT.Modify).observe(_usernameText),
					BeanProperties.value(EODatabaseConfig.class, EODatabaseConfig.USERNAME, String.class).observe(_databaseConfig),
					null, null);
			_bindingContext.bindValue(
					WidgetProperties.text(SWT.Modify).observe(_passwordText),
					BeanProperties.value(EODatabaseConfig.class, EODatabaseConfig.PASSWORD, String.class).observe(_databaseConfig),
					null, null);
			_bindingContext.bindValue(
					WidgetProperties.text(SWT.Modify).observe(_urlText),
					BeanProperties.value(EODatabaseConfig.class, EODatabaseConfig.URL, String.class).observe(_databaseConfig),
					null, null);
			_bindingContext.bindValue(
					WidgetProperties.text(SWT.Modify).observe(_driverText),
					BeanProperties.value(EODatabaseConfig.class, EODatabaseConfig.DRIVER, String.class).observe(_databaseConfig),
					null, null);
			_bindingContext.bindValue(
					WidgetProperties.text(SWT.Modify).observe(_pluginText),
					BeanProperties.value(EODatabaseConfig.class, EODatabaseConfig.PLUGIN, String.class).observe(_databaseConfig),
					null, null);
		}
	}

	public void disposeBindings() {
		if (_bindingContext != null) {
			_bindingContext.dispose();
		}
	}

	public void dispose() {
		disposeBindings();
		super.dispose();
	}
}
