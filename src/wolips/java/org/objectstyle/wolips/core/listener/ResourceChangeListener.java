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
package org.objectstyle.wolips.core.listener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jdt.core.JavaCore;
import org.objectstyle.wolips.core.plugin.IWOLipsPluginConstants;
import org.objectstyle.wolips.core.project.PBProjectUpdater;
import org.objectstyle.wolips.core.project.WOLipsJavaProject;
import org.objectstyle.wolips.core.project.WOLipsProject;
import org.objectstyle.wolips.logging.WOLipsLog;
/**
 * Tracking changes in resources and synchronizes webobjects project file
 */
public class ResourceChangeListener
	implements IResourceChangeListener, IWOLipsPluginConstants {
	/**
	 * Constructor for ResourceChangeListener.
	 */
	public ResourceChangeListener() {
		super();
	}
	/**
	 * Adds instance of inner class ProjectFileResourceValidator to events
	 * resource delta.
	 * <br>
	 * @see ProjectFileResourceValidator
	 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(IResourceChangeEvent)
	 */
	public final void resourceChanged(IResourceChangeEvent event) {
		//System.out.println("******* resourceChanged begin ");
		ProjectFileResourceValidator resourceValidator =
			new ProjectFileResourceValidator();
		try {
			event.getDelta().accept(resourceValidator);
		} catch (CoreException e) {
			WOLipsLog.log(e);
		}
		// update project files
		IFile projectFileToUpdate;
		PBProjectUpdater projectUpdater;
		Object[] allAddedKeys =
			resourceValidator.getAddedResourcesProjectDict().keySet().toArray();
		for (int i = 0; i < allAddedKeys.length; i++) {
			projectFileToUpdate = (IFile) allAddedKeys[i];
			projectUpdater =
				PBProjectUpdater.instance(projectFileToUpdate.getParent());
			projectUpdater.syncFilestable(
				(HashMap) resourceValidator.getAddedResourcesProjectDict().get(
					projectFileToUpdate),
				IResourceDelta.ADDED);
		}
		Object[] allRemovedKeys =
			resourceValidator
				.getRemovedResourcesProjectDict()
				.keySet()
				.toArray();
		for (int i = 0; i < allRemovedKeys.length; i++) {
			projectFileToUpdate = (IFile) allRemovedKeys[i];
			// ensure project file container exists
			// if no container exists the whole project is deleted
			if (projectFileToUpdate.getParent().exists()) {
				projectUpdater =
					PBProjectUpdater.instance(projectFileToUpdate.getParent());
				projectUpdater.syncFilestable(
					(HashMap) resourceValidator
						.getRemovedResourcesProjectDict()
						.get(
						projectFileToUpdate),
					IResourceDelta.REMOVED);
			}
		}
	}
	private final class ProjectFileResourceValidator
		implements IResourceDeltaVisitor {
		//private QualifiedName resourceQualifier;
		private IFile projectFile;
		private HashMap addedResourcesProjectDict;
		private HashMap removedResourcesProjectDict;
		/**
		 * @see java.lang.Object#Object()
		 */
		/**
		 * Constructor for ProjectFileResourceValidator.
		 */
		public ProjectFileResourceValidator() {
			super();
			addedResourcesProjectDict = new HashMap();
			removedResourcesProjectDict = new HashMap();
		}
		/**
		 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(IResourceDelta)
		 */
		public final boolean visit(IResourceDelta delta) {
			IResource resource = delta.getResource();
			try {
				return examineResource(resource, delta.getKind());
			} catch (CoreException e) {
				WOLipsLog.log(e);
				return false;
			}
		}
		/**
		 * Method examineResource. Examines changed resources for added and/or removed webobjects project
		 * resources and synchronizes project file.
		 * <br>
		 * @see #updateProjectFile(int, IResource, QualifiedName, IFile)
		 * @param resource
		 * @param kindOfChange
		 * @return boolean
		 * @throws CoreException
		 */
		private final boolean examineResource(
			IResource resource,
			int kindOfChange)
			throws CoreException {
			//see bugreport #708385 
			if (!resource.isAccessible() && kindOfChange != IResourceDelta.REMOVED)
				return false;
			// reset project file to update
			projectFile = null;
			switch (resource.getType()) {
				case IResource.ROOT :
					// further investigation of resource delta needed
					return true;
				case IResource.PROJECT :
					if (!resource.exists() || !resource.isAccessible()) {
						// project deleted no further investigation needed
						return false;
					}
					WOLipsProject wolipsProject =
						new WOLipsProject((IProject) resource);
					if (wolipsProject.getNaturesAccessor().hasWOLipsNature()) {
						// resource change concerns to webobjects project
						// -> visit childs
						return true;
					} // no webobjects project
					return false;
				case IResource.FOLDER :
					if (needsProjectFileUpdate(kindOfChange)) {
						if (EXT_FRAMEWORK.equals(resource.getFileExtension())
							|| EXT_WOA.equals(resource.getFileExtension())
							|| EXT_BUILD.equals(resource.getFileExtension())) {
							// no further examination needed
							return false;
						}
						if (EXT_COMPONENT
							.equals(resource.getFileExtension())) {
							updateProjectFile(
								kindOfChange,
								resource,
								COMPONENTS_ID,
								resource.getParent().getFile(
									new Path(PROJECT_FILE_NAME)));
						} else if (
							EXT_EOMODEL.equals(resource.getFileExtension())) {
							updateProjectFile(
								kindOfChange,
								resource,
								RESOURCES_ID,
								resource.getParent().getFile(
									new Path(PROJECT_FILE_NAME)));
						} else if (
							EXT_EOMODEL_BACKUP.equals(
								resource.getFileExtension())) {
							deleteTeamPrivateMembers((IFolder) resource);
						} else if (
							EXT_SUBPROJECT.equals(
								resource.getFileExtension())) {
							updateProjectFile(
								kindOfChange,
								resource,
								SUBPROJECTS_ID,
								resource.getParent().getFile(
									new Path(PROJECT_FILE_NAME)));
							if (IResourceDelta.REMOVED == kindOfChange) {
								// remove project's source folder from
								// classpathentries
								try {
									WOLipsJavaProject woLipsJavaProject =
										new WOLipsJavaProject(
											JavaCore.create(
												resource.getProject()));
									woLipsJavaProject
										.getClasspathAccessor()
										.removeSourcefolderFromClassPath(
										woLipsJavaProject
											.getClasspathAccessor()
											.getSubprojectSourceFolder(
											(IFolder) resource,
											false),
										null);
								} catch (InvocationTargetException e) {
									WOLipsLog.log(e);
								}
							}
						}
					}
					// further examination of resource delta needed
					return true;
				case IResource.FILE :
					if (needsProjectFileUpdate(kindOfChange)) {
						// this file must be an webobjects project file
						QualifiedName resourceQualifier = null;
						// files with java extension are located in src folders
						// the relating project file is determined through the
						// name of the src folder containing the java file
						//if (EXT_JAVA.equals(resource.getFileExtension())) {
						if (false) {
							// determine project file
							IResource parent = resource;
							IPath projectFilePath = new Path(PROJECT_FILE_NAME);
							while ((parent = parent.getParent())
								!= resource.getProject()) {
								if (EXT_SRC.equals(parent.getFileExtension())
									&& parent instanceof IContainer) {
									// determine name of project file container
									// (remove ".src" extension from resource containing
									// changed java file
									final String projectFileContainerName =
										parent.getName().substring(
											0,
											parent.getName().length()
												- (EXT_SRC.length() + 1));
									// search for project file
									IResourceVisitor projectFileFinder =
										new IResourceVisitor() {
										private IContainer projectFileContainer;
										public boolean visit(IResource resource) {
											if ((resource.getType()
												== IResource.FOLDER
												|| resource.getType()
													== IResource.PROJECT)
												&& resource.getName().equals(
													projectFileContainerName)) {
												projectFileContainer =
													(IContainer) resource;
												projectFile =
													projectFileContainer
														.getFile(
														new Path(PROJECT_FILE_NAME));
												if (projectFile.exists()) {
													// file found
													return false;
												} else {
													// continue search	
													projectFile = null;
													return true;
												}
											}
											return true;
										}
									};
									resource.getProject().accept(
										projectFileFinder);
									if (projectFile == null
										&& resource
											.getProject()
											.getFile(projectFilePath)
											.exists()) {
										// try to find application project file
										projectFile =
											resource.getProject().getFile(
												projectFilePath);
									}
								}
							}
							updateProjectFile(
								kindOfChange,
								resource,
								CLASSES_ID,
								projectFile);
						}
						if (EXT_JAVA.equals(resource.getFileExtension())) {
							updateProjectFile(
								kindOfChange,
								resource,
								CLASSES_ID,
								resource.getParent().getFile(
									new Path(PROJECT_FILE_NAME)));
						} else if (
							EXT_API.equals(resource.getFileExtension())
								|| EXT_STRINGS.equals(
									resource.getFileExtension())) {
							updateProjectFile(
								kindOfChange,
								resource,
								RESOURCES_ID,
								resource.getParent().getFile(
									new Path(PROJECT_FILE_NAME)));
						}
						//return false;
					}
			}
			return false;
		}
		/**
		 * @param resource
		 */
		private void deleteTeamPrivateMembers(IFolder folder) {
			try {
				File file =
					new File(
						folder.getLocation().toOSString()
							+ System.getProperty("path.separator")
							+ "CVS");
				if (file.exists() && file.isDirectory())
					file.delete();
			} catch (Exception exception) {
				WOLipsLog.log(exception);
			}
		}
		/**
		 * Method needsProjectFileUpdate.
		 * @param kindOfChange
		 * @return boolean
		 */
		private final boolean needsProjectFileUpdate(int kindOfChange) {
			return IResourceDelta.ADDED == kindOfChange
				|| IResourceDelta.REMOVED == kindOfChange;
		}
		/**
		* Method updateProjectFile adds or removes resources from project file
		* if the resources belongs to project file (determined in
		*  @link WOProjectFileUpdater#isProjectResource(IResource))
		* <br><br>
		* 
		* @param kind of change - resource added or removed
		* @param resource to update
		*/
		private final void updateProjectFile(
			int kindOfChange,
			IResource resourceToUpdate,
			String fileStableId,
			IFile projectFileToUpdate) {
			if (projectFileToUpdate == null) {
				return;
			}
			ArrayList changedResourcesArray = null;
			// let's examine the type of change
			switch (kindOfChange) {
				case IResourceDelta.ADDED :
					changedResourcesArray =
						getChangedResourcesArray(
							addedResourcesProjectDict,
							fileStableId,
							projectFileToUpdate);
					break;
				case IResourceDelta.REMOVED :
					changedResourcesArray =
						getChangedResourcesArray(
							removedResourcesProjectDict,
							fileStableId,
							projectFileToUpdate);
					break;
			}
			if (changedResourcesArray != null) {
				changedResourcesArray.add(resourceToUpdate);
			}
		}
		/**
		 * Method getChangedResourcesArray.
		 * @param projectDict
		 * @param fileStableId
		 * @param projectFileToUpdate
		 * @return NSMutableArray
		 */
		private final ArrayList getChangedResourcesArray(
			HashMap projectDict,
			String fileStableId,
			IFile projectFileToUpdate) {
			HashMap fileStableIdDict;
			ArrayList changedResourcesArray;
			if (projectDict.get(projectFileToUpdate) == null) {
				// new project found add file stable dict
				fileStableIdDict = new HashMap();
				projectDict.put(projectFileToUpdate, fileStableIdDict);
			} else {
				fileStableIdDict =
					(HashMap) projectDict.get(projectFileToUpdate);
			}
			if (fileStableIdDict.get(fileStableId) == null) {
				// add changedResourcesArray of type fileStableId
				changedResourcesArray = new ArrayList();
				fileStableIdDict.put(fileStableId, changedResourcesArray);
			} else {
				changedResourcesArray =
					(ArrayList) fileStableIdDict.get(fileStableId);
			}
			return changedResourcesArray;
		}
		/**
		 * Returns the addedResourcesProjectDict.
		 * @return NSMutableDictionary
		 */
		public final HashMap getAddedResourcesProjectDict() {
			return addedResourcesProjectDict;
		}
		/**
		 * Returns the removedResourcesProjectDict.
		 * @return NSMutableDictionary
		 */
		public final HashMap getRemovedResourcesProjectDict() {
			return removedResourcesProjectDict;
		}
	}
}
