/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2002 - 2006 The ObjectStyle Group
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
package org.objectstyle.wolips.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.objectstyle.wolips.datasets.adaptable.JavaProject;
import org.objectstyle.wolips.datasets.adaptable.Project;
import org.objectstyle.wolips.datasets.resources.IWOLipsModel;
import org.objectstyle.wolips.templateengine.ComponentEngine;

/**
 * @author mnolte
 * @author uli
 */
public class WOComponentCreator implements IRunnableWithProgress {
	private String componentName;

	private String packageName;

	private boolean createBodyTag;

	private boolean createApiFile;

	private boolean createWooFile;

	private IResource parentResource;

	private WOComponentCreationPage page;

	private int htmlBodyType;

	private String wooEncoding;

	/**
	 * Constructor for WOComponentCreator.
	 *
	 * @param parentResource
	 * @param componentName
	 * @param packageName
	 * @param createBodyTag
	 * @param createApiFile
	 * @param createWooFile
	 */
	public WOComponentCreator(IResource parentResource, String componentName, String packageName, boolean createBodyTag, boolean createApiFile, boolean createWooFile, WOComponentCreationPage page) {
		this.parentResource = parentResource;
		this.componentName = componentName;
		this.packageName = packageName;
		this.createBodyTag = createBodyTag;
		this.createApiFile = createApiFile;
		this.createWooFile = createWooFile;
		this.page = page;
		this.htmlBodyType = page.getSelectedHTMLDocType().getTemplateIndex();
		this.wooEncoding = page.getSelectedEncoding();
	}

	public void run(IProgressMonitor monitor) throws InvocationTargetException {
		try {
			createWOComponent(monitor);
		} catch (CoreException e) {
			throw new InvocationTargetException(e);
		}
	}

	/**
	 * Method createWOComponent.
	 *
	 * @param monitor
	 * @throws CoreException
	 * @throws InvocationTargetException
	 */
	public void createWOComponent(IProgressMonitor monitor) throws CoreException, InvocationTargetException {
		IFolder componentFolder = null;
		IPath componentJavaPath = null;
		IPath apiPath = null;
		IFolder componentFolderToReveal = null;
		IJavaProject iJavaProject = JavaCore.create(this.parentResource.getProject());
		JavaProject javaProject = (JavaProject) iJavaProject.getAdapter(JavaProject.class);
		Project project = (Project) this.parentResource.getProject().getAdapter(Project.class);
		switch (this.parentResource.getType()) {
		case IResource.PROJECT:
			componentFolder = ((IProject) this.parentResource).getFolder(this.componentName + "." + IWOLipsModel.EXT_COMPONENT);
			componentFolderToReveal = (IFolder) javaProject.getProjectSourceFolder();
			componentJavaPath = componentFolderToReveal.getLocation();
			apiPath = this.parentResource.getProject().getLocation();
			break;
		case IResource.FOLDER:
			componentFolder = ((IFolder) this.parentResource).getFolder(this.componentName + "." + IWOLipsModel.EXT_COMPONENT);
			componentFolderToReveal = javaProject.getSubprojectSourceFolder((IFolder) this.parentResource, true);
			componentJavaPath = componentFolderToReveal.getLocation();
			apiPath = componentFolder.getParent().getLocation();
			IFolder pbFolder = project.getParentFolderWithPBProject((IFolder) this.parentResource);
			if (pbFolder != null) {
				apiPath = pbFolder.getLocation();
			}
			break;
		default:
			throw new InvocationTargetException(new Exception("Wrong parent resource - check validation"));
		}
		if (packageName != null && packageName.length() > 0) {
			componentJavaPath = componentJavaPath.append(new Path(packageName.replace('.', '/')));
		}
		prepareFolder(componentFolder, monitor);
		String projectName = this.parentResource.getProject().getName();
		IPath path = componentFolder.getLocation();
		IPath projectRelativeJavaPath = componentJavaPath.removeFirstSegments(this.parentResource.getProject().getLocation().segmentCount());
		IFolder javaSourceFolder = this.parentResource.getProject().getFolder(projectRelativeJavaPath);
		prepareFolder(javaSourceFolder, monitor);
		ComponentEngine componentEngine = new ComponentEngine();
		try {
			componentEngine.init();
		} catch (Exception e) {
			WizardsPlugin.getDefault().log(e);
			throw new InvocationTargetException(e);
		}
		// TODO: select template in the user interface
//		componentEngine.setSelectedTemplateName(componentEngine.names()[0]);
		componentEngine.setProjectName(projectName);
		componentEngine.setCreateBodyTag(this.createBodyTag);
		componentEngine.setComponentName(this.componentName);
		componentEngine.setPackageName(this.packageName);
		componentEngine.setComponentPath(path);
		componentEngine.setApiPath(apiPath);
		componentEngine.setJavaPath(componentJavaPath);
		componentEngine.setCreateWooFile(this.createWooFile);
		componentEngine.setCreateApiFile(this.createApiFile);
		componentEngine.setHTMLBodyType(this.htmlBodyType);
		componentEngine.setWOOEncoding(this.wooEncoding);

		try {
			componentEngine.run(new NullProgressMonitor());
			this.parentResource.getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);
			IResource[] resources = new IResource[] {componentFolderToReveal.findMember(this.componentName + "." + IWOLipsModel.EXT_JAVA), componentFolder.findMember(this.componentName + "." + IWOLipsModel.EXT_WOD)};
			page.setResourcesToReveal(resources);
		} catch (Exception e) {
			WizardsPlugin.getDefault().log(e);
			throw new InvocationTargetException(e);
		}
	}

	public void prepareFolder(IFolder _folder, IProgressMonitor _progressMonitor) throws CoreException {
		if (!_folder.exists()) {
			IContainer parent = _folder.getParent();
			if (parent instanceof IFolder) {
				prepareFolder((IFolder) parent, _progressMonitor);
			}
			_folder.create(false, true, _progressMonitor);
		}
	}
}