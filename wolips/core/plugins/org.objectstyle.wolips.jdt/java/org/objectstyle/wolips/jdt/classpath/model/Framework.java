/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2004 The ObjectStyle Group 
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
package org.objectstyle.wolips.jdt.classpath.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;

/**
 * @author ulrich
 */
public class Framework {
	private String name;

	private Root root;

	private String jarFiles[];

	private String zipFiles[];

	private String order;

	private String sourceFile;

	private IPath javaDocPath;

	private IPath srcPath;

	private boolean exported = false;

	protected Framework(String name, Root root, String jarFiles[], String zipFiles[], String sourceFile) {
		this.name = name;
		this.root = root;
		this.jarFiles = jarFiles;
		this.zipFiles = zipFiles;
		this.sourceFile = sourceFile;
	}

	/**
	 * @return
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return
	 */
	public IPath[] getLibraryPaths() {
		List<IPath> arrayList = new ArrayList<IPath>(this.jarFiles.length + this.zipFiles.length);
		for (int i = 0; i < this.jarFiles.length; i++) {
			arrayList.add(this.libraryPath(this.jarFiles[i]));
		}
		for (int i = 0; i < this.zipFiles.length; i++) {
			arrayList.add(this.libraryPath(this.zipFiles[i]));
		}
		return arrayList.toArray(new IPath[arrayList.size()]);
	}

	private IPath libraryPath(String string) {
		return this.root.getRootPath().append(this.getName() + ".framework").append("Resources").append("Java").append(string);
	}

	/**
	 * @return
	 */
	public IPath getPath() {
		return this.root.getRootPath().append(this.name + ".framework");
	}

	/**
	 * @return Returns the root.
	 */
	public Root getRoot() {
		return this.root;
	}

	/**
	 * @return Returns the exported.
	 */
	public boolean isExported() {
		return exported;
	}

	/**
	 * @param exported
	 *            The exported to set.
	 */
	public void setExported(boolean exported) {
		this.exported = exported;
	}

	/**
	 * @return Returns the javaDocPath.
	 */
	public IPath getJavaDocPath() {
		return javaDocPath;
	}

	/**
	 * @param javaDocPath
	 *            The javaDocPath to set.
	 */
	public void setJavaDocPath(IPath javaDocPath) {
		this.javaDocPath = javaDocPath;
	}

	/**
	 * @return Returns the order.
	 */
	public String getOrder() {
		return order;
	}

	/**
	 * @param order
	 *            The order to set.
	 */
	public void setOrder(String order) {
		this.order = order;
	}

	/**
	 * @return Returns the srcPath.
	 */
	public IPath getSrcPath() {
		return this.srcPath;
	}

	/**
	 * @param srcPath
	 *            The srcPath to set.
	 */
	public void setSrcPath(IPath srcPath) {
		this.srcPath = srcPath;
	}

	/**
	 * @return
	 */
	public IPath getSourcePath() {
		IPath sourcePath = null;
		if (sourceFile != null)
			sourcePath = libraryPath(sourceFile);
		return sourcePath;
	}
}