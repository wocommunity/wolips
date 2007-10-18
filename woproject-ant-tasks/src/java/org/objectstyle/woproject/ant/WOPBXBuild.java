/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2002 - 2006 The ObjectStyle Group
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
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.objectstyle.woenvironment.pbx.PBXBuildFile;
import org.objectstyle.woenvironment.pbx.PBXProject;
import org.objectstyle.woenvironment.pbx.PBXProjectCoder;
import org.objectstyle.woenvironment.pbx.PBXResourcesBuildPhase;
import org.objectstyle.woenvironment.pbx.PBXSourcesBuildPhase;
import org.objectstyle.woenvironment.pbx.PBXTarget;
import org.objectstyle.woenvironment.util.Parser;

/**
 * This Task uses a WOTask to builds either frameworks or WOApps
 *
 * @author tlg
 *
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class WOPBXBuild extends Task {
	protected String pbxproj;

	protected WOTask task;

	protected Vector<FileSet> classes = new Vector<FileSet>();

	protected String name = "";

	protected String destDir = "";

	public void setPbxproj(String pbxproj) {
		this.pbxproj = pbxproj;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDestDir(String destDir) {
		this.destDir = destDir;
	}

	public String getPbxproj() {
		return this.pbxproj;
	}

	public void addClasses(FileSet set) {
		this.classes.add(set);
	}

	@Override
	public void execute() throws BuildException {
		Map dico = null;
		Enumeration<FileSet> e;
		/**
		 * Get Dictionary from pbproj
		 */
		try {
			dico = (Map) new Parser(new File(new Path(this.getProject(), getPbxproj()).toString())).propertyList();
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
		/**
		 * TODO: Handle Better Warnings
		 */
		if (dico == null)
			return;
		/**
		 * Objects contains all probject related objects
		 */
		Map objects = (Map) dico.get("objects");
		String rootObject = (String) dico.get("rootObject");
		PBXProjectCoder coder = new PBXProjectCoder(objects, rootObject);
		String sourcesFiles = this.setUp(coder, rootObject);
		coder = null;
		objects = null;
		dico = null;
		/**
		 * Compile sources
		 */
		Javac javac = new Javac();
		task.getSubtaskFactory().initChildTask(javac);
		javac.setClasspathRef(new Reference(this.getProject(), "classpath"));
		javac.setDestdir(new File(this.getProject().getBaseDir(), "build"));
		javac.setSrcdir(new Path(this.getProject(), "."));
		javac.setIncludes(sourcesFiles);
		javac.setOptimize(true);
		javac.execute();
		/**
		 * Add classes to task
		 */
		e = classes.elements();
		while (e.hasMoreElements()) {
			task.addClasses((WOFileSet) e.nextElement());
		}
		task.execute();
	}

	private String setUp(PBXProjectCoder coder, String rootObject) {
		Iterator i;
		PBXProject pbxProject = (PBXProject) coder.objectForRef(rootObject);
		PBXTarget mainTaget = (PBXTarget) pbxProject.getTargets().get(0);
		PBXTarget appTarget = (PBXTarget) pbxProject.getTargets().get(1);
		PBXTarget webTarget = (PBXTarget) pbxProject.getTargets().get(2);
		PBXSourcesBuildPhase javaPhase = null;
		PBXResourcesBuildPhase resourcesPhase = null;
		PBXResourcesBuildPhase webResourcesPhase = null;
		/**
		 * get build pahses (needed to find the files)
		 */
		i = appTarget.getBuildPhases().iterator();
		while (i.hasNext()) {
			Object o = i.next();
			if (o.getClass() == PBXSourcesBuildPhase.class) {
				javaPhase = (PBXSourcesBuildPhase) o;
			}
			if (o.getClass() == PBXResourcesBuildPhase.class) {
				resourcesPhase = (PBXResourcesBuildPhase) o;
			}
		}
		i = webTarget.getBuildPhases().iterator();
		while (i.hasNext()) {
			Object o = i.next();
			if (o.getClass() == PBXResourcesBuildPhase.class) {
				webResourcesPhase = (PBXResourcesBuildPhase) o;
			}
		}
		Collection javaFiles = javaPhase.getFiles();
		Collection resourcesFiles = resourcesPhase.getFiles();
		Collection webResourcesFiles = webResourcesPhase.getFiles();
		/**
		 * Setup the WOTask
		 */
		String isa = mainTaget.getProductReference().getIsa();
		if (isa.equals("PBXApplicationReference")) {
			task = new WOApplication();
		} else if (isa.equals("PBXFrameworkReference")) {
			task = new WOFramework();
		}
		task.setOwningTarget(this.getOwningTarget());
		task.setProject(this.getProject());
		task.setTaskName(this.getTaskName());
		task.setLocation(this.getLocation());
		task.setName(this.name);
		task.setDestDir(this.destDir);
		/**
		 * gather java sources files
		 */
		String sourcesFiles = "";
		i = javaFiles.iterator();
		while (i.hasNext()) {
			String file = ((PBXBuildFile) i.next()).getPath();
			sourcesFiles += file + ",";
		}
		/**
		 * gather resources files
		 */
		i = resourcesFiles.iterator();
		while (i.hasNext()) {
			WOFileSet set = new WOFileSet();
			File tempFile = new File(this.getProject().getBaseDir(), ((PBXBuildFile) i.next()).getPath());
			set.setProject(this.getProject());
			if (tempFile.isDirectory()) {
				set.setDir(tempFile.getParentFile());
				set.setIncludes(tempFile.getName() + "/*");
			} else
				set.setFile(tempFile);
			task.addResources(set);
		}
		/**
		 * gather web resources files
		 */
		i = webResourcesFiles.iterator();
		while (i.hasNext()) {
			WOFileSet set = new WOFileSet();
			File tempFile = new File(this.getProject().getBaseDir(), ((PBXBuildFile) i.next()).getPath());
			set.setProject(this.getProject());
			if (tempFile.isDirectory()) {
				set.setDir(tempFile.getParentFile());
				set.setIncludes(tempFile.getName() + "/*");
			} else
				set.setFile(tempFile);
			task.addWsresources(set);
		}
		return sourcesFiles;
	}
}
