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

import org.apache.tools.ant.taskdefs.Mkdir;

/**
 * Helper class that creates all necessary run scripts to start a
 * WebObjects application. It is used by WOApplication task. 
 *
 * @author Andrei Adamchik
 */
public class AppScriptBuilder extends TemplateProcessor {
	protected String pathSeparator = File.separator;

	public AppScriptBuilder(WOApplication task) {
		super(task);
	}

	protected WOApplication getAppTask() {
		return (WOApplication) task;
	}

	protected File getWindowsDir() {
		return new File(getAppTask().contentsDir(), "Windows");
	}

	protected File getUnixDir() {
		return new File(getAppTask().contentsDir(), "UNIX");
	}

	protected File getMacDir() {
		return new File(getAppTask().contentsDir(), "MacOS");
	}

	protected String replaceTokens(String line) {
		String tok1 = "@APP_JAR@";
		int i1 = line.indexOf(tok1);
		if (i1 >= 0) {
			return replace(
				tok1,
				line,
				"APPROOT"
					+ pathSeparator
					+ "Resources"
					+ pathSeparator
					+ "Java"
					+ pathSeparator
					+ getName().toLowerCase()
					+ ".jar");
		}

		String tok2 = "@FRAMEWORK_JAR@";
		int i2 = line.indexOf(tok2);
		if (i2 >= 0) {
			return replace(tok2, line, "# Framework goes here...");
		}

		return line;
	}

	public void buildScripts() throws IOException {
		buildWindows();
		buildUnix();
		buildMac();
	}

	protected void buildWindows() throws IOException {
		pathSeparator = "\\";

		File dir = getWindowsDir();
		File runScript = new File(dir, getName() + ".cmd");
		mkdir(dir);

		fileFromTemplate("woapp/Contents/Windows/appstart.cmd", runScript);
		fileFromTemplate(
			"woapp/Contents/Windows/CLSSPATH.TXT",
			new File(dir, "CLSSPATH.TXT"));
		fileFromTemplate(
			"woapp/Contents/Windows/SUBPATHS.TXT",
			new File(dir, "SUBPATHS.TXT"));
	}

	protected void buildUnix() throws IOException {
		pathSeparator = "/";

		mkdir(getUnixDir());
	}

	protected void buildMac() throws IOException {
		pathSeparator = "/";

		mkdir(getMacDir());
	}

	protected void mkdir(File dir) {
		Mkdir mkdir = new Mkdir();
		task.initChildTask(mkdir);
		mkdir.setDir(dir);
		mkdir.execute();
	}
}