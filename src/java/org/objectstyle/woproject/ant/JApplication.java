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
import java.util.Collection;
import java.util.StringTokenizer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

public class JApplication extends Task {

	public static final String WINDOWS_OS = "windows";

	public static final String MAC_OS = "mac";

	public static final String JAVA_OS = "java";

	public static final String NSIS_HOME_DEFAULT = "C:\\Program Files\\NSIS";

	protected String name;

	protected String mainClass;

	protected String os;

	protected File destDir;

	protected String longName;

	protected File icon;

	protected String jvm;

	protected String jvmOptions;

	protected String nsisHome;

	protected String version;

	protected Collection libs = new ArrayList();

	/**
	 * Returns default operating system for a given platform. If no exact
	 * platform match is found, java platform is returned.
	 */
	public String getDefaultOs() {
		String vmOS = System.getProperty("os.name").toUpperCase();
		if (vmOS.startsWith("WINDOWS")) {
			return WINDOWS_OS;
		} else if (vmOS.startsWith("MAC")) {
			return MAC_OS;
		} else {
			return JAVA_OS;
		}
	}

	public void execute() throws BuildException {
		validate();
		initDefaults();

		log("Building Java Application '" + name + "', os: " + os + ", dir: " + destDir);

		JApplicationWorker worker;

		if (WINDOWS_OS.equals(os)) {
			worker = new JApplicationWindowsWorker();
		} else if (MAC_OS.equals(os)) {
			worker = new JApplicationMacWorker();
		} else {
			worker = new JApplicationJavaWorker();
		}

		worker.execute(this);
	}

	protected void validate() throws BuildException {
		if (isBlankString(name)) {
			throw new BuildException("'name' attribute is required");
		}
		validateMainClass();
	}

	protected void validateMainClass() throws BuildException {
		if (isBlankString(mainClass)) {
			throw new BuildException("'mainClass' attribute is required");
		}

		StringTokenizer classToks = new StringTokenizer(mainClass, ".");
		while (classToks.hasMoreTokens()) {
			String tok = classToks.nextToken();
			for (int i = 0; i < tok.length(); i++) {

				if (i == 0) {
					if (!Character.isJavaIdentifierStart(tok.charAt(0))) {
						throw new BuildException("Invalid java class name: " + mainClass);
					}
				} else {
					if (!Character.isJavaIdentifierPart(tok.charAt(i))) {
						throw new BuildException("Invalid java class name: " + mainClass);
					}
				}
			}
		}
	}

	protected void validateOs() throws BuildException {
		if (os != null) {
			if (!(os.equals(WINDOWS_OS) || os.equals(MAC_OS) || os.equals(JAVA_OS))) {
				throw new BuildException("Unsupported OS: " + os + ", only the following are supported: " + WINDOWS_OS + "," + MAC_OS + "," + JAVA_OS);
			}
		}
	}

	/**
	 * A utility method to create subtasks.
	 */
	protected Task createSubtask(Class subtaskClass) throws BuildException {

		if (subtaskClass == null) {
			throw new IllegalArgumentException("Null subtask class");
		}

		if (!Task.class.isAssignableFrom(subtaskClass)) {
			throw new IllegalArgumentException("Invalid subtask class, must be a subclass of Task: " + subtaskClass.getName());
		}

		Task subtask;
		try {
			subtask = (Task) subtaskClass.newInstance();
		} catch (Exception e) {
			throw new BuildException("Can't create subtask: " + subtaskClass.getName());
		}

		subtask.setOwningTarget(getOwningTarget());
		subtask.setProject(getProject());
		subtask.setTaskName(getTaskName());
		subtask.setLocation(getLocation());
		return subtask;
	}

	protected void initDefaults() {
		if (longName == null) {
			longName = name;
		}

		if (destDir == null) {
			destDir = getProject().getBaseDir();
		}

		if (os == null) {
			os = getDefaultOs();
		}

		if (nsisHome == null) {
			nsisHome = NSIS_HOME_DEFAULT;
		}

		if (version == null) {
			version = "0.0";
		}

		if (jvm == null) {
			jvm = "1.4+";
		}
	}

	private boolean isBlankString(String string) {
		return string == null || string.trim().length() == 0;
	}

	public Collection getLibs() {
		return libs;
	}

	public void addLib(FileSet lib) {
		this.libs.add(lib);
	}

	public void setDestDir(File destDir) {
		this.destDir = destDir;
	}

	public void setIcon(File icon) {
		this.icon = icon;
	}

	public void setJvm(String jvm) {
		this.jvm = jvm;
	}

	public void setJvmOptions(String jvmOptions) {
		this.jvmOptions = jvmOptions;
	}

	public void setLongName(String longName) {
		this.longName = longName;
	}

	public void setMainClass(String mainClass) {
		this.mainClass = mainClass;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public void setNsisHome(String nsisHome) {
		this.nsisHome = nsisHome;
	}

	public File getDestDir() {
		return destDir;
	}

	public File getIcon() {
		return icon;
	}

	public String getJvm() {
		return jvm;
	}

	public String getJvmOptions() {
		return jvmOptions;
	}

	public String getLongName() {
		return longName;
	}

	public String getMainClass() {
		return mainClass;
	}

	public String getName() {
		return name;
	}

	public String getNsisHome() {
		return nsisHome;
	}

	public String getOs() {
		return os;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}
