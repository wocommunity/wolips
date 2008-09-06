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

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.objectstyle.wolips.core.resources.internal.pattern.PatternList;
import org.objectstyle.wolips.core.resources.internal.types.project.ProjectPatternsets;
import org.objectstyle.wolips.core.resources.types.folder.IBuildAdapter;
import org.objectstyle.wolips.core.resources.types.folder.IDotApplicationAdapter;
import org.objectstyle.wolips.core.resources.types.folder.IDotFrameworkAdapter;

/**
 * @author ulrich/mike
 */
public class AbstractBuildVisitor {
	public static int WO_RESOURCE_TYPE_IGNORE = 0;

	public static int WO_RESOURCE_TYPE_RESOURCE = 1;

	public static int WO_RESOURCE_TYPE_WEB_SERVER_RESOURCE = 2;

	public static int WO_RESOURCE_TYPE_CLASS = 3;

	public static int WO_RESOURCE_TYPE_CLASSPATH = 4;

	public static int WO_RESOURCE_TYPE_OTHER = 5;

	public static int WO_RESOURCE_TYPE_SOURCE = 6;

	private IProgressMonitor myProgressMonitor;

	private BuilderWrapper[] myBuilderWrappers;

	private ProjectPatternsets projectPatternsets;

	private Map myBuildCache;

	private final static PatternList DEFAULT_EXCLUDE_MATCHER = new PatternList(new String[] { "**/.svn", "**/.svn/**", "**/CVS", "**.*~/**", "**/CVS/**", "**/build/**", "**/dist/**" });

	public AbstractBuildVisitor(BuilderWrapper[] _builderWrappers, IProgressMonitor _progressMonitor, Map _buildCache) {
		myBuilderWrappers = _builderWrappers;
		myProgressMonitor = _progressMonitor;
		myBuildCache = _buildCache;
	}

	public Map getBuildCache() {
		return myBuildCache;
	}

	public IProgressMonitor getProgressMonitor() {
		return myProgressMonitor;
	}

	public boolean isCanceled() {
		return myProgressMonitor != null && myProgressMonitor.isCanceled();
	}

	public BuilderWrapper[] getBuilderWrappers() {
		return myBuilderWrappers;
	}

	public void buildStarted(IProject project) {
		projectPatternsets = new ProjectPatternsets(project);
	}

	public void visitingDone() {
		projectPatternsets = null;
	}

	public int getWoResourceType(IResource _resource) {
		int woResourceType;
		if (_resource == null || _resource.isDerived()) {
			return AbstractBuildVisitor.WO_RESOURCE_TYPE_IGNORE;
		}
		if (_resource.getType() == IResource.FOLDER) {
			if (IBuildAdapter.FILE_NAME_DIST.equals(_resource.getName())) {
				return AbstractBuildVisitor.WO_RESOURCE_TYPE_IGNORE;
			}
			IDotFrameworkAdapter dotFrameworkAdapter = (IDotFrameworkAdapter) _resource.getAdapter(IDotFrameworkAdapter.class);
			if (dotFrameworkAdapter != null) {
				return AbstractBuildVisitor.WO_RESOURCE_TYPE_IGNORE;
			}
			IDotApplicationAdapter dotApplicationAdapter = (IDotApplicationAdapter) _resource.getAdapter(IDotApplicationAdapter.class);
			if (dotApplicationAdapter != null) {
				return AbstractBuildVisitor.WO_RESOURCE_TYPE_IGNORE;
			}
		}
		String[] strings = toProjectRelativePaths(_resource);
		if (DEFAULT_EXCLUDE_MATCHER.matches(strings)) {
			woResourceType = AbstractBuildVisitor.WO_RESOURCE_TYPE_IGNORE;
		} else if (!projectPatternsets.getClassesExcludeMatcher().match(strings) && projectPatternsets.getClassesIncludeMatcher().match(strings)) {
			woResourceType = AbstractBuildVisitor.WO_RESOURCE_TYPE_CLASS;
		} else if (!projectPatternsets.getResourcesExcludeMatcher().match(strings) && projectPatternsets.getResourcesIncludeMatcher().match(strings)) {
			woResourceType = AbstractBuildVisitor.WO_RESOURCE_TYPE_RESOURCE;
		} else if (!projectPatternsets.getWoappResourcesExcludeMatcher().match(strings) && projectPatternsets.getWoappResourcesIncludeMatcher().match(strings)) {
			woResourceType = AbstractBuildVisitor.WO_RESOURCE_TYPE_WEB_SERVER_RESOURCE;
		} else if (_resource.getType() == IResource.FILE && _resource.getName().endsWith(".java")) {
			woResourceType = AbstractBuildVisitor.WO_RESOURCE_TYPE_SOURCE;
		} else {
			woResourceType = AbstractBuildVisitor.WO_RESOURCE_TYPE_OTHER;
		}
		if (_resource.getType() == IResource.FILE && _resource.getName().equals(".classpath")) {
			woResourceType = AbstractBuildVisitor.WO_RESOURCE_TYPE_CLASSPATH;
		}
		return woResourceType;
	}

	private String[] toProjectRelativePaths(IResource resource) {
		String[] returnValue = null;
		if (resource.getParent().getType() != IResource.ROOT) {

			String string = null;
			if (resource.getType() != IResource.FOLDER) {
				returnValue = new String[1];
			} else {
				returnValue = new String[2];
				string = "/" + resource.getName() + "/";
				returnValue[0] = string;
			}

		} else {
			returnValue = new String[1];
		}
		IPath path = resource.getProjectRelativePath();
		String string = path.toString();
		if (returnValue.length == 2) {
			returnValue[1] = string;
		} else {
			returnValue[0] = string;
		}
		return returnValue;
	}
}
