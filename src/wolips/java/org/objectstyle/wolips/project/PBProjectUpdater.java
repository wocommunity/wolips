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

import java.io.File;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.objectstyle.wolips.WOLipsPlugin;
import org.objectstyle.wolips.io.FileStringScanner;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

/**
 * @author uli
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class PBProjectUpdater {
	
	public static String PBProject = "PB.project";
	private PBProject pbProject;
	private IProject project;

	/**
	 * Constructor for PBProjectUpdater.
	 */
	public PBProjectUpdater(IProject aProject) {
		super();
		pbProject = getPBProject(aProject);
		project = aProject;
	}

	public void updatePBProject() throws CoreException {
		syncPBProjectWithProject();
	}
	
	private PBProject getPBProject(IProject aProject) {
		System.out.println("PBProjectUpdater.getPBProject");
		IFile aPBProject = aProject.getFile(PBProjectUpdater.PBProject);
		System.out.println("aPBProject" + aPBProject);
		File aFile = aPBProject.getLocation().toFile();
		System.out.println("aFile: " + aFile);
		boolean isWOApp = ProjectHelper.isWOAppBuilderInstalled(aProject);
		if(!aFile.exists()) {
			try {
				aFile.createNewFile();
	
				String contents;
				URL aStarterURL = WOLipsPlugin.getDefault().getDescriptor().getInstallURL();
				URL aURL;
				if(isWOApp) {
					aURL= new URL(aStarterURL, "templates/woapplication/PB.project");
				}
				else {
					aURL= new URL(aStarterURL, "templates/woframework/PB.project");    		
				}
				String aPBProjectFile = Platform.asLocalURL(aURL).getFile();
				contents = FileStringScanner.stringFromFile(new File(aPBProjectFile));
				FileStringScanner.stringToFile(aFile, contents);
			}
			catch (Exception anException) {
				System.out.println(anException);
			}
		}
		return new PBProject(aFile, isWOApp);
	}
	
	private void syncPBProjectWithProject() {
		System.out.println("PBProjectUpdater.syncPBProjectWithProject");
		System.out.println("pbProject.update");
		pbProject.update();
		this.syncFilestable();
		this.syncProjectName();
		System.out.println("pbProject.saveChanges");
		pbProject.saveChanges();
	}
	
	private void syncFilestable() {
		System.out.println("PBProjectUpdater.syncFilestable");
		NSMutableArray aClassesList = new NSMutableArray();
		NSMutableArray aWOComponentsList = new NSMutableArray();
		NSMutableArray aWOAppResourcesList = new NSMutableArray();
		
		IResource[] resources;
		try {
			resources = project.members();
		}
		catch (Exception anException) {
			System.out.println(anException);
			return;
		}
		int lastResource = resources.length;
		int i = 0;
		while (i<lastResource) {
			IResource aResource = resources[i];
			i++;
			proceedResource(aResource, aClassesList, aWOComponentsList, aWOAppResourcesList);
		}
		
		this.syncClasses(aClassesList);
		this.syncWOComponents(aWOComponentsList);
		this.syncWOAppResources(aWOAppResourcesList);
	}
	
	private void proceedResource(IResource aResource, NSMutableArray aClassesList, NSMutableArray aWOComponentsList, NSMutableArray aWOAppResourcesList) {
		try {
			String aPath = aResource.getProjectRelativePath().toString();
			System.out.println("Member path: " + aPath);
			File aFile = new File(aResource.getLocation().toOSString());
			IFolder aFolder = null;
			if(aFile.isDirectory())
				aFolder = project.getFolder(aResource.getProjectRelativePath());
			System.out.println("aFolder: " + aFolder + " ");
			if(aFolder != null) {
				if(aPath.endsWith(".wo")) aWOComponentsList.addObject(aPath);
				else {
					IResource[] resources;
					resources = aFolder.members();
					int lastResource = resources.length;
					int i = 0;
					while (i<lastResource) {
						IResource aFolderResource = resources[i];
						i++;
						this.proceedResource(aFolderResource, aClassesList, aWOComponentsList, aWOAppResourcesList);
					}
				}
			}
			else {
				if(aPath.endsWith(".java")) aClassesList.addObject(aPath);
				
				if(aPath.endsWith(".api")) aWOAppResourcesList.addObject(aPath);
			}
		}
		catch(Exception anException) {
			System.out.println(anException);
		}
	}
	
	private void syncProjectName() {
		System.out.println("PBProjectUpdater.syncProjectName");
		pbProject.setProjectName(project.getName());
	}
	
	private void syncClasses(NSMutableArray anNSMutableArray) {
		System.out.println("PBProjectUpdater.syncClasses");
		System.out.println(anNSMutableArray);
		pbProject.setClasses(anNSMutableArray);
	}
	
	private void syncWOComponents(NSMutableArray anNSMutableArray) {
		System.out.println("PBProjectUpdater.syncWOComponents");
		System.out.println(anNSMutableArray);
		pbProject.setWoComponents(anNSMutableArray);
	}
	
	private void syncWOAppResources(NSMutableArray anNSMutableArray) {
		System.out.println("PBProjectUpdater.syncWOAppResources");
		System.out.println(anNSMutableArray);
		pbProject.setWoAppResources(anNSMutableArray);
	}
}
