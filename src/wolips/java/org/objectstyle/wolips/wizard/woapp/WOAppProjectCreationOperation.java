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
package org.objectstyle.wolips.wizard.woapp;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.objectstyle.wolips.WOLipsPlugin;
import org.objectstyle.wolips.project.ProjectHelper;
import org.objectstyle.wolips.wizard.common.ProjectCreationOperation;

public class WOAppProjectCreationOperation extends ProjectCreationOperation  implements IRunnableWithProgress {

	private IResource fElementToOpen;
	
	private WOAppProjectCreationWizardPage[] fPages;
	private IOverwriteQuery fOverwriteQuery;
	
	/**
	 * Constructor for WOAppProjectCreationOperation
	 */
	public WOAppProjectCreationOperation(WOAppProjectCreationWizardPage[] pages, IOverwriteQuery overwriteQuery) {
		fElementToOpen= null;
		fPages= pages;
		fOverwriteQuery= overwriteQuery;
	}
	
	/*
	 * @see IRunnableWithProgress#run(IProgressMonitor)
	 */
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		if (monitor == null) {
			monitor= new NullProgressMonitor();
		}
		try {
			monitor.beginTask(WOAppProjectMessages.getString("WOAppProjectCreationOperation.op_desc"), fPages.length); //$NON-NLS-1$
			IWorkspaceRoot root= WOLipsPlugin.getWorkspace().getRoot();
			
			for (int i= 0; i < fPages.length; i++) {
				createProject(root, fPages[i], new SubProgressMonitor(monitor, 1));
			}
		} finally {
			monitor.done();
		}
	}		
	
	public IResource getElementToOpen() {
		return fElementToOpen;
	}
	

	private void createProject(IWorkspaceRoot root, WOAppProjectCreationWizardPage page, IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		IConfigurationElement desc= page.getConfigurationElement();
		
		IConfigurationElement[] imports= desc.getChildren("import"); //$NON-NLS-1$
		IConfigurationElement[] natures= desc.getChildren("nature"); //$NON-NLS-1$
		IConfigurationElement[] references= desc.getChildren("references"); //$NON-NLS-1$
		int nImports= (imports == null) ? 0 : imports.length;
		int nNatures= (natures == null) ? 0 : natures.length;
		int nReferences= (references == null) ? 0 : references.length;
		
		monitor.beginTask(WOAppProjectMessages.getString("WOAppProjectCreationOperation.op_desc_proj"), nImports + 1); //$NON-NLS-1$

		String name= page.getName();
		
		String[] natureIds= new String[nNatures];
		for (int i= 0; i < nNatures; i++) {
			natureIds[i]= natures[i].getAttribute("id"); //$NON-NLS-1$
		}
		IProject[] referencedProjects= new IProject[nReferences];
		for (int i= 0; i < nReferences; i++) {
			referencedProjects[i]= root.getProject(references[i].getAttribute("id")); //$NON-NLS-1$
		}		
		
		IProject proj= configNewProject(root, name, natureIds, referencedProjects, monitor);
			
		for (int i= 0; i < nImports; i++) {
			doImports(proj, imports[i], new SubProgressMonitor(monitor, 1));
		}
		
		String open= desc.getAttribute("open"); //$NON-NLS-1$
		if (open != null && open.length() > 0) {
			IResource fileToOpen= proj.findMember(new Path(open));
			if (fileToOpen != null) {
				fElementToOpen= fileToOpen;
			}
		}		
		
	}
	
	protected  IProject configNewProject(IWorkspaceRoot root, String name, String[] natureIds, IProject[] referencedProjects, IProgressMonitor monitor) throws InvocationTargetException {
		IProject aProject = super.configNewProject(root, name, natureIds, referencedProjects, monitor);
		try {
			ProjectHelper.installWOBuilder(aProject, ProjectHelper.WOAPPLICATION_BUILDER_ID);
		}
			catch (CoreException ex) {
				System.out.println(ex.getMessage());
			}return aProject;
	}
	
	private void doImports(IProject project, IConfigurationElement curr, IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		try {
			IPath destPath;
			String name= curr.getAttribute("dest"); //$NON-NLS-1$
			if (name == null || name.length() == 0) {
				destPath= project.getFullPath();
			} else {
				IFolder folder= project.getFolder(name);
				if (!folder.exists()) {
					folder.create(true, true, null);
				}
				destPath= folder.getFullPath();
			}
			String importPath= curr.getAttribute("src"); //$NON-NLS-1$
			if (importPath == null) {
				importPath= ""; //$NON-NLS-1$
				WOLipsPlugin.log("projectsetup descriptor: import missing"); //$NON-NLS-1$
				return;
			}
		
			//ZipFile zipFile= getZipFileFromPluginDir(importPath);
			//importFilesFromZip(zipFile, destPath, new SubProgressMonitor(monitor, 1));
			importFilesFromDirectory(importPath, destPath, new SubProgressMonitor(monitor, 1), fOverwriteQuery); 
			//changeBuildScript(project, "/scripts/exportwoapplication.xml");
			//changeBuildScript(project, "/scripts/generateeos.xml");
			changeBuildScript(project, "/build.properties");
		} catch (CoreException e) {
			throw new InvocationTargetException(e);
		}
	}
	
}
