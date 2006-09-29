/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2006 The ObjectStyle Group,
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
package org.objectstyle.woproject.maven2;

import java.io.File;
import java.util.Iterator;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.objectstyle.woproject.ant.JApplication;

/**
 * @goal japplication
 * @requiresDependencyResolution compile
 * @author andrus
 */
public class JApplicationMojo extends DependencyMojo {

	/**
	 * The name of the application without OS-specific extension
	 * 
	 * @parameter expression="${name}"
	 */
	protected String name;

	/**
	 * Main Java class
	 * 
	 * @parameter expression="${mainClass}"
	 * @required
	 */
	protected String mainClass;

	/**
	 * A family of operating systems. Currently supported values are "mac",
	 * "windows" and "java".
	 * 
	 * @parameter expression="${os}"
	 */
	protected String os;

	/**
	 * An optional string identifying the application human-readable name. If
	 * not specified, "name" is used.
	 * 
	 * @parameter expression="${longName}"
	 */
	protected String longName;

	/**
	 * A destination directory where the application launcher should be
	 * installed.
	 * 
	 * @parameter expression="${destDir}"
	 *            default-value="${project.build.directory}
	 */
	protected File destDir;

	/**
	 * Platform-specific icon file (usually "*.ico" on Windows and "*.icns" on
	 * Mac)
	 * 
	 * @parameter expression="${icon}"
	 */
	protected File icon;

	/**
	 * Minimal version of the Java Virtual machine required.
	 * 
	 * @parameter expression="${jvm}"
	 */
	protected String jvm;

	/**
	 * Optional parameters to pass to the JVM, such as memory settings, etc.
	 * 
	 * @parameter expression="${jvmOptions}"
	 */
	protected String jvmOptions;

	/**
	 * Location of NSIS installation. Ignored except on Windows.
	 * 
	 * @parameter expression="${nsisHome}"
	 */
	protected String nsisHome;

	/**
	 * Contains the full list of projects in the reactor.
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	protected MavenProject project;

	public void execute() throws MojoExecutionException, MojoFailureException {

		getLog().debug(
				"JApplication [name: " + name + "; mainClass:" + mainClass
						+ "; destDir:" + destDir.getAbsolutePath() + "]");

		JApplication task = new JApplication();

		// TODO, andrus, 9/28/2006 - hook up maven loggers to the Ant project.
		task.setProject(new Project());

		// build name from the artifact if not set
		if (name == null) {
			String prefix = project.getArtifact().getArtifactId();
			String suffix = (os != null) ? os : task.getDefaultOs();
			name = prefix + "-" + suffix;
		}

		String fullName = name + "-" + project.getArtifact().getVersion();

		task.setName(fullName);
		task.setMainClass(mainClass);
		task.setDestDir(destDir);
		task.setOs(os);
		task.setLongName(longName);
		task.setIcon(icon);
		task.setJvm(jvm);
		task.setJvmOptions(jvmOptions);
		task.setNsisHome(nsisHome);
		task.setVersion(project.getArtifact().getVersion());

		// TODO: andrus, 9/28/2006 - we are bundling all external dependencies
		// and a current artifact. This will likely break if one of the
		// dependencies is in the reactor... need to test (and fix) this case.

		Iterator it = getDependencies().iterator();
		while (it.hasNext()) {
			Artifact a = (Artifact) it.next();
			addArtifact(task, a);
		}

		// add main project artifact
		addArtifact(task, project.getArtifact());

		try {
			task.execute();
		} catch (BuildException e) {
			throw new MojoExecutionException("Failed to build application "
					+ name, e);
		}
	}

	protected void addArtifact(JApplication task, Artifact artifact) {

		getLog().debug("packaging artifact '" + artifact.getId() + "'...");

		FileSet fs = new FileSet();
		fs.setFile(artifact.getFile());
		task.addLib(fs);
	}
}
