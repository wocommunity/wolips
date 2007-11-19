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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.StandardClasspathProvider;
import org.objectstyle.wolips.core.resources.types.project.IProjectAdapter;
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
		Set<IPath> allProjectArchiveEntries = new HashSet<IPath>();
		
		Map<String, IPath> addedFramework = new HashMap<String, IPath>();
		List<IRuntimeClasspathEntry> pendingResult = new LinkedList<IRuntimeClasspathEntry>();
		IRuntimeClasspathEntry[] originalResult = super.resolveClasspath(entries, configuration);
		for (IRuntimeClasspathEntry entry : originalResult) {
			IPath entryPath = entry.getPath();
			int frameworkSegment = frameworkSegmentForPath(entryPath);
			boolean addEntry = false;
			if (frameworkSegment == -1) {
				// MS: If ".framework" isn't in the path and we have a project, then
				// put a "fake" entry in the framework list corresponding to the project.  This
				// prevents /Library/Framework versions of the framework from loading later on 
				// in the classpath.
				if (IRuntimeClasspathEntry.PROJECT == entry.getType()) {
					IProject project = (IProject) entry.getResource();
					String projectFrameworkName = frameworkNameForProject(project);
					addedFramework.put(projectFrameworkName, entryPath);
				}
				addEntry = true;
			}
			else {
				// MS: Otherwise, we have a regular framework path.  In this case, we
				// want to skip any jar that is coming from a different path for the 
				// framework than we have previously loaded.
				String frameworkName = entryPath.segment(frameworkSegment);
				IPath frameworkPath = entryPath.removeLastSegments(entryPath.segmentCount() - frameworkSegment - 1);
				IPath previousFrameworkPath = addedFramework.get(frameworkName);
				if (previousFrameworkPath == null) {
					addEntry = true;
					addedFramework.put(frameworkName, frameworkPath);
				}
				else if (previousFrameworkPath.equals(frameworkPath)) {
					addEntry = true;
				}
			}
			
			// MS: ... all the stars have aligned, and this is a valid entry.  Lets add it.
			if (addEntry) {
				IPath projectArchive = getWOJavaArchive(entry);
				// MS: We need to get the build/BuiltFramework.framework folder from
				// a project and add that instead of the bin folder ...
				if (projectArchive != null) {
					if (!allProjectArchiveEntries.contains(projectArchive)) {
						IRuntimeClasspathEntry resolvedEntry = JavaRuntime.newArchiveRuntimeClasspathEntry(projectArchive);
						pendingResult.add(resolvedEntry);
						allProjectArchiveEntries.add(projectArchive);
					}
				} else {
					pendingResult.add(entry);
				}
			}
		}

		// sort classpath: project in front, then frameworks, then apple frameworks, then the rest
		List<IRuntimeClasspathEntry> otherJars = new ArrayList<IRuntimeClasspathEntry>();
		List<IRuntimeClasspathEntry> noAppleJars = new ArrayList<IRuntimeClasspathEntry>();
		List<IRuntimeClasspathEntry> appleJars = new ArrayList<IRuntimeClasspathEntry>();
		List<IRuntimeClasspathEntry> projects = new ArrayList<IRuntimeClasspathEntry>();
		List<IRuntimeClasspathEntry> woa = new ArrayList<IRuntimeClasspathEntry>();
		for (IRuntimeClasspathEntry entry : pendingResult) {
			if (IRuntimeClasspathEntry.PROJECT == entry.getType()) {
				projects.add(entry);
			} else if (isAppleProvided(entry)) {
				appleJars.add(entry);
			} else if (isFrameworkJar(entry)) {
				noAppleJars.add(entry);
			} else if (isBuildProject(entry)) {
				noAppleJars.add(entry);
			} else if (isWoa(entry)) {
				woa.add(entry);
			} else {
				otherJars.add(entry);
			}
		}
		
		ArrayList<IRuntimeClasspathEntry> sortedEntries = new ArrayList<IRuntimeClasspathEntry>();
		if (woa.size() > 0) {
			sortedEntries.addAll(woa);
		}
		if (projects.size() > 0) {
			sortedEntries.addAll(projects);
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
//		for (IRuntimeClasspathEntry entry : sortedEntries) {
//			System.out.println("WORuntimeClasspathProvider.resolveClasspath: final = " + entry);
//		}
		return sortedEntries.toArray(new IRuntimeClasspathEntry[sortedEntries.size()]);
	}
	
	// MS: This is a total hack ... It should use
	// the WOLips API to framework name.  For most, I think it works
	// out, and in particular, for Wonder it does. 
	protected String frameworkNameForProject(IProject project) {
		return project.getName() + ".framework";
	}
	
	protected int frameworkSegmentForPath(IPath path) {
		int frameworkSegment = -1;
		for (int segmentNum = 0; frameworkSegment == -1 && segmentNum < path.segmentCount(); segmentNum ++) {
			String segment = path.segment(segmentNum);
			if (segment.endsWith(".framework")) {
				frameworkSegment = segmentNum;
			}
		}
		return frameworkSegment;
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
			String pattern = "(?i).*?/(\\w+)\\.framework/Resources/Java/\\1.jar";
			if (location.replace('\\', '/').matches(pattern)) {
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
		IPath woJavaArchivePath = null;
		if (IRuntimeClasspathEntry.PROJECT == entry.getType()) {
			IProject project = (IProject) entry.getResource();
			IProjectAdapter projectAdapter = (IProjectAdapter) project.getAdapter(IProjectAdapter.class);
			if (projectAdapter != null) {
				woJavaArchivePath = projectAdapter.getWOJavaArchive();
			}
			else {
				woJavaArchivePath = null;
			}
		}
		return woJavaArchivePath;
	}
}
