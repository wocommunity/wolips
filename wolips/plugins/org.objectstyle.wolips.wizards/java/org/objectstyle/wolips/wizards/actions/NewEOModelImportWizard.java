package org.objectstyle.wolips.wizards.actions;

import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.objectstyle.wolips.wizards.EOModelResourceImportPage;

public class NewEOModelImportWizard  extends Wizard implements IImportWizard {
	EOModelResourceImportPage importPage;
	IStructuredSelection currentSelection;
	IWorkbench aWorkBench;
	String projectPath;
	IProject project;
	
//	public NewEOModelImportWizard () {
//
//	}
	
	public void addPages() {
		importPage = new EOModelResourceImportPage("EOModelImportPage"); //$NON-NLS-1$ 
		addPage(importPage);
	}
	
	@Override
	public boolean performFinish() {
		
		if (importPage != null && project != null) {
			HashMap <String, String> paths = importPage.getModelPaths();
//			return EOModelImportSupport.importEOModelsToProject(paths, project);
			EOModelImportWorkspaceJob job = new EOModelImportWorkspaceJob("eomodel import", paths, project);
			job.schedule();
		}
		
		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		currentSelection = selection;
		aWorkBench = workbench;
		project = null;
		
		//FIXME: validate if we actual have a project selected
		Object o = selection.getFirstElement();

		if (o instanceof IProject || o instanceof IJavaProject) {
			IJavaProject aProject = (IJavaProject)selection.getFirstElement();
			project = aProject.getProject();
			IPath pathToProject = aProject.getProject().getLocation();
			projectPath = pathToProject.toOSString();
		} else {
			System.out.println("Project not selected! Object: "+o);
		}
		
	}


}
