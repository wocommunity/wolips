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
	
}

