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
package org.objectstyle.wolips.wizards;
import java.lang.reflect.InvocationTargetException;
import java.util.Vector;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.objectstyle.wolips.datasets.project.WOLipsJavaProject;
import org.objectstyle.wolips.datasets.resources.IWOLipsModel;
import org.objectstyle.wolips.templateengine.TemplateDefinition;
import org.objectstyle.wolips.templateengine.TemplateEngine;
/**
 * @author mnolte
 * @author uli Creates new eo model file resources from values gathered by
 *         EOModelCreationPage. <br>
 * @see com.neusta.webobjects.eclipse.wizards.EOModelCreationPage
 */
public class EOModelCreator implements IRunnableWithProgress {
	private String modelName;
	private String adaptorName;
	private IResource parentResource;
	/**
	 * Constructor for EOModelCreator.
	 */
	public EOModelCreator(IResource parentResource, String modelName,
			String adaptorName) {
		this.parentResource = parentResource;
		this.modelName = modelName;
		this.adaptorName = adaptorName;
	}
	/**
	 * @see WOProjectResourceCreator#run(IProgressMonitor)
	 */
	public void run(IProgressMonitor monitor) throws InvocationTargetException {
		try {
			createEOModel(monitor);
		} catch (CoreException e) {
			throw new InvocationTargetException(e);
		}
	}
	/**
	 * Method createEOModelNamed. Creates eo model file resources. All file
	 * resource changes are registered in ResourceChangeListener where the
	 * project file is updated. <br>All folder resource changes are registered
	 * in @link WOProjectResourceCreator#createResourceFolderInProject(IFolder,
	 * IProgressMonitor). <br>
	 * 
	 * @see com.neusta.webobjects.eclipse.ResourceChangeListener
	 * @param modelName
	 * @param adaptorName
	 * @param monitor
	 * @throws CoreException
	 * @throws InvocationTargetException
	 */
	public void createEOModel(IProgressMonitor monitor) throws CoreException,
			InvocationTargetException {
		IFolder modelFolder = null;
		switch (parentResource.getType()) {
			case IResource.PROJECT :
				modelFolder = ((IProject) parentResource).getFolder(modelName
						+ "." + IWOLipsModel.EXT_EOMODEL);
				break;
			case IResource.FOLDER :
				modelFolder = ((IFolder) parentResource).getFolder(modelName
						+ "." + IWOLipsModel.EXT_EOMODEL);
				break;
			default :
				throw new InvocationTargetException(new Exception(
						"Wrong parent resource - check validation"));
		}
		modelFolder.create(false, true, monitor);
		String projectName = parentResource.getProject().getName();
		String path = modelFolder.getLocation().toOSString();
		TemplateEngine templateEngine = new TemplateEngine();
		try {
			templateEngine.init();
		} catch (Exception e) {
			WizardsPlugin.getDefault().getPluginLogger().log(e);
			throw new InvocationTargetException(e);
		}
		templateEngine.getWolipsContext().setProjectName(projectName);
		templateEngine.getWolipsContext().setAdaptorName(adaptorName);
		templateEngine.addTemplate(new TemplateDefinition(
				"eomodel/index.eomodeld.vm", path, "index.eomodeld", "index.eomodeld"));
		templateEngine.addTemplate(new TemplateDefinition(
				"eomodel/DiagramLayout.vm", path, "DiagramLayout", "DiagramLayout"));
		try {
			templateEngine.run(new NullProgressMonitor());
		} catch (Exception e) {
			WizardsPlugin.getDefault().getPluginLogger().log(e);
			throw new InvocationTargetException(e);
		}
		modelFolder.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		// add adaptor framework
		if (!"None".equals(adaptorName)) {
			IJavaProject projectToUpdate = JavaCore.create(parentResource
					.getProject());
			Vector newAdaptorFrameworkList = new Vector();
			newAdaptorFrameworkList.add("Java" + adaptorName + "Adaptor."
					+ IWOLipsModel.EXT_FRAMEWORK);
			WOLipsJavaProject wolipsJavaProject = new WOLipsJavaProject(
					projectToUpdate);
			IClasspathEntry[] newClasspathEntries = wolipsJavaProject
					.getClasspathAccessor().addFrameworkListToClasspathEntries(
							newAdaptorFrameworkList);
			try {
				projectToUpdate.setRawClasspath(newClasspathEntries, null);
			} catch (JavaModelException e) {
				throw new InvocationTargetException(e);
			} finally {
				projectToUpdate = null;
				newAdaptorFrameworkList = null;
				newClasspathEntries = null;
			}
		}
	}
}
