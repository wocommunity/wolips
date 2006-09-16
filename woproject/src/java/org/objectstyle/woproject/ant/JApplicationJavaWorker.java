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
import java.util.Iterator;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.Expand;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.taskdefs.Manifest;
import org.apache.tools.ant.taskdefs.ManifestException;
import org.apache.tools.ant.taskdefs.ManifestTask;
import org.apache.tools.ant.types.FileSet;

class JApplicationJavaWorker implements JApplicationWorker {

	protected JApplication task;

	protected File baseDir;

	protected File scratchDir;

	protected Collection unpackedJarDirs;

	protected File manifestFile;

	public void execute(JApplication task) throws BuildException {
		this.task = task;
		this.baseDir = task.getDestDir();
		this.unpackedJarDirs = new ArrayList();

		try {
			executeInternal();
		} finally {
			// must clean the tempdir
			if (!recursiveDelete(scratchDir)) {
				throw new BuildException("Failed to clean up temp directory: " + scratchDir);
			}
		}
	}

	protected void executeInternal() throws BuildException {
		createDirectories();
		createManifest();
		unpackJars();
		createFatJar();
	}

	void createManifest() throws BuildException {
		this.manifestFile = new File(scratchDir, "MANIFEST.MF");

		Manifest.Attribute mainClass = new Manifest.Attribute();
		mainClass.setName("Main-Class");
		mainClass.setValue(task.getMainClass());

		ManifestTask manifest = makeManifestTask();
		manifest.setFile(manifestFile);
		try {
			manifest.addConfiguredAttribute(mainClass);
		} catch (ManifestException e) {
			throw new BuildException("Manifest error", e);
		}

		manifest.execute();
	}

	void createFatJar() throws BuildException {
		File fatJar = new File(baseDir, task.getName() + ".jar");
		fatJar.delete();

		Jar jar = makeJarTask();
		jar.setDestFile(fatJar);

		Iterator it = unpackedJarDirs.iterator();
		while (it.hasNext()) {

			// append to the initial file
			if (fatJar.exists()) {
				jar.setUpdate(true);
			}

			File jarDir = (File) it.next();
			jar.setBasedir(jarDir);

			// add manifest at the end
			if (!it.hasNext()) {
				jar.setManifest(manifestFile);
			}

			jar.execute();
		}

	}

	void unpackJars() throws BuildException {
		// unpack in separate dirs, to allow multiple instances of the same
		// resource from different jars.

		int jarId = 0;

		Iterator it = task.getLibs().iterator();
		while (it.hasNext()) {
			FileSet fs = (FileSet) it.next();
			DirectoryScanner scanner = fs.getDirectoryScanner(task.getProject());

			String[] files = scanner.getIncludedFiles();
			Expand unjar = makeUnjarTask();

			for (int i = 0; i < files.length; i++) {

				File unpackDir = new File(scratchDir, jarId++ + "");
				unpackDir.mkdirs();
				unpackedJarDirs.add(unpackDir);

				unjar.setDest(unpackDir);
				unjar.setSrc(new File(scanner.getBasedir(), files[i]));
				unjar.execute();
			}
		}
	}

	void createDirectories() throws BuildException {

		if (!baseDir.isDirectory() && !baseDir.mkdirs()) {
			throw new BuildException("Can't create directory " + baseDir.getAbsolutePath());
		}

		File tmpDir = new File(System.getProperty("java.io.tmpdir"));
		String label = "japplication-" + System.currentTimeMillis();
		for (int i = 0; i < 10; i++) {
			File dir = new File(tmpDir, label + i);
			if (!dir.exists() && dir.mkdirs()) {
				this.scratchDir = dir;
				break;
			}
		}

		if (!scratchDir.isDirectory()) {
			throw new BuildException("Can't create scratch directory");
		}
	}

	boolean recursiveDelete(File file) {
		if (!file.exists()) {
			return true;
		}

		if (!file.isDirectory()) {
			return file.delete();
		}

		String[] list = file.list();
		for (int i = 0; i < list.length; i++) {
			if (!recursiveDelete(new File(file, list[i]))) {
				return false;
			}

		}

		return file.delete();
	}

	ManifestTask makeManifestTask() {
		ManifestTask manifest = new ManifestTask();
		manifest.setOwningTarget(task.getOwningTarget());
		manifest.setProject(task.getProject());
		manifest.setTaskName(task.getTaskName());
		manifest.setLocation(task.getLocation());
		return manifest;
	}

	Jar makeJarTask() {
		Jar jar = new Jar();
		jar.setOwningTarget(task.getOwningTarget());
		jar.setProject(task.getProject());
		jar.setTaskName(task.getTaskName());
		jar.setLocation(task.getLocation());
		return jar;
	}

	Expand makeUnjarTask() {
		Expand expand = new Expand();
		expand.setOwningTarget(task.getOwningTarget());
		expand.setProject(task.getProject());
		expand.setTaskName(task.getTaskName());
		expand.setLocation(task.getLocation());
		return expand;
	}
}
