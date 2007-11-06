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
package org.objectstyle.wolips.jdt.ant;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.objectstyle.wolips.core.resources.types.project.IProjectAdapter;
import org.objectstyle.wolips.variables.VariablesPlugin;

/**
 * @author mnolte
 * 
 */
public abstract class UpdateIncludeFiles {

	protected String[] rootPaths = null;

	private IPath[] paths = null;

	protected String INCLUDES_FILE_PREFIX;

	private IProject iProject;

	private IProjectAdapter projectAdapter;

	/**
	 * 
	 */
	public UpdateIncludeFiles() {
		this.paths = new IPath[] { VariablesPlugin.getDefault().getUserHome(), VariablesPlugin.getDefault().getLocalRoot(), VariablesPlugin.getDefault().getSystemRoot() };
		this.rootPaths = new String[] { "user.home", "wo.wolocalroot", "wo.wosystemroot" };
		if (this.paths[1].toOSString().length() < this.paths[2].toOSString().length()) {
			this.paths = new IPath[] { VariablesPlugin.getDefault().getUserHome(), VariablesPlugin.getDefault().getSystemRoot(), VariablesPlugin.getDefault().getLocalRoot() };
			this.rootPaths = new String[] { "user.home", "wo.wosystemroot", "wo.wolocalroot" };
		}
	}

	/**
	 * @return
	 */
	public IPath[] getPaths() {
		return this.paths;
	}

	/**
	 * Method buildIncludeFiles.
	 */
	protected abstract void buildIncludeFiles();

	/**
	 * @return
	 */
	public IProject getIProject() {
		return this.iProject;
	}

	/**
	 * @param iProject
	 */
	public void setIProject(IProject iProject) {
		this.iProject = iProject;
		this.projectAdapter = (IProjectAdapter) iProject.getAdapter(IProjectAdapter.class);
	}

	/**
	 * @return Returns the project.
	 */
	protected IProjectAdapter getProjectAdapter() {
		return this.projectAdapter;
	}
}
