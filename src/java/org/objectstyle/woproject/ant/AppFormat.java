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
package org.objectstyle.woproject.ant;

import java.io.File;
import java.util.*;

import org.apache.tools.ant.*;
import org.apache.tools.ant.types.FilterSet;
import org.apache.tools.ant.types.FilterSetCollection;

/**
 * Subclass of ProjectFormat that defines file copying 
 * strategy for WOApplications.
 *
 * @author Andrei Adamchik
 */
public class AppFormat extends ProjectFormat {
	protected HashMap templateMap = new HashMap();
	protected HashMap filterMap = new HashMap();
	protected String appPath;
	protected String frameworkPaths;

	/** 
	 * Creates new AppFormat and initializes it with the name
	 * of the project being built.
	 */
	public AppFormat(WOTask task) {
		super(task);
		prepare();
	}

	/** 
	 * Builds a list of files for the application, 
	 * maps them to templates and filters. 
	 */
	private void prepare() {
		preparePaths();

		prepareWindows();
		prepareUnix();
		prepareMac();

		// @todo - create web.xml and/or classpath files

		// add Info.plist
		String infoFile =
			new File(getApplicatonTask().contentsDir(), "Info.plist").getPath();
		createMappings(infoFile, "woapp/Info.plist", infoFilter(null));
	}

	/**
	 * Prepares all path values needed for substitutions.
	 */
	private void preparePaths() {
		appPath = buildAppPath();
		frameworkPaths = buildFrameworkPaths();
	}

	/**
	 * Returns a String that consists of paths to the aplication jar. 
	 * File separator used is platform dependent
	 * and may need to be changed when creating files for multiple 
	 * platforms.
	 */
	protected String buildAppPath() {
		String name = getApplicatonTask().getName().toLowerCase() + ".jar";
		return "APPROOT"
			+ File.separator
			+ "Resources"
			+ File.separator
			+ "Java"
			+ File.separator
			+ name;
	}

	/** 
	 * Returns a String that consists of paths of all framework's jar's
	 * needed by the application. File separator used is platform dependent
	 * and may need to be changed when creating files for multiple 
	 * platforms.
	 */
	protected String buildFrameworkPaths() {
		StringBuffer buf = new StringBuffer();

		List frameworkSets = getApplicatonTask().getFrameworkSets();
		int size = frameworkSets.size();
		for (int i = 0; i < size; i++) {
			FrameworkSet fs = (FrameworkSet) frameworkSets.get(i);
			String root = fs.getRootPrefix();
			try {
				DirectoryScanner ds = fs.getDirectoryScanner(task.getProject());
				String[] dirs = ds.getIncludedDirectories();

				for (int j = 0; j < dirs.length; j++) {
					// using Windows line ending, since all templates have it anyway.
					// Please report any problems with that on other platforms
					buf.append(root).append(File.separatorChar).append(
						dirs[i]).append(
						"\r\n");
				}
			} catch (BuildException be) {
				// directory doesn't exist or is not readable
				log(be.getMessage(), Project.MSG_WARN);
			}
		}

		return buf.toString();
	}

	/** 
	 * Prepare mappings for Windows subdirectory. 
	 */
	private void prepareWindows() {
		File winDir = new File(getApplicatonTask().contentsDir(), "Windows");

		String cp = new File(winDir, "CLSSPATH.TXT").getPath();
		createMappings(
			cp,
			"woapp/Contents/Windows/CLSSPATH.TXT",
			classpathFilter('\\'));

		String subp = new File(winDir, "SUBPATHS.TXT").getPath();
		createMappings(subp, "woapp/Contents/Windows/SUBPATHS.TXT");

		// add run script to Win. directory
		String runScript = new File(winDir, getName() + ".cmd").getPath();
		createMappings(runScript, "woapp/Contents/Windows/appstart.cmd");

		// add run script to top-level directory
		File taskDir = getApplicatonTask().taskDir();
		String topRunScript = new File(taskDir, getName() + ".cmd").getPath();
		createMappings(topRunScript, "woapp/Contents/Windows/appstart.cmd");
	}

	/** 
	 * Prepare mappings for UNIX subdirectory. 
	 */
	private void prepareUnix() {
		File dir = new File(getApplicatonTask().contentsDir(), "UNIX");

		String cp = new File(dir, "UNIXClassPath.txt").getPath();
		createMappings(
			cp,
			"woapp/Contents/UNIX/UNIXClassPath.txt",
			classpathFilter('/'));
	}

	/** 
	 * Prepare mappings for MacOS subdirectory. 
	 */
	private void prepareMac() {
		File macDir = new File(getApplicatonTask().contentsDir(), "MacOS");

		String cp = new File(macDir, "MacOSClassPath.txt").getPath();
		createMappings(
			cp,
			"woapp/Contents/MacOS/MacOSClassPath.txt",
			classpathFilter('/'));

		String servercp =
			new File(macDir, "MacOSXServerClassPath.txt").getPath();
		createMappings(
			servercp,
			"woapp/Contents/MacOS/MacOSXServerClassPath.txt",
			classpathFilter('/'));

		// add run script to Mac directory
		String runScript = new File(macDir, getName()).getPath();
		createMappings(runScript, "woapp/Contents/MacOS/appstart");

		// add run script to top-level directory
		File taskDir = getApplicatonTask().taskDir();
		String topRunScript = new File(taskDir, getName()).getPath();
		createMappings(topRunScript, "woapp/Contents/MacOS/appstart");
	}

	/** 
	 * Creates a filter for Classpath helper files.
	 */
	private FilterSet classpathFilter(char pathSeparator) {
		FilterSet filter = new FilterSet();

		if (pathSeparator == File.separatorChar) {
			filter.addFilter("APP_JAR", appPath);
			filter.addFilter("FRAMEWORK_JAR", frameworkPaths);
		} else {
			filter.addFilter(
				"APP_JAR",
				appPath.replace(File.separatorChar, pathSeparator));
			filter.addFilter(
				"FRAMEWORK_JAR",
				frameworkPaths.replace(File.separatorChar, pathSeparator));
		}

		return filter;
	}

	private void createMappings(
		String fileName,
		String template,
		FilterSet filter) {
		createMappings(fileName, template, new FilterSetCollection(filter));
	}

	private void createMappings(String fileName, String template) {
		createMappings(fileName, template, (FilterSetCollection) null);
	}

	private void createMappings(
		String fileName,
		String template,
		FilterSetCollection filter) {
		templateMap.put(fileName, template);
		filterMap.put(fileName, filter);
	}

	private WOApplication getApplicatonTask() {
		return (WOApplication) task;
	}

	public Iterator fileIterator() {
		return templateMap.keySet().iterator();
	}

	public String templateForTarget(String targetName) throws BuildException {
		String template = (String) templateMap.get(targetName);
		if (template == null) {
			throw new BuildException(
				"Invalid target, no template found: " + targetName);
		}
		return template;
	}

	public FilterSetCollection filtersForTarget(String targetName)
		throws BuildException {

		if (!filterMap.containsKey(targetName)) {
			throw new BuildException("Invalid target: " + targetName);
		}
		return (FilterSetCollection) filterMap.get(targetName);
	}
}