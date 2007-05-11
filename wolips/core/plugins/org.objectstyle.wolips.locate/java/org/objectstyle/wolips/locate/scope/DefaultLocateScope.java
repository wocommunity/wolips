/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2005 The ObjectStyle Group,
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
package org.objectstyle.wolips.locate.scope;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

public class DefaultLocateScope extends AbstractLocateScope {

	private ILocateScope _projectLocateScope;

	private IgnoredFolderLocateScope _ignoredFolderScope;

	private IncludeFileLocateScope _includeFileScope;

	private IncludeFolderLocateScope _includeFolderScope;

	public DefaultLocateScope(IProject project, String[] includedFilesNames, String[] includedFolderNames) {
		this(project, includedFilesNames, null, includedFolderNames, new DefaultIgnoredFolderLocateScope());
	}

	public DefaultLocateScope(IProject project, String[] includedFilesNames, String[] includedFileExt, String[] includedFolderNames, IgnoredFolderLocateScope ignoredFolderScope) {
		super();
		_projectLocateScope = new ProjectLocateScope(project);
		_ignoredFolderScope = ignoredFolderScope;
		_includeFileScope = new IncludeFileLocateScope(includedFilesNames, includedFileExt);
		_includeFolderScope = new IncludeFolderLocateScope(includedFolderNames, null);
	}

	public DefaultLocateScope(ILocateScope projectLocateScope, String[] includedFilesNames, String[] includedFolderNames) {
		_projectLocateScope = projectLocateScope;
		_ignoredFolderScope = new DefaultIgnoredFolderLocateScope();
		_includeFileScope = new IncludeFileLocateScope(includedFilesNames, null);
		_includeFolderScope = new IncludeFolderLocateScope(includedFolderNames, null);
	}

	public boolean ignoreContainer(IContainer container) {
		if (_projectLocateScope.ignoreContainer(container)) {
			return true;
		}
		if (_ignoredFolderScope.ignoreContainer(container)) {
			return true;
		}
		if (_includeFileScope.ignoreContainer(container)) {
			return true;
		}
		if (_includeFolderScope.ignoreContainer(container)) {
			return true;
		}
		return false;
	}

	public boolean addToResult(IFile file) {
		if (_projectLocateScope.addToResult(file)) {
			return true;
		}
		if (_ignoredFolderScope.addToResult(file)) {
			return true;
		}
		if (_includeFileScope.addToResult(file)) {
			return true;
		}
		if (_includeFolderScope.addToResult(file)) {
			return true;
		}
		return false;
	}

	public boolean addToResult(IContainer container) {
		if (_projectLocateScope.addToResult(container)) {
			return true;
		}
		if (_ignoredFolderScope.addToResult(container)) {
			return true;
		}
		if (_includeFileScope.addToResult(container)) {
			return true;
		}
		if (_includeFolderScope.addToResult(container)) {
			return true;
		}
		return false;
	}
}
