/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2005 - 2006 The ObjectStyle Group,
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
package org.objectstyle.wolips.locate.result;

import java.util.ArrayList;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.objectstyle.wolips.locate.LocateException;
import org.objectstyle.wolips.locate.LocatePlugin;

public class LocalizedComponentsLocateResult extends AbstractLocateResult {
	private ArrayList components = new ArrayList();

	private IFile dotJava;

	//private IType dotJavaType;

	private IFile dotApi;

	public LocalizedComponentsLocateResult() {
		super();
	}

	public void add(IResource resource) throws LocateException {
		super.add(resource);
		if (resource.getType() == IResource.FOLDER) {
			components.add(resource);
		} else if (resource.getType() == IResource.FILE) {
			IFile file = (IFile) resource;
			String extension = resource.getFileExtension();
			if (extension.equals("java")) {
				if (dotJava != null) {
					throw new LocateException("Duplicate located: " + dotJava + " " + file);
				}
				dotJava = file;
			} else if (extension.equals("api")) {
				if (dotApi != null) {
					throw new LocateException("Duplicate located: " + dotApi + " " + file);
				}
				dotApi = file;
			} else {
				throw new LocateException("unknown extension on " + file);
			}

		} else {
			throw new LocateException("unsupported type " + resource);
		}
	}

	public IFolder[] getComponents() {
		return (IFolder[]) components.toArray(new IFolder[components.size()]);
	}

	public IFile getDotApi() {
		return dotApi;
	}
	
	public IFile getDotApi(boolean guessIfMissing) throws CoreException {
		IFile apiFile = dotApi;
		if (apiFile == null && guessIfMissing) {
			IFile firstHtmlFile = getFirstHtmlFile();
			IContainer apiFolder = null;
			if (firstHtmlFile != null) {
				apiFolder = firstHtmlFile.getParent().getParent();
			}
			if (apiFolder != null) {
				apiFile = apiFolder.getFile(new Path(LocatePlugin.getDefault().fileNameWithoutExtension(firstHtmlFile) + ".api"));
			}
		}
		return apiFile;
	}

	public IFile getDotJava() {
		return dotJava;
	}

	public IType getDotJavaType() throws JavaModelException {
		IType dotJavaType = null;
		if (dotJavaType == null) {
			IFile javaFile = getDotJava();
			if (javaFile != null) {
				IJavaElement javaElement = JavaCore.create(javaFile);
				if (javaElement instanceof ICompilationUnit) {
					IType[] types = ((ICompilationUnit) javaElement).getTypes();
					// NTS: What do we do about multiple types in a file??
					if (types.length > 0) {
						dotJavaType = types[0];
					}
				}
			}
		}
		return dotJavaType;
	}

	public IFile getFirstHtmlFile() throws CoreException {
		IFile htmlFile;
		if (components.size() > 0) {
			IFolder componentFolder = (IFolder) components.get(0);
			htmlFile = LocalizedComponentsLocateResult.getHtml(componentFolder);
		} else {
			htmlFile = null;
		}
		return htmlFile;
	}

	public IFile getFirstWodFile() throws CoreException {
		IFile wodFile;
		if (components.size() > 0) {
			IFolder componentFolder = (IFolder) components.get(0);
			wodFile = LocalizedComponentsLocateResult.getWod(componentFolder);
		} else {
			wodFile = null;
		}
		return wodFile;
	}

	public IFile getFirstWooFile() throws CoreException {
		IFile wooFile;
		if (components.size() > 0) {
			IFolder componentFolder = (IFolder) components.get(0);
			wooFile = LocalizedComponentsLocateResult.getWoo(componentFolder);
		} else {
			wooFile = null;
		}
		return wooFile;
	}

	public static IFile getHtml(IFolder component) throws CoreException {
		return LocalizedComponentsLocateResult.getMemberWithExtension(component, "html");
	}

	public static IFile getWod(IFolder component) throws CoreException {
		return LocalizedComponentsLocateResult.getMemberWithExtension(component, "wod");
	}

	public static IFile getWoo(IFolder component) throws CoreException {
		return LocalizedComponentsLocateResult.getMemberWithExtension(component, "woo");
	}

	private static IFile getMemberWithExtension(IFolder folder, String extension) throws CoreException {
		IResource[] member = folder.members();
		for (int i = 0; i < member.length; i++) {
			IResource resource = member[i];
			String fileExtension = resource.getFileExtension();
			if (resource.getType() == IResource.FILE && fileExtension != null && fileExtension.equalsIgnoreCase(extension)) {
				return (IFile) resource;
			}

		}
		return null;
	}
}
