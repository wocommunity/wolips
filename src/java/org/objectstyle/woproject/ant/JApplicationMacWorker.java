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
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Chmod;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.types.FileSet;

class JApplicationMacWorker implements JApplicationWorker {

	private static final String STUB = "/System/Library/Frameworks/JavaVM.framework/Versions/Current/Resources/MacOS/JavaApplicationStub";

	protected JApplication task;

	protected File contentsDir;

	protected File resourcesDir;

	protected File javaDir;

	protected File macOSDir;

	protected File stub;

	public void execute(JApplication task) throws BuildException {

		this.task = task;
		File baseDir = new File(task.getDestDir(), task.getName() + ".app");
		this.contentsDir = new File(baseDir, "Contents");
		this.macOSDir = new File(contentsDir, "MacOS");
		this.resourcesDir = new File(contentsDir, "Resources");
		this.javaDir = new File(resourcesDir, "Java");

		this.stub = new File(STUB);

		// sanity check...
		if (!stub.isFile()) {
			throw new BuildException("Java stub file not found. Is this a Mac? " + STUB);
		}

		createDirectories();
		copyStub();
		copyIcon();
		copyJars();

		// do this AFTER the jars, as we need to list them in the Info.plist
		createInfoPlist();
	}

	void createDirectories() throws BuildException {
		createDirectory(task.getDestDir());
		createDirectory(resourcesDir);
		createDirectory(javaDir);
		createDirectory(macOSDir);
	}

	void createDirectory(File file) throws BuildException {
		if (!file.isDirectory() && !file.mkdirs()) {
			throw new BuildException("Can't create directory " + file.getAbsolutePath());
		}
	}

	void createInfoPlist() throws BuildException {
		File targetInfoPlist = new File(contentsDir, "Info.plist");
		String targetIcon = task.getIcon() != null && task.getIcon().isFile() ? task.getIcon().getName() : "";
		String jvmOptions = task.getJvmOptions() != null ? task.getJvmOptions() : "";

		StringBuffer jars = new StringBuffer();
		String[] jarFiles = javaDir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".jar");
			}
		});

		for (int i = 0; i < jarFiles.length; i++) {
			jars.append("          <string>\\$JAVAROOT/").append(jarFiles[i]).append("</string>\n");
		}

		Map tokens = new HashMap();
		tokens.put("@NAME@", task.getName());
		tokens.put("@VERSION@", task.getVersion());
		tokens.put("@LONG_NAME@", task.getLongName());
		tokens.put("@MAIN_CLASS@", task.getMainClass());
		tokens.put("@VERSION@", task.getVersion());
		tokens.put("@ICON@", targetIcon);
		tokens.put("@JVM@", task.getJvm());
		tokens.put("@JVM_OPTIONS@", jvmOptions);
		tokens.put("@JARS@", jars.toString());

		new TokenFilter(tokens).copy("japplication/mac/Info.plist", targetInfoPlist);
	}

	void copyStub() throws BuildException {
		Copy cp = makeCopyTask();
		cp.setTodir(macOSDir);
		cp.setFile(stub);
		cp.execute();

		Chmod chmod = makeChmodTask();
		chmod.setPerm("755");
		chmod.setFile(new File(macOSDir, "JavaApplicationStub"));
		chmod.execute();
	}

	void copyIcon() throws BuildException {
		if (task.getIcon() != null && task.getIcon().isFile()) {
			Copy cp = makeCopyTask();
			cp.setTodir(resourcesDir);
			cp.setFile(task.getIcon());
			cp.execute();
		}
	}

	void copyJars() {
		if (!task.getLibs().isEmpty()) {
			Copy cp = makeCopyTask();
			cp.setTodir(javaDir);
			cp.setFlatten(true);

			Iterator it = task.getLibs().iterator();
			while (it.hasNext()) {
				FileSet fs = (FileSet) it.next();
				cp.addFileset(fs);
			}

			cp.execute();
		}
	}

	Copy makeCopyTask() {
		Copy cp = new Copy();
		cp.setOwningTarget(task.getOwningTarget());
		cp.setProject(task.getProject());
		cp.setTaskName(task.getTaskName());
		cp.setLocation(task.getLocation());
		return cp;
	}

	Chmod makeChmodTask() {
		Chmod chmod = new Chmod();
		chmod.setOwningTarget(task.getOwningTarget());
		chmod.setProject(task.getProject());
		chmod.setTaskName(task.getTaskName());
		chmod.setLocation(task.getLocation());
		return chmod;
	}
}
