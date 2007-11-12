/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2002 - 2007 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *  3. The end-user documentation included with the redistribution, if any,
 * must include the following acknowlegement: "This product includes software
 * developed by the ObjectStyle Group (http://objectstyle.org/)." Alternately,
 * this acknowlegement may appear in the software itself, if and wherever such
 * third-party acknowlegements normally appear.
 *  4. The names "ObjectStyle Group" and "Cayenne" must not be used to endorse
 * or promote products derived from this software without prior written
 * permission. For written permission, please contact andrus@objectstyle.org.
 *  5. Products derived from this software may not be called "ObjectStyle" nor
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
 * This software consists of voluntary contributions made by many individuals
 * on behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/> .
 *  
 */

package org.objectstyle.wolips.launching.classpath;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.StandardClasspathProvider;
import org.objectstyle.wolips.core.resources.types.project.IProjectAdapter;
import org.objectstyle.wolips.datasets.project.WOLipsCore;
import org.objectstyle.wolips.variables.VariablesPlugin;

/**
 * @author hn3000
 * @author uli
 */
public class WORuntimeClasspathProvider extends StandardClasspathProvider {

	public final static String ID = "org.objectstyle.wolips.launching.WORuntimeClasspathProvider";

	public final static String OLD_ID = "org.objectstyle.wolips.launching.classpath.WORuntimeClasspathProvider";

	public final static String VERY_OLD_ID = "org.objectstyle.wolips.launching.WORuntimeClasspath";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.launching.IRuntimeClasspathProvider#computeUnresolvedClasspath(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public IRuntimeClasspathEntry[] computeUnresolvedClasspath(ILaunchConfiguration configuration) throws CoreException {
		return super.computeUnresolvedClasspath(configuration);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.launching.IRuntimeClasspathProvider#resolveClasspath(org.eclipse.jdt.launching.IRuntimeClasspathEntry[],
	 *      org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public IRuntimeClasspathEntry[] resolveClasspath(IRuntimeClasspathEntry[] entries, ILaunchConfiguration configuration) throws CoreException {

		List<IRuntimeClasspathEntry> others = new ArrayList<IRuntimeClasspathEntry>();
		List<IRuntimeClasspathEntry> resolved = new ArrayList<IRuntimeClasspathEntry>();
		List<IProject> projects = new ArrayList<IProject>();

		// used for duplicate removal
		Set<String> allProjectArchiveEntries = new HashSet<String>();

		// looks like we need to let super do it's thing before
		// we start tinkering with things ourselves
		
		IRuntimeClasspathEntry[] result = super.resolveClasspath(entries, configuration);
		// resolve WO framework/application projects ourselves, let super do the
		// rest
		for (int i = 0; i < result.length; ++i) {
			IRuntimeClasspathEntry entry = result[i];
			if (IRuntimeClasspathEntry.PROJECT == entry.getType()) {
				IProject project = (IProject) entry.getResource();
				projects.add(project);
			}
			IPath projectArchive = getWOJavaArchive(entry);
			if (projectArchive != null) {
				// I think this line here breaks things: (hn3000)
				// resolved.add(entry);
				if (!allProjectArchiveEntries.contains(projectArchive.toString())) {
					IRuntimeClasspathEntry resolvedEntry = JavaRuntime.newArchiveRuntimeClasspathEntry(projectArchive);
					resolved.add(resolvedEntry);
					allProjectArchiveEntries.add(projectArchive.toString());
				}
				allProjectArchiveEntries.add(entry.toString());
			} else {
				others.add(entry);
			}
		}

		// used for duplicate removal
		Set<IPath> allOtherEntries = new HashSet<IPath>();
		// remove duplicates from the resulting classpath
		if (others.size() != 0) {
			IRuntimeClasspathEntry oe[] = others.toArray(new IRuntimeClasspathEntry[others.size()]);

			for (int i = 0; i < oe.length; ++i) {
				IRuntimeClasspathEntry entry = oe[i];
				String ls = entry.getLocation();
				IPath loc = (null == ls) ? null : new Path(ls);
				if (null == loc) {
					resolved.add(entry);
				} else {
					if (!allOtherEntries.contains(loc)) {
						String lastSegment = loc.lastSegment();
						if (lastSegment != null && !isProjectJar(lastSegment, projects)) {
							resolved.add(entry);
							allOtherEntries.add(loc);
						}
					}
				}
			}
		}
		result = resolved.toArray(new IRuntimeClasspathEntry[resolved.size()]);
		
		// sort classpath: project in front, then frameworks, then apple frameworks, then the rest
		ArrayList<IRuntimeClasspathEntry> otherJars = new ArrayList<IRuntimeClasspathEntry>();
		ArrayList<IRuntimeClasspathEntry> noAppleJars = new ArrayList<IRuntimeClasspathEntry>();
		ArrayList<IRuntimeClasspathEntry> appleJars = new ArrayList<IRuntimeClasspathEntry>();
		IRuntimeClasspathEntry woa = null;
		for (int i = 0; i < result.length; i++) {
			IRuntimeClasspathEntry runtimeClasspathEntry = result[i];

			if (isAppleProvided(runtimeClasspathEntry)) {
				appleJars.add(runtimeClasspathEntry);
			} else if (isFrameworkJar(runtimeClasspathEntry)) {
				noAppleJars.add(runtimeClasspathEntry);
			} else if (isBuildProject(runtimeClasspathEntry)) {
				noAppleJars.add(runtimeClasspathEntry);
			} else if (isWoa(runtimeClasspathEntry)) {
				woa = runtimeClasspathEntry;
			} else {
				otherJars.add(runtimeClasspathEntry);
			}
		}
		
		ArrayList<IRuntimeClasspathEntry> sortedEntries = new ArrayList<IRuntimeClasspathEntry>();
		if (woa != null) {
			sortedEntries.add(woa);
		}
		if (noAppleJars.size() > 0) {
			sortedEntries.addAll(noAppleJars);
		}
		if (appleJars.size() > 0) {
			sortedEntries.addAll(appleJars);
		}
		if (otherJars.size() > 0) {
			sortedEntries.addAll(otherJars);
		}
		result = sortedEntries.toArray(new IRuntimeClasspathEntry[sortedEntries.size()]);
		return result;
	}

	private boolean isAppleProvided(IRuntimeClasspathEntry runtimeClasspathEntry) {
		String location = runtimeClasspathEntry.getLocation();
		if (location != null) {
			// check user settings (from wobuild.properties)
			IPath rootPath = VariablesPlugin.getDefault().getSystemRoot();
			if(rootPath != null && location.startsWith(rootPath.toString())) {
				return location.indexOf("JavaVM") < 0;
			}
			// check maven path (first french version)
			if (location.indexOf("webobjects" + File.separator + "apple") > 0) {
				return true;
			}
			// check maven path
			if (location.indexOf("apple" + File.separator + "webobjects") > 0) {
				return true;
			}
			if (location.indexOf("System" + File.separator + "Library") > 0) {
				return location.indexOf("JavaVM") < 0;
			}
			// check win path
			if (location.indexOf("Apple" + File.separator + "Library") > 0) {
				return true;
			}
		}
		return false;
	}

	private boolean isWoa(IRuntimeClasspathEntry runtimeClasspathEntry) {
		String location = runtimeClasspathEntry.getLocation();
		if (location != null) {
			if (location.indexOf(".woa") > 0) {
				return true;
			}
		}
		return false;
	}

	private boolean isBuildProject(IRuntimeClasspathEntry runtimeClasspathEntry) {
		String location = runtimeClasspathEntry.getLocation();
		if (location != null) {
			if (location.indexOf(File.separator + "build" + File.separator) > 0) {
				return true;
			}
		}
		return false;
	}

	private boolean isFrameworkJar(IRuntimeClasspathEntry runtimeClasspathEntry) {
		String location = runtimeClasspathEntry.getLocation();
		if (location != null) {
			String pattern = ".*?(\\w+)\\.framework" +File.separator + "Resources" +File.separator + "Java" +File.separator + "\\1.jar";
			if (location.toLowerCase().matches(pattern.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	private boolean isProjectJar(String lastSegment, List projects) {
		String lower = lastSegment.toLowerCase();
		for (int i = 0; i < projects.size(); i++) {
			IProject project = (IProject) projects.get(i);
			if (lower.startsWith((project.getName() + ".jar").toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	IPath getWOJavaArchive(IRuntimeClasspathEntry entry) throws CoreException {
		if (IRuntimeClasspathEntry.PROJECT == entry.getType()) {
			IProject project = (IProject) entry.getResource();
			IProjectAdapter projectAdapter = (IProjectAdapter) project.getAdapter(IProjectAdapter.class);
			return projectAdapter.getWOJavaArchive();
		}
		return null;
	}
}
