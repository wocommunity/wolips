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

import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.objectstyle.wolips.io.FileFromTemplateCreator;


/**
 * @author mnolte
 * @author uli
 */
public class WOComponentCreator extends WOProjectResourceCreator {
	
	private String componentName;
	private boolean createBodyTag;

	/**
	 * Constructor for WOComponentCreator.
	 */
	public WOComponentCreator(IResource parentResource,String componentName, boolean createBodyTag) {
		super(parentResource);
		this.componentName = componentName;
		this.createBodyTag = createBodyTag;
	}
	
	protected int getType() {
		return COMPONENT_CREATOR;
	}
	
	/**
	 * @see WOProjectResourceCreator#run(IProgressMonitor)
	 */
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		super.run(monitor);
		try {
			createWOComponent(monitor);
		} catch (CoreException e) {
			throw new InvocationTargetException(e);
		} 
	}

	public void createWOComponent(IProgressMonitor monitor)
		throws CoreException, InvocationTargetException {		

		// create the new file resources
		if (fileCreator == null) {
			fileCreator = new FileFromTemplateCreator();
		}

		IFolder componentFolder = null;
		IFile componentJavaFile = null;
		IFile componentApiFile = null;

		switch (parentResource.getType()) {
			case IResource.PROJECT :
				componentFolder = ((IProject) parentResource).getFolder(componentName + "." + COMPONENT);
				componentJavaFile = ((IProject) parentResource).getFolder("src").getFile(componentName + "." + CLASS);
				componentApiFile = ((IProject) parentResource).getFile(componentName + "." + API);

				break;
			case IResource.FOLDER :
				componentFolder = ((IFolder) parentResource).getFolder(componentName + "." + COMPONENT);
				componentJavaFile = ((IFolder) parentResource).getFolder("src").getFile(componentName + "." + CLASS);
				componentApiFile = ((IFolder) parentResource).getFile(componentName + "." + API);

				break;
			default :
				throw new InvocationTargetException(new Exception("Wrong parent resource - check validation"));
		}

		IFile componentDescription = componentFolder.getFile(componentName + "." + WOD);
		IFile componentHTMLTemplate = componentFolder.getFile(componentName + "." + HTML);

		createResourceFolderInProject(componentFolder, monitor);

		fileCreator.create(componentDescription, monitor);

		if (createBodyTag) {
			fileCreator.create(componentHTMLTemplate, monitor);
		} else {
			// create empty file
			componentHTMLTemplate.create(new ByteArrayInputStream("".getBytes()), false, null);
		}

		fileCreator.create(componentJavaFile, "wocomponent", monitor);
		fileCreator.create(componentApiFile, monitor);

	}

	
}
