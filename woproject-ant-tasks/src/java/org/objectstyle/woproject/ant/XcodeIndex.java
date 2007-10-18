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

import org.apache.tools.ant.BuildException;
import org.objectstyle.woenvironment.pb.PBXProject;
import org.objectstyle.woenvironment.pb.XcodeProjProject;
import org.objectstyle.woenvironment.pb.XcodeProject;

/**
 * @author Jonathan 'Wolf' Rentzsch
 */
public class XcodeIndex extends PBXIndex {
	private boolean myXcodeProj;

	/**
	 * If true, this outputs .xcodeproj format files from Xcode 2.1.
	 *
	 * @param _xcodeProj
	 *            whether or not to output .xcodeproj files
	 */
	public void setXcodeProj(boolean _xcodeProj) {
		myXcodeProj = _xcodeProj;
	}

	public boolean isXcodeProj() {
		return myXcodeProj;
	}

	/**
	 * @see org.apache.tools.ant.Task#execute()
	 */
	@Override
	public void execute() throws BuildException {
		validateAttributes();

		PBXProject proj;
		if (myXcodeProj) {
			proj = new XcodeProjProject();
		} else {
			proj = new XcodeProject();
		}
		addToProject(proj);

		if (getProjectFile().exists()) {
			if (!getProjectFile().isDirectory())
				throw new BuildException("Specified Xcode project package is not a directory.");
		} else
			getProjectFile().mkdir();
		File pbxprojFile = new File(getProjectFile(), "project.pbxproj");
		if (!pbxprojFile.exists()) {
			try {
				pbxprojFile.createNewFile();
			} catch (IOException x) {
				throw new BuildException("Failed to create project.pbxproj Xcode project package file: " + x);
			}
		}

		proj.save(pbxprojFile);
	}
}
