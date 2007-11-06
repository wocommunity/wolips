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
package org.objectstyle.wolips.jdt.classpath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.objectstyle.wolips.jdt.classpath.model.Framework;

/**
 * @author ulrich
 */
public class ContainerEntries {
	private List<ContainerEntry> entries = new ArrayList<ContainerEntry>();

	/**
	 * 
	 */
	public ContainerEntries() {
		super();
	}

	/**
	 * @param path
	 * @return a container with the entries from the path
	 * @throws PathCoderException
	 */
	public static ContainerEntries initWithPath(IPath path) throws PathCoderException {
		ContainerEntries containerEntries = new ContainerEntries();
		IPath[] entries = PathCoder.decode(path);
		for (int i = 0; i < entries.length; i++) {
			ContainerEntry containerEntry = ContainerEntry.initWithPath(entries[i]);
			containerEntries.add(containerEntry);
		}
		return containerEntries;
	}

	/**
	 * @return
	 */
	public IClasspathEntry[] getEntries() {
		List<IClasspathEntry> arrayList = new ArrayList<IClasspathEntry>();
		for (int i = 0; i < this.entries.size(); i++) {
			ContainerEntry containerEntry = this.entries.get(i);
			arrayList.addAll(containerEntry.getEntries());
		}
		return arrayList.toArray(new IClasspathEntry[this.entries.size()]);
	}

	/**
	 * @return
	 */
	public IPath getPath() {
		IPath path = new Path("");
		for (int i = 0; i < this.entries.size(); i++) {
			ContainerEntry containerEntry = this.entries.get(i);
			IPath entryPath = containerEntry.getPath();
			path = path.append(PathCoder.encode(entryPath));
		}
		return path;
	}

	/**
	 * @param containerEntry
	 */
	public void add(ContainerEntry containerEntry) {
		this.entries.add(containerEntry);
	}

	/**
	 * @param framework
	 * @return
	 */
	public boolean contains(Framework framework) {
		ContainerEntry containerEntry = this.getEntry(framework);
		return containerEntry != null;
	}

	/**
	 * @param framework
	 * @return
	 */
	public ContainerEntry getEntry(Framework framework) {
		for (int i = 0; i < this.entries.size(); i++) {
			ContainerEntry containerEntry = this.entries.get(i);
			if (framework.getName().equals(containerEntry.getName())) {
				return containerEntry;
			}
		}
		return null;
	}

	/**
	 * @param entries
	 */
	public void setEntries(List<ContainerEntry> entries) {
		ContainerEntry[] containerEntries = entries.toArray(new ContainerEntry[entries.size()]);
		Arrays.sort(containerEntries);
		this.entries = new ArrayList<ContainerEntry>();
		for (int i = 0; i < containerEntries.length; i++) {
			this.entries.add(containerEntries[i]);
		}
	}

	/**
	 * @param containerEntries
	 */
	public void add(ArrayList<ContainerEntry> containerEntries) {
		if(containerEntries == null) {
			return;
		}
		for (int i = 0; i < containerEntries.size(); i++) {
			ContainerEntry containerEntry = containerEntries.get(i);
			this.add(containerEntry);
		}
	}
}
