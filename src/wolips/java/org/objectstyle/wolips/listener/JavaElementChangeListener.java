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
package org.objectstyle.wolips.listener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.objectstyle.wolips.IWOLipsPluginConstants;
import org.objectstyle.wolips.io.WOLipsLog;
import org.objectstyle.wolips.project.PBProjectUpdater;
import org.objectstyle.woproject.pb.PBProject;
/**
 * Tracking changes in classpath and synchronizes webobjects project file
 */
public class JavaElementChangeListener implements IElementChangedListener {
	/**
	 * Constructor for JavaElementChangeListener.
	 */
	public JavaElementChangeListener() {
		super();
	}
	/**
	 * Catches changed webobjects nature projects and updates frameworks in webobjects
	 * project file.
	 * <br>
	 * @see org.eclipse.jdt.core.IElementChangedListener#elementChanged(ElementChangedEvent)
	 */
	public final void elementChanged(ElementChangedEvent event) {
		HashMap addedFrameworksProjectDict =
			new HashMap();
		HashMap removedFrameworksProjectDict =
			new HashMap();
		if (event.getDelta().getElement().getElementType()
			== IJavaElement.JAVA_MODEL) {
			IJavaElementDelta elementDeltaToExamine = event.getDelta();
			// java model has changed - get affected webobjects projects
			for (int i = 0;
				i < elementDeltaToExamine.getChangedChildren().length;
				i++) {
				// examine changed children if they are webobjects projects
				if (elementDeltaToExamine
					.getChangedChildren()[i]
					.getElement()
					.getElementType()
					== IJavaElement.JAVA_PROJECT) {
					IProject projectToExamine =
						((IJavaProject) elementDeltaToExamine
							.getChangedChildren()[i]
							.getElement())
							.getProject();
					if (!projectToExamine.exists()) {
						// project deleted no further investigatin needed
						continue;
					}

					try {
						if (projectToExamine
							.hasNature(
								IWOLipsPluginConstants.WO_APPLICATION_NATURE)
							|| projectToExamine.hasNature(
								IWOLipsPluginConstants.WO_FRAMEWORK_NATURE)) {
							addedFrameworksProjectDict.put(projectToExamine,new ArrayList());
							removedFrameworksProjectDict.put(projectToExamine,new ArrayList());
							// webobjects project changed 
							ArrayList foundElements = new ArrayList();
							// search deltas for classpath changes
							searchDeltas(
								elementDeltaToExamine
									.getChangedChildren()[i]
									.getChangedChildren(),
								IJavaElementDelta.F_ADDED_TO_CLASSPATH,
								foundElements);
							IPackageFragmentRoot currentPackageFragmentRoot;
							for (int j = 0; j < foundElements.size(); j++) {
								currentPackageFragmentRoot =
									(IPackageFragmentRoot) foundElements.get(j);
								ArrayList addedFrameworks =
									(ArrayList) addedFrameworksProjectDict.get(projectToExamine);
								addedFrameworks.add(
									currentPackageFragmentRoot
										.getRawClasspathEntry()
										.getPath());
								/*
								WOProjectFileUpdater.addFrameworkToPBFile(
									currentPackageFragmentRoot
										.getRawClasspathEntry()
										.getPath(),
									WOProjectFileUpdater
										.projectFileFromParentResource(
										projectToExamine),
									null);
									*/
							}
							foundElements = new ArrayList();
							// search deltas for classpath changes
							searchDeltas(
								elementDeltaToExamine
									.getChangedChildren()[i]
									.getChangedChildren(),
								IJavaElementDelta.F_REMOVED_FROM_CLASSPATH,
								foundElements);
							for (int j = 0; j < foundElements.size(); j++) {
								currentPackageFragmentRoot =
									(IPackageFragmentRoot) foundElements.get(j);
								ArrayList removedFrameworks =
									(ArrayList) removedFrameworksProjectDict.get(projectToExamine);
								removedFrameworks.add(
									new Path(
										currentPackageFragmentRoot
											.getElementName()));
								/*
								WOProjectFileUpdater
								.removeFrameworkFromPBFile(
								new Path(
								currentPackageFragmentRoot
									.getElementName()),
								WOProjectFileUpdater
								.projectFileFromParentResource(
								projectToExamine),
								null);
								*/
							}
						}
					} catch (CoreException e) {
						WOLipsLog.log(e);
					}
				}
			}
		}
		// update project files
		updateProjects(
			addedFrameworksProjectDict,
			removedFrameworksProjectDict);
	}
	/**
	 * Method searchDeltas. Recursive search in java element deltas for changed elements
	 * matching the change flag to search for. The results are stored in foundElements.
	 * <br><br>
	 * @param deltasToExamine
	 * @param changeFlagToSearch
	 * @param foundElements
	 * @return boolean
	 */
	private final boolean searchDeltas(
		IJavaElementDelta[] deltasToExamine,
		int changeFlagToSearch,
		ArrayList foundElements) {
		for (int i = 0; i < deltasToExamine.length; i++) {
			if (deltasToExamine[i].getFlags()
				== IJavaElementDelta.F_CHILDREN) {
				// further examination needed
				while (searchDeltas(deltasToExamine[i].getChangedChildren(),
					changeFlagToSearch,
					foundElements));
			} else if (deltasToExamine[i].getFlags() == changeFlagToSearch) {
				// element found 
				foundElements.add(deltasToExamine[i].getElement());
			}
		}
		return false;
	}
	/**
	 * Method updateProjects.
	 * @param addedFrameworksProjectDict
	 * @param removedFrameworksProjectDict
	 */
	private final void updateProjects(
		HashMap addedFrameworksProjectDict,
	HashMap removedFrameworksProjectDict) {
		IProject currentProject;
		PBProject currentPBProject;
		List frameworks;
		List changedFrameworks;
		Object[] allAddedKeys = addedFrameworksProjectDict.keySet().toArray();
		for (int i = 0;i < allAddedKeys.length;i++) {
			currentProject =
				(IProject)allAddedKeys[i];
			changedFrameworks =
				(ArrayList) addedFrameworksProjectDict.get(currentProject);
			if (changedFrameworks.size() > 0) {
				PBProjectUpdater projectUpdater =
					new PBProjectUpdater(currentProject);
				projectUpdater.addFrameworks(changedFrameworks);
			}
		}
		Object[] allRemovedKeys = removedFrameworksProjectDict.keySet().toArray();
		for (int i = 0;i < allRemovedKeys.length;i++) {
			currentProject =
				(IProject)allRemovedKeys[i];
			changedFrameworks =
				(List) removedFrameworksProjectDict.get(currentProject);
			if (changedFrameworks.size() > 0) {
				PBProjectUpdater projectUpdater =
					new PBProjectUpdater(currentProject);
				projectUpdater.removeFrameworks(changedFrameworks);
			}
		}
	}
}
