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

package org.objectstyle.wolips.project;

import java.net.URL;

import org.eclipse.core.internal.boot.URLContentFilter;
import org.eclipse.core.internal.plugins.PluginClassLoader;
import org.objectstyle.wolips.logging.WOLipsLog;
import org.objectstyle.wolips.plugin.WOLipsPlugin;
import org.objectstyle.woproject.env.Environment;
import org.objectstyle.woproject.env.WOVariables;
import org.objectstyle.woproject.util.FileStringScanner;

import com.webobjects.foundation.NSNotificationCenter;

/**
 * @author uli
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class PBProjectNotifications {

	private static String PBProjectWillUpgradeNotification =
		"Project Will Upgrade";
	private static String PBProjectDidUpgradeNotification = "Project Upgraded";
	private static String PBProjectDidChangeNotification = "Project Changed";
	private static String PBProjectWillSaveNotification = "Project Will Save";
	private static String PBProjectDidSaveNotification = "Project Saved";
	private static String PBProjectSaveDidFailNotification =
		"Project Save Failed";
	private static String PBFileAddedToProjectNotification =
		"File Added to Project";
	private static String PBFileRemovedFromProjectNotification =
		"File Removed from Project";
	private static NSNotificationCenter notificationCenter = null;
	private static boolean loadedFoundationClasses = false;

	//postNotification(notification, dict);

	/**
	 * Constructor for PBProjectNotifications.
	 */
	private PBProjectNotifications() {
		super();
	}
	/**
	 * Method notificationCenter.
	 * @return NSNotificationCenter
	 */
	private static NSNotificationCenter notificationCenter() {
		if (PBProjectNotifications.notificationCenter == null)
			PBProjectNotifications.notificationCenter =
				NSNotificationCenter.defaultCenter();
		return PBProjectNotifications.notificationCenter;
	}
	/**
	 * Method postPBProjectDidUpgradeNotification.
	 * @param aProjectName
	 */
	public static void postPBProjectDidUpgradeNotification(String aProjectName) {
		try {
			PBProjectNotifications.loadFoundationClasses();
			PBProjectNotifications.notificationCenter().postNotification(
				PBProjectNotifications.PBFileAddedToProjectNotification,
				aProjectName);
		} catch (Exception anException) {
			WOLipsLog.log(anException);
		}
	}
	/**
	 * Method foundationJarPath.
	 * @return String
	 */
	private static String foundationJarPath() {
		String foundationJarPath = null;
		try {
			if (Environment.isNextRootSet())
				foundationJarPath =
					"file:///"
						+ FileStringScanner.replace(
							WOVariables.nextRoot(),
							"/",
							"\\")
						+ "\\Library\\Frameworks\\JavaFoundation.framework\\Resources\\Java\\javafoundation.jar";
			else
				foundationJarPath =
					"file:///System/Library/Frameworks/JavaFoundation.framework/Resources/Java/javafoundation.jar";
		} catch (Exception anException) {
			WOLipsLog.log(anException);
		}
		return foundationJarPath;
	}

	/**
	 * Loads the foundation classes.
	 */
	private static void loadFoundationClasses() {
		if (PBProjectNotifications.loadedFoundationClasses)
			return;
		ClassLoader aClassLoader =
			WOLipsPlugin.getDefault().getClass().getClassLoader();
		URLContentFilter[] theURLContentFilter = new URLContentFilter[1];
		theURLContentFilter[0] = new URLContentFilter(true);
		URL[] theUrls = new URL[1];
		try {
			theUrls[0] = new URL(PBProjectNotifications.foundationJarPath());
			((PluginClassLoader) aClassLoader).addURLs(
				theUrls,
				theURLContentFilter,
				null,
				null);
			PBProjectNotifications.loadedFoundationClasses = true;
		} catch (Exception anException) {
			WOLipsLog.log(anException);
		} finally {
			aClassLoader = null;
			theURLContentFilter = null;
			theUrls = null;
		}
	}
}
