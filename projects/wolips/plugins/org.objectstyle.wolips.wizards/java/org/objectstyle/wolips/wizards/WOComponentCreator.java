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
package org.objectstyle.wolips.wizards;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.objectstyle.wolips.datasets.adaptable.JavaProject;
import org.objectstyle.wolips.datasets.resources.IWOLipsModel;
import org.objectstyle.wolips.templateengine.ComponentEngine;
/**
 * @author mnolte
 * @author uli
 */
public class WOComponentCreator implements IRunnableWithProgress {
	private String componentName;
	private boolean createBodyTag;
	private boolean createApiFile;
	private boolean createWooFile;
	private IResource parentResource;
	/**
	 * Constructor for WOComponentCreator.
	 * 
	 * @param parentResource
	 * @param componentName
	 * @param createBodyTag
	 * @param createApiFile
	 * @param createWooFile
	 */
	public WOComponentCreator(IResource parentResource, String componentName,
			boolean createBodyTag, boolean createApiFile, boolean createWooFile) {
		this.parentResource = parentResource;
		this.componentName = componentName;
		this.createBodyTag = createBodyTag;
		this.createApiFile = createApiFile;
		this.createWooFile = createWooFile;
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
	public void createWOComponent(IProgressMonitor monitor)
			throws CoreException, InvocationTargetException {
		IFolder componentFolder = null;
		IPath componentJavaPath = null;
		IJavaProject iJavaProject = JavaCore.create(
				this.parentResource.getProject());
		JavaProject javaProject = (JavaProject) iJavaProject.getAdapter(JavaProject.class);
		switch (this.parentResource.getType()) {
			case IResource.PROJECT :
				componentFolder = ((IProject) this.parentResource)
						.getFolder(this.componentName + "."
								+ IWOLipsModel.EXT_COMPONENT);
				componentJavaPath = javaProject.getProjectSourceFolder()
						.getLocation();
				break;
			case IResource.FOLDER :
				componentFolder = ((IFolder) this.parentResource)
						.getFolder(this.componentName + "."
								+ IWOLipsModel.EXT_COMPONENT);
				componentJavaPath = javaProject.getSubprojectSourceFolder(
						(IFolder) this.parentResource, true).getLocation();
				break;
			default :
				throw new InvocationTargetException(new Exception(
						"Wrong parent resource - check validation"));
		}
		componentFolder.create(false, true, monitor);
		String projectName = this.parentResource.getProject().getName();
		IPath path = componentFolder.getLocation();
		IPath projectPath = this.parentResource.getProject().getLocation();
		ComponentEngine componentEngine = new ComponentEngine();
		try {
			componentEngine.init();
		} catch (Exception e) {
			WizardsPlugin.getDefault().getPluginLogger().log(e);
			throw new InvocationTargetException(e);
		}
		//TODO: select template in the user interface
		componentEngine.setSelectedTemplateName(componentEngine.names()[0]);
		componentEngine.setProjectName(projectName);
		componentEngine.setCreateBodyTag(this.createBodyTag);
		componentEngine.setComponentName(this.componentName);
		componentEngine.setComponentPath(path);
		componentEngine.setApiPath(projectPath);
		componentEngine.setJavaPath(componentJavaPath);
		componentEngine.setCreateWooFile(this.createWooFile);
		componentEngine.setCreateApiFile(this.createApiFile);
		try {
			componentEngine.run(new NullProgressMonitor());
			this.parentResource.getProject().refreshLocal(
					IResource.DEPTH_INFINITE, monitor);
		} catch (Exception e) {
			WizardsPlugin.getDefault().getPluginLogger().log(e);
			throw new InvocationTargetException(e);
		}
	}
}