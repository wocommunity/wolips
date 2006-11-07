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
import java.util.HashMap;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.ExecTask;

class JApplicationWindowsWorker extends JApplicationJavaWorker {

	static final String EMBEDDED_NSIS_PATH = "japplication/windows/nsis-2.20";

	protected String nsisExe;

	protected File nsiScript;

	protected void executeInternal() throws BuildException {
		// build fat runnable jar
		super.executeInternal();

		createNsisScript();
		initNsis();
		execNsis();
	}

	void execNsis() throws BuildException {
		ExecTask exec = (ExecTask) task.createSubtask(ExecTask.class);
		exec.setDir(baseDir);
		exec.setExecutable(nsisExe);
		exec.setFailonerror(true);

		exec.createArg().setLine(nsiScript.getAbsolutePath());

		exec.execute();
	}

	void initNsis() throws BuildException {

		task.log("Extracting embedded NSIS", Project.MSG_DEBUG);

		File nsisDir = new File(scratchDir, "nsis");

		// extract embedded NSIS into the scratch directory
		extractResource("makensis.exe", nsisDir);
		extractResource("Stubs/bzip2", nsisDir);
		extractResource("Stubs/bzip2_solid", nsisDir);
		extractResource("Stubs/lzma", nsisDir);
		extractResource("Stubs/lzma_solid", nsisDir);
		extractResource("Stubs/uninst", nsisDir);
		extractResource("Stubs/zlib", nsisDir);
		extractResource("Stubs/zlib_solid", nsisDir);

		this.nsisExe = new File(nsisDir, "makensis.exe").getAbsolutePath();
	}

	void createNsisScript() throws BuildException {

		String targetIcon = task.getIcon() != null && task.getIcon().isFile() ? "Icon \"" + task.getIcon().getAbsolutePath() + "\"" : "";
		String jvmOptions = task.getJvmOptions() != null ? task.getJvmOptions() : "";
		String outFile = new File(baseDir, task.getName() + ".exe").getAbsolutePath();

		Map tokens = new HashMap();
		tokens.put("@NAME@", task.getName());
		tokens.put("@LONG_NAME@", task.getLongName());
		tokens.put("@MAIN_CLASS@", task.getMainClass());
		tokens.put("@ICON@", targetIcon);
		tokens.put("@JVM_OPTIONS@", jvmOptions);
		tokens.put("@OUT_FILE@", outFile);

		this.nsiScript = new File(scratchDir, "app.nsi");
		new TokenFilter(tokens).copy("japplication/windows/app.nsi", nsiScript);
	}

	void extractResource(String resourceName, File dir) {
		String path = EMBEDDED_NSIS_PATH + '/' + resourceName;

		String name = ('/' != File.separatorChar) ? resourceName.replace('/', File.separatorChar) : resourceName;
		FileUtil.copy(path, new File(dir, name));
	}
}
