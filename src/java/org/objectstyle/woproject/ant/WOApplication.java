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
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Chmod;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.PatternSet;

/**
 * Ant task to build WebObjects application. For detailed instructions go to the
 * <a href="../../../../../ant/woapplication.html">manual page</a> .
 *
 *
 * @ant.task category="packaging"
 * 
 * @author Emily Bache
 * @author Andrei Adamchik
 */
public class WOApplication extends WOTask {
	public static final String[] stdFrameworkNames =
		new String[] {
			"JavaWebObjects",
			"JavaWOExtensions",
			"JavaEOAccess",
			"JavaEOControl",
			"JavaFoundation",
			"JavaJDBCAdaptor",
			"JavaXML" };

	protected ArrayList frameworkSets = new ArrayList();
	protected ArrayList otherClasspathSets = new ArrayList();
	protected boolean stdFrameworks = true;
	protected boolean embedStdFrameworks = false;

	public String getPrincipalClass() {
		String principalClass = super.getPrincipalClass();
		if (principalClass == null) {
			principalClass = "Application";
		}
		return principalClass;
	}

	/** 
	 * Runs WOApplication task. Main worker method that would validate
	 * all task settings and create a WOApplication.
	 */
	public void execute() throws BuildException {
		validateAttributes();

		log("Installing " + name + " in " + destDir);
		createDirectories();
		if (hasClasses()) {
			jarClasses();
		}
		if (hasLib()) {
			copyLibs();
		}
		if (hasResources()) {
			copyResources();
		}
		if (hasWs()) {
			copyWsresources();
		}
		if (hasEmbeddedFrameworks()) {
			copyEmbeddedFrameworks();
		}

		// create all needed scripts
		new AppFormat(this).processTemplates();

		// chmod UNIX scripts
		chmodScripts();
	}

	/**
	 * Sets executable flag for all scripts. This is required 
	 * on UNIX/Mac platforms. On Windows this action is simply ignored.
	 */
	protected void chmodScripts() throws BuildException {
		if (System.getProperty("os.name").toLowerCase().indexOf("win") < 0) {
			File dir = null;
			FileSet fs = null;
			Chmod chmod = null;
			try {
				dir = taskDir();
				super.log("chmod scripts in " + dir, Project.MSG_VERBOSE);

				fs = new FileSet();
				fs.setDir(dir);
				fs.createInclude().setName("**/" + name);
				fs.createInclude().setName("**/*.sh");

				chmod = subtaskFactory.getChmod();
				chmod.setPerm("gu+x");
				chmod.addFileset(fs);
				chmod.execute();
			} finally {
				dir = null;
				fs = null;
				chmod = null;
			}
		} else {
			super.log(
				"'"
					+ System.getProperty("os.name")
					+ "' is some kind of windows, skipping chmod.");
		}
	}

	/**
	 * Method copyEmbeddedFrameworks.
	 * @throws BuildException
	 */
	protected void copyEmbeddedFrameworks() throws BuildException {
		Copy cp = new Copy();
		cp.setOwningTarget(getOwningTarget());
		cp.setProject(getProject());
		cp.setTaskName(getTaskName());
		cp.setLocation(getLocation());

		cp.setTodir(embeddedFrameworksDir());

		// The purpose of this is to create filesets that actually
		// allow the framework directory to be copied into the
		// WOApplication directory.  If we didn't do this, we'd
		// have to append '/' or '/**' to the end of the includes
		// in the <frameworks> tag.
		List frameworkSets = getFrameworkSets();
		int size = frameworkSets.size();
		for (int i = 0; i < size; i++) {
			FrameworkSet fs = (FrameworkSet) frameworkSets.get(i);

			if (fs.getEmbed() == false) {
				continue;
			}

			File root = fs.getDir(getProject());
			DirectoryScanner ds = fs.getDirectoryScanner(getProject());
			String[] dirs = ds.getIncludedDirectories();

			for (int j = 0; j < dirs.length; j++) {
				String includeName = dirs[j];

				if (includeName.endsWith(".framework") == false) {
					throw new BuildException("'name' attribute must end with '.framework'");
				}

				FileSet newFs = new FileSet();
				PatternSet.NameEntry include;

				newFs.setDir(root);
				include = newFs.createInclude();
				include.setName(includeName + "/Resources/");
				include = newFs.createInclude();
				include.setName(includeName + "/WebServerResources/");

				cp.addFileset(newFs);
			}
		}
		cp.execute();
	}

	/**
	 * Returns a list of standard frameworks as a FrameworkSet. */
	public FrameworkSet standardSet() {
		FrameworkSet set = new FrameworkSet();
		WOPropertiesHandler aHandler =
			new WOPropertiesHandler(this.getProject());

		set.setProject(this.getProject());
		set.setRoot(new File(aHandler.getWORootPath() + "/Library/Frameworks"));

		for (int i = 0; i < stdFrameworkNames.length; i++) {
			String path =
				//				"Library/Frameworks/" + stdFrameworkNames[i] + ".framework";
	stdFrameworkNames[i] + ".framework";
			PatternSet.NameEntry include = set.createInclude();
			include.setName(path);
		}

		// Force embedding of the standard frameworks.
		set.setEmbed(embedStdFrameworks);
		return set;
	}

	/** 
	 * Sets a flag indicating that standard frameworks,
	 * namely JavaWebObjects, JavaWOExtensions, JavaEOAccess, JavaEOControl, 
	 * JavaFoundation, JavaJDBCAdaptor should be automatically 
	 * referenced in deployed application.
	 */
	public void setStdFrameworks(boolean flag) {
		stdFrameworks = flag;
	}

	public void setEmbedStdFrameworks(boolean flag) {
		embedStdFrameworks = flag;
		// If we request embedding for the standard
		// frameworks, we certainly want to reference
		// them.
		if (flag) {
			stdFrameworks = true;
		}
	}

	/**
	 * Returns location where WOApplication is being built up. 
	 * For WebObjects applications this is a <code>.woa</code> directory.
	 */
	protected File taskDir() {
		return getProject().resolveFile(
			destDir + File.separator + name + ".woa");
	}

	protected File contentsDir() {
		return new File(taskDir(), "Contents");
	}

	protected File embeddedFrameworksDir() {
		return new File(contentsDir(), "Frameworks");
	}

	protected File resourcesDir() {
		return new File(contentsDir(), "Resources");
	}

	protected File wsresourcesDir() {
		return new File(contentsDir(), "WebServerResources");
	}

	protected boolean hasLib() {
		return lib.size() > 0;
	}

	protected boolean hasEmbeddedFrameworks() {
		List frameworkSets = getFrameworkSets();
		int size = frameworkSets.size();

		for (int i = 0; i < size; i++) {
			FrameworkSet fs = (FrameworkSet) frameworkSets.get(i);

			if (fs.getEmbed()) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Create a nested FrameworkSet.
	 */
	public FrameworkSet createFrameworks() {
		FrameworkSet frameSet = new FrameworkSet();
		frameworkSets.add(frameSet);
		return frameSet;
	}

	public List getFrameworkSets() {
		if (stdFrameworks) {
			ArrayList fullList = new ArrayList(frameworkSets.size() + 1);
			fullList.add(standardSet());
			fullList.addAll(frameworkSets);
			return fullList;
		} else {
			return frameworkSets;
		}
	}

	/**
	 * Create a nested OtherClasspath.
	 */
	public OtherClasspathSet createOtherclasspath() {
		OtherClasspathSet otherClasspathSet = new OtherClasspathSet();
		otherClasspathSets.add(otherClasspathSet);
		return otherClasspathSet;
	}

	public List getOtherClasspath() {
		return otherClasspathSets;
	}
}
