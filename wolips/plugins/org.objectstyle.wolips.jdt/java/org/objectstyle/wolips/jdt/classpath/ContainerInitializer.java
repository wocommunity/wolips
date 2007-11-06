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

import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.progress.IProgressService;
import org.objectstyle.wolips.jdt.JdtPlugin;
import org.objectstyle.wolips.jdt.classpath.model.Framework;

/**
 * @author ulrich
 */
public class ContainerInitializer extends ClasspathContainerInitializer {

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
			//convert old container
			final String OLD_WOLIPS_CLASSPATH_CONTAINER_IDENTITY = "org.objectstyle.wolips.WO_CLASSPATH";
			if (firstSegment.startsWith(OLD_WOLIPS_CLASSPATH_CONTAINER_IDENTITY)) {
				final WorkspaceModifyOperation op = new WorkspaceModifyOperation(project.getProject()) {

					public void execute(IProgressMonitor monitor) throws CoreException {
						IClasspathEntry[] classpathEntries = project.getRawClasspath();
						IClasspathEntry[] newClasspathEntries = new IClasspathEntry[classpathEntries.length];
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
								newClasspathEntries[i] = newClasspathEntry;
							} else {
								newClasspathEntries[i] = classpathEntry;
							}
						}
						project.setRawClasspath(newClasspathEntries, monitor);
					}

				};

				Display.getDefault().asyncExec(new Runnable() {

					public void run() {
						IProgressService service = PlatformUI.getWorkbench().getProgressService();

						// run the classpath update operation
						try {
							service.run(true, true, op);
						} catch (InterruptedException e) {
							return;
						} catch (final InvocationTargetException e) {

							// ie.- one of the steps resulted in a core
							// exception
							Throwable t = e.getTargetException();
							if (t instanceof CoreException) {
								if (((CoreException) t).getStatus().getCode() == IResourceStatus.CASE_VARIANT_EXISTS) {
									MessageDialog.openError(null, "WebObjects Frameworks Error while updating classpath", project.getProject().getName());
								} else {
									ErrorDialog.openError(null, "WebObjects Frameworks", null, // no
											// special
											// message
											((CoreException) t).getStatus());
								}
							} else {
								// CoreExceptions are handled above, but
								// unexpected
								// runtime
								// exceptions and errors may still occur.
								IDEWorkbenchPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, IDEWorkbenchPlugin.IDE_WORKBENCH, 0, t.toString(), t));
								MessageDialog.openError(null, "WebObjects Frameworks Error while updating classpath", t.getMessage());
							}
						}
					}
				});
			}
		}
	}
}