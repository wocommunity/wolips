/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2002 - 2004 The ObjectStyle Group 
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
package org.objectstyle.wolips.jdt.classpath;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.objectstyle.wolips.jdt.JdtPlugin;
import org.objectstyle.wolips.jdt.classpath.model.Framework;

/**
 * @author ulrich
 */
public class ContainerEntry implements Comparable {
	private List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();

	private String name;

	private String order;

	private IPath srcPath;

	private IPath javaDocPath;

	private boolean exported = false;

	/**
	 * @param name
	 * @param srcPath
	 * @param javaDocPath
	 * @param order
	 * @param exported
	 */
	public ContainerEntry(String name, IPath srcPath, IPath javaDocPath, String order, String exported) {
		super();
		this.name = name;
		this.order = order;
		this.srcPath = srcPath;
		this.javaDocPath = javaDocPath;

		if (this.srcPath != null && "nil".equals(this.srcPath.toString())) {
			this.srcPath = null;
		}
		if (this.javaDocPath != null && "nil".equals(this.javaDocPath.toString())) {
			this.javaDocPath = null;
		}
		if ("true".equals(exported)) {
			this.exported = true;
		}

		JdtPlugin.getDefault().getPluginLogger().debug("name : " + this.name);
		JdtPlugin.getDefault().getPluginLogger().debug("order : " + this.order);
		JdtPlugin.getDefault().getPluginLogger().debug("srcPath : " + this.srcPath);
		JdtPlugin.getDefault().getPluginLogger().debug("javaDocPath : " + this.javaDocPath);
		JdtPlugin.getDefault().getPluginLogger().debug("exported : " + this.exported);
		Framework framework = JdtPlugin.getDefault().getClasspathModel().getFrameworkWithName(this.name);
		if (framework != null) {
			IPath[] libraryPaths = framework.getLibraryPaths();
			for (int i = 0; i < libraryPaths.length; i++) {
				IClasspathEntry entry = JavaCore.newLibraryEntry(libraryPaths[i], framework.getSourcePath(), this.javaDocPath, this.exported);
				this.entries.add(entry);
			}
		} else {
			// TODO: Was nun?
		}
	}

	/**
	 * @param name
	 */
	public ContainerEntry(String name) {
		this(name, null, null, null, null);
	}
	/**
	 * @param path
	 * @return
	 * @throws PathCoderException
	 */
	public static ContainerEntry initWithPath(IPath path) throws PathCoderException {
		IPath[] details = PathCoder.decode(path);
		return new ContainerEntry(details[0].toString(), details[1], details[2], details[3].toString(), details[4].toString());
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return Returns the order.
	 */
	public int getOrder() {
		return Integer.parseInt(this.order);
	}

	/**
	 * @param order
	 *            The order to set.
	 */
	public void setOrder(int order) {
		this.order = "" + order;
	}

	/**
	 * @return Returns the entries.
	 */
	public List<IClasspathEntry> getEntries() {
		return this.entries;
	}

	/**
	 * @return
	 */
	public IPath getPath() {
		IPath path = new Path("");
		path = path.append(PathCoder.encode(new Path(this.name)));
		path = path.append(PathCoder.encode(this.srcPath));
		path = path.append(PathCoder.encode(this.javaDocPath));
		if (this.order == null) {
			JdtPlugin.getDefault().getPluginLogger().log(this.name + ": null order, setting to '0'");
			this.order = "0";
		}
		path = path.append(PathCoder.encode(new Path(this.order)));
		if (this.isExported()) {
			path = path.append(PathCoder.encode(new Path("true")));
		} else {
			path = path.append(PathCoder.encode(new Path("false")));
		}
		return path;
	}

	/**
	 * @return Returns the exported.
	 */
	protected boolean isExported() {
		return this.exported;
	}

	/**
	 * @param framework
	 */
	public void push(Framework framework) {
		entries = new ArrayList<IClasspathEntry>();
		this.srcPath = framework.getSrcPath();
		this.javaDocPath = framework.getJavaDocPath();
		this.order = framework.getOrder();
		this.exported = framework.isExported();
		IPath[] libraryPaths = framework.getLibraryPaths();
		for (int j = 0; j < libraryPaths.length; j++) {
			IClasspathEntry entry = JavaCore.newLibraryEntry(libraryPaths[j], framework.getSrcPath(), this.javaDocPath, this.exported);
			this.entries.add(entry);
		}
		if (this.order == null) {
			order = "0";
		}
	}

	/**
	 * @param framework
	 */
	public void pull(Framework framework) {
		framework.setSrcPath(this.srcPath);
		framework.setJavaDocPath(this.javaDocPath);
		framework.setOrder(this.order);
		framework.setExported(this.exported);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object object) {
		ContainerEntry compareTo = (ContainerEntry) object;
		Integer left = new Integer(0);
		Integer right = new Integer(0);
		if (this.order != null && this.order.length() > 0) {
			try {
				left = new Integer(this.order);
			} catch (NumberFormatException numberFormatException) {
				JdtPlugin.getDefault().getPluginLogger().log(numberFormatException);
			}
		}
		if (compareTo.order != null && compareTo.order.length() > 0) {
			try {
				right = new Integer(compareTo.order);
			} catch (NumberFormatException numberFormatException) {
				JdtPlugin.getDefault().getPluginLogger().log(numberFormatException);
			}
		}
		return left.compareTo(right);
	}
}