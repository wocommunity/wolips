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
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.objectstyle.wolips.logging.WOLipsLog;
import org.objectstyle.wolips.plugin.IWOLipsPluginConstants;
/**
 * @author uli
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class PBProjectUpdater extends _PBProjectUpdater {
	private static Hashtable projectUpdater = new Hashtable();
	/**
	 * Method PBProjectUpdater.
	 * @param aProjectContainer
	 */
	private PBProjectUpdater(IContainer aProjectContainer) {
		super(aProjectContainer);
	}
	/**
	 * Method forgetCache.
	 */
	public static void forgetCache() {
		projectUpdater = new Hashtable();
	}
	/**
	 * Method instance.
	 * @param aProjectContainer
	 * @return PBProjectUpdater
	 */
	public static PBProjectUpdater instance(IContainer aProjectContainer) {
		PBProjectUpdater returnValue =
			(PBProjectUpdater) PBProjectUpdater.projectUpdater.get(
				aProjectContainer);
		if (returnValue == null) {
			returnValue = new PBProjectUpdater(aProjectContainer);
			PBProjectUpdater.projectUpdater.put(aProjectContainer, returnValue);
		}
		return returnValue;
	}
	/**
	 * Method updatePBProject.
	 * @throws CoreException
	 */
	public void updatePBProject() throws CoreException {
		this.syncPBProjectWithProject();
		if (this.getProjectContainer() != null)
			PBProjectNotifications.postPBProjectDidUpgradeNotification(
				this.getProjectContainer().getName());
	}
	/**
	 * Method forceRebuild.
	 */
	public void forceRebuild() throws CoreException  {
		this.getPbProject().forgetAllFiles();
		this.updatePBProject();
	}
	/**
	 * Method syncFilestable.
	 * @param changedResources
	 * @param kindOfChange
	 */
	public void syncFilestable(Map changedResources, int kindOfChange) {
		List actualResources;
		String currentKey;
		Object[] allKeys = changedResources.keySet().toArray();
		for (int i = 0; i < allKeys.length; i++) {
			currentKey = (String) allKeys[i];
			if (IWOLipsPluginConstants.RESOURCES_ID.equals(currentKey)) {
				actualResources = this.getPbProject().getWoAppResources();
				switch (kindOfChange) {
					case IResourceDelta.ADDED :
					this.getPbProject().setWoAppResources(
							addResources(
								(List) changedResources.get(currentKey),
								actualResources));
						break;
					case IResourceDelta.REMOVED :
					this.getPbProject().setWoAppResources(
							removeResources(
								(List) changedResources.get(currentKey),
								actualResources));
						break;
				}
			} else if (IWOLipsPluginConstants.CLASSES_ID.equals(currentKey)) {
				actualResources = this.getPbProject().getClasses();
				switch (kindOfChange) {
					case IResourceDelta.ADDED :
					this.getPbProject().setClasses(
							addResources(
								(List) changedResources.get(currentKey),
								actualResources));
						break;
					case IResourceDelta.REMOVED :
					this.getPbProject().setClasses(
							removeResources(
								(List) changedResources.get(currentKey),
								actualResources));
						break;
				}
			} else if (
				IWOLipsPluginConstants.SUBPROJECTS_ID.equals(currentKey)) {
				actualResources = this.getPbProject().getSubprojects();
				switch (kindOfChange) {
					case IResourceDelta.ADDED :
					this.getPbProject().setSubprojects(
							addResources(
								(List) changedResources.get(currentKey),
								actualResources));
						break;
					case IResourceDelta.REMOVED :
					this.getPbProject().setSubprojects(
							removeResources(
								(List) changedResources.get(currentKey),
								actualResources));
						break;
				}
			} else if (
				IWOLipsPluginConstants.COMPONENTS_ID.equals(currentKey)) {
				actualResources = this.getPbProject().getWoComponents();
				switch (kindOfChange) {
					case IResourceDelta.ADDED :
					this.getPbProject().setWoComponents(
							addResources(
								(List) changedResources.get(currentKey),
								actualResources));
						break;
					case IResourceDelta.REMOVED :
					this.getPbProject().setWoComponents(
							removeResources(
								(List) changedResources.get(currentKey),
								actualResources));
						break;
				}
			}
		}
		try {
			this.getPbProject().saveChanges();
		} catch (IOException e) {
			WOLipsLog.log(e);
		}
	}
	/**
	 * Method addFrameworks.
	 * @param newFrameworks
	 */
	public void addFrameworks(List newFrameworks) {
		List actualFrameworks = this.getPbProject().getFrameworks();
		String frameworkName = null;
		for (int j = 0; j < newFrameworks.size(); j++) {
			frameworkName =
				_PBProjectUpdater.frameworkIdentifierFromPath(
					(Path) newFrameworks.get(j));
			if (frameworkName != null
				&& !actualFrameworks.contains(frameworkName)) {
				actualFrameworks.add(frameworkName);
			}
		}
		try {
			this.getPbProject().saveChanges();
		} catch (IOException e) {
			WOLipsLog.log(e);
		}
	}
	/**
	 * Method removeFrameworks.
	 * @param removedFrameworks
	 */
	public void removeFrameworks(List removedFrameworks) {
		List actualFrameworks = this.getPbProject().getFrameworks();
		String frameworkName = null;
		for (int j = 0; j < removedFrameworks.size(); j++) {
			frameworkName =
				_PBProjectUpdater.frameworkIdentifierFromPath(
					(Path) removedFrameworks.get(j));
			if (frameworkName != null
				&& actualFrameworks.contains(frameworkName)) {
				actualFrameworks.remove(frameworkName);
			}
		}
		try {
			this.getPbProject().saveChanges();
		} catch (IOException e) {
			WOLipsLog.log(e);
		}
	}
}
