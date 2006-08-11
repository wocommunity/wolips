/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2002 - 2005 The ObjectStyle Group
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
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.selectors.SelectorUtils;

/**
 * A subclass of FileSet that with special support for matching WOFrameworks.
 * 
 * @author Andrei Adamchik
 */
public class FrameworkSet extends FileSet {

	protected boolean embed;

	protected File deploymentDir;

	protected String ifCondition = "";

	/**
	 * Creates new FrameworkSet.
	 */
	public FrameworkSet() {
		super();
	}

	/**
	 * Sets the deployment root directory (can be different from the normal
	 * <code>root</code> specified). The idea is that you can specify the
	 * compile time path via <code>dir=/some_binary_release_path/</code>, but
	 * still end up with <code>LOCALROOT/Library/Frameworks/Bar.framework</code>
	 * as the prefix.
	 * 
	 * @param root
	 */
	public void setDeploymentDir(File root) {
		this.deploymentDir = root;
	}

	public File getDeployedFile(File file) {
		File result = file;
		if (this.deploymentDir != null && !getEmbed()) {
			// maps
			// foo/bar/Baz.framework/Resources/Java/xxx.jar ->
			// /System/Library/Frameworks/Baz.framework
			String oldPath = file.getPath();
			String newRoot = deploymentDir.getPath();
			String newPath = oldPath.replaceFirst("(.*?)(/\\w+\\.framework/)",
					newRoot + "$2");
			System.out.println(oldPath + "->" + newPath);
			result = new File(newPath);
		}
		return result;
	}

	/**
	 * Sets root directory of this FileSet based on a symbolic name, that can be
	 * "wo.homeroot", "wo.woroot", "wo.localroot". Throws BuildException if an
	 * invalid root is specified.
	 * 
	 * @deprecated since WOProject 1.1, use {@link #setDir(File)}.
	 */
	public void setRoot(File root) throws BuildException {
		setDir(root);
	}

	public void setEmbed(boolean flag) {
		this.embed = flag;
	}

	public boolean getEmbed() {
		if (isReference() && getProject() != null) {
			return ((FrameworkSet) getRef(getProject())).getEmbed();
		}
		return this.embed;
	}

	public void setIf(String string) {
		ifCondition = string == null ? "" : string;
	}

	private static String replaceProperties(Project project, String value,
			Hashtable keys) throws BuildException {
		PropertyHelper ph = PropertyHelper.getPropertyHelper(project);
		return ph.replaceProperties(null, value, keys);
	}

	private boolean testIfCondition() {
		if ("".equals(ifCondition)) {
			return true;
		}
		String string = FrameworkSet.replaceProperties(getProject(),
				ifCondition, getProject().getProperties());
		return getProject().getProperty(string) != null;
	}

	public String[] getFrameworks() {
		String[] files = getDirectoryScanner(getProject())
				.getIncludedDirectories();
		return files;
	}

	public File[] findJars(String frameworkDir) {
		if (!testIfCondition())
			return new File[] {};

		String jarDirName = frameworkDir + File.separator + "Resources"
				+ File.separator + "Java";

		File jarDir = new File(getDir(this.getProject()), jarDirName);
		if (!jarDir.isDirectory()) {
			return null;
		}

		File[] finalFiles = jarDir.listFiles(new JarFilter());
		return finalFiles;
	}

	class JarFilter implements FilenameFilter {

		public boolean accept(File dir, String name) {
			return (name.endsWith(".jar") || name.endsWith(".zip"))
					&& !name.equals("src.jar");
		}
	}

	private void addJarsForFrameworkNameToPath(String frameworkName, Path path) {
		File[] jarFiles = findJars(frameworkName);
		if (jarFiles == null || jarFiles.length == 0) {
			log("No Jars in " + getDir(getProject()).getPath() + "/"
					+ frameworkName + ".", Project.MSG_VERBOSE);
		} else {
			for (int jarFileIndex = 0; jarFileIndex < jarFiles.length; jarFileIndex++) {
				try {
					File jarFile = getDeployedFile(jarFiles[jarFileIndex]);
					log(": Framework JAR " + jarFile, Project.MSG_VERBOSE);
					path.setLocation(jarFile);
				} catch (BuildException buildException) {
					log(buildException.getMessage(), Project.MSG_WARN);
				}
			}
		}
	}

	public Path jarsPath() {
		Path path = new Path(getProject());
		String[] frameworkNames = getFrameworks();
		for (int frameworkNameIndex = 0; frameworkNameIndex < frameworkNames.length; frameworkNameIndex++)
			addJarsForFrameworkNameToPath(frameworkNames[frameworkNameIndex],
					path);
		return path;
	}

	public static Path jarsPathForFrameworkSets(Project project,
			Collection frameworkSets, boolean excludeEmbed) {
		Path path = new Path(project);
		for (Iterator frameworkSetIterator = frameworkSets.iterator(); frameworkSetIterator
				.hasNext();) {
			FrameworkSet frameworkSet = (FrameworkSet) frameworkSetIterator
					.next();
			if (!(excludeEmbed && frameworkSet.getEmbed()))
				path.append(frameworkSet.jarsPath());
		}
		return path;
	}

	/**
	 * Overrides the super method in order to return the right DirectoryScanner
	 * that doesn't sort directories. See svn revision detail:
	 * http://svn.apache.org/viewcvs.cgi?rev=274976&view=rev
	 * 
	 * Instead sort by the order defined in the include list.
	 */
	public DirectoryScanner getDirectoryScanner(Project p) {
		DirectoryScanner ds = super.getDirectoryScanner(p);
		if (isReference())
			return ds;
		// Setup a new type for the directory scanner to avoid sorting included
		// directories as set by:
		// http://svn.apache.org/viewcvs.cgi?rev=274976&view=rev
		// but rather sort by order of the includes list.
		ds = new SortedDirectoryScanner();
		setupDirectoryScanner(ds, p);
		ds.setFollowSymlinks(isFollowSymlinks());
		ds.scan();
		return ds;
	}

	class SortedDirectoryScanner extends DirectoryScanner implements Comparator {

		List includeNonPatternList;

		public synchronized String[] getIncludedDirectories() {

			if (dirsIncluded == null) {
				throw new IllegalStateException();
			}
			if (includeNonPatternList == null) {
				includeNonPatternList = new ArrayList();
				fillNonPatternList(includeNonPatternList, includes);
			}
			Collections.sort(dirsIncluded, this);
			String directories[] = new String[dirsIncluded.size()];
			dirsIncluded.copyInto(directories);
			return directories;
		}

		public int compare(Object o1, Object o2) {
			String frameworkDir1 = (String) o1;
			String frameworkDir2 = (String) o2;
			if (isCaseSensitive())
				return includeNonPatternList.indexOf(frameworkDir1)
						- includeNonPatternList.indexOf(frameworkDir2);
			return includeNonPatternList.indexOf(frameworkDir1.toUpperCase())
					- includeNonPatternList
							.indexOf(frameworkDir2.toUpperCase());
		}

		private String[] fillNonPatternList(List list, String patterns[]) {
			ArrayList al = new ArrayList(patterns.length);
			for (int i = 0; i < patterns.length; i++)
				if (!SelectorUtils.hasWildcards(patterns[i]))
					list.add(isCaseSensitive() ? ((Object) (patterns[i]))
							: ((Object) (patterns[i].toUpperCase())));
				else
					al.add(patterns[i]);

			return list.size() != 0 ? (String[]) al.toArray(new String[al
					.size()]) : patterns;
		}

	}

}
