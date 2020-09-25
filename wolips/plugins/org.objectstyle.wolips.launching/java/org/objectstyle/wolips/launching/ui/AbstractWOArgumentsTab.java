/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2002 - 2005 The ObjectStyle Group
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

package org.objectstyle.wolips.launching.ui;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * @author ulrich To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class AbstractWOArgumentsTab implements ILaunchConfigurationTab {

	/**
	 * The control for this page, or <code>null</code>
	 */
	private Control fControl;

	/**
	 * The launch configuration dialog this tab is contained in.
	 */
	private ILaunchConfigurationDialog fLaunchConfigurationDialog;

	/**
	 * Current error message, or <code>null</code>
	 */
	private String fErrorMessage;

	/**
	 * Current message, or <code>null</code>
	 */
	private String fMessage;

	/**
	 * Whether this tab needs to apply changes. This attribute is initialized to
	 * <code>true</code> to be backwards compatible. If clients want to take
	 * adavantage of such a feature, they should set the flag to false, and
	 * check it before applying changes to the lanuch configuration working
	 * copy.
	 * 
	 * @since 2.1
	 */
	private boolean fDirty = true;

	/**
	 * Returns the dialog this tab is contained in, or <code>null</code> if
	 * not yet set.
	 * 
	 * @return launch configuration dialog, or <code>null</code>
	 */
	protected ILaunchConfigurationDialog getLaunchConfigurationDialog() {
		return this.fLaunchConfigurationDialog;
	}

	/**
	 * Updates the buttons and message in this page's launch configuration
	 * dialog.
	 */
	protected void updateLaunchConfigurationDialog() {
		if (getLaunchConfigurationDialog() != null) {
			getLaunchConfigurationDialog().updateMessage();
			getLaunchConfigurationDialog().updateButtons();
		}
	}

	/**
	 * @see ILaunchConfigurationTab#getControl()
	 */
	public Control getControl() {
		return this.fControl;
	}

	/**
	 * Sets the control to be displayed in this tab.
	 * 
	 * @param control
	 *            the control for this tab
	 */
	protected void setControl(Control control) {
		this.fControl = control;
	}

	/**
	 * @see ILaunchConfigurationTab#getErrorMessage()
	 */
	public String getErrorMessage() {
		return this.fErrorMessage;
	}

	/**
	 * @see ILaunchConfigurationTab#getMessage()
	 */
	public String getMessage() {
		return this.fMessage;
	}

	/**
	 * @see ILaunchConfigurationTab#setLaunchConfigurationDialog(ILaunchConfigurationDialog)
	 */
	public void setLaunchConfigurationDialog(ILaunchConfigurationDialog dialog) {
		this.fLaunchConfigurationDialog = dialog;
	}

	/**
	 * Sets this page's error message, possibly <code>null</code>.
	 * 
	 * @param errorMessage
	 *            the error message or <code>null</code>
	 */
	protected void setErrorMessage(String errorMessage) {
		this.fErrorMessage = errorMessage;
	}

	/**
	 * Sets this page's message, possibly <code>null</code>.
	 * 
	 * @param message
	 *            the message or <code>null</code>
	 */
	protected void setMessage(String message) {
		this.fMessage = message;
	}

	/**
	 * Convenience method to return the launch manager.
	 * 
	 * @return the launch manager
	 */
	protected ILaunchManager getLaunchManager() {
		return DebugPlugin.getDefault().getLaunchManager();
	}

	/**
	 * By default, do nothing.
	 * 
	 * @see ILaunchConfigurationTab#dispose()
	 */
	public void dispose() {
		return;
	}

	/**
	 * Returns the shell this tab is contained in, or <code>null</code>.
	 * 
	 * @return the shell this tab is contained in, or <code>null</code>
	 */
	protected Shell getShell() {
		Control control = getControl();
		if (control != null) {
			return control.getShell();
		}
		return null;
	}

	/**
	 * @see ILaunchConfigurationTab#canSave()
	 */
	public boolean canSave() {
		return true;
	}

	/**
	 * @see ILaunchConfigurationTab#isValid(ILaunchConfiguration)
	 */
	public boolean isValid(ILaunchConfiguration launchConfig) {
		return true;
	}

	/**
	 * Create some empty space.
	 * 
	 * @param comp
	 * @param colSpan
	 */
	protected void createVerticalSpacer(Composite comp, int colSpan) {
		Label label = new Label(comp, SWT.NONE);
		GridData gd = new GridData();
		gd.horizontalSpan = colSpan;
		label.setLayoutData(gd);
	}

	/**
	 * @see ILaunchConfigurationTab#getImage()
	 */
	public Image getImage() {
		return null;
	}

	/**
	 * Convenience method to set a boolean attribute of on a launch
	 * configuration. If the value being set is the default, the attribute's
	 * value is set to <code>null</code>.
	 * 
	 * @param attribute
	 *            attribute identifier
	 * @param configuration
	 *            the configuration on which to set the attribute
	 * @param value
	 *            the value of the attribute
	 * @param defaultValue
	 *            the default value of the attribute
	 * @since 2.1
	 */
	protected void setAttribute(String attribute, ILaunchConfigurationWorkingCopy configuration, boolean value, boolean defaultValue) {
		if (value == defaultValue) {
			configuration.setAttribute(attribute, (String) null);
		} else {
			configuration.setAttribute(attribute, value);
		}
	}

	/**
	 * Returns whether this tab is dirty. It is up to clients to set/reset and
	 * consult this attribute as required. By default, a tab is initialized to
	 * dirty.
	 * 
	 * @return whether this tab is dirty
	 * @since 2.1
	 */
	protected boolean isDirty() {
		return this.fDirty;
	}

	/**
	 * Returns whether this tab is dirty. It is up to clients to set/reset and
	 * consult this attribute as required. By default, a tab is initialized to
	 * dirty.
	 * 
	 * @param dirty
	 *            whether this tab is dirty
	 * @since 2.1
	 */
	protected void setDirty(boolean dirty) {
		this.fDirty = dirty;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#activated(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	public void activated(ILaunchConfigurationWorkingCopy workingCopy) {
		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#deactivated(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	public void deactivated(ILaunchConfigurationWorkingCopy workingCopy) {
		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#launched(org.eclipse.debug.core.ILaunch)
	 */
	public void launched(ILaunch launch) {
		return;
	}

	/**
	 * Returns a width hint for a button control.
	 */
	private static int getButtonWidthHint(Button button) {
		GC gc = new GC(button);
		gc.setFont(button.getFont());
		FontMetrics fFontMetrics = gc.getFontMetrics();
		gc.dispose();
		int widthHint = Dialog.convertHorizontalDLUsToPixels(fFontMetrics, IDialogConstants.BUTTON_WIDTH);
		return Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
	}

	/**
	 * Sets width and height hint for the button control. <b>Note:</b> This is
	 * a NOP if the button's layout data is not an instance of
	 * <code>GridData</code>.
	 * 
	 * @param the
	 *            button for which to set the dimension hint
	 */
	private static void setButtonDimensionHint(Button button) {
		Assert.isNotNull(button);
		Object gd = button.getLayoutData();
		if (gd instanceof GridData) {
			((GridData) gd).widthHint = getButtonWidthHint(button);
			((GridData) gd).horizontalAlignment = GridData.FILL;
		}
	}

	/**
	 * Creates and returns a new push button with the given label and/or image.
	 * 
	 * @param parent
	 *            parent control
	 * @param label
	 *            button label or <code>null</code>
	 * @param image
	 *            image of <code>null</code>
	 * @return a new push button
	 */
	public static Button createPushButton(Composite parent, String label, Image image) {
		Button button = new Button(parent, SWT.PUSH);
		button.setFont(parent.getFont());
		if (image != null) {
			button.setImage(image);
		}
		if (label != null) {
			button.setText(label);
		}
		GridData gd = new GridData();
		button.setLayoutData(gd);
		AbstractWOArgumentsTab.setButtonDimensionHint(button);
		return button;
	}

}
