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
 
 package org.objectstyle.wolips.wizard.common;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.eclipse.ui.wizards.datatransfer.FileSystemStructureProvider;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;
import org.eclipse.ui.wizards.datatransfer.ZipFileStructureProvider;
import org.objectstyle.wolips.WOLipsPlugin;
import org.objectstyle.wolips.io.FileStringScanner;

/**
 * @author uli
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ProjectCreationOperation {

	protected IProject configNewProject(IWorkspaceRoot root, String name, String[] natureIds, IProject[] referencedProjects, IProgressMonitor monitor) throws InvocationTargetException {
		try {
			IProject project= root.getProject(name);
			if (!project.exists()) {
				project.create(null);
			}
			if (!project.isOpen()) {
				project.open(null);
			}
			IProjectDescription desc= project.getDescription();
			desc.setLocation(null);
			desc.setNatureIds(natureIds);
			desc.setReferencedProjects(referencedProjects);
			
			project.setDescription(desc, new SubProgressMonitor(monitor, 1));

			return project;
		} catch (CoreException e) {
			throw new InvocationTargetException(e);
		}
	}
	protected void importFilesFromDirectory(String pluginRelativePath, IPath destPath, IProgressMonitor monitor, IOverwriteQuery aOverwriteQuery) throws InvocationTargetException, InterruptedException, CoreException {		
		File file;
		try {
			URL starterURL= new URL(WOLipsPlugin.getDefault().getDescriptor().getInstallURL(), pluginRelativePath);
		    String folder = Platform.asLocalURL(starterURL).getFile();
			file = new File(folder);
			}		
		 catch (IOException e) {
			String message= pluginRelativePath + ": " + e.getMessage(); //$NON-NLS-1$
			Status status= new Status(IStatus.ERROR, WOLipsPlugin.getPluginId(), IStatus.ERROR, message, e);
			throw new CoreException(status);
		}
		ImportOperation op= new ImportOperation(destPath, file, FileSystemStructureProvider.INSTANCE, aOverwriteQuery);
		op.setCreateContainerStructure(false);
		op.run(monitor);
	}
	
	protected ZipFile getZipFileFromPluginDir(String pluginRelativePath) throws CoreException {
		try {
			URL starterURL= new URL(WOLipsPlugin.getDefault().getDescriptor().getInstallURL(), pluginRelativePath);
			return new ZipFile(Platform.asLocalURL(starterURL).getFile());
		} catch (IOException e) {
			String message= pluginRelativePath + ": " + e.getMessage(); //$NON-NLS-1$
			Status status= new Status(IStatus.ERROR, WOLipsPlugin.getPluginId(), IStatus.ERROR, message, e);
			throw new CoreException(status);
		}
	}
	
	protected void importFilesFromZip(ZipFile srcZipFile, IPath destPath, IProgressMonitor monitor, IOverwriteQuery aOverwriteQuery) throws InvocationTargetException, InterruptedException {		
		ZipFileStructureProvider structureProvider=	new ZipFileStructureProvider(srcZipFile);
		ImportOperation op= new ImportOperation(destPath, structureProvider.getRoot(), structureProvider, aOverwriteQuery);
		//op.setCreateContainerStructure(true);
		op.run(monitor);
	}
	
	protected void changeBuildScript(IProject project, String relativePath) throws CoreException {
		//change name of framework and project
		String path = project.getLocation().toOSString();
		String file = path + relativePath;
		try {	
			FileStringScanner.FileOpenReplaceWith(file, "xxxxx", project.getName().toLowerCase());
			FileStringScanner.FileOpenReplaceWith(file, "yyyyy", project.getName());
				}		
		 catch (IOException e) {
			String message= path + ": " + e.getMessage(); //$NON-NLS-1$
			Status status= new Status(IStatus.ERROR, WOLipsPlugin.getPluginId(), IStatus.ERROR, message, e);
			throw new CoreException(status);
		 }
	}
	
}
