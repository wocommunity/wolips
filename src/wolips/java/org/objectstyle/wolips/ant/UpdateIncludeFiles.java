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
package org.objectstyle.wolips.ant;

import java.util.TreeMap;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * @author mnolte
 *
 */
public abstract class UpdateIncludeFiles extends Task {

	protected static final String[] ROOT_PATHS =
		{ "user.home", "wo.wolocalroot", "wo.woroot" };

	private static String[] sortedRootPaths;

	protected String INCLUDES_FILE_PREFIX;

	private String projectName;
	protected IProject actualProject;

	protected abstract void buildIncludeFiles() throws BuildException;

	protected abstract void validateAttributes() throws BuildException;

	/**
	 * Method sortedRootPaths. Array of root path keys ordered by the
	 * segmentCount of the underlying <code>Path</code>
	 * @return String[]
	 */
	protected String[] sortedRootPaths() {
		// IMPROVE ME this poor algorithm is as result of the programmer's bad cold
		if (sortedRootPaths == null) {
			TreeMap map = new TreeMap();
			IPath currentRootPath = null;
			int offSet = 0;
			for (int i = 0; i < ROOT_PATHS.length; i++) {
				currentRootPath = this.getROOTPATHProperty(ROOT_PATHS[i]);
				if (map
					.get(
						new Integer(
							currentRootPath.segmentCount() * 10 + offSet))
					!= null) {
					offSet++;
				}
				map.put(
					new Integer(currentRootPath.segmentCount() * 10 + offSet),
					ROOT_PATHS[i]);

			}
			sortedRootPaths = new String[ROOT_PATHS.length];
			int sortedIndex = 0;
			while (map.size() > 0) {
				sortedRootPaths[sortedIndex++] =
					(String) map.get(map.lastKey());
				map.remove(map.lastKey());
			}
		}
		System.out.println("sortedRootPaths: " + sortedRootPaths.toString());
		for(int x=0;x<sortedRootPaths.length;x++) {
			System.out.println("sortedRootPaths" + x + ": " + sortedRootPaths[x]);
		}
		return sortedRootPaths;
	}

	/**
	 * Returns the projectName.
	 * @return String
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * Sets the projectName.
	 * @param projectName The projectName to set
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	protected IPath getROOTPATHProperty(String aProperty) {
		//Workaround for MacOSX otherwise any jar seems to be under the wolocalroot
		IPath currentRootPath = new Path(project.getProperty(aProperty));
		System.out.println("currentRootPath: " + currentRootPath.toOSString());
		if(currentRootPath != null && "/".equals(currentRootPath.toOSString()))
			currentRootPath = currentRootPath.append("Library/");
		System.out.println("currentRootPath: " + currentRootPath.toOSString());
		return currentRootPath;
	}
}
