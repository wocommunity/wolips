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
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.core.model.AbstractEOArgument;
import org.objectstyle.wolips.eomodeler.core.model.EOFactoryMethodArgumentType;
import org.objectstyle.wolips.eomodeler.editors.attribute.EOFactoryMethodArgumentTypeContentProvider;
import org.objectstyle.wolips.eomodeler.editors.attribute.EOFactoryMethodArgumentTypeLabelProvider;
import org.objectstyle.wolips.eomodeler.utils.ComboViewerBinding;
import org.objectstyle.wolips.eomodeler.utils.UglyFocusHackWorkaroundListener;

public class CustomDataTypePanel extends Composite implements IDataTypePanel {
	private Text myExternalWidthText;

	private Text myValueClassNameText;

	private Text myValueTypeText;

	private Text myFactoryClassText;

	private Text myFactoryMethodText;

	private Text myConversionClassText;

	private Text myConversionMethodText;

	private ComboViewer myArgumentTypeComboViewer;

	private ComboViewerBinding myArgumentTypeBinding;

	private DataBindingContext myBindingContext;

	public CustomDataTypePanel(Composite _parent, int _style, TabbedPropertySheetWidgetFactory _widgetFactory) {
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
		UglyFocusHackWorkaroundListener.addListener(myExternalWidthText);

		_widgetFactory.createCLabel(this, Messages.getString("AbstractEOArgument." + AbstractEOArgument.VALUE_CLASS_NAME), SWT.NONE);
		myValueClassNameText = new Text(this, SWT.BORDER);
		GridData valueClassNameFieldLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		myValueClassNameText.setLayoutData(valueClassNameFieldLayoutData);
		UglyFocusHackWorkaroundListener.addListener(myValueClassNameText);

		_widgetFactory.createCLabel(this, Messages.getString("AbstractEOArgument." + AbstractEOArgument.VALUE_TYPE), SWT.NONE);
		myValueTypeText = new Text(this, SWT.BORDER);
		GridData valueTypeFieldLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		myValueTypeText.setLayoutData(valueTypeFieldLayoutData);
		UglyFocusHackWorkaroundListener.addListener(myValueTypeText);

		_widgetFactory.createCLabel(this, Messages.getString("AbstractEOArgument." + AbstractEOArgument.VALUE_FACTORY_CLASS_NAME), SWT.NONE);
		myFactoryClassText = new Text(this, SWT.BORDER);
		GridData factoryClassFieldLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		myFactoryClassText.setLayoutData(factoryClassFieldLayoutData);
		UglyFocusHackWorkaroundListener.addListener(myFactoryClassText);

		_widgetFactory.createCLabel(this, Messages.getString("AbstractEOArgument." + AbstractEOArgument.VALUE_FACTORY_METHOD_NAME), SWT.NONE);
		myFactoryMethodText = new Text(this, SWT.BORDER);
		GridData factoryMethodFieldLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		myFactoryMethodText.setLayoutData(factoryMethodFieldLayoutData);
		UglyFocusHackWorkaroundListener.addListener(myFactoryMethodText);

		_widgetFactory.createCLabel(this, Messages.getString("AbstractEOArgument." + AbstractEOArgument.ADAPTOR_VALUE_CONVERSION_CLASS_NAME), SWT.NONE);
		myConversionClassText = new Text(this, SWT.BORDER);
		GridData conversionClassFieldLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		myConversionClassText.setLayoutData(conversionClassFieldLayoutData);
		UglyFocusHackWorkaroundListener.addListener(myConversionClassText);

		_widgetFactory.createCLabel(this, Messages.getString("AbstractEOArgument." + AbstractEOArgument.ADAPTOR_VALUE_CONVERSION_METHOD_NAME), SWT.NONE);
		myConversionMethodText = new Text(this, SWT.BORDER);
		GridData conversionMethodFieldLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		myConversionMethodText.setLayoutData(conversionMethodFieldLayoutData);
		UglyFocusHackWorkaroundListener.addListener(myConversionMethodText);

		_widgetFactory.createCLabel(this, Messages.getString("AbstractEOArgument." + AbstractEOArgument.FACTORY_METHOD_ARGUMENT_TYPE), SWT.NONE);
		Combo argumentTypeComboViewer = new Combo(this, SWT.BORDER | SWT.FLAT | SWT.READ_ONLY);
		myArgumentTypeComboViewer = new ComboViewer(argumentTypeComboViewer);
		myArgumentTypeComboViewer.setLabelProvider(new EOFactoryMethodArgumentTypeLabelProvider());
		myArgumentTypeComboViewer.setContentProvider(new EOFactoryMethodArgumentTypeContentProvider());
		myArgumentTypeComboViewer.setInput(EOFactoryMethodArgumentType.ARGUMENT_TYPES);
		GridData argumentTypeFieldLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		argumentTypeComboViewer.setLayoutData(argumentTypeFieldLayoutData);
	}

	public void setArgument(AbstractEOArgument _argument) {
		if (myBindingContext != null) {
			myBindingContext.dispose();
		}
		if (myArgumentTypeBinding != null) {
			myArgumentTypeBinding.dispose();
		}
		if (_argument != null) {
			myBindingContext = new DataBindingContext();
			myBindingContext.bindValue(
					//SWTObservables.observeText(myExternalWidthText, SWT.Modify),
					WidgetProperties.text(SWT.Modify).observe(myExternalWidthText),
					//BeansObservables.observeValue(_argument, AbstractEOArgument.WIDTH),
					BeanProperties.value(AbstractEOArgument.WIDTH).observe(_argument),
					null, null);
			myBindingContext.bindValue(
					//SWTObservables.observeText(myValueClassNameText, SWT.Modify),
					WidgetProperties.text(SWT.Modify).observe(myValueClassNameText),
					//BeansObservables.observeValue(_argument, AbstractEOArgument.VALUE_CLASS_NAME),
					BeanProperties.value(AbstractEOArgument.VALUE_CLASS_NAME).observe(_argument),
					null, null);
			myBindingContext.bindValue(
					//SWTObservables.observeText(myValueTypeText, SWT.Modify),
					WidgetProperties.text(SWT.Modify).observe(myValueTypeText),
					//BeansObservables.observeValue(_argument, AbstractEOArgument.VALUE_TYPE),
					BeanProperties.value(AbstractEOArgument.VALUE_TYPE).observe(_argument),
					null, null);
			myBindingContext.bindValue(
					//SWTObservables.observeText(myFactoryClassText, SWT.Modify),
					WidgetProperties.text(SWT.Modify).observe(myFactoryClassText),
					//BeansObservables.observeValue(_argument, AbstractEOArgument.VALUE_FACTORY_CLASS_NAME),
					BeanProperties.value(AbstractEOArgument.VALUE_FACTORY_CLASS_NAME).observe(_argument),
					null, null);
			myBindingContext.bindValue(
					//SWTObservables.observeText(myFactoryMethodText, SWT.Modify),
					WidgetProperties.text(SWT.Modify).observe(myFactoryMethodText),
					//BeansObservables.observeValue(_argument, AbstractEOArgument.VALUE_FACTORY_METHOD_NAME),
					BeanProperties.value(AbstractEOArgument.VALUE_FACTORY_METHOD_NAME).observe(_argument),
					null, null);
			myBindingContext.bindValue(
					//SWTObservables.observeText(myConversionClassText, SWT.Modify),
					WidgetProperties.text(SWT.Modify).observe(myConversionClassText),
					//BeansObservables.observeValue(_argument, AbstractEOArgument.ADAPTOR_VALUE_CONVERSION_CLASS_NAME),
					BeanProperties.value(AbstractEOArgument.ADAPTOR_VALUE_CONVERSION_CLASS_NAME).observe(_argument),
					null, null);
			myBindingContext.bindValue(
					//SWTObservables.observeText(myConversionMethodText, SWT.Modify),
					WidgetProperties.text(SWT.Modify).observe(myConversionMethodText),
					//BeansObservables.observeValue(_argument, AbstractEOArgument.ADAPTOR_VALUE_CONVERSION_METHOD_NAME),
					BeanProperties.value(AbstractEOArgument.ADAPTOR_VALUE_CONVERSION_METHOD_NAME).observe(_argument),
					null, null);
			myArgumentTypeBinding = new ComboViewerBinding(myArgumentTypeComboViewer, _argument, AbstractEOArgument.FACTORY_METHOD_ARGUMENT_TYPE, null, null, EOFactoryMethodArgumentTypeContentProvider.BLANK_ARGUMENT_TYPE);
		}
	}

	public void dispose() {
		setArgument(null);
		super.dispose();
	}
}
