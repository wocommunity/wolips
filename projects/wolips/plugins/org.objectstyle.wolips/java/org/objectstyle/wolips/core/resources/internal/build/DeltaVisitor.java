/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2005 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * 3. The end-user documentation included with the redistribution, if any, must
 * include the following acknowlegement: "This product includes software
 * developed by the ObjectStyle Group (http://objectstyle.org/)." Alternately,
 * this acknowlegement may appear in the software itself, if and wherever such
 * third-party acknowlegements normally appear.
 * 
 * 4. The names "ObjectStyle Group" and "Cayenne" must not be used to endorse or
 * promote products derived from this software without prior written permission.
 * For written permission, please contact andrus@objectstyle.org.
 * 
 * 5. Products derived from this software may not be called "ObjectStyle" nor
 * may "ObjectStyle" appear in their names without prior written permission of
 * the ObjectStyle Group.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * OBJECTSTYLE GROUP OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals on
 * behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/>.
 *  
 */
package org.objectstyle.wolips.core.resources.internal.build;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.objectstyle.wolips.core.resources.internal.pattern.PatternList;
import org.objectstyle.wolips.core.resources.types.file.IDotWOLipsAdapter;
import org.objectstyle.wolips.core.resources.types.folder.IDotApplicationAdapter;
import org.objectstyle.wolips.core.resources.types.folder.IDotFrameworkAdapter;
import org.objectstyle.wolips.core.resources.types.project.IProjectAdapter;

/**
 * @author ulrich
 */
public class DeltaVisitor implements IResourceDeltaVisitor {
	private BuilderWrapper[] builderWrapper;

	private IDotWOLipsAdapter dotWOLipsAdapter;

	private final static PatternList DEFAULT_EXCLUDE_MATCHER = new PatternList(
			new String[] { "**/.svn", "**/.svn/**", "**/CVS", "**.*~/**",
					"**/CVS/**", "**/build/**", "**/dist/**" });

	public DeltaVisitor(BuilderWrapper[] builderWrapper) {
		super();
		this.builderWrapper = builderWrapper;
	}

	public void buildStarted(IProject project) {
		IProjectAdapter projectAdapter = (IProjectAdapter) project
				.getAdapter(IProjectAdapter.class);
		this.dotWOLipsAdapter = projectAdapter.getDotWOLipsAdapter();
	}

	public void visitingDeltasDone() {
		this.dotWOLipsAdapter = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
	 */
	public boolean visit(IResourceDelta delta) throws CoreException {
		if(delta == null) {
			return false;
		}
		IResource resource = delta.getResource();
		if (resource.getType() == IResource.FOLDER) {
			IDotFrameworkAdapter dotFrameworkAdapter = (IDotFrameworkAdapter) resource
					.getAdapter(IDotFrameworkAdapter.class);
			if (dotFrameworkAdapter != null) {
				return false;
			}
			IDotApplicationAdapter dotApplicationAdapter = (IDotApplicationAdapter) resource
					.getAdapter(IDotApplicationAdapter.class);
			if (dotApplicationAdapter != null) {
				return false;
			}
		}
		String[] strings = this.toProjectRelativePaths(resource);
		if (DEFAULT_EXCLUDE_MATCHER.matches(strings))
			return false;
		if (!this.dotWOLipsAdapter.getClassesExcludePatternList().matches(
				strings)
				&& this.dotWOLipsAdapter.getClassesIncludePatternList()
						.matches(strings)) {
			this.notifyBuilderHandleClassesDelta(delta);
		} else if (!this.dotWOLipsAdapter.getResourcesExcludePatternList()
				.matches(strings)
				&& this.dotWOLipsAdapter.getResourcesIncludePatternList()
						.matches(strings)) {
			this.notifyBuilderHandleResourcesDelta(delta);
		} else if (!this.dotWOLipsAdapter.getWoappResourcesExcludePatternList()
				.matches(strings)
				&& this.dotWOLipsAdapter.getWoappResourcesIncludePatternList()
						.matches(strings)) {
			this.notifyBuilderHandleWebServerResourcesDelta(delta);
		} else {
			this.notifyBuilderHandleOtherDelta(delta);
		}
		if (resource.getType() == IResource.FILE) {
			if (resource.getName().equals(".classpath")) {
				this.notifyBuilderClasspathChanged(delta);
			}
		}
		return true;
	}

	private void notifyBuilderClasspathChanged(IResourceDelta delta) {
		for (int i = 0; i < this.builderWrapper.length; i++) {
			this.builderWrapper[i].getBuilder().classpathChanged(delta);
		}
	}

	private void notifyBuilderHandleClassesDelta(IResourceDelta delta) {
		for (int i = 0; i < this.builderWrapper.length; i++) {
			this.builderWrapper[i].getBuilder().handleClassesDelta(delta);
		}
	}

	private void notifyBuilderHandleResourcesDelta(IResourceDelta delta) {
		for (int i = 0; i < this.builderWrapper.length; i++) {
			this.builderWrapper[i].getBuilder().handleWoappResourcesDelta(delta);
		}
	}

	private void notifyBuilderHandleWebServerResourcesDelta(IResourceDelta delta) {
		for (int i = 0; i < this.builderWrapper.length; i++) {
			this.builderWrapper[i].getBuilder()
					.handleWebServerResourcesDelta(delta);
		}
	}

	private void notifyBuilderHandleOtherDelta(IResourceDelta delta) {
		for (int i = 0; i < this.builderWrapper.length; i++) {
			this.builderWrapper[i].getBuilder().handleOtherDelta(delta);
		}
	}

	private String[] toProjectRelativePaths(IResource resource) {
		String[] returnValue = null;
		if (resource.getParent().getType() != IResource.ROOT
		/* && resource.getParent().getType() != IResource.PROJECT */) {
			returnValue = new String[2];
			String string = null;
			if (resource.getType() != IResource.FOLDER) {
				IPath path = resource.getParent().getProjectRelativePath();
				/*
				 * String string = resource.getProject().getName() + "/" +
				 * path.toString() + "/";
				 */
				string = path.toString() + "/";
			} else {
				string = "/" + resource.getName() + "/";
			}
			returnValue[0] = string;
		} else {
			returnValue = new String[1];
		}
		IPath path = resource.getProjectRelativePath();
		// String string = resource.getProject().getName() + "/" +
		// path.toString();
		String string = path.toString();
		if (returnValue.length == 2) {
			returnValue[1] = string;
		} else {
			returnValue[0] = string;
		}
		return returnValue;
	}
}
