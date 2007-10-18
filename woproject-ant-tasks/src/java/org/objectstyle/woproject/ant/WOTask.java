/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2002- 2006 The ObjectStyle Group
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
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.taskdefs.Mkdir;
import org.apache.tools.ant.types.FileSet;

/**
 * A <b>WOTask</b> is a common superclass of WOApplication and WOFramework that
 * implements common build functionality.
 *
 * @author Emily Bache
 * @author Andrei Adamchik
 */
public abstract class WOTask extends Task {

	protected Vector<WOFileSet> classes = new Vector<WOFileSet>();

	protected String name;

	protected String destDir;

	protected String wsDestDir;

	protected String principalClass;

	protected String manifest;

	protected String jarName;

	protected String customInfoPListContent;

	protected Vector<WOFileSet> sources = new Vector<WOFileSet>();

	protected Vector<WOFileSet> resources = new Vector<WOFileSet>();

	protected Vector<WOFileSet> wsresources = new Vector<WOFileSet>();

	protected Vector<FileSet> flattenfiles = new Vector<FileSet>();

	protected Vector<FileSet> lib = new Vector<FileSet>();

	protected boolean hasComponents = true;

	protected String version;

	protected String cfbundleversion;

	protected String cfbundleshortversion;

	protected String cfbundleID;

	protected String javaVersion;

	private SubtaskFactory subtaskFactory;

	// this leaks
	// public Log log;

	public WOTask() {
		super();
	}

	/**
	 * Method setName.
	 *
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Method getName.
	 *
	 * @return String
	 */
	public String getName() {
		return name;
	}

	public String getVersion() {
		return version == null ? "" : version;
	}

	public void setVersion(String value) {
		version = value;
	}

	/**
	 * CFBundleVersion for Info.plist
	 * @return
	 */
	public String getCFBundleVersion() {
		return (cfbundleversion == null || cfbundleversion.equals("${cfBundleVersion}")) ? "" : cfbundleversion;
	}

	/**
	 * CFBundleVersion for Info.plist
	 * @param value
	 */
	public void setCFBundleVersion(String value) {
		cfbundleversion = value;
	}

	/**
	 * CFBundleShortVersionString
	 * @return
	 */
	public String getCFBundleShortVersion() {
		return (cfbundleshortversion == null || cfbundleshortversion.equals("${cfBundleShortVersion}")) ? "" : cfbundleshortversion;
	}

	/**
	 * CFBundleShortVersionString
	 * @param value
	 */
	public void setCFBundleShortVersion(String value) {
		cfbundleshortversion = value;
	}

	/**
	 * CFBundleIdentifier
	 * @return
	 */
	public String getCFBundleID() {
		return (cfbundleID == null || cfbundleID.equals("${cfBundleID}")) ? "com.apple.myapp" : cfbundleID;
	}

	/**
	 * CFBundleIdentifier
	 * @param value
	 */
	public void setCFBundleID(String value) {
		cfbundleID = value;
	}

	/**
	 * JVM selector string in Info.plist<br>
	 * As specified by <a href="http://developer.apple.com/documentation/Java/Conceptual/JavaPropVMInfoRef/Articles/JavaDictionaryInfo.plistKeys.html">Apple Documentation</a>
	 * <br>default value is 1.5+
	 * @return
	 */
	public String getJavaVersion() {
		return (javaVersion == null ||  javaVersion.equals("${javaVersion}")) ? "1.5+" : javaVersion;
	}

	/**
	 * JVM selector string in Info.plist<br>
	 * As specified by <a href="http://developer.apple.com/documentation/Java/Conceptual/JavaPropVMInfoRef/Articles/JavaDictionaryInfo.plistKeys.html">Apple Documentation</a>
	 * @param value
	 */
	public void setJavaVersion(String value) {
		javaVersion = value;
	}

	/**
	 * Method setJarName.
	 *
	 * @param jarName
	 */
	public void setJarName(String jarName) {
		this.jarName = jarName;
	}

	/**
	 * Method getJarName.
	 *
	 * @return String
	 */
	public String getJarName() {
		if (jarName == null)
			jarName = getName().toLowerCase();
		return jarName;
	}

	/**
	 * Method setPrincipalClass.
	 *
	 * @param principalClass
	 */
	public void setPrincipalClass(String principalClass) {
		if (principalClass.equals("${principalClass}")) {
			principalClass = "Application";
		}
		this.principalClass = principalClass;
	}

	/**
	 * Method setManifest.
	 *
	 * @param manifest
	 */
	public void setManifest(String manifest) {
		this.manifest = manifest;
	}

	public void setWsDestDir(String wsDestDir) {
		this.wsDestDir = wsDestDir;
	}

	/**
	 * Method getPrincipalClass.
	 *
	 * @return String
	 */
	public String getPrincipalClass() {
		return principalClass;
	}

	/**
	 * Method getManifest.
	 *
	 * @return String
	 */
	public String getManifest() {
		return manifest;
	}

	/**
	 * @return The CustomContent for the Info.plist
	 */
	public String getCustomInfoPListContent() {
		return customInfoPListContent;
	}

	/**
	 * @return The CustomContent for the Info.plist
	 */
	public void setCustomInfoPListContent(String customInfoPListContent) {
		this.customInfoPListContent = customInfoPListContent;
	}

	/**
	 * Method setDestDir.
	 *
	 * @param destDir
	 */
	public void setDestDir(String destDir) {
		this.destDir = destDir;
	}

	/**
	 * Method addClasses.
	 *
	 * @param set
	 */
	public void addClasses(WOFileSet set) {
		classes.addElement(set);
	}

	/**
	 * Method addSources.
	 *
	 * @param set
	 */
	public void addSources(WOFileSet set) {
		sources.addElement(set);
	}

	/**
	 * Method addResources.
	 *
	 * @param set
	 */
	public void addResources(WOFileSet set) {
		resources.addElement(set);
	}

	/**
	 * Method addLib.
	 *
	 * @param set
	 */
	public void addLib(FileSet set) {
		lib.addElement(set);
	}

	/**
	 * Method addWsresources.
	 *
	 * @param set
	 */
	public void addWsresources(WOFileSet set) {
		wsresources.addElement(set);
	}

	/**
	 * Method addFlattenfiles.
	 *
	 * @param set
	 */
	public void addFlattenfiles(FileSet set) {
		flattenfiles.addElement(set);
	}

	/**
	 * Returns web server root directory.
	 */
	protected File webServerDir() {
		return getProject().resolveFile(wsDestDir);
	}

	/**
	 * Returns a location where WOTask is being built up. For instance the
	 * <code>.woa</code> dir or the </code>.framework</code> dir.
	 */
	protected abstract File taskDir();

	/**
	 * Returns a location where resources should be put. For instance this can
	 * be WOComponents, EOModels etc.
	 */
	protected abstract File resourcesDir();

	/**
	 * Returns a location where web server resources should be copied.
	 * WebServerResources are normally images, JavaScript files, stylesheets,
	 * etc.
	 */
	protected abstract File wsresourcesDir();

	/**
	 * Returns a location where web server resources should be copied during
	 * split install.
	 */
	protected abstract File wsresourcesDestDir();

	/**
	 * Ensure we have a consistent and legal set of attributes, and set any
	 * internal flags necessary based on different combinations of attributes.
	 *
	 * @throws BuildException
	 *             if task attributes are inconsistent or missing.
	 */
	protected void validateAttributes() throws BuildException {
		if (name == null) {
			throw new BuildException("'name' attribute is missing.");
		}

		if (destDir == null) {
			throw new BuildException("'destDir' attribute is missing.");
		}
	}

	/**
	 * Method createDirectories.
	 *
	 * @throws BuildException
	 */
	protected void createDirectories() throws BuildException {
		Mkdir mkdir = this.getSubtaskFactory().getMkdir();

		File taskDir = taskDir();

		mkdir.setDir(taskDir);
		mkdir.execute();

		File resourceDir = resourcesDir();
		mkdir.setDir(resourceDir);
		mkdir.execute();

		mkdir.setDir(new File(resourceDir, "Java"));
		mkdir.execute();

		if (hasWs()) {
			mkdir.setDir(wsresourcesDir());
			mkdir.execute();
		}
	}

	/**
	 * Method hasWs.
	 *
	 * @return boolean
	 */
	public boolean hasWs() {
		return wsresources.size() > 0;
	}

	/**
	 * Method hasFlattenfiles.
	 *
	 * @return boolean
	 */
	public boolean hasFlattenfiles() {
		return flattenfiles.size() > 0;
	}

	/**
	 * Returns true if split install of WebServerResources is required and
	 * possible.
	 */
	public boolean doingSplitInstall() {
		return wsDestDir != null && hasWs();
	}

	/**
	 * Method hasResources.
	 *
	 * @return boolean
	 */
	public boolean hasResources() {
		return resources.size() > 0;
	}

	/**
	 * Method hasSources.
	 *
	 * @return boolean
	 */
	public boolean hasSources() {
		return sources.size() > 0;
	}

	/**
	 * Method hasClasses.
	 *
	 * @return boolean
	 */
	public boolean hasClasses() {
		return classes.size() > 0;
	}

	/**
	 * Method hasManifest.
	 *
	 * @return boolean
	 */
	public boolean hasManifest() {
		return (manifest != null);
	}

	/**
	 * Method getManifestFile.
	 *
	 * @return File
	 */
	public File getManifestFile() {
		return (new File(manifest));
	}

	/**
	 * Method jarSources
	 *
	 * @throws BuildException
	 */
	protected void jarSources() throws BuildException {
		Jar jar = this.getSubtaskFactory().getJar();
		File taskJar = new File(resourcesDir(), "Java" + File.separator + "src.jar");
		// jar.setJarfile(taskJar);
		// jar.setLocation(new Location(resourcesDir() + "Java" + File.separator
		// + getJarName() + ".jar"));
		jar.setDestFile(taskJar);
                boolean hasFileSets = false;
		if (hasSources()) {
			Enumeration<WOFileSet> en = sources.elements();
			while (en.hasMoreElements()) {
                            WOFileSet wofs = (WOFileSet) en.nextElement();
                            if( wofs.testIfCondition() ){
                                jar.addFileset( (FileSet) wofs );
                                hasFileSets = true;
                            }
			}
		}

                if( hasFileSets ){
                    jar.execute();
                }
	}

	/**
	 * Method jarClasses.
	 *
	 * @throws BuildException
	 */
	protected void jarClasses() throws BuildException {
		Jar jar = this.getSubtaskFactory().getJar();
		File taskJar = new File(resourcesDir(), "Java" + File.separator + getJarName() + ".jar");
		// jar.setJarfile(taskJar);
		// jar.setLocation(new Location(resourcesDir() + "Java" + File.separator
		// + getJarName() + ".jar"));
		jar.setDestFile(taskJar);
		if (hasClasses()) {
			Enumeration<WOFileSet> en = classes.elements();
			while (en.hasMoreElements()) {
				WOFileSet wofs = (WOFileSet) en.nextElement();
				if( wofs.testIfCondition() ){
					jar.addFileset( (FileSet) wofs );
				}
			}
		}

		if (hasManifest()) {
			jar.setManifest(getManifestFile());
		}

		jar.execute();
	}

	/**
	 * Method copyResources.
	 *
	 * @throws BuildException
	 */
	protected void copyResources() throws BuildException {
		Copy cp = this.getSubtaskFactory().getResourceCopy();

		cp.setTodir(resourcesDir());
		int count = 0;
		Enumeration<WOFileSet> en = resources.elements();
		while (en.hasMoreElements()) {
			WOFileSet wofs = (WOFileSet) en.nextElement();
			if (wofs.testIfCondition()) {
				cp.addFileset( wofs );
				count++;
			}
		}

		// if no filesets were added, then don't run copy
		if (count > 0) {
			cp.execute();
		}
	}

	/**
	 * Copies WebServerResources to the target location. Performs split install
	 * if requested.
	 *
	 * @throws BuildException
	 */
	protected void copyWsresources() throws BuildException {
		Copy cp = this.getSubtaskFactory().getResourceCopy();
		cp.setTodir(wsresourcesDir());

		int count = 0;
		Enumeration<WOFileSet> en = wsresources.elements();
		while (en.hasMoreElements()) {
			WOFileSet wofs = (WOFileSet) en.nextElement();
			if (wofs.testIfCondition()) {
				cp.addFileset(wofs);
				count++;
			}
		}

		// if no filesets were added, then don't run copy
		if (count > 0) {
			cp.execute();

			// do split install
			if (doingSplitInstall()) {
				log("Split install WebServerResources of " + name + " in " + wsDestDir);
				cp.setTodir(wsresourcesDestDir());
				cp.execute();
			}
		}
	}

	/**
	 * Method copyLibs.
	 *
	 * @throws BuildException
	 */
	protected void copyLibs() throws BuildException {
		Copy cp = this.getSubtaskFactory().getResourceCopy();
		cp.setTodir(new File(resourcesDir(), "Java"));
		cp.setFlatten(true);

		Enumeration<FileSet> en = lib.elements();
		while (en.hasMoreElements()) {
			cp.addFileset((FileSet) en.nextElement());
		}
		cp.execute();
	}

	/**
	 * Method hasLib.
	 *
	 * @return boolean
	 */
	protected boolean hasLib() {
		return lib.size() > 0;
	}

	/**
	 * Method hasJava.
	 *
	 * @return boolean
	 */
	protected boolean hasJava() {
		return classes.size() > 0 || lib.size() > 0;
	}

	/**
	 * Returns an Iterator over the file names of the library files included in
	 * the lib nested element.
	 */
	public Iterator<String> getLibNames() {
		ArrayList<String> libNames = new ArrayList<String>();
		Enumeration<FileSet> en = lib.elements();
		while (en.hasMoreElements()) {
			FileSet fs = (FileSet) en.nextElement();
			DirectoryScanner scanner = fs.getDirectoryScanner(getProject());
			String[] libs = scanner.getIncludedFiles();
			for (int i = 0; i < libs.length; i++) {
				File libFile = new File(libs[i]);
				libNames.add(libFile.getPath());
			}
		}
		return libNames.iterator();
	}

	public SubtaskFactory getSubtaskFactory() {
		if (subtaskFactory == null)
			subtaskFactory = new SubtaskFactory(this);
		return subtaskFactory;
	}

	/**
	 *
	 */
	public void release() {
		subtaskFactory.release();
		subtaskFactory = null;
	}

	/**
	 * @return Returns an Iterator over the file names of the mapper files
	 *         included in the mapperfiles nested element.
	 */
	public Iterator<String> getFlattenfileNames() {
		ArrayList<String> flattenfilesNames = new ArrayList<String>();
		Enumeration<FileSet> en = flattenfiles.elements();
		while (en.hasMoreElements()) {
			FileSet fs = (FileSet) en.nextElement();
			DirectoryScanner scanner = fs.getDirectoryScanner(getProject());
			String[] files = scanner.getIncludedFiles();
			for (int i = 0; i < files.length; i++) {
				flattenfilesNames.add(files[i]);
			}
		}
		return flattenfilesNames.iterator();
	}

	public boolean getHasComponents() {
		return hasComponents;
	}

	public void setHasComponents(boolean value) {
		hasComponents = value;
	}

}
