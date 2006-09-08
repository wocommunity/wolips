/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2005 The ObjectStyle Group,
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
package org.objectstyle.wolips.locate.scope;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.objectstyle.wolips.locate.LocatePlugin;

public abstract class AbstractProjectReferencesLocateScope implements ILocateScope {
	private IProject myProject;

	private List myProjects;

	private boolean myFindProjectsThatDependOnThis;

	private boolean myFindProjectsThatThisDependsOn;

	private boolean myIncludeThis;

	public AbstractProjectReferencesLocateScope(IProject project, boolean _findProjectsThatDependOnThis, boolean _findProjectsThatThisDependOn, boolean _includeThis) {
		myProject = project;
		myIncludeThis = _includeThis;
		myFindProjectsThatDependOnThis = _findProjectsThatDependOnThis;
		myFindProjectsThatThisDependsOn = _findProjectsThatThisDependOn;
	}

	public boolean ignoreContainer(IContainer container) {
		if (container.getType() == IResource.PROJECT) {
			return ignoreProject((IProject) container);
		}
		return _ignoreContainer(container);
	}

	protected abstract boolean _ignoreContainer(IContainer _container);

	private boolean ignoreProject(IProject projectToValidate) {
		if (myProjects == null) {
			myProjects = new LinkedList();
			IProject[] allProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
			for (int projectNum = 0; projectNum < allProjects.length; projectNum++) {
				if (myIncludeThis && myProject.equals(allProjects[projectNum])) {
					myProjects.add(allProjects[projectNum]);
				} else if ((myFindProjectsThatDependOnThis && doesProjectDependOnProject(allProjects[projectNum], myProject)) || (myFindProjectsThatThisDependsOn && doesProjectDependOnProject(myProject, allProjects[projectNum]))) {
					myProjects.add(allProjects[projectNum]);
				}
			}
		}
		return !myProjects.contains(projectToValidate);
	}

	private boolean doesProjectDependOnProject(IProject _project, IProject _maybeDependsOnProject) {
		boolean projectIsDependedOn = false;
		try {
			if (_project.isOpen() || _project.isAccessible()) {
				IProject[] referencedProjects = _project.getReferencedProjects();
				for (int projectNum = 0; !projectIsDependedOn && projectNum < referencedProjects.length; projectNum++) {
					projectIsDependedOn = referencedProjects[projectNum].equals(_maybeDependsOnProject);
				}
			}
		} catch (Exception anException) {
			LocatePlugin.getDefault().log(anException);
		}
		return projectIsDependedOn;
	}
}
