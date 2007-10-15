/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2005 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 1.
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The end-user documentation
 * included with the redistribution, if any, must include the following
 * acknowlegement: "This product includes software developed by the ObjectStyle
 * Group (http://objectstyle.org/)." Alternately, this acknowlegement may
 * appear in the software itself, if and wherever such third-party
 * acknowlegements normally appear. 4. The names "ObjectStyle Group" and
 * "Cayenne" must not be used to endorse or promote products derived from this
 * software without prior written permission. For written permission, please
 * contact andrus@objectstyle.org. 5. Products derived from this software may
 * not be called "ObjectStyle" nor may "ObjectStyle" appear in their names
 * without prior written permission of the ObjectStyle Group.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * OBJECTSTYLE GROUP OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals
 * on behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/> .
 *  
 */
/**
 * 
 */
package org.objectstyle.wolips.core.resources.types;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.TypeNameRequestor;

/**
 * @author mike
 */
public class TypeNameCollector extends TypeNameRequestor {
	private IJavaProject _project;

	private Set<String> _typeNames;

	private Map<String, String> _typeNameToPath;

	private Map<String, IType> _typeNameToType;

	private IType _woElementType;

	private boolean _requireTypeInProject;

	public TypeNameCollector(IJavaProject project, boolean requireTypeInProject) throws JavaModelException {
		this(project, requireTypeInProject, new TreeSet<String>());
	}

	public TypeNameCollector(IJavaProject project, boolean requireTypeInProject, Set<String> typeNames) throws JavaModelException {
		_project = project;
		_typeNames = typeNames;
		_typeNameToPath = new HashMap<String, String>();
		_typeNameToType = new HashMap<String, IType>();
		_requireTypeInProject = requireTypeInProject;
		_woElementType = _project.findType("com.webobjects.appserver.WOElement");
	}

	public Set<String> getTypeNames() {
		return _typeNames;
	}

	public boolean isExactMatch() {
		return _typeNames.size() == 1;
	}

	public boolean isEmpty() {
		return _typeNames.isEmpty();
	}

	public String firstTypeName() {
		return _typeNames.iterator().next();
	}

	public Iterator<String> typeNames() {
		return _typeNames.iterator();
	}

	public String getPathForClassName(String _className) {
		return _typeNameToPath.get(_className);
	}

	public IType getTypeForClassName(String _className) {
		return _typeNameToType.get(_className);
	}

	@Override
	public void acceptType(int _modifiers, char[] _packageName, char[] _simpleTypeName, char[][] _enclosingTypeNames, String _path) {
		String className;
		String simpleClassName = new String(_simpleTypeName);
		if (_packageName == null || _packageName.length == 0) {
			className = simpleClassName;
		} else {
			String packageName = new String(_packageName);
			className = packageName + "." + simpleClassName;
		}
		try {
			IType type = _project.findType(className);
			if (type != null) {
				boolean typeMatches = true;
				if (_requireTypeInProject) {
					IResource correspondingResource = type.getResource();
					if (correspondingResource == null) {
						typeMatches = false;
					}
				}
				if (typeMatches) {
					ITypeHierarchy typeHierarchy = SuperTypeHierarchyCache.getTypeHierarchy(type);
					if (_woElementType != null && typeHierarchy.contains(_woElementType)) {
						_typeNames.add(className);
						_typeNameToPath.put(className, _path);
						_typeNameToType.put(className, type);
					}
				}
			}
		} catch (Throwable t) {
			// ignore
			t.printStackTrace();
		}
	}
}