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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.FilterSet;
import org.apache.tools.ant.types.FilterSetCollection;
import org.apache.tools.ant.types.PatternSet;
import org.objectstyle.woproject.util.FileStringScanner;

/**
 * Subclass of ProjectFormat that defines file copying 
 * strategy for WOApplications.
 *
 * @author Andrei Adamchik
 */
public class AppFormat extends ProjectFormat {
	protected HashMap templateMap = new HashMap();
	protected HashMap filterMap = new HashMap();
	protected String appPaths;
	protected String frameworkPaths;
	protected String otherClasspaths;

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

		prepare52();
		prepareWindows();
		prepareUnix();
		prepareMac();

		// @todo - create web.xml and/or classpath files

		// add Info.plist
		String infoFile =
			new File(getApplicatonTask().contentsDir(), "Info.plist").getPath();
		createMappings(
			infoFile,
			woappPlusVersion() + "/Info.plist",
			infoFilter(null));
		// add Info.plist
		String webXMLFile =
			new File(getApplicatonTask().contentsDir(), "web.xml").getPath();
		createMappings(
			webXMLFile,
			woappPlusVersion() + "/ServletEnv_web.xml",
			webXMLFilter());
	}

	/**
	 * Prepares all path values needed for substitutions.
	 */
	private void preparePaths() {
		appPaths = buildAppPaths();
		frameworkPaths = buildFrameworkPaths();
		otherClasspaths = buildOtherClassPaths();
	}

	/**
	 * Prepares all path values needed for substitutions.
	 */
	private void prepare52() {
		if (this
			.getApplicatonTask()
			.getWOEnvironment()
			.wo52(this.getApplicatonTask().getProject())) {
			Copy cp = new Copy();
			//cp.setOwningTarget(getApplicatonTask().getProject().getDefaultTarget());
			cp.setProject(getApplicatonTask().getProject());
			cp.setTaskName("copy bootstrap");
			cp.setFile(
				this.getApplicatonTask().getWOEnvironment().bootstrap(
					this.getApplicatonTask().getProject()));
			cp.setTodir(getApplicatonTask().taskDir());
			cp.execute();
		}
	}
	/**
	 * Returns a String that consists of paths to the aplication jar. 
	 * File separator used is platform dependent
	 * and may need to be changed when creating files for multiple 
	 * platforms.
	 */
	protected String buildAppPaths() {
		FileSet fs = null;
		PatternSet.NameEntry include = null;
		DirectoryScanner ds = null;
		String[] files = null;
		StringBuffer buf = null;
		try {
			fs = new FileSet();
			fs.setDir(getApplicatonTask().contentsDir());
			include = fs.createInclude();
			include.setName("**/Resources/Java/**/*.jar");

			ds = fs.getDirectoryScanner(task.getProject());
			files = ds.getIncludedFiles();
			buf = new StringBuffer();

			// prepend the path with Resources/Java (for CompilerProxy support)
			buf
				.append("APPROOT")
				.append(File.separatorChar)
				.append("Resources")
				.append(File.separatorChar)
				.append("Java")
				.append(File.separatorChar)
				.append("\r\n");
			for (int i = 0; i < files.length; i++) {
				buf.append("APPROOT").append(File.separatorChar).append(
					files[i]).append(
					"\r\n");
			}
			return buf.toString();
		} catch (Exception anException) {
			log(anException.getMessage(), Project.MSG_WARN);
		} finally {
			fs = null;
			include = null;
			ds = null;
			files = null;
			buf = null;
		}
		return "";
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
		Project project = task.getProject();

		// track included jar files to avoid double entries
		Vector jarSet = new Vector();

		for (int i = 0; i < frameworkSets.size(); i++) {

			FrameworkSet fs = null;
			DirectoryScanner ds = null;
			String[] dirs = null;
			try {
				fs = (FrameworkSet) frameworkSets.get(i);
				// Don't bother checking if it's embedded.
				if (!fs.isReference()) {
					log(
						"fs.createPatternSet().getIncludePatterns(project) "
							+ fs.createPatternSet().getIncludePatterns(project),
						Project.MSG_VERBOSE);
					log(
						"fs.createPatternSet().getExcludePatterns(project) "
							+ fs.createPatternSet().getExcludePatterns(project),
						Project.MSG_VERBOSE);
				}
				if (true /*
													|| fs.hasSelectors()
													|| (fs.hasPatterns()
														&& fs.createPatternSet() != null
														&& (fs.createPatternSet().createInclude() != null
															&& fs.createPatternSet().createExclude() != null
															&& (fs.createPatternSet().getIncludePatterns(project)
																!= null
																|| fs.createPatternSet().getExcludePatterns(
																	project)
																	!= null)))*/
					) {
					if (fs.getEmbed()) {
						continue;
					}
					ds = fs.getDirectoryScanner(project);
					dirs = ds.getIncludedDirectories();

					for (int j = 0; j < dirs.length; j++) {
						File[] jars = fs.findJars(project, dirs[j]);

						if (jars == null || jars.length == 0) {
							log(
								"No Jars in " + dirs[j] + ", ignoring.",
								Project.MSG_VERBOSE);
							continue;
						}

						int jsize = jars.length;
						for (int k = 0; k < jsize; k++) {
							if (!jarSet.contains(jars[k]))
								jarSet.add(jars[k]);
						}
					}
				}
			} catch (Exception anException) {
				// directory doesn't exist or is not readable
				log(anException.getMessage(), Project.MSG_WARN);
			} finally {
				fs = null;
				ds = null;
				dirs = null;
			}
		}
		File aFile = null;
		try {
			for (int i = 0; i < jarSet.size(); i++) {
				aFile = (File) jarSet.elementAt(i);
				log(": Framework JAR " + aFile, Project.MSG_VERBOSE);
				buf
					.append(
						this
							.getApplicatonTask()
							.getWOEnvironment()
							.getWOVariables()
							.encodePathForFile(
							aFile))
					.append("\r\n");
			}
			return buf.toString();
		} catch (Exception anException) {
			// directory doesn't exist or is not readable
			log(anException.getMessage(), Project.MSG_WARN);
		} finally {
			aFile = null;
		}
		return "";
	}

	/**
	 * Method buildOtherClassPaths.
	 * @return String
	 */
	protected String buildOtherClassPaths() {
		StringBuffer buf = new StringBuffer();

		List classpathSets = getApplicatonTask().getOtherClasspath();
		Project project = task.getProject();

		// track included paths to avoid double entries
		HashSet pathSet = new HashSet();

		int size = classpathSets.size();
		try {
			for (int i = 0; i < size; i++) {

				OtherClasspathSet cs = (OtherClasspathSet) classpathSets.get(i);
				if (true /*cs.hasSelectors()
													|| (cs.hasPatterns()
														&& cs.createPatternSet() != null
														&& (cs.createPatternSet().createInclude() != null
															&& cs.createPatternSet().createExclude() != null
															&& (cs.createPatternSet().getIncludePatterns(project)
																!= null
																|| cs.createPatternSet().getExcludePatterns(
																	project)
																	!= null)))*/
					)
					cs.collectClassPaths(project, pathSet);
			}
		} catch (BuildException be) {
			// paths doesn't exist or are not readable
			log(be.getMessage(), Project.MSG_WARN);
		}
		if (pathSet.size() > 0) {
			Object someFiles[] = pathSet.toArray();
			size = someFiles.length;
			for (int i = 0; i < size; i++) {
				//log(": Framework JAR " + (File) someFiles[i], Project.MSG_VERBOSE);
				buf
					.append(
						this
							.getApplicatonTask()
							.getWOEnvironment()
							.getWOVariables()
							.encodePathForFile(
							(File) someFiles[i]))
					.append("\r\n");
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
			woappPlusVersion() + "/Contents/Windows/CLSSPATH.TXT",
			classpathFilter('\\'));
		String subp = new File(winDir, "SUBPATHS.TXT").getPath();
		createMappings(
			subp,
			woappPlusVersion() + "/Contents/Windows/SUBPATHS.TXT");
		// add run script to Win. directory
		String runScript = new File(winDir, getName() + ".cmd").getPath();
		createMappings(
			runScript,
			woappPlusVersion() + "/Contents/Windows/appstart.cmd");
		// add run script to top-level directory
		File taskDir = getApplicatonTask().taskDir();
		String topRunScript = new File(taskDir, getName() + ".cmd").getPath();
		createMappings(
			topRunScript,
			woappPlusVersion() + "/Contents/Windows/appstart.cmd");
	}

	/** 
	 * Prepare mappings for UNIX subdirectory. 
	 */
	private void prepareUnix() {
		File dir = new File(getApplicatonTask().contentsDir(), "UNIX");
		String cp = new File(dir, "UNIXClassPath.txt").getPath();
		createMappings(
			cp,
			woappPlusVersion() + "/Contents/UNIX/UNIXClassPath.txt",
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
			woappPlusVersion() + "/Contents/MacOS/MacOSClassPath.txt",
			classpathFilter('/'));
		String servercp =
			new File(macDir, "MacOSXServerClassPath.txt").getPath();
		createMappings(
			servercp,
			woappPlusVersion() + "/Contents/MacOS/MacOSXServerClassPath.txt",
			classpathFilter('/'));
		// add run script to Mac directory
		String runScript = new File(macDir, getName()).getPath();
		createMappings(
			runScript,
			woappPlusVersion() + "/Contents/MacOS/appstart");
		// add run script to top-level directory
		File taskDir = getApplicatonTask().taskDir();
		String topRunScript = new File(taskDir, getName()).getPath();
		createMappings(
			topRunScript,
			woappPlusVersion() + "/Contents/MacOS/appstart");
	}

	/** 
	 * Creates a filter for Classpath helper files.
	 */
	private FilterSet classpathFilter(char pathSeparator) {
		FilterSet filter = new FilterSet();
		if (pathSeparator == File.separatorChar) {
			filter.addFilter("APP_JAR", appPaths);
			filter.addFilter("FRAMEWORK_JAR", frameworkPaths);
			filter.addFilter("OTHER_PATHS", otherClasspaths);
		} else {
			filter.addFilter(
				"APP_JAR",
				appPaths.replace(File.separatorChar, pathSeparator));
			filter.addFilter(
				"FRAMEWORK_JAR",
				frameworkPaths.replace(File.separatorChar, pathSeparator));
			filter.addFilter(
				"OTHER_PATHS",
				otherClasspaths.replace(File.separatorChar, pathSeparator));
		}

		return filter;
	}

	/**
	 * Method getAppClass.
	 * @return String
	 */
	private String getAppClass() {
		return task.getPrincipalClass();
	}

	/**
	 * Method createMappings.
	 * @param fileName
	 * @param template
	 * @param filter
	 */
	private void createMappings(
		String fileName,
		String template,
		FilterSet filter) {
		FilterSetCollection fsCollection = new FilterSetCollection(filter);
		FilterSet additionalBuildSettingsFilter =
			additionalBuildSettingsFilter();

		filter.addFilter("APP_CLASS", getAppClass());
		filter.addFilter("JAR_NAME", getJarName());

		if (additionalBuildSettingsFilter != null) {
			fsCollection.addFilterSet(additionalBuildSettingsFilter);
		}

		createMappings(fileName, template, fsCollection);
	}

	/**
	 * Method createMappings.
	 * @param fileName
	 * @param template
	 */
	private void createMappings(String fileName, String template) {
		createMappings(fileName, template, (FilterSetCollection) null);
	}

	/**
	 * Method createMappings.
	 * @param fileName
	 * @param template
	 * @param filter
	 */
	private void createMappings(
		String fileName,
		String template,
		FilterSetCollection filter) {
		templateMap.put(fileName, template);
		filterMap.put(fileName, filter);
	}

	/**
	 * Method getApplicatonTask.
	 * @return WOApplication
	 */
	private WOApplication getApplicatonTask() {
		return (WOApplication) task;
	}

	/**
	 * @see org.objectstyle.woproject.ant.ProjectFormat#fileIterator()
	 */
	public Iterator fileIterator() {
		return templateMap.keySet().iterator();
	}

	/**
	 * @see org.objectstyle.woproject.ant.ProjectFormat#templateForTarget(java.lang.String)
	 */
	public String templateForTarget(String targetName) throws BuildException {
		String template = (String) templateMap.get(targetName);
		if (template == null) {
			throw new BuildException(
				"Invalid target, no template found: " + targetName);
		}
		return template;
	}

	/**
	 * @see org.objectstyle.woproject.ant.ProjectFormat#filtersForTarget(java.lang.String)
	 */
	public FilterSetCollection filtersForTarget(String targetName)
		throws BuildException {

		if (!filterMap.containsKey(targetName)) {
			throw new BuildException("Invalid target: " + targetName);
		}
		return (FilterSetCollection) filterMap.get(targetName);
	}

	/**
	 * Method woappPlusVersion returns the template name.
	 * @return String
	 */
	public String woappPlusVersion() {
		if (this
			.getApplicatonTask()
			.getWOEnvironment()
			.wo5or51(this.getApplicatonTask().getProject()))
			return "woapp";
		return "woapp_52";
	}

	private FilterSet additionalBuildSettingsFilter() {
		String jvmOptions = getApplicatonTask().getJvmOptions();
		if (jvmOptions != null) {
			FilterSet filter = new FilterSet();
			filter.addFilter("JVM_OPTIONS", jvmOptions);
			return filter;
		}

		return null;
	}

	/**
	 * 
	 */
	public void release() {
		super.release();
	}

	/** 
	 * Returns a FilterSet that can be used to build web.xml file. 
	 */
	public FilterSetCollection webXMLFilter() {
		FilterSet filter = new FilterSet();
		String WEBINFROOT = "WEBINFROOT";
		String paths = "";
		if (appPaths != null && appPaths.length() > 0) {
			paths = paths + WEBINFROOT;
			paths =
				paths
					+ FileStringScanner.replace(
						appPaths,
						"\n",
						"\n" + WEBINFROOT);
		}
		if (frameworkPaths != null && frameworkPaths.length() > 0) {
			paths = paths + WEBINFROOT;
			paths =
				paths
					+ FileStringScanner.replace(
						frameworkPaths,
						"\n",
						"\n" + WEBINFROOT);
		}
		if (otherClasspaths != null && otherClasspaths.length() > 0) {
			paths = paths + WEBINFROOT;
			paths =
				paths
					+ FileStringScanner.replace(
						otherClasspaths,
						"\n",
						"\n" + WEBINFROOT);
		}

		WOApplication woappTask = (WOApplication) this.task;
		filter.addFilter("WOROOT", woappTask.getWebXML_WOROOT());
		filter.addFilter("LOCALROOT", woappTask.getWebXML_LOCALROOT());
		filter.addFilter(
			"WOAINSTALLROOT",
			woappTask.getWebXML_WOAINSTALLROOT());
		filter.addFilter("WOAppMode", woappTask.getWebXML_WOAppMode());
		filter.addFilter("WOClassPath", paths);
		filter.addFilter("WOApplicationClass", this.getAppClass());
		filter.addFilter("WOtaglib", woappTask.getWebXML_WOtaglib());

		return new FilterSetCollection(filter);
	}
}
