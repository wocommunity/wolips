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
 
 package org.objectstyle.wolips.project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.internal.ui.util.CoreUtility;

/**
 * @author uli
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ProjectHelper {
	
	public static String WOFRAMEWORK_BUILDER_ID = "org.objectstyle.wolips.woframeworkbuilder";
	public static String WOAPPLICATION_BUILDER_ID = "org.objectstyle.wolips.woapplicationbuilder";

	/**
	 * Constructor for ProjectHelper.
	 */
	public ProjectHelper() {
		super();
	}

	/**
	 * Method removeJavaBuilder.
	 * @param project
	 */
	public static void removeWOBuilder(IProject aProject, String aBuilder) throws CoreException{
			IProjectDescription desc = aProject.getDescription();
			ICommand[] coms = desc.getBuildSpec();
			ArrayList comList = new ArrayList();
			List tmp = Arrays.asList(coms);
			comList.addAll(tmp);
			boolean foundJBuilder = false;
			for (int i = 0; i < comList.size(); i++) {
				if ( ((ICommand)comList.get(i)).getBuilderName()
												.equals(aBuilder) ) {
					comList.remove(i);
					foundJBuilder = true;
					System.out.println("found javabuilder");
				} 
			}
			
			if (foundJBuilder) {
				ICommand[] newCom = new ICommand[comList.size()];
				for (int i = 0; i < comList.size(); i++) {
					newCom[i] = (ICommand)comList.get(i);
				}	
				desc.setBuildSpec(newCom);
				aProject.setDescription(desc, null);
			}
		}


	public static void installWOBuilder(IProject aProject, String aBuilder) throws CoreException {
		IProjectDescription desc = aProject.getDescription();
			ICommand[] coms = desc.getBuildSpec();

			boolean foundJBuilder = false;

			for (int i = 0; i < coms.length; i++) {
				if ( coms[i].getBuilderName().equals(aBuilder) ) {
					foundJBuilder = true;
					System.out.println("found javabuilder");	
				} 
			}
			
			if (!foundJBuilder) {
				ICommand[] newIc = null;
				
				ICommand command = desc.newCommand();
				command.setBuilderName(aBuilder);
				
				newIc = new ICommand[coms.length+1];
				System.arraycopy(coms, 0, newIc, 0, coms.length);
				newIc[coms.length] = command;	
				
				for (int i = 0; i < newIc.length; i++) {
					System.out.println(newIc[i].getBuilderName());
				}
				
				desc.setBuildSpec(newIc);
				aProject.setDescription(desc, null);
				
			}
	}
	
	public static void addWOFrameworkStuffToJavaProject(IProject aProject, IProgressMonitor aMonitor) throws CoreException {
		ProjectHelper.addCommonStuff( aProject, aMonitor);	
	}
	
	public static void addWOApplicationStuffToJavaProject(IProject aProject, IProgressMonitor aMonitor) throws CoreException {
		ProjectHelper.addCommonStuff( aProject, aMonitor);
	}
	
	private static void addCommonStuff(IProject aProject, IProgressMonitor aMonitor) throws CoreException {
		ProjectHelper.createFolder("resources", aProject, aMonitor);
		ProjectHelper.createFolder("WSResources", aProject, aMonitor);	
	}
	
	private static void createFolder(String aFolderName, IProject aProject, IProgressMonitor aMonitor) throws CoreException {
		IFolder folder= aProject.getFolder(aFolderName);
		IPath path= folder.getFullPath();
		if (!folder.exists()) {
			CoreUtility.createFolder(folder, true, true, aMonitor);			
		}
	}
	
	public static boolean isWOAppBuilderInstalled(IProject aProject) {
		return ProjectHelper.isBuilderInstalled(aProject, ProjectHelper.WOAPPLICATION_BUILDER_ID);
	}
	
	public static boolean isWOFwBuilderInstalled(IProject aProject) {
		return ProjectHelper.isBuilderInstalled(aProject, ProjectHelper.WOFRAMEWORK_BUILDER_ID);
	}

	private static boolean isBuilderInstalled(IProject aProject, String anID) {
		try {
			ICommand[] nids = aProject.getDescription().getBuildSpec();
			for (int i = 0; i < nids.length; i++) {
				if (nids[i].getBuilderName().equals(anID)) return true;		
			}
		}
		catch (Exception anException) {
			System.out.println(anException.getMessage());
			return false;
		}
		return false; 	
	}
	
}

