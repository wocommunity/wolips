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
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;

/**
 * Customized subclass of FileSet used to locate frameworks.
 *
 * @author Andrei Adamchik
 */
public class FrameworkSet extends FileSet {
	protected String root;
	protected boolean embed = false;

	/** 
	 * Creates new FrameworkSet.
	 */
	public FrameworkSet() {
		super();
	}

	public String getRoot() {
		return root;
	}

	/** 
	 * Returns a symbolic root prefix that can be used in
	 * classpath files (like CLASSPATH.TXT).
	 */
	public String getRootPrefix() throws BuildException {
		if (isWORoot()) {
			return AntStringUtils.replace(root, WOPropertiesHandler.WO_ROOT, "WOROOT");
		} else if (isHomeRoot()) {
			return AntStringUtils.replace(root, WOPropertiesHandler.HOME_ROOT, "HOMEROOT");
		} else if (isLocalRoot()) {
			return AntStringUtils.replace(root, WOPropertiesHandler.LOCAL_ROOT, "LOCALROOT");
		} else if (isAbsoluteRoot()) {
			return getRoot();
		} else {
			throw new BuildException("Unrecognized or indefined root: " + root);
		}
	}

	public boolean isWORoot() {
		return root.startsWith(WOPropertiesHandler.WO_ROOT);
	}

	public boolean isHomeRoot() {
		return root.startsWith(WOPropertiesHandler.HOME_ROOT);
	}

	public boolean isLocalRoot() {
		return root.startsWith(WOPropertiesHandler.LOCAL_ROOT);
	}

	public boolean isAbsoluteRoot() {
		return root.charAt(0) == '/';
	}

	/** 
	 * Overrides parent to discard the value. A warning 
	 * will be printed if log level is high enough.
	 */
	public void setDir(File dir) throws BuildException {
		// noop
		log(
			"FrameworkSet does not support 'dir' attribute, ignoring.",
			Project.MSG_WARN);
	}

	/** 
	 * Sets root directory of this FileSet based on a symbolic name, 
	 * that can be "wo.homeroot", "wo.woroot", "wo.localroot". Throws
	 * BuildException if an invalid root is specified.
	 */
	public void setRoot(String root) throws BuildException {
		this.root = root;

		WOPropertiesHandler propsHandler =
			new WOPropertiesHandler(this.getProject());

		if (isWORoot()) {
			String newRoot = AntStringUtils.replace(root, WOPropertiesHandler.WO_ROOT, propsHandler.getWORoot());
			super.setDir(new File(newRoot));
		} else if (isLocalRoot()) {
			String newRoot = AntStringUtils.replace(root, WOPropertiesHandler.LOCAL_ROOT, propsHandler.getLocalRoot());
			super.setDir(new File(newRoot));
		} else if (isHomeRoot()) {
			String newRoot = AntStringUtils.replace(root, WOPropertiesHandler.HOME_ROOT, propsHandler.getHomeRoot());
		} else if (isAbsoluteRoot()) {
			super.setDir(new File(root));
		} else {
			throw new BuildException("Unrecognized root: " + root);
		}
	}

	public void setEmbed(boolean flag) {
		this.embed = flag;
	}

	public boolean getEmbed() {
		return this.embed;
	}

	public String[] findJars(Project project, String frameworkDir) {
		String jarDirName =
			frameworkDir
				+ File.separator
				+ "Resources"
				+ File.separator
				+ "Java";

		File jarDir = new File(getDir(project), jarDirName);
		if (!jarDir.isDirectory()) {
			return null;
		}

		String[] files = jarDir.list(new JarFilter());
		
		// prepend path
		String[] finalFiles = new String[files.length];
		for(int i = 0; i < finalFiles.length; i++) {
			finalFiles[i] = jarDirName + File.separator + files[i];
		}
		
		return finalFiles;
	}


	class JarFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			return name.endsWith(".jar");
		}
	}
}
