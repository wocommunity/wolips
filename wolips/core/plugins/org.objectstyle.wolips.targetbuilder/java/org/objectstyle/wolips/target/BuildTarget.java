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

package org.objectstyle.wolips.target;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;

public class BuildTarget {
	private String _name;

	private List _sourcePaths;

	private List _classPath;

	private List _projectClassPath;

	private IPath _outputLocation;

	public BuildTarget(Map targetMap) {
		super();

		Object value;

		if ((value = targetMap.get(TargetBuilderNature.NAME)) != null)
			setName((String) value);
		if ((value = targetMap.get(TargetBuilderNature.OUTPUT)) != null)
			setOutputLocation(new Path((String) value));
		if ((value = targetMap.get(TargetBuilderNature.SOURCE)) != null)
			setSourcePath((List) value);
		if ((value = targetMap.get(TargetBuilderNature.CLASSPATH)) != null)
			setClassPath((List) value);
		if ((value = targetMap.get(TargetBuilderNature.PROJECTCLASSPATH)) != null)
			setProjectClassPath((List) value);
	}

	public void setName(String value) {
		this._name = value;
	}

	public void setOutputLocation(IPath value) {
		this._outputLocation = value;
	}

	public void setSourcePath(List value) {
		this._sourcePaths = new ArrayList();
		for (int i = 0; i < value.size(); i++)
			_sourcePaths.add(JavaCore.newSourceEntry(new Path((String) value.get(i))));
	}

	public void setClassPath(List value) {
		this._classPath = new ArrayList();
		for (int i = 0; i < value.size(); i++)
			_classPath.add(JavaCore.newLibraryEntry(new Path((String) value.get(i)), null, null, false));
	}

	public void setProjectClassPath(List value) {
		this._projectClassPath = new ArrayList();
		for (int i = 0; i < value.size(); i++)
			_projectClassPath.add(JavaCore.newLibraryEntry(new Path((String) value.get(i)), null, null, false));
	}

	public String name() {
		return _name;
	}

	public IPath outputLocation() {
		return _outputLocation;
	}

	public IClasspathEntry[] classPathEntries() {
		ArrayList classPathEntries = new ArrayList();
		classPathEntries.addAll(_sourcePaths);
		if (_projectClassPath != null && _projectClassPath.size() > 0)
			classPathEntries.addAll(_projectClassPath);
		classPathEntries.addAll(_classPath);

		IClasspathEntry[] result = new IClasspathEntry[classPathEntries.size()];
		for (int i = 0; i < classPathEntries.size(); i++)
			result[i] = (IClasspathEntry) classPathEntries.get(i);

		return result;
	}
}
