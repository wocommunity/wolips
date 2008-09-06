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
 */
package org.objectstyle.wolips.jdt.classpath.model;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.objectstyle.woenvironment.frameworks.IFramework;
import org.objectstyle.wolips.jdt.classpath.WOFrameworkClasspathContainer;

public interface IEclipseFramework extends IFramework {
	public List<IClasspathEntry> getClasspathEntries();

	public static class Utility {
		public static void addFrameworksToProject(List<IEclipseFramework> frameworksToAdd, IJavaProject javaProject, boolean reload) throws JavaModelException {
			IClasspathEntry[] existingEntries;
			if (reload) {
				existingEntries = javaProject.readRawClasspath();
			} else {
				existingEntries = javaProject.getRawClasspath();
			}

			List<IClasspathEntry> newEntries = new LinkedList<IClasspathEntry>();
			for (IClasspathEntry existingEntry : existingEntries) {
				newEntries.add(existingEntry);
			}

			boolean addedFramework = false;
			for (IEclipseFramework newFramework : frameworksToAdd) {
				addedFramework |= IEclipseFramework.Utility.addFrameworkToProject(newFramework, javaProject, newEntries);
			}
			if (addedFramework) {
				javaProject.setRawClasspath(newEntries.toArray(new IClasspathEntry[newEntries.size()]), null);
			}
		}

		public static void addFrameworkToProject(IEclipseFramework frameworkToAdd, IJavaProject javaProject, boolean reload) throws JavaModelException {
			IClasspathEntry[] existingEntries;
			if (reload) {
				existingEntries = javaProject.readRawClasspath();
			} else {
				existingEntries = javaProject.getRawClasspath();
			}

			List<IClasspathEntry> newEntries = new LinkedList<IClasspathEntry>();
			for (IClasspathEntry existingEntry : existingEntries) {
				newEntries.add(existingEntry);
			}

			boolean addedFramework = IEclipseFramework.Utility.addFrameworkToProject(frameworkToAdd, javaProject, newEntries);
			if (addedFramework) {
				javaProject.setRawClasspath(newEntries.toArray(new IClasspathEntry[newEntries.size()]), null);
			}
		}

		public static boolean addFrameworkToProject(IEclipseFramework frameworkToAdd, IJavaProject javaProject, List<IClasspathEntry> existingEntries) throws JavaModelException {
			boolean addFramework = true;
			String name = frameworkToAdd.getName();
			for (IClasspathEntry existingEntry : existingEntries) {
				WOFrameworkClasspathContainer frameworkContainer = WOFrameworkClasspathContainer.getFrameworkClasspathContainer(javaProject, existingEntry);
				if (frameworkContainer != null) {
					IFramework framework = frameworkContainer.getFramework();
					if (framework.getName().equals(name)) {
						addFramework = false;
					}
				}
			}

			if (addFramework) {
				WOFrameworkClasspathContainer newContainer = new WOFrameworkClasspathContainer(frameworkToAdd);
				IClasspathEntry newEntry = JavaCore.newContainerEntry(newContainer.getPath());
				existingEntries.add(newEntry);
			}

			return addFramework;
		}

		public static void removeFrameworksFromProject(List<IEclipseFramework> frameworksToRemove, IJavaProject javaProject, boolean reload) throws JavaModelException {
			IClasspathEntry[] existingEntries;
			if (reload) {
				existingEntries = javaProject.readRawClasspath();
			} else {
				existingEntries = javaProject.getRawClasspath();
			}

			List<IClasspathEntry> newEntries = new LinkedList<IClasspathEntry>();
			for (IClasspathEntry existingEntry : existingEntries) {
				newEntries.add(existingEntry);
			}

			boolean removedFramework = false;
			for (IEclipseFramework newFramework : frameworksToRemove) {
				removedFramework |= IEclipseFramework.Utility.removeFrameworkFromProject(newFramework, javaProject, newEntries);
			}
			if (removedFramework) {
				javaProject.setRawClasspath(newEntries.toArray(new IClasspathEntry[newEntries.size()]), null);
			}
		}

		public static void removeFrameworkFromProject(IEclipseFramework frameworkToRemove, IJavaProject javaProject, boolean reload) throws JavaModelException {
			IClasspathEntry[] existingEntries;
			if (reload) {
				existingEntries = javaProject.readRawClasspath();
			} else {
				existingEntries = javaProject.getRawClasspath();
			}

			List<IClasspathEntry> newEntries = new LinkedList<IClasspathEntry>();
			for (IClasspathEntry existingEntry : existingEntries) {
				newEntries.add(existingEntry);
			}

			boolean removedFramework = IEclipseFramework.Utility.removeFrameworkFromProject(frameworkToRemove, javaProject, newEntries);
			if (removedFramework) {
				javaProject.setRawClasspath(newEntries.toArray(new IClasspathEntry[newEntries.size()]), null);
			}
		}

		public static boolean removeFrameworkFromProject(IEclipseFramework frameworkToRemove, IJavaProject javaProject, List<IClasspathEntry> existingEntries) throws JavaModelException {
			IClasspathEntry removeEntry = null;
			String name = frameworkToRemove.getName();
			for (IClasspathEntry existingEntry : existingEntries) {
				WOFrameworkClasspathContainer frameworkContainer = WOFrameworkClasspathContainer.getFrameworkClasspathContainer(javaProject, existingEntry);
				if (frameworkContainer != null) {
					IFramework framework = frameworkContainer.getFramework();
					if (framework.getName().equals(name)) {
						removeEntry = existingEntry;
					}
				}
			}

			if (removeEntry != null) {
				existingEntries.remove(removeEntry);
			}

			return removeEntry != null;
		}

		public static List<IEclipseFramework> getFrameworks(IJavaProject javaProject) throws JavaModelException {
			List<IEclipseFramework> frameworks = new LinkedList<IEclipseFramework>();
			IClasspathEntry[] classpathEntries = javaProject.getRawClasspath();
			for (IClasspathEntry classpathEntry : classpathEntries) {
				WOFrameworkClasspathContainer frameworkContainer = WOFrameworkClasspathContainer.getFrameworkClasspathContainer(javaProject, classpathEntry);
				if (frameworkContainer != null) {
					IEclipseFramework framework = frameworkContainer.getFramework();
					frameworks.add(framework);
				}
			}
			return frameworks;
		}
	}
}