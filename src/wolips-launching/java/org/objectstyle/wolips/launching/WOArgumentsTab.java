/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2002 The ObjectStyle Group
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

package org.objectstyle.wolips.launching;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
import org.eclipse.jdt.internal.debug.ui.JavaDebugImages;
import org.eclipse.jdt.internal.debug.ui.launcher.JavaLaunchConfigurationTab;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.objectstyle.wolips.core.plugin.WOLipsPlugin;
import org.objectstyle.wolips.logging.WOLipsLog;

/**
 * @author uli
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public abstract class WOArgumentsTab extends JavaLaunchConfigurationTab {

	protected String fPrgmArgumentsText;

	protected static final String EMPTY_STRING = ""; //$NON-NLS-1$

	/**
	 * @see ILaunchConfigurationTab#createControl(Composite)
	 */
	public abstract void createControl(Composite parent);
	/**
	 * @see ILaunchConfigurationTab#dispose()
	 */
	public void dispose() {
	}

	/**
	 * @see ILaunchConfigurationTab#isValid(ILaunchConfiguration)
	 */
	public boolean isValid(ILaunchConfiguration config) {
		return true;
	}

	/**
	 * Defaults are empty.
	 * 
	 * @see ILaunchConfigurationTab#setDefaults(ILaunchConfigurationWorkingCopy)
	 */
	public void setDefaults(ILaunchConfigurationWorkingCopy config) {
		config.setAttribute(
			WOJavaLocalApplicationLaunchConfigurationDelegate
				.ATTR_WOLIPS_LAUNCH_WOARGUMENTS,
			this.getDefaultArguments(config));
	}

	/**
	 * @see ILaunchConfigurationTab#initializeFrom(ILaunchConfiguration)
	 */
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			fPrgmArgumentsText = configuration.getAttribute(WOJavaLocalApplicationLaunchConfigurationDelegate.ATTR_WOLIPS_LAUNCH_WOARGUMENTS, ""); //$NON-NLS-1$
		} catch (CoreException e) {
			setErrorMessage(LaunchingMessages.getString("WOArgumentsTab.Exception_occurred_reading_configuration___15") + e.getStatus().getMessage()); //$NON-NLS-1$
			JDIDebugUIPlugin.log(e);
		}
	}

	/**
	 * @see ILaunchConfigurationTab#performApply(ILaunchConfigurationWorkingCopy)
	 */
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(
			WOJavaLocalApplicationLaunchConfigurationDelegate
				.ATTR_WOLIPS_LAUNCH_WOARGUMENTS,
			fPrgmArgumentsText);
	}

	/**
	 * Retuns the string in the text widget, or <code>null</code> if empty.
	 * 
	 * @return text or <code>null</code>
	 */
	protected String getAttributeValueFrom(Text text) {
		String content = text.getText().trim();
		if (content.length() > 0) {
			return content;
		}
		return null;
	}

	/**
	 * @see ILaunchConfigurationTab#getName()
	 */
	public String getName() {
		return "";
	}

	/**
	 * @see ILaunchConfigurationTab#setLaunchConfigurationDialog(ILaunchConfigurationDialog)
	 */
	public void setLaunchConfigurationDialog(ILaunchConfigurationDialog dialog) {
		super.setLaunchConfigurationDialog(dialog);
	}
	/**
	 * @see ILaunchConfigurationTab#getErrorMessage()
	 */
	public String getErrorMessage() {
		return super.getErrorMessage();
	}

	/**
	 * @see ILaunchConfigurationTab#getMessage()
	 */
	public String getMessage() {
		return super.getMessage();
	}

	/**
	 * @see ILaunchConfigurationTab#getImage()
	 */
	public Image getImage() {
		return JavaDebugImages.get(JavaDebugImages.IMG_VIEW_ARGUMENTS_TAB);
	}

	/**
	 * Method getDefaultArguments.
	 * @return String
	 */
	private String getDefaultArguments(ILaunchConfigurationWorkingCopy config) {
		try {
			String path =
				config.getAttribute(
					IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME,
					(String) null);
			IPath aPath = null;
			if (path != null) {
				aPath = new Path(path);
			}
			IResource res =
				ResourcesPlugin.getWorkspace().getRoot().findMember(path);
			if (res instanceof IContainer && res.exists()) {
				IResource aRes =
					((IContainer) res).findMember(aPath.toString() + ".woa");
				if (aRes != null) {
					if (aRes instanceof IContainer && aRes.exists()) {
						config.setAttribute(
							IJavaLaunchConfigurationConstants
								.ATTR_WORKING_DIRECTORY,
							((IContainer) aRes)
								.getFullPath()
								.toString()
								.substring(
								1));
					}
				}
			}

		} catch (Exception anException) {
			WOLipsLog.log(anException);
		}
		return this.getWOApplicationPlatformSpecificArguments()
			+ this.getWOApplicationClassNameArgument(config)
			+ this.getCommonWOApplicationArguments();
	}

	/**
	 * Method getWOApplicationPlatformSpecificArguments.
	 * @return String
	 */
	private String getWOApplicationPlatformSpecificArguments() {
		if (!WOLipsPlugin
			.getDefault()
			.getWOEnvironment()
			.getWOVariables()
			.systemRoot()
			.startsWith("/System"))
			return "";
		return "-WORoot = "
			+ WOLipsPlugin
				.getDefault()
				.getWOEnvironment()
				.getWOVariables()
				.systemRoot()
			+ " ";
	}

	/**
	 * Method getWOApplicationClassNameArgument.
	 * @return String
	 */
	private String getWOApplicationClassNameArgument(ILaunchConfigurationWorkingCopy config) {
		String main = null;
		try {
			main =
				config.getAttribute(
					IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME,
					"");
		} catch (Exception anException) {
			WOLipsLog.log(anException);
			return "";
		}
		if ("".equals(main))
			return "";
		return "WOApplicationClass=" + main + " ";
	}

	/**
	 * Method getCommonWOApplicationArguments.
	 * @return String
	 */
	private String getCommonWOApplicationArguments() {
		return LaunchingMessages.getString("WOArguments.common");
	}

}
