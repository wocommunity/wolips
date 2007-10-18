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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.objectstyle.woenvironment.pb.PBProject;

/**
 * @author Andrei Adamchik
 */
public class PBIndex extends Task {
	protected String name;

	protected File projectFile;

	protected boolean framework;

	protected Vector<FileSet> src = new Vector<FileSet>();

	protected Vector<FileSet> wocomponents = new Vector<FileSet>();

	protected Vector<FileSet> resources = new Vector<FileSet>();

	protected Vector<FileSet> wsresources = new Vector<FileSet>();

	protected Vector<FrameworkSet> frameworkSets = new Vector<FrameworkSet>();

	protected SubtaskFactory subtaskFactory = new SubtaskFactory(this);

	public void addWocomponents(FileSet set) {
		wocomponents.addElement(set);
	}

	public void addSrc(FileSet set) {
		src.addElement(set);
	}

	public void addResources(FileSet set) {
		resources.addElement(set);
	}

	public void addWsresources(FileSet set) {
		wsresources.addElement(set);
	}

	public void addFrameworks(FrameworkSet set) {
		frameworkSets.addElement(set);
	}

	/**
	 * Sets the name.
	 *
	 * @param name
	 *            The name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the name.
	 *
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the projectFile.
	 *
	 * @return String
	 */
	public File getProjectFile() {
		return projectFile;
	}

	/**
	 * Sets the projectFile.
	 *
	 * @param projectFile
	 *            The projectFile to set
	 */
	public void setProjectFile(File projectFile) {
		this.projectFile = projectFile;
	}

	/**
	 * Returns the framework.
	 *
	 * @return boolean
	 */
	public boolean isFramework() {
		return framework;
	}

	/**
	 * Sets the framework.
	 *
	 * @param framework
	 *            The framework to set
	 */
	public void setFramework(boolean framework) {
		this.framework = framework;
	}

	/**
	 * @see org.apache.tools.ant.Task#execute()
	 */
	@Override
	public void execute() throws BuildException {
		validateAttributes();
		PBProject proj = null;
		try {
			proj = (projectFile != null) ? new PBProject(projectFile.getPath(), framework) : new PBProject(framework);

			proj.setProjectName(name);
			proj.setClasses(extractJavaFiles(src));
			proj.setWoComponents(extractWOComponents(wocomponents));
			proj.setWoAppResources(extractResources(resources, "**/*.eomodeld/index.eomodeld"));
			proj.setWebServerResources(extractResources(wsresources, null));
			extractFrameworks(proj);

			proj.saveChanges();

			// notify everybody
			// PBProjectNotifications.postPBProjectDidUpgradeNotification(name);
		} catch (IOException ioex) {
			log("Error saving project file", Project.MSG_ERR);
			throw new BuildException("Error saving project file", ioex);
		} finally {
			proj = null;
		}
	}

	/**
	 * Ensure we have a consistent and legal set of attributes, and set any
	 * internal flags necessary based on different combinations of attributes.
	 *
	 * @throws BuildException
	 *             if task attributes are inconsistent or missing.
	 */
	protected void validateAttributes() throws BuildException {
		if (name == null) {
			throw new BuildException("Required 'name' attribute is missing.");
		}
	}

	/**
	 * Takes a vector of FileSet objects, returns an array of Strings
	 * corresponding to files.
	 */
	protected List<String> extractWOComponents(Vector<FileSet> filesets) {
		ArrayList<String> files = new ArrayList<String>();

		Iterator<FileSet> it = filesets.iterator();
		while (it.hasNext()) {
			FileSet fs = (FileSet) it.next();
			DirectoryScanner ds = fs.getDirectoryScanner(getProject());
			ds.scan();
			String[] dirs = ds.getIncludedDirectories();
			for (int i = 0; i < dirs.length; i++) {
				if (dirs[i].endsWith(".wo")) {
					files.add(fixPath(dirs[i]));
				}
			}
		}
		return files;
	}

	/**
	 * Takes a vector of FileSet objects, returns an array of Strings
	 * corresponding to files.
	 */
	protected List<String> extractJavaFiles(Vector<FileSet> filesets) {
		ArrayList<String> files = new ArrayList<String>();

		Iterator<FileSet> it = filesets.iterator();
		while (it.hasNext()) {
			FileSet fs = (FileSet) it.next();

			// for now exclude subprojects,
			// later we must create a better support for subprojects
			fs.createExclude().setName("*.subproj/**");

			DirectoryScanner ds = fs.getDirectoryScanner(getProject());
			ds.scan();
			String[] allFiles = ds.getIncludedFiles();
			for (int i = 0; i < allFiles.length; i++) {
				if (allFiles[i].endsWith(".java")) {
					files.add(fixPath(allFiles[i]));
				}
			}
		}
		return files;
	}

	/**
	 * Takes a vector of FileSet objects, returns an array of Strings
	 * corresponding to matching files and directories.
	 */
	protected List<String> extractResources(Vector<FileSet> filesets, String extraExcludes) {
		ArrayList<String> files = new ArrayList<String>();

		if (filesets.size() > 0) {
			Iterator<FileSet> it = filesets.iterator();
			while (it.hasNext()) {
				FileSet fs = (FileSet) it.next();

				// extra filter
				if (extraExcludes != null) {
					fs.createExclude().setName(extraExcludes);
				}

				DirectoryScanner ds = fs.getDirectoryScanner(getProject());
				ds.scan();

				String[] allFiles = ds.getIncludedFiles();
				for (int i = 0; i < allFiles.length; i++) {
					files.add(fixPath(allFiles[i]));
				}

				String[] allDirs = ds.getIncludedDirectories();
				for (int i = 0; i < allDirs.length; i++) {
					files.add(fixPath(allDirs[i]));
				}
			}
		}
		return files;
	}

	/**
	 * Loads extra frameworks and inserts their /Library/Frameworks-relative
	 * paths.
	 */
	protected void extractFrameworks(PBProject proj) {
		List<String> projectFrameworkPaths = proj.getFrameworks();

		Iterator<FrameworkSet> it = frameworkSets.iterator();
		while (it.hasNext()) {
			FrameworkSet fs = (FrameworkSet) it.next();
			File baseDir = fs.getDir(fs.getProject());
			String[] frameworkSubPaths = fs.getDirectoryScanner(fs.getProject()).getIncludedDirectories();
			for (int i = 0; i < frameworkSubPaths.length; i++) {
				File aFramework = new File(baseDir, frameworkSubPaths[i]);

				if (!projectFrameworkPaths.contains(aFramework.getName())) {
					// modifying the original list
					projectFrameworkPaths.add(aFramework.getName());
				}
			}
		}
	}

	/** Replaces back slashes with forward slashes */
	protected String fixPath(String path) {
		return (File.separatorChar == '\\') ? path.replace('\\', '/') : path;
	}
}
