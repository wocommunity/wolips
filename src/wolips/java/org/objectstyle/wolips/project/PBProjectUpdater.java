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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.objectstyle.wolips.IWOLipsPluginConstants;
import org.objectstyle.wolips.io.FileStringScanner;
import org.objectstyle.wolips.io.WOLipsLog;
import org.objectstyle.woproject.pb.PBProject;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
/**
 * @author uli
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class PBProjectUpdater {
	//public static String PBProject = "PB.projectContainer"; moved to IWOLipsPluginConstants.PROJECT_FILE_NAME (mn)
	private PBProject pbProject;
	private IContainer projectContainer;
	private static final String dirtyPBProject = "<?xml";
	/**
	 * Constructor for PBProjectUpdater.
	 */
	public PBProjectUpdater(IContainer aProjectContainer) {
		super();
		//check if theres a PB.project in the Container. If not go to the parent
		IContainer findContainer = aProjectContainer;
		while ((findContainer
			.findMember(IWOLipsPluginConstants.PROJECT_FILE_NAME)
			== null)
			&& (findContainer.getParent() != null)) {
			findContainer = findContainer.getParent();
		}
		if (findContainer.getParent() == null)
			projectContainer = projectContainer.getProject();
		if (findContainer.findMember(IWOLipsPluginConstants.PROJECT_FILE_NAME)
			!= null)
			projectContainer = findContainer;
		getPBProject(projectContainer);
		//projectContainer = aProjectContainer;
	}
	public void updatePBProject() throws CoreException {
		syncPBProjectWithProject();
		if (projectContainer != null)
			PBProjectNotifications.postPBProjectDidUpgradeNotification(
				projectContainer.getName());
	}
	/**
	 * On MacOSX the EOModeler converts the PB.project file to xml.
	 */
	private void fixEOModelerMacOSXBug(File aFile) {
		try {
			if ((aFile != null) && (aFile.exists())) {
				String file = FileStringScanner.stringFromFile(aFile);
				if (file.startsWith(PBProjectUpdater.dirtyPBProject))
					aFile.delete();
			}
		} catch (Exception anException) {
			WOLipsLog.log(anException);
		}
	}
	private void getPBProject(IContainer aProject) {
		File aFile =
			aProject
				.getFile(new Path(IWOLipsPluginConstants.PROJECT_FILE_NAME))
				.getLocation()
				.toFile();
		pbProject = null;
		fixEOModelerMacOSXBug(aFile);
		try {
			boolean sync = !aFile.exists();
			pbProject =
				new PBProject(
					aFile,
					!ProjectHelper.isWOAppBuilderInstalled(
						(IProject) aProject.getProject()));
			if (sync)
				syncPBProjectWithProject();
		} catch (Exception anException) {
			WOLipsLog.log(anException);
		}
	}
	private void syncPBProjectWithProject() {
		try {
			pbProject.update();
			this.syncFilestable();
			this.syncProjectName();
			pbProject.saveChanges();
		} catch (Exception ioex) {
			WOLipsLog.log(ioex);
		}
	}
	private void syncFilestable() {
		ArrayList aClassesList = new ArrayList();
		ArrayList aWOComponentsList = new ArrayList();
		ArrayList aWOAppResourcesList = new ArrayList();
		IResource[] resources;
		try {
			resources = projectContainer.members();
		} catch (Exception anException) {
			WOLipsLog.log(anException);
			return;
		}
		int lastResource = resources.length;
		int i = 0;
		while (i < lastResource) {
			IResource aResource = resources[i];
			i++;
			proceedResource(
				aResource,
				aClassesList,
				aWOComponentsList,
				aWOAppResourcesList);
		}
		this.syncClasses(aClassesList);
		this.syncWOComponents(aWOComponentsList);
		this.syncWOAppResources(aWOAppResourcesList);
	}
	private void proceedResource(
		IResource aResource,
		List aClassesList,
		List aWOComponentsList,
		List aWOAppResourcesList) {
		try {
			String aPath = aResource.getProjectRelativePath().toString();
			File aFile = new File(aResource.getLocation().toOSString());
			IFolder aFolder = null;
			if (aFile.isDirectory())
				aFolder =
					projectContainer.getFolder(
						aResource.getProjectRelativePath());
			if (aFolder != null) {
				if (aPath.endsWith(".wo"))
					aWOComponentsList.add(aPath);
				else if (
					!aPath.endsWith(".woa")
						&& !aPath.endsWith(".build")
						&& !aPath.endsWith(".framework")) {
					IResource[] resources;
					resources = aFolder.members();
					int lastResource = resources.length;
					int i = 0;
					while (i < lastResource) {
						IResource aFolderResource = resources[i];
						i++;
						this.proceedResource(
							aFolderResource,
							aClassesList,
							aWOComponentsList,
							aWOAppResourcesList);
					}
				}
			} else {
				if (aPath.endsWith(".java"))
					aClassesList.add(aPath);
				if (aPath.endsWith(".api"))
					aWOAppResourcesList.add(aPath);
			}
		} catch (Exception anException) {
			WOLipsLog.log(anException);
		}
	}
	private void syncProjectName() {
		pbProject.setProjectName(projectContainer.getName());
	}
	private void syncClasses(List list) {
		pbProject.setClasses(list);
	}
	private void syncWOComponents(List list) {
		pbProject.setWoComponents(list);
	}
	private void syncWOAppResources(List list) {
		pbProject.setWoAppResources(list);
	}
	//////////// removing adding filestable resources
	public void syncFilestable(
		NSDictionary changedResources,
		int kindOfChange) {
		List actualResources;
		String currentKey;
		for (int i = 0; i < changedResources.allKeys().count(); i++) {
			currentKey = (String) changedResources.allKeys().objectAtIndex(i);
			if (IWOLipsPluginConstants.RESOURCES_ID.equals(currentKey)) {
				actualResources = pbProject.getWoAppResources();
				switch (kindOfChange) {
					case IResourceDelta.ADDED :
						pbProject.setWoAppResources(
							addResources(
								(NSArray) changedResources.objectForKey(
									currentKey),
								actualResources));
						break;
					case IResourceDelta.REMOVED :
						pbProject.setWoAppResources(
							removeResources(
								(NSArray) changedResources.objectForKey(
									currentKey),
								actualResources));
						break;
				}
			} else if (IWOLipsPluginConstants.CLASSES_ID.equals(currentKey)) {
				actualResources = pbProject.getClasses();
				switch (kindOfChange) {
					case IResourceDelta.ADDED :
						pbProject.setClasses(
							addResources(
								(NSArray) changedResources.objectForKey(
									currentKey),
								actualResources));
						break;
					case IResourceDelta.REMOVED :
						pbProject.setClasses(
							removeResources(
								(NSArray) changedResources.objectForKey(
									currentKey),
								actualResources));
						break;
				}
			} else if (
				IWOLipsPluginConstants.SUBPROJECTS_ID.equals(currentKey)) {
				actualResources = pbProject.getSubprojects();
				switch (kindOfChange) {
					case IResourceDelta.ADDED :
						pbProject.setSubprojects(
							addResources(
								(NSArray) changedResources.objectForKey(
									currentKey),
								actualResources));
						break;
					case IResourceDelta.REMOVED :
						pbProject.setSubprojects(
							removeResources(
								(NSArray) changedResources.objectForKey(
									currentKey),
								actualResources));
						break;
				}
			} else if (
				IWOLipsPluginConstants.COMPONENTS_ID.equals(currentKey)) {
				actualResources = pbProject.getWoComponents();
				switch (kindOfChange) {
					case IResourceDelta.ADDED :
						pbProject.setWoComponents(
							addResources(
								(NSArray) changedResources.objectForKey(
									currentKey),
								actualResources));
						break;
					case IResourceDelta.REMOVED :
						pbProject.setWoComponents(
							removeResources(
								(NSArray) changedResources.objectForKey(
									currentKey),
								actualResources));
						break;
				}
			}
		}
		try {
			pbProject.saveChanges();
		} catch (IOException e) {
			WOLipsLog.log(e);
		}
	}
	private List addResources(NSArray newResources, List actualResources) {
		if (actualResources == null) {
			actualResources = new ArrayList();
		}
		String relativResourcePath;
		IFile projectFile =
			projectContainer.getFile(
				new Path(IWOLipsPluginConstants.PROJECT_FILE_NAME));
		for (int i = 0; i < newResources.count(); i++) {
			relativResourcePath =
				relativResourcePath(
					(IResource) newResources.objectAtIndex(i),
					projectFile);
			if (relativResourcePath != null
				&& !actualResources.contains(relativResourcePath)) {
				actualResources.add(relativResourcePath);
			}
		}
		return actualResources;
	}
	private List removeResources(
		NSArray removedResources,
		List actualResources) {
		if (actualResources == null) {
			return new ArrayList();
		}
		String relativResourcePath;
		IFile projectFile =
			projectContainer.getFile(
				new Path(IWOLipsPluginConstants.PROJECT_FILE_NAME));
		for (int i = 0; i < removedResources.count(); i++) {
			relativResourcePath =
				relativResourcePath(
					(IResource) removedResources.objectAtIndex(i),
					projectFile);
			if (relativResourcePath != null
				&& actualResources.contains(relativResourcePath)) {
				actualResources.remove(relativResourcePath);
			}
		}
		return actualResources;
	}
	private String relativResourcePath(IResource resource, IFile projectFile) {
		// determine relativ path to resource
		String resourcePath;
		if (projectFile.getParent().equals(resource.getParent())) {
			// same folder
			resourcePath = resource.getName();
		} else if (
			projectFile.getParent().getFullPath().matchingFirstSegments(
				resource.getFullPath())
				== projectFile.getParent().getFullPath().segmentCount()) {
			// resource is deeper in directory structure 
			resourcePath =
				resource
					.getFullPath()
					.removeFirstSegments(
						projectFile
							.getParent()
							.getFullPath()
							.matchingFirstSegments(
							resource.getFullPath()))
					.toString();
		} else {
			// resource is higher or paralell in directory structure
			resourcePath = resource.getProjectRelativePath().toString();
			for (int i = 0;
				i < projectFile.getProjectRelativePath().segmentCount() - 1;
				i++) {
				resourcePath = "../" + resourcePath;
			}
		}
		return resourcePath;
	}
	////////// framework adding/removing
	public void addFrameworks(NSArray newFrameworks) {
		List actualFrameworks = pbProject.getFrameworks();
		String frameworkName = null;
		for (int j = 0; j < newFrameworks.count(); j++) {
			frameworkName =
				frameworkIdentifierFromPath(
					(Path) newFrameworks.objectAtIndex(j));
			if (frameworkName != null
				&& !actualFrameworks.contains(frameworkName)) {
				actualFrameworks.add(frameworkName);
			}
		}
		try {
			pbProject.saveChanges();
		} catch (IOException e) {
			WOLipsLog.log(e);
		}
	}
	public void removeFrameworks(NSArray removedFrameworks) {
		List actualFrameworks = pbProject.getFrameworks();
		String frameworkName = null;
		for (int j = 0; j < removedFrameworks.count(); j++) {
			frameworkName =
				frameworkIdentifierFromPath(
					(Path) removedFrameworks.objectAtIndex(j));
			if (frameworkName != null
				&& actualFrameworks.contains(frameworkName)) {
				actualFrameworks.remove(frameworkName);
			}
		}
		try {
			pbProject.saveChanges();
		} catch (IOException e) {
			WOLipsLog.log(e);
		}
	}
	private String frameworkIdentifierFromPath(Path frameworkPath) {
		String frameworkName = null;
		// search framework segment in path
		for (int i = 0; i < frameworkPath.segmentCount(); i++) {
			frameworkName = frameworkPath.segment(i);
			if (frameworkName
				.endsWith("." + IWOLipsPluginConstants.EXT_FRAMEWORK)) {
				break;
			} else {
				frameworkName = null;
			}
		}
		return frameworkName;
	}
}
