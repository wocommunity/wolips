/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2002 - 2004 The ObjectStyle Group 
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
package org.objectstyle.wolips.jdt.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.objectstyle.wolips.datasets.adaptable.Project;
import org.objectstyle.wolips.datasets.project.PBProjectUpdater;
import org.objectstyle.wolips.datasets.resources.IWOLipsModel;
import org.objectstyle.wolips.jdt.JdtPlugin;
import org.objectstyle.wolips.jdt.ant.UpdateFrameworkIncludeFiles;
import org.objectstyle.wolips.jdt.ant.UpdateOtherClasspathIncludeFiles;

/**
 * Tracking changes in classpath and synchronizes webobjects project file
 */
public class JavaElementChangeListener extends WorkspaceJob {
	private ElementChangedEvent event;

	/**
	 * Constructor for JavaElementChangeListener.
	 */
	public JavaElementChangeListener() {
		super("WOLips Project Files Updates (Java Elements)");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.WorkspaceJob#runInWorkspace(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
		/*
		 * if (ElementChangedEvent.POST_CHANGE != event.getType()) return;
		 */
		if (this.event.getDelta().getElement().getElementType() != IJavaElement.JAVA_MODEL)
			return new Status(IStatus.OK, JdtPlugin.getPluginId(), IStatus.OK,
					"Done", null);
		
		IJavaElementDelta elementDeltaToExamine = this.event.getDelta();
		ArrayList foundChangedElements = new ArrayList();
		HashMap addedFrameworksProjectDict = new HashMap();
		HashMap removedFrameworksProjectDict = new HashMap();
		boolean javaProjectChanges = false;
		// java model has changed - get affected webobjects projects
		for (int i = 0; i < elementDeltaToExamine.getChangedChildren().length; i++) {
			// examine changed children if they are webobjects projects
			if (elementDeltaToExamine.getChangedChildren()[i].getElement()
					.getElementType() == IJavaElement.JAVA_PROJECT) {
				IProject projectToExamine = ((IJavaProject) elementDeltaToExamine
						.getChangedChildren()[i].getElement()).getProject();
				if (!projectToExamine.exists()
						|| !projectToExamine.isAccessible()) {
					// project deleted no further investigatin needed
					continue;
				}
				if (!searchDeltasForElementType(elementDeltaToExamine
						.getChangedChildren()[i].getChangedChildren(),
						IJavaElement.PACKAGE_FRAGMENT_ROOT,
						foundChangedElements))
					continue;
				javaProjectChanges = true;
				UpdateOtherClasspathIncludeFiles updateOtherClasspathIncludeFiles = new UpdateOtherClasspathIncludeFiles();
				updateOtherClasspathIncludeFiles
						.setIProject(projectToExamine);
				updateOtherClasspathIncludeFiles.execute();
				UpdateFrameworkIncludeFiles updateFrameworkIncludeFiles = new UpdateFrameworkIncludeFiles();
				updateFrameworkIncludeFiles
						.setIProject(projectToExamine);
				updateFrameworkIncludeFiles.execute();
				
				
				Project woLipsProject = null;
				try {
					woLipsProject = (Project) (projectToExamine)
							.getAdapter(Project.class);
					if (woLipsProject.hasWOLipsNature()) {
						addedFrameworksProjectDict.put(projectToExamine,
								new ArrayList());
						removedFrameworksProjectDict.put(projectToExamine,
								new ArrayList());
						// webobjects project changed
						ArrayList foundElements = new ArrayList();
						// search deltas for classpath changes
						searchDeltas(
								elementDeltaToExamine.getChangedChildren()[i]
										.getChangedChildren(),
								IJavaElementDelta.F_ADDED_TO_CLASSPATH,
								foundElements);
						IPackageFragmentRoot currentPackageFragmentRoot;
						ArrayList addedFrameworks = (ArrayList) addedFrameworksProjectDict
								.get(projectToExamine);
						for (int j = 0; j < foundElements.size(); j++) {
							currentPackageFragmentRoot = (IPackageFragmentRoot) foundElements
									.get(j);
							//System.out.println(currentPackageFragmentRoot);
							addedFrameworks.add(currentPackageFragmentRoot
									.getPath());
						}
						foundElements = new ArrayList();
						// search deltas for classpath changes
						searchDeltas(
								elementDeltaToExamine.getChangedChildren()[i]
										.getChangedChildren(),
								IJavaElementDelta.F_REMOVED_FROM_CLASSPATH,
								foundElements);
						ArrayList removedFrameworks = (ArrayList) removedFrameworksProjectDict
								.get(projectToExamine);
						for (int j = 0; j < foundElements.size(); j++) {
							currentPackageFragmentRoot = (IPackageFragmentRoot) foundElements
									.get(j);
							IPath rawClasspathEntryPath = currentPackageFragmentRoot
									.getPath();
							//.getRawClasspathEntry()
							//.getPath();
							removedFrameworks.add(rawClasspathEntryPath);
						}
					}
				} catch (CoreException e) {
					JdtPlugin.getDefault().getPluginLogger().log(e);
				}
			}
		}
		if (javaProjectChanges)
			// update project files
			updateProjects(addedFrameworksProjectDict,
					removedFrameworksProjectDict);
		return new Status(IStatus.OK, JdtPlugin.getPluginId(), IStatus.OK,
				"Done", null);
	}

	/**
	 * Method searchDeltas. Recursive search in java element deltas for changed
	 * elements matching the change flag to search for. The results are stored
	 * in foundElements. <br>
	 * <br>
	 * 
	 * @param deltasToExamine
	 * @param elementType
	 * @param foundElements
	 * @return boolean
	 */
	private final boolean searchDeltasForElementType(
			IJavaElementDelta[] deltasToExamine, int elementType,
			ArrayList foundElements) {
		for (int i = 0; i < deltasToExamine.length; i++) {
			if (deltasToExamine[i].getFlags() == IJavaElementDelta.F_CHILDREN) {
				// further examination needed
				while (searchDeltas(deltasToExamine[i].getChangedChildren(),
						elementType, foundElements)) {
					continue;
				}
			} else if (deltasToExamine[i].getElement().getElementType() == elementType) {
				// element found
				foundElements.add(deltasToExamine[i].getElement());
			}
		}
		return (foundElements != null && foundElements.size() > 0);
	}

	/**
	 * Method searchDeltas. Recursive search in java element deltas for changed
	 * elements matching the change flag to search for. The results are stored
	 * in foundElements. <br>
	 * <br>
	 * 
	 * @param deltasToExamine
	 * @param changeFlagToSearch
	 * @param foundElements
	 * @return boolean
	 */
	private final boolean searchDeltas(IJavaElementDelta[] deltasToExamine,
			int changeFlagToSearch, ArrayList foundElements) {
		for (int i = 0; i < deltasToExamine.length; i++) {
			if (deltasToExamine[i].getFlags() == IJavaElementDelta.F_CHILDREN) {
				// further examination needed
				while (searchDeltas(deltasToExamine[i].getChangedChildren(),
						changeFlagToSearch, foundElements)) {
					continue;
				}
			} else if ((deltasToExamine[i].getFlags() & changeFlagToSearch) == 0) {
				// element found
				foundElements.add(deltasToExamine[i].getElement());
			}
		}
		return (foundElements != null && foundElements.size() > 0);
	}

	/**
	 * Method updateProjects.
	 * 
	 * @param addedFrameworksProjectDict
	 * @param removedFrameworksProjectDict
	 */
	private final void updateProjects(HashMap addedFrameworksProjectDict,
			HashMap removedFrameworksProjectDict) {
		IProject currentProject;
		List changedFrameworks;
		Object[] allRemovedKeys = removedFrameworksProjectDict.keySet()
				.toArray();
		for (int i = 0; i < allRemovedKeys.length; i++) {
			currentProject = (IProject) allRemovedKeys[i];
			changedFrameworks = (List) removedFrameworksProjectDict
					.get(currentProject);
			if (changedFrameworks.size() > 0) {
				PBProjectUpdater projectUpdater = PBProjectUpdater
						.instance(currentProject);
                            if(projectUpdater != null)
                                projectUpdater.removeFrameworks(this
						.toFrameworkNames(changedFrameworks));
			}
		}
		Object[] allAddedKeys = addedFrameworksProjectDict.keySet().toArray();
		for (int i = 0; i < allAddedKeys.length; i++) {
			currentProject = (IProject) allAddedKeys[i];
			changedFrameworks = (ArrayList) addedFrameworksProjectDict
					.get(currentProject);
			if (changedFrameworks.size() > 0) {
				PBProjectUpdater projectUpdater = PBProjectUpdater
						.instance(currentProject);
                            if(projectUpdater != null)
				projectUpdater.addFrameworks(this
						.toFrameworkNames(changedFrameworks));
			}
		}
	}

	private List toFrameworkNames(List paths) {
		ArrayList arrayList = new ArrayList();
		for (int i = 0; i < paths.size(); i++) {
			IPath path = (IPath) paths.get(i);
			arrayList.add(this.getFrameworkName(path));
		}
		return arrayList;
	}

	private String getFrameworkName(IPath frameworkPath) {
		String frameworkName = null;
		int i = 0;
		int count = frameworkPath.segmentCount();
		while (i < count && frameworkName == null) {
			String segment = frameworkPath.segment(i);
			if (segment.endsWith("." + IWOLipsModel.EXT_FRAMEWORK))
				frameworkName = segment;
			else
				i++;
		}
		return frameworkName;
	}

	/**
	 * @param event
	 *            The event to set.
	 */
	public void setEvent(ElementChangedEvent event) {
		this.event = event;
	}
}