/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2002, 2004 The ObjectStyle Group
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FilterSet;
import org.apache.tools.ant.types.FilterSetCollection;

/**
 * Abstract helper class that defines the algorithm for autogeneration of
 * standard project files needed for deployment of the applications and
 * frameworks. In a way it "formats" deployed project.
 *
 * @author Andrei Adamchik
 */
public abstract class ProjectFormat {
	protected WOTask task;

	/**
	 * Creates new TemplateFilter and initializes it with the name of the
	 * project being built.
	 */
	public ProjectFormat(WOTask task) {
		this.task = task;
	}

	/**
	 * Returns a name of WebObjects project being built.
	 */
	public String getName() {
		return task.getName();
	}

	/**
	 * Returns a version of WebObjects project being built.
	 */
	public String getVersion() {
		return task.getVersion();
	}

	/**
	 * Returns a CFBundleVersion string of WebObjects project being built.
	 */
	public String getCFBundleVersion() {
		return task.getCFBundleVersion();
	}

	/**
	 * Returns a CFBundleShortVersion string of WebObjects project being built.
	 */
	public String getCFBundleShortVersion() {
		return task.getCFBundleShortVersion();
	}

	/**
	 * Returns a CFBundleIdentifier string of WebObjects project being built.
	 */
	public String getCFBundleID() {
		return task.getCFBundleID();
	}

	/**
	 * JVM selector string in Info.plist<br>
	 * As specified by <a href="http://developer.apple.com/documentation/Java/Conceptual/JavaPropVMInfoRef/Articles/JavaDictionaryInfo.plistKeys.html">Apple Documentation</a>
	 * @return
	 */
	public String getJavaVersion() {
		return task.getJavaVersion();
	}

	/**
	 * Returns a name of the jar WebObjects project being built with ".jar"
	 * appended.
	 */
	public String getJarName() {
		return task.getJarName() + ".jar";
	}

	/**
	 * Creates all needed files based on WOProject templates. This is a main
	 * worker method. Returns true when a template is written.
	 */
	public boolean processTemplates() throws BuildException {
		Iterator it = fileIterator();
		boolean returnValue = false;
		try {
			ClassLoader cl = this.task.getClass().getClassLoader();
			if (cl == null) {
				// cl = ClassLoader.getSystemClassLoader();
				throw new BuildException("Could not load classloader");
			}
			while (it.hasNext()) {
				String targetName = (String) it.next();
				String templName = templateForTarget(targetName);
				FilterSetCollection filters = filtersForTarget(targetName);

				InputStream template = cl.getResourceAsStream(templName);
				File target = new File(targetName);
				if (copyFile(template, target, filters))
					returnValue = true;
			}
		} catch (IOException ioex) {
			throw new BuildException("Error doing project formatting.", ioex);
		} finally {
			it = null;
		}
		return returnValue;
	}

	/**
	 * Returns an iterator over String objects that specify paths of the files
	 * to be created during the build process.
	 */
	public abstract Iterator fileIterator();

	/**
	 * Returns a path to the template that should be used to build a target
	 * file.
	 */
	public abstract String templateForTarget(String targetName) throws BuildException;

	/**
	 * Returns a FilterSetCollection that should be applied when generating a
	 * target file.
	 */
	public abstract FilterSetCollection filtersForTarget(String targetName) throws BuildException;

	/**
	 * Convienence method to copy a file from a source to a destination
	 * specifying if token filtering must be used.
	 *
	 * <p>
	 * <i>This method is copied from Ant FileUtils with some changes and
	 * simplifications. FileUtils can't be used directly, since its API doesn't
	 * allow InputStreams for the source file. </i>
	 * </p>
	 *
	 * @throws IOException
	 *             Returns true when a .sh file is copied.
	 */
	public boolean copyFile(InputStream src, File destFile, FilterSetCollection filters) throws IOException {
		log("destFile.getName(): " + destFile.getName() + " this.getName(): " + this.getName(), Project.MSG_VERBOSE);
		if (destFile.exists() && destFile.isFile() && destFile.getName().equals(this.getName())) {
			// these files only need an update when a new Version of WO is
			// installed.
			// A clean in that case is better.
			// destFile.delete();
			src.close();
			return false;
		}
		if (destFile.exists() && destFile.isFile()) {
			destFile.delete();
		}

		// ensure that parent dir of dest file exists!
		// not using getParentFile method to stay 1.1 compat
		File parent = new File(destFile.getParent());
		if (!parent.exists()) {
			parent.mkdirs();
		}

		if (filters != null && filters.hasFilters()) {
			BufferedReader in = new BufferedReader(new InputStreamReader(src));
			BufferedWriter out = new BufferedWriter(new FileWriter(destFile));
			log("filters: " + filters, Project.MSG_VERBOSE);
			String newline = null;
			String line = in.readLine();
			while (line != null) {
				if (line.length() == 0) {
					out.newLine();
				} else {
					log("line: " + line, Project.MSG_VERBOSE);
					newline = filters.replaceTokens(line);
					log("newline: " + newline, Project.MSG_VERBOSE);
					out.write(newline);
					out.newLine();
				}
				line = in.readLine();
			}

			out.close();
			in.close();
		} else {
			FileOutputStream out = new FileOutputStream(destFile);

			byte[] buffer = new byte[8 * 1024];
			int count = 0;
			do {
				out.write(buffer, 0, count);
				count = src.read(buffer, 0, buffer.length);
			} while (count != -1);

			src.close();
			out.close();
		}
		return destFile.getName().equals(this.getName());
	}

	/**
	 * Returns a string that can be used in Info.plist file to indicate JARs
	 * required by the project.
	 */
	private String libString(Iterator extLibs) {
		String endLine = System.getProperty("line.separator");
		StringBuffer buf = new StringBuffer();

		buf.append("<array>");
		if (task.hasClasses()) {
			buf.append(endLine).append("\t\t<string>").append(getJarName()).append("</string>");
		}

		if (extLibs != null) {
			while (extLibs.hasNext()) {
				String libFile = (String) extLibs.next();
				buf.append(endLine).append("\t\t<string>");
				buf.append(libFile);
				buf.append("</string>");
			}
		}
		buf.append(endLine).append("\t</array>");
		return buf.toString();
	}

	/**
	 * Returns a string that can be used in Info.plist file to indicate the
	 * principal class for the framework or app.
	 */
	private String principalClassString() {
		String endLine = System.getProperty("line.separator");
		StringBuffer buf = new StringBuffer();
		if (task.getPrincipalClass() != null && task.getPrincipalClass().length() > 0) {
			buf.append("<key>NSPrincipalClass</key>").append(endLine);
			buf.append("\t<string>").append(task.getPrincipalClass()).append("</string>").append(endLine);
		}
		return buf.toString();
	}

	/**
	 * Returns a string that can be used in Info.plist file to indicate the
	 * principal class for the framework or app.
	 */
	private String getCustomInfoPListContent() {
		String string = task.getCustomInfoPListContent();
		if (string != null) {
			return string;
		}
		return "";
	}

	/**
	 * Returns a FilterSet that can be used to build Info.plist file.
	 */
	public FilterSetCollection infoFilter(Iterator extLibs) {
		FilterSet filter = new FilterSet();

		filter.addFilter("PRINCIPAL_CLASS", principalClassString());
		filter.addFilter("NAME", getName());
		filter.addFilter("VERSION", getVersion());
		filter.addFilter("JAVA_VERSION", getJavaVersion());
		filter.addFilter("JAR_NAME", getJarName());
		filter.addFilter("JAR_ARRAY", libString(extLibs));
		filter.addFilter("CUSTOM_CONTENT", getCustomInfoPListContent());
		filter.addFilter("HAS_COMPONENTS", "<" + hasComponents() + "/>");
		filter.addFilter("CFBUNDLE_VERSION", getCFBundleVersion());
		filter.addFilter("CFBUNDLE_SHORTVERSION", getCFBundleShortVersion());
		filter.addFilter("CFBUNDLE_IDENTIFIER", getCFBundleID());

		return new FilterSetCollection(filter);
	}

	private boolean hasComponents() {
		return task.getHasComponents();
	}

	public void log(String msg) {
		task.log(msg);
	}

	public void log(String msg, int msgLevel) {
		task.log(msg, msgLevel);
	}

	public void release() {
		task = null;
	}
}