package org.objectstyle.wolips.project;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

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

	/**
	 * Constructor for PBProjectUpdater.
	 */
	private PBProjectUpdater() {
		super();
	}

	public static void updatePBProject(IProject aProject) throws CoreException {
		IFile aPBProject = PBProjectUpdater.getPBProject(aProject);
		PBProjectUpdater.syncPBProjectWithProject(aPBProject, aProject);
	}
	
	private static IFile getPBProject(IProject aProject) {
		IFile aPBProject = aProject.getFile(PBProjectUpdater.PBProject);
		if(aPBProject == null) aPBProject = PBProjectUpdater.createPBProject(aProject);
		return aPBProject;
	}
	
	private static IFile createPBProject(IProject aProject) {
		return null;
	}
	
	private static void syncPBProjectWithProject(IFile aPBProject, IProject aProject) {
	}
}
