/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2004 The ObjectStyle Group 
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
 *//*
 * Created on 12.06.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.objectstyle.wolips.jdt.classpath;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.actions.WorkbenchRunnableAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.objectstyle.wolips.jdt.JdtPlugin;
import org.objectstyle.wolips.jdt.classpath.model.Framework;

/**
 * @author ulrich
 */
public class ContainerInitializer extends ClasspathContainerInitializer {
	public static final String OLD_WOLIPS_CLASSPATH_CONTAINER_IDENTITY = "org.objectstyle.wolips.WO_CLASSPATH";

	/**
	 * The default Constructor
	 */
	public ContainerInitializer() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.core.ClasspathContainerInitializer#canUpdateClasspathContainer(org.eclipse.core.runtime.IPath,
	 *      org.eclipse.jdt.core.IJavaProject)
	 */
	public boolean canUpdateClasspathContainer(IPath containerPath, IJavaProject project) {
		return super.canUpdateClasspathContainer(containerPath, project);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.core.ClasspathContainerInitializer#getComparisonID(org.eclipse.core.runtime.IPath,
	 *      org.eclipse.jdt.core.IJavaProject)
	 */
	public Object getComparisonID(IPath containerPath, IJavaProject project) {
		return containerPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.core.ClasspathContainerInitializer#getDescription(org.eclipse.core.runtime.IPath,
	 *      org.eclipse.jdt.core.IJavaProject)
	 */
	public String getDescription(IPath containerPath, IJavaProject project) {
		return super.getDescription(containerPath, project);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.core.ClasspathContainerInitializer#requestClasspathContainerUpdate(org.eclipse.core.runtime.IPath,
	 *      org.eclipse.jdt.core.IJavaProject,
	 *      org.eclipse.jdt.core.IClasspathContainer)
	 */
	public void requestClasspathContainerUpdate(IPath containerPath, IJavaProject project, IClasspathContainer containerSuggestion) throws CoreException {
		super.requestClasspathContainerUpdate(containerPath, project, containerSuggestion);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.core.ClasspathContainerInitializer#initialize(org.eclipse.core.runtime.IPath,
	 *      org.eclipse.jdt.core.IJavaProject)
	 */
	public void initialize(final IPath containerPath, final IJavaProject project) throws CoreException {
		int size = containerPath.segmentCount();
		if (size > 0) {
			String firstSegment = containerPath.segment(0);
			// current classpath container
			if (firstSegment.startsWith(Container.CONTAINER_IDENTITY)) {
				ContainerEntries containerEntries = null;
				try {
					containerEntries = ContainerEntries.initWithPath(containerPath.removeFirstSegments(1));
				} catch (PathCoderException e) {
					JdtPlugin.getDefault().getPluginLogger().log(e);
				}
				if (containerEntries != null) {
					IClasspathContainer classpathContainer = new Container(containerEntries);
					JavaCore.setClasspathContainer(containerPath, new IJavaProject[] { project }, new IClasspathContainer[] { classpathContainer }, null);
				}
			}
			// convert old container
			else if (firstSegment.startsWith(ContainerInitializer.OLD_WOLIPS_CLASSPATH_CONTAINER_IDENTITY)) {
				convertOldClasspathContainer(project, containerPath);
			}
		}
	}

	protected void convertOldClasspathContainer(IJavaProject javaProject, IPath containerPath) throws JavaModelException {
		System.out.println("ContainerInitializer.convertOldClasspathContainer: UPGRADING " + javaProject);
		IClasspathEntry[] classpathEntries = javaProject.getRawClasspath();
		List<IClasspathEntry> newClasspathEntries = new LinkedList<IClasspathEntry>();
		for (int i = 0; i < classpathEntries.length; i++) {
			IClasspathEntry classpathEntry = classpathEntries[i];
			String firstSegementOfEntry = classpathEntry.getPath().segment(0);
			if (firstSegementOfEntry != null && firstSegementOfEntry.startsWith(OLD_WOLIPS_CLASSPATH_CONTAINER_IDENTITY)) {
				Container container = new Container(null);
				ArrayList<Framework> frameworks = new ArrayList<Framework>();
				for (int j = 1; j < containerPath.segmentCount(); j++) {
					String segment = containerPath.segment(j);
					Framework framework = JdtPlugin.getDefault().getClasspathModel().getFrameworkWithName(segment);
					if (framework != null) {
						frameworks.add(framework);
					}
				}
				container.setContent(frameworks.toArray(new Framework[frameworks.size()]));
				IPath newContainerPath = container.getPath();
				IClasspathEntry newClasspathEntry = JavaCore.newContainerEntry(newContainerPath);
				newClasspathEntries.add(newClasspathEntry);
			} else {
				newClasspathEntries.add(classpathEntry);
			}
		}

		updateRawClasspath(javaProject, newClasspathEntries);
	}

	protected void updateRawClasspath(final IJavaProject javaProject, final List<IClasspathEntry> newClasspathEntries) {
		final IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				javaProject.setRawClasspath(newClasspathEntries.toArray(new IClasspathEntry[newClasspathEntries.size()]), monitor);
			}
		};
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				try {
					PlatformUI.getWorkbench().getProgressService().run(true, true, new WorkbenchRunnableAdapter(runnable));
				} catch (InvocationTargetException e) {
					JdtPlugin.getDefault().getPluginLogger().log(e);
				} catch (InterruptedException e) {
					JdtPlugin.getDefault().getPluginLogger().log(e);
				}
			}
		});
	}
}