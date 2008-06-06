/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2002 - 2006 The ObjectStyle Group
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
/*Portions of this code are Copyright Apple Inc. 2008 and licensed under the
ObjectStyle Group Software License, version 1.0.  This license from Apple
applies solely to the actual code contributed by Apple and to no other code.
No other license or rights are granted by Apple, explicitly, by implication,
by estoppel, or otherwise.  All rights reserved.*/
package org.objectstyle.wolips.wizards;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.WizardNewLinkPage;


/**
 * Used by WOLips project creation wizards to assign default packages for the template
 * Java classes.
 * @see NewWOProjectWizard
 */
/*
 * Subclasses WizardNewLinkPage due to the fact it's the easiest to
 * override UI elements.  All we need is to display a text field for configuring
 * a java package.
 */
public class PackageSpecifierWizardPage extends WizardNewLinkPage {
	static String packageNameText;

	/**
	 *
	 */
	public static final String OPTIONS_STRING = Messages.getString("PackageSpecifierWizardPage.options.text");
	/**
	 *
	 */
	public static final String DEFAULT_PACKAGE_STRING = Messages.getString("PackageSpecifierWizardPage.options.defaultPackage");

	/**
	 * @param pageName
	 * @param type
	 */
	public PackageSpecifierWizardPage(String pageName, int type) {
		super(pageName, type);
	}

	/**
	 * @return full package name as specified in textfield
	 */
	public String getPackageName() {
		return (packageNameText != null) ? packageNameText : "";
	}

	//translate '.' to file spearators
	/**
	 * @return converted package name into path
	 */
	public String getConvertedPath () {
		String convertedPackagePath = "";
		if (getPackageName().trim().length() > 0) {
			 convertedPackagePath = getPackageName().trim().replace(".", File.separator);
		}
		return convertedPackagePath;
	}

	public void createControl(Composite parent) {
		Font font = parent.getFont();
		initializeDialogUnits(parent);

		// top level group
		Composite topLevel = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		topLevel.setLayout(layout);
		topLevel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
		topLevel.setFont(font);

        Group optionGroup = new Group(topLevel, SWT.SHADOW_IN);
        GridLayout clientlayout = new GridLayout();
        optionGroup.setLayout(clientlayout);
        optionGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL
                | GridData.GRAB_HORIZONTAL));
        optionGroup.setText(OPTIONS_STRING);
        optionGroup.setFont(topLevel.getFont());

        Text text = new Text(optionGroup, SWT.SINGLE | SWT.BORDER);
		text.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL
                | GridData.GRAB_HORIZONTAL));
		text.setText(DEFAULT_PACKAGE_STRING);
		packageNameText = DEFAULT_PACKAGE_STRING;
		text.addModifyListener(new TextFieldModifyListener());

		setErrorMessage(null);
		setMessage(null);
		setControl(topLevel);
	}

	/**
	 *
	 */
	public void updatePageComplete() {
		setPageComplete(false);

		//TODO: we could validate against more errors
		if (packageNameText.endsWith(".") || packageNameText.contains("/")) {
			setMessage(null);
			setErrorMessage("Invalid package name: "+packageNameText);
			return;
		}

		setPageComplete(true);
		setMessage(null);
		setErrorMessage(null);
	}

	class TextFieldModifyListener implements ModifyListener {

		public void modifyText(ModifyEvent event) {

			if (event.getSource() instanceof Text) {
				packageNameText = ((Text)event.getSource()).getText();
				updatePageComplete();
			}
		}

	}


}
