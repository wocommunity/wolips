/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2002, 2004 The ObjectStyle Group 
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
package org.objectstyle.wolips.projectbuild.builder.incremental;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.objectstyle.wolips.datasets.adaptable.Project;
import org.objectstyle.wolips.projectbuild.ProjectBuildPlugin;

/**
 * @author Harald Niesche
 */
public class BuildVisitor extends BuildHelper {
	IPath outputPath = null;

	//IPath _buildPath = null;

	boolean _checkJavaOutputPath = false;

	int count = 0;

	// key: IPath/destination, value: IResource/source
	private Map _destinations = new HashMap();

	/**
	 * 
	 */
	public BuildVisitor() {
		super();
	}

	public void reinitForNextBuild(Project project) {
		super.reinitForNextBuild( project);
		try {
			IJavaProject jp = this.getIncrementalNature().getJavaProject();
			outputPath = jp.getOutputLocation();
			_checkJavaOutputPath = !outputPath.equals(jp.getPath());
		} catch (CoreException up) {
			outputPath = new Path("/dummy");
		}
		count = 0;
		_destinations.clear();
	}

	public void resetCount() {
		count = 0;
	}
	
	/**
	 * @param res
	 * @param delta
	 * @param copyToPath
	 * @return
	 * @throws CoreException
	 */
	public boolean _checkResource(IResource res, IResourceDelta delta,
			IPath copyToPath) throws CoreException {
		boolean result;
		if (null == copyToPath) {
			unmarkResource(res, ProjectBuildPlugin.MARKER_BUILD_DUPLICATE);
			return false;
		}
		IResource src = (IResource) _destinations.get(copyToPath);
		boolean deleted = (null != delta)
				&& (delta.getKind() == IResourceDelta.REMOVED);
		if (null == src) {
			if (!deleted) {
				_destinations.put(copyToPath, res);
			}
			result = true;
		} else if (src.equals(res)) {
			if (deleted) {
				_destinations.remove(copyToPath);
			}
			result = true;
		} else {
			if (!deleted) {
				IPath shortened = copyToPath.removeFirstSegments(2);
				String message = "duplicate resource for destination .../"
						+ shortened.toString();
				//_getLogger().debug("** " + message);
				markResource(res, ProjectBuildPlugin.MARKER_BUILD_DUPLICATE,
						IMarker.SEVERITY_ERROR, message, src.getFullPath()
								.toString());
				result = false; // ignore this one, it's a duplicate
			} else {
				result = true;
			}
		}
		if (result && !deleted) {
			unmarkResource(res, ProjectBuildPlugin.MARKER_BUILD_DUPLICATE);
		}
		return result;
	}

	public boolean _checkDirs() throws CoreException {
		IPath buildPath = this.getIncrementalNature().getBuildPath();
		IPath resPath = this.getIncrementalNature().getResourceOutputPath();
		IPath javaPath = this.getIncrementalNature().getJavaOutputPath();
		IPath webresPath = this.getIncrementalNature()
				.getWebResourceOutputPath();
		boolean result = checkDerivedDir(buildPath, null);
		result = checkDerivedDir(resPath, null) && result;
		result = checkDerivedDir(javaPath, null) && result;
		result = checkDerivedDir(webresPath, null) && result;
		return (result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectstyle.wolips.projectbuild.builder.WOIncrementalBuilder.WOBuildHelper#handleResource(org.eclipse.core.resources.IResource,
	 *      org.eclipse.core.resources.IResourceDelta)
	 */
	public void handleResource(IResource res, IResourceDelta delta)
			throws CoreException {
		++count;
		IPath fullPath = res.getFullPath();
		boolean ignore = false;
		// ignore resources already in build folder
		if (this.getIncrementalNature().getBuildPath().isPrefixOf(fullPath)) {
			ignore = true;
		}
		// ignore resources copied to the Java output folder
		if (!ignore && _checkJavaOutputPath && outputPath.isPrefixOf(fullPath)) {
			ignore = true;
		}
		boolean handled = false;
		if (!ignore) {
			if (this.getProject().matchesResourcesPattern(res)) {
				IPath dest = this.getIncrementalNature().asResourcePath(
						res.getFullPath(), res);
				if (_checkResource(res, delta, dest)) {
					handled = _handleResource(res, delta, dest);
				} else {
					handled = true;
				}
			} else if (res.toString().indexOf("/Resources/") != -1) {
				//					_getLogger().debug("ignoring probable resource! "+res);
			}
			if (this.getProject().matchesWOAppResourcesPattern(res)) {
				IPath dest = this.getIncrementalNature().asWebResourcePath(
						res.getFullPath(), res);
				if (_checkResource(res, delta, dest)) {
					handled = _handleResource(res, delta, dest);
				} else {
					handled = true;
				}
			}
		}
		if (!handled) {
			_getLogger().debug("//not a (ws)resource: " + res);
			unmarkResource(res, ProjectBuildPlugin.MARKER_BUILD_DUPLICATE);
		}
	}

	/**
	 * @param res
	 * @param delta
	 * @param copyToPath
	 * @return
	 * @throws CoreException
	 */
	public boolean _handleResource(IResource res, IResourceDelta delta,
			IPath copyToPath) {
		if (null == copyToPath)
			return false;

		boolean handled = false;
		if ((null != delta) && (delta.getKind() == IResourceDelta.REMOVED)) {
			addTask(new DeleteTask(copyToPath, "build"));
			handled = true;
		} else {
			if (!res.isTeamPrivateMember()) {
				addTask(new CopyTask(res, copyToPath, "build"));
				handled = true;
			}
		}
		return handled;
	}

	public int getCount() {
		return count;
	}

}