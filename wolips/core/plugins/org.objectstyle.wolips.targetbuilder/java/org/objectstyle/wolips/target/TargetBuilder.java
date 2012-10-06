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

package org.objectstyle.wolips.target;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaModelMarker;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.jdt.internal.core.builder.JavaBuilder;
import org.eclipse.jdt.internal.core.builder.State;

public class TargetBuilder extends JavaBuilder {
	public static String ID = "org.objectstyle.wolips.targetbuilder.targetbuilder";

	public static String RESOURCE = "resource";

	public static String ATTRIBUTES = "attributes";

	private HashMap _problemMarkers;

	public TargetBuilder() {
		super();
	}

	private void cancelBuildOnErrors() throws CoreException {
		Integer severityError = Integer.valueOf(IMarker.SEVERITY_ERROR);

		IMarker[] problemMarkers = JavaBuilder.getProblemsFor(getProject());
		for (int j = 0; j < problemMarkers.length; j++) {
			if (problemMarkers[j].getAttribute(IMarker.SEVERITY).equals(severityError)) {
				// if (isResourceTargetMember(problemMarkers[j].getResource()))
				throw new OperationCanceledException("Compilation Errors");
			}
		}
	}

	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
		State buildState;
		String buldStateKey;
		IProject[] result = null;

		IProject project = getProject();

		List targets = targets();
		if (targets == null)
			return null;

		IJavaProject javaProject = JavaCore.create(project);
		IPath projectOutputLocation = javaProject.getOutputLocation();
		IClasspathEntry[] projectClasspath = javaProject.getRawClasspath();
		_problemMarkers = new HashMap();
		TargetBuilderPlugin plugin = TargetBuilderPlugin.getDefault();

		try {
			for (int i = 0; i < targets.size(); i++) {
				BuildTarget target = (BuildTarget) targets.get(i);
				buldStateKey = project.getName() + "/" + target.name();
				javaProject.setOutputLocation(target.outputLocation(), monitor);
				javaProject.setRawClasspath(target.classPathEntries(), monitor);
				buildState = plugin.buildStateForKey(buldStateKey);
				JavaModelManager.getJavaModelManager().setLastBuiltState(project, buildState);

				result = super.build(kind, args, monitor);

				buildState = (State) JavaModelManager.getJavaModelManager().getLastBuiltState(project, monitor);
				plugin.setBuildStateForKey(buildState, buldStateKey);
				registerProblemMarkers(JavaBuilder.getProblemsFor(getProject()));
				// cancelBuildOnErrors(javaProject);
			}
		} finally {
			javaProject.setOutputLocation(projectOutputLocation, monitor);
			javaProject.setRawClasspath(projectClasspath, monitor);
			updateProblemMarkers();
			cancelBuildOnErrors();
		}

		return result;
	}

	private List targets() throws CoreException {
		IResourceDelta targetFileDelta = null;
		IProject project = getProject();

		TargetBuilderNature targetNature = (TargetBuilderNature) project.getNature(TargetBuilderNature.ID);
		if (targetNature == null)
			return null;

		// check for changes in targets.plist file
		IResourceDelta sourceDelta = getDelta(project);
		if (sourceDelta != null)
			targetFileDelta = sourceDelta.findMember(new Path(TargetBuilderNature.TARGETFILE));
		if (targetNature.targets() == null || targetNature.targets().size() == 0 || targetFileDelta != null)
			targetNature.synchronizeWithFile();

		List targets = targetNature.targets();
		if (targets == null)
			return null;

		return targets;
	}

	private void updateProblemMarkers() throws CoreException {
		// JavaBuilder.removeProblemsFor(getProject());
		JavaBuilder.removeProblemsAndTasksFor(getProject());
		Set problemAttributes = _problemMarkers.entrySet();
		for (Iterator iter = problemAttributes.iterator(); iter.hasNext();) {
			Map element = (Map) ((Map.Entry) iter.next()).getValue();
			IResource resource = (IResource) element.get(TargetBuilder.RESOURCE);
			Map attributes = (Map) element.get(TargetBuilder.ATTRIBUTES);
			IMarker marker = resource.createMarker(IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER);
			marker.setAttributes(attributes);
		}
	}

	public void registerProblemMarkers(IMarker[] problemMarkers) throws CoreException {
		Map tempMap;

		for (int i = 0; i < problemMarkers.length; i++) {
			// if (isResourceTargetMember(problemMarkers[i].getResource()))
			{
				String hashKey = "";

				if (problemMarkers[i].getResource().getFullPath() != null)
					hashKey = hashKey + problemMarkers[i].getResource().getFullPath().toString();

				if (problemMarkers[i].getAttribute(IMarker.LINE_NUMBER) != null)
					hashKey = hashKey + problemMarkers[i].getAttribute(IMarker.LINE_NUMBER).toString();

				if (problemMarkers[i].getAttribute(IMarker.MESSAGE) != null)
					hashKey = hashKey + problemMarkers[i].getAttribute(IMarker.MESSAGE);

				if (!_problemMarkers.containsKey(hashKey)) {
					tempMap = new HashMap();
					tempMap.put(TargetBuilder.RESOURCE, problemMarkers[i].getResource());
					tempMap.put(TargetBuilder.ATTRIBUTES, problemMarkers[i].getAttributes());
					_problemMarkers.put(hashKey, tempMap);
				}
			}
		}
	}
}
