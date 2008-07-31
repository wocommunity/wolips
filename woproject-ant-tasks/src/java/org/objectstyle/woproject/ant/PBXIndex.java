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
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.objectstyle.woenvironment.pb.PBXProject;

/**
 * @author Jonathan 'Wolf' Rentzsch
 */
public class PBXIndex extends Task {
	protected File projectFile;

	protected Vector resources = new Vector();

	protected Vector wsresources = new Vector();

	protected Vector frameworkSets = new Vector();

	protected Vector sources = new Vector();

	protected SubtaskFactory subtaskFactory = new SubtaskFactory(this);

	public void addSources(WOFileSet wofs) {
            sources.addElement( wofs );
	}

	public void addResources(WOFileSet wofs) {
            resources.addElement( wofs );
	}

	public void addWsResources(WOFileSet wofs) {
            wsresources.addElement( wofs );
	}

	public void addFrameworks(FrameworkSet set) {
            frameworkSets.addElement(set);
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
	 * @see org.apache.tools.ant.Task#execute()
	 */
	public void execute() throws BuildException {
		validateAttributes();

		PBXProject proj = new PBXProject();
		addToProject(proj);

		if (getProjectFile().exists()) {
			if (!getProjectFile().isDirectory())
				throw new BuildException("Specified PBX project package is not a directory.");
		} else
			getProjectFile().mkdir();
		File pbxprojFile = new File(getProjectFile(), "project.pbxproj");
		if (!pbxprojFile.exists()) {
			try {
				pbxprojFile.createNewFile();
			} catch (IOException x) {
				throw new BuildException("Failed to create project.pbxproj PBX project package file: " + x);
			}
		}


    try {
      proj.save(pbxprojFile);
    } catch (Exception x) {
      throw new BuildException("Failed to save project.pbxproj Xcode project package file: " + x);
    }
	}

	protected void addToProject(PBXProject proj) {
		File dir;
		Iterator it;

		// Add source file references.
		it = sources.iterator();
		while (it.hasNext()) {
                        WOFileSet wofs = (WOFileSet) it.next();
                        if( wofs.testIfCondition() ){
                            dir = wofs.getDir(getProject());
                            DirectoryScanner ds = wofs.getDirectoryScanner(getProject());
                            ds.scan();

                            String[] allFiles = ds.getIncludedFiles();
                            for (int i = 0; i < allFiles.length; i++) {
				proj.addSourceReference((new File(dir, fixPath(allFiles[i]))).getAbsolutePath());
                            }

                            String[] allDirs = ds.getIncludedDirectories();
                            for (int i = 0; i < allDirs.length; i++) {
				proj.addSourceReference((new File(dir, fixPath(allDirs[i]))).getAbsolutePath());
                            }
                        }
		}

		// Add Resources references.
		it = resources.iterator();
		while (it.hasNext()) {
			WOFileSet wofs = (WOFileSet) it.next();
                        if( wofs.testIfCondition() ){
                            dir = wofs.getDir(getProject());
                            DirectoryScanner ds = wofs.getDirectoryScanner(getProject());
                            ds.scan();

                            String[] allFiles = ds.getIncludedFiles();
                            for (int i = 0; i < allFiles.length; i++) {
				proj.addResourceFileReference((new File(dir, fixPath(allFiles[i]))).getAbsolutePath());
                            }

                            String[] allDirs = ds.getIncludedDirectories();
                            for (int i = 0; i < allDirs.length; i++) {
				proj.addResourceFolderReference((new File(dir, fixPath(allDirs[i]))).getAbsolutePath());
                            }
                        }
		}

		// Add WebServerResources references.
		it = wsresources.iterator();
		while (it.hasNext()) {
			WOFileSet wofs = (WOFileSet) it.next();
                        if( wofs.testIfCondition() ){
                            dir = wofs.getDir(getProject());
                            DirectoryScanner ds = wofs.getDirectoryScanner(getProject());
                            ds.scan();

                            String[] allFiles = ds.getIncludedFiles();
                            for (int i = 0; i < allFiles.length; i++) {
				proj.addWSResourceFileReference((new File(dir, fixPath(allFiles[i]))).getAbsolutePath());
                            }

                            String[] allDirs = ds.getIncludedDirectories();
                            for (int i = 0; i < allDirs.length; i++) {
				proj.addWSResourceFolderReference((new File(dir, fixPath(allDirs[i]))).getAbsolutePath());
                            }
                        }
		}

		// Add framework references.
		it = frameworkSets.iterator();
		while (it.hasNext()) {
			FrameworkSet fs = (FrameworkSet) it.next();
			dir = fs.getDir(getProject());
			// System.out.println( "**** frameworks dir: "+dir );
			DirectoryScanner ds = fs.getDirectoryScanner(getProject());
			ds.scan();

			String[] allDirs = ds.getIncludedDirectories();
			for (int i = 0; i < allDirs.length; i++) {
				proj.addFrameworkReference((new File(dir, fixPath(allDirs[i]))).getAbsolutePath());
			}
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
		if (projectFile == null) {
			throw new BuildException("Required 'projectFile' attribute is missing.");
		}
	}

	/** Replaces back slashes with forward slashes */
	protected String fixPath(String path) {
		return (File.separatorChar == '\\') ? path.replace('\\', '/') : path;
	}
}
