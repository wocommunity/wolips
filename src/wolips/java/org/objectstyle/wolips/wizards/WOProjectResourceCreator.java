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

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.objectstyle.wolips.IWOLipsPluginConstants;
import org.objectstyle.wolips.io.FileFromTemplateCreator;

/**
 * @author mnolte
 * @author uli
 */
public abstract class WOProjectResourceCreator
	implements IWOLipsPluginConstants, IRunnableWithProgress {

	// define types of creators
	protected static final int COMPONENT_CREATOR = 1;
	protected static final int EOMODEL_CREATOR = 2;
	protected static final int SUBPROJECT_CREATOR = 3;
	protected static final int PROJECT_CREATOR = 4;
	protected FileFromTemplateCreator fileCreator;
	
	protected IResource parentResource;
	private static boolean aboutToCreateProperty;
	private static QualifiedName currentlyCreatedResourceQualifier;
	
	/**
	 * Constructor for WOProjectResourceCreator.
	 */
	public WOProjectResourceCreator(IResource parentResource) {
		super();
		this.parentResource = parentResource;
	}
	/**
	 * Method getType.
	 * @return int
	 */
	protected abstract int getType();
	/**
	 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(IProgressMonitor)
	 */
	public void run(IProgressMonitor monitor)
		throws InvocationTargetException, InterruptedException {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}

	}
	/**
	 * Method createResourceFolderInProject.
	 * @param folderToCreate
	 * @param monitor
	 * @throws CoreException
	 * @throws InvocationTargetException
	 */
	protected void createResourceFolderInProject(
		IFolder folderToCreate,
		IProgressMonitor monitor)
		throws CoreException, InvocationTargetException {
		folderToCreate.create(false, true, monitor);
		/*
				String identifier = null;
				switch (getType()) {
		
					case COMPONENT_CREATOR :
						identifier = EXT_COMPONENT;
						break;
		
					case EOMODEL_CREATOR :
						identifier = EXT_EOMODEL;
						break;
		
					case SUBPROJECT_CREATOR :
						identifier = EXT_SUBPROJECT;
						break;
		
				}
		
				if (identifier != null) {
		
					QualifiedName resourceQualifier = WOLipsUtils.qualifierFromResourceIdentifier(identifier);
					String listId = (String) WOLipsUtils.getResourceQualifierToListIdDict().get(resourceQualifier);
					setCurrentlyCreatedResourceQualifier(resourceQualifier);
					aboutToCreateProperty = true;
					folderToCreate.create(false, true, monitor);
					// mark resource as project depending
					WOProjectFileUpdater.addNewResourceToPBFile(folderToCreate, resourceQualifier, null, monitor);
					//folderToCreate.setPersistentProperty(resourceQualifier, listId);
					setCurrentlyCreatedResourceQualifier(null);
					aboutToCreateProperty = false;
				}
		*/
	}
	protected FileFromTemplateCreator fileCreator() {
		if (fileCreator == null)
		fileCreator = new FileFromTemplateCreator();
		return fileCreator;
	}
	/**
	 * Returns if an instance of WOProjectResourceCreator is creating a project resource.<br>
	 * @return boolean
	 */
	public static boolean isAboutToCreateProjectResource() {
		return aboutToCreateProperty;
	}
	/**
	 * Returns the resource qualifier for the resource currently created or null if
	 * no resource is currently created.
	 * @return QualifiedName
	 */
	public static QualifiedName getCurrentlyCreatedResourceQualifier() {
		return currentlyCreatedResourceQualifier;
	}
	/**
	 * Sets the currently created resource's qualifier.
	 * @param currentlyCreatedResourceQualifier The currentlyCreatedResourceQualifier to set
	 */
	public static void setCurrentlyCreatedResourceQualifier(QualifiedName currentlyCreatedResourceQualifier) {
		WOProjectResourceCreator.currentlyCreatedResourceQualifier =
			currentlyCreatedResourceQualifier;
	}

}
