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
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.objectstyle.wolips.locate.LocateException;
import org.objectstyle.wolips.locate.LocatePlugin;

public class LocalizedComponentsLocateResult extends AbstractLocateResult {
	private List<IFolder> components = new ArrayList<IFolder>();

	private IFile dotJava;

	private IFile dotGroovy;

	// private IType dotJavaType;

	private IFile dotApi;

	private String[] superclasses = new String[] { "com.webobjects.appserver.WOElement" };

	public LocalizedComponentsLocateResult() {
		super();
	}
	
	public String getName() {
		String name = null;
		IFile javaFile = getDotJava();
		if (javaFile != null) {
			name = LocatePlugin.getDefault().fileNameWithoutExtension(javaFile);
		}
		else {
			IFolder[] componentFolders = getComponents();
			if (componentFolders != null) {
				for (IFolder componentFolder : componentFolders) {
					name = LocatePlugin.getDefault().fileNameWithoutExtension(componentFolder);
					break;
				}
			}
			if (name == null) {
				IFile apiFile = getDotApi();
				if (apiFile != null) {
					name = LocatePlugin.getDefault().fileNameWithoutExtension(javaFile);
				}
			}
		}
		if (name == null) {
			name = "Unknown Component";
		}
		return name;
	}

	public void add(IResource resource) throws LocateException {
		super.add(resource);
		if (resource.getType() == IResource.FOLDER) {
			components.add((IFolder) resource);
		} else if (resource.getType() == IResource.FILE) {
			IFile file = (IFile) resource;
			String extension = resource.getFileExtension();
			if (extension.equals("java")) {
				if (dotJava != null) {
					IJavaElement javaElement = JavaCore.create(file);
					try {
						IJavaProject javaProject = javaElement.getJavaProject();
						if (javaProject != null && javaProject.isOnClasspath(javaElement)) {
							if (!isValidSubclass(javaElement)) {
								file = null;
							}
						} else {
							file = null;
						}
					} catch (JavaModelException e) {
						file = null;
						LocatePlugin.getDefault().log(e);
					}
				}
				if (file != null && dotJava != null) {
					IJavaElement javaElement = JavaCore.create(dotJava);
					try {
						IJavaProject javaProject = javaElement.getJavaProject();
						if (javaProject != null && javaProject.isOnClasspath(javaElement)) {
							if (!isValidSubclass(javaElement)) {
								dotJava = null;
							}
						} else {
							dotJava = null;
						}
					} catch (JavaModelException e) {
						dotJava = null;
						LocatePlugin.getDefault().log(e);
					}
				}
				if (file != null && dotJava != null) {
					String message = "Duplicate located: " + dotJava + " " + file;
					alert(message);
					throw new LocateException(message);
				}
				if (file != null) {
					dotJava = file;
				}
			} else if (extension.equals("groovy")) {
				if (dotGroovy != null) {
					String message = "Duplicate located: " + dotGroovy + " " + file;
					alert(message);
					throw new LocateException(message);
				}
				dotGroovy = file;
			} else if (extension.equals("api")) {
				if (dotApi != null) {
					String message = "Duplicate located: " + dotApi + " " + file;
					alert(message);
					//throw new LocateException(message);
				} else {
					dotApi = file;
				}
			} else {
				String message = "unknown extension on " + file;
				alert(message);
				throw new LocateException(message);
			}

		} else {
			String message = "unsupported type " + resource;
			alert(message);
			throw new LocateException(message);
		}
	}

	private void alert(final String message) {
		Display.getDefault().asyncExec(new Runnable() {

			public void run() {
				MessageDialog.openError(null, "", message);
			}

		});
	}

	public IFolder[] getComponents() {
		return components.toArray(new IFolder[components.size()]);
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

	public IType getDotJavaType() {
		IType dotJavaType = null;
		// MS: Don't hold onto java types
		// if (dotJavaType == null) {
		IFile javaFile = getDotJava();
		if (javaFile != null) {
			try {
					IJavaElement javaElement = JavaCore.create(javaFile);
					if (javaElement instanceof ICompilationUnit) {
						IType[] types = ((ICompilationUnit) javaElement).getTypes();
						// NTS: What do we do about multiple types in a file??
						if (types.length > 0) {
							dotJavaType = types[0];
						}
					}
			} catch (JavaModelException e) {
				LocatePlugin.getDefault().log(new RuntimeException(javaFile.getLocation() + " had a problem.", e));
			}
		}
		// }
		return dotJavaType;
	}

	public IFile getDotGroovy() {
		return dotGroovy;
	}

	public IFile getFirstHtmlFile() throws CoreException {
		IFile htmlFile;
		if (components.size() > 0) {
			IFolder componentFolder = components.get(0);
			htmlFile = LocalizedComponentsLocateResult.getHtml(componentFolder);
		} else {
			htmlFile = null;
		}
		return htmlFile;
	}

	public IFile getFirstWodFile() throws CoreException {
		IFile wodFile;
		if (components.size() > 0) {
			IFolder componentFolder = components.get(0);
			wodFile = LocalizedComponentsLocateResult.getWod(componentFolder);
		} else {
			wodFile = null;
		}
		return wodFile;
	}

	public IFile getFirstWooFile() throws CoreException {
		IFile wooFile;
		if (components.size() > 0) {
			IFolder componentFolder = components.get(0);
			wooFile = LocalizedComponentsLocateResult.getWoo(componentFolder);
		} else {
			wooFile = null;
		}
		return wooFile;
	}

	public boolean isValid() {
		boolean valid = true;

		for (IFolder component : components) {
			if (!component.exists()) {
				valid = false;
			}
		}

		if (dotApi == null) {
			try {
				IFile guessDotApi = getDotApi(true);
				if (guessDotApi != null && guessDotApi.exists()) {
					valid = false;
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
		} else if (!dotApi.exists()) {
			valid = false;
		}

		if (dotJava != null && !dotJava.exists()) {
			valid = false;
		}

		return valid;
	}

	public static IFile getHtml(IFolder component) throws CoreException {
		return LocalizedComponentsLocateResult.getMemberWithExtension(component, "html", true);
	}

	public static IFile getWod(IFolder component) throws CoreException {
		return LocalizedComponentsLocateResult.getMemberWithExtension(component, "wod", true);
	}

	public static IFile getWoo(IFolder component) throws CoreException {
		return LocalizedComponentsLocateResult.getMemberWithExtension(component, "woo", false);
	}

	private static IFile getMemberWithExtension(IFolder folder, String extension, boolean mustExist) throws CoreException {
		IResource[] member = folder.members();
		for (int i = 0; i < member.length; i++) {
			IResource resource = member[i];
			String fileExtension = resource.getFileExtension();
			if (resource.getType() == IResource.FILE && fileExtension != null && fileExtension.equalsIgnoreCase(extension)) {
				return (IFile) resource;
			}

		}
		if (!mustExist) {
			return folder.getFile(LocatePlugin.getDefault().fileNameWithoutExtension(folder.getName()) + "." + extension);
		}
		return null;
	}

	private boolean isValidSubclass(IJavaElement javaElement) throws JavaModelException {
		if (superclasses == null || superclasses.length == 0) {
			return true;
		}
		ICompilationUnit compilationUnit = (ICompilationUnit) javaElement;
		IType typeToCeck = compilationUnit.findPrimaryType();
		ITypeHierarchy typeHierarchy = typeToCeck.newSupertypeHierarchy(new NullProgressMonitor());
		IType[] types = typeHierarchy.getAllClasses();
		for (int i = 0; i < types.length; i++) {
			IType type = types[i];
			for (int j = 0; j < superclasses.length; j++) {
				String superclass = superclasses[j];
				if (type.getFullyQualifiedName().equals(superclass)) {
					return true;
				}

			}
		}
		return false;
	}
}
