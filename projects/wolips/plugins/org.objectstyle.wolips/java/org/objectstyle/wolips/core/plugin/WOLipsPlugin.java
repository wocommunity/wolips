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
package org.objectstyle.wolips.core.plugin;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.objectstyle.woenvironment.env.WOEnvironment;
import org.objectstyle.wolips.core.logging.WOLipsLog;
import org.objectstyle.wolips.core.preferences.Preferences;
/**
 * The main plugin class to be used in the desktop.
 * 
 * @author uli
 * @author markus
 */
public class WOLipsPlugin extends AbstractUIPlugin implements IStartup {
	//The plugin.
	private static WOLipsPlugin plugin;
	private WOEnvironment woEnvironment;
	private static final String PLUGIN_ID = "org.objectstyle.wolips";
	public static final String WO_TEMPLATE_DIRECTORY = "templates";
	public static final String WO_TEMPLATE_FILES = "/wo_file_templates.xml";
	public static final String WO_TEMPLATE_PROJECT =
		"/wo_project_templates.xml";
	public static final boolean DEBUG = true;
	private static WOLipsLog log =
		new WOLipsLog(WOLipsPlugin.class.getName(), WOLipsLog.ERROR);
	/**
	 * The constructor.
	 * @param descriptor
	 */
	//The constructur is very sensitive. Make sure that your stuff works.
	//If this cunstructor fails, the whole plugin will be disabled.
	public WOLipsPlugin(IPluginDescriptor descriptor) {
		super(descriptor);
		plugin = this;
		try {
			// set up missing preferences
			Preferences.setDefaults();
		} catch (Exception exception) {
			System.out.println(
				"Exception in WOLips constructor: " + exception.getMessage());
		}
	}
	/**
	 * Calls EarlyStartup.earlyStartup().
	 * <br>
	 * @see org.eclipse.ui.IStartup#earlyStartup()
	 */
	public void earlyStartup() {
		EarlyStartup earlyStartup = new EarlyStartup();
		earlyStartup.earlyStartup();
	}
	/**
	 * Returns the shared instance.
	 * @return
	 */
	public static WOLipsPlugin getDefault() {
		if (plugin == null) {
			// ensure plugin instance is always available using id
			return new WOLipsPlugin(
				Platform.getPlugin(WOLipsPlugin.PLUGIN_ID).getDescriptor());
		}
		return plugin;
	}
	/**
	 * Method baseURL.
	 * @return URL
	 */
	public static URL baseURL() {
		return WOLipsPlugin.getDefault().getDescriptor().getInstallURL();
	}
	/**
	 * Method handleException.
	 * @param shell
	 * @param target
	 * @param message
	 */
	public static void handleException(
		Shell shell,
		Throwable target,
		String message) {
		WOLipsLog.debug(target);
		String title = "Error";
		if (message == null) {
			message = target.getMessage();
		}
		if (target instanceof CoreException) {
			IStatus status = ((CoreException) target).getStatus();
			ErrorDialog.openError(shell, title, message, status);
			//WOLipsLog.log(status);
		} else {
			MessageDialog.openError(shell, title, target.getMessage());
			//WOLipsLog.log(target);
		}
		log.error(message, target);
	}
	/**
	 * Method informUser.
	 * @param shell
	 * @param message
	 */
	public static void informUser(Shell shell, String message) {
		String title = "Error";
		MessageDialog.openError(shell, title, message);
	}
	/**
	 * Returns the PluginID.
	 * @return
	 */
	public static String getPluginId() {
		if (plugin != null) {
			return getDefault().getDescriptor().getUniqueIdentifier();
		} else
			return WOLipsPlugin.PLUGIN_ID;
	}
	/**
	 * @return WOEnvironment
	 */
	public WOEnvironment getWOEnvironment() {
		if (woEnvironment == null)
			woEnvironment = new WOEnvironment();
		return woEnvironment;
	}
}
