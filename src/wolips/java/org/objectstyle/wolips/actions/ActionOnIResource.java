package org.objectstyle.wolips.actions;

import java.util.ArrayList;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.objectstyle.wolips.WOLipsPlugin;

/**
 * @author uli
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ActionOnIResource implements IActionDelegate {

	private IProject project;
	private IResource actionResource;

	public ActionOnIResource() {
		super();
	}

	/**
	 * @return Returns the IProject
	 */
	protected IProject project() {
		return project;
	}

	/**
		 * @return Returns the IProject
		 */
	protected IResource actionResource() {
		return actionResource;
	}

	public void dispose() {
	}

	/**
	 * Has to be implemented in the subclass.
	 */
	public void run(IAction action) {
	}

	/**
	 * Resets the project when the selection is changed.
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		Object obj = (((IStructuredSelection) selection).getFirstElement());
		project = null;
		actionResource = null;
		if (obj != null && obj instanceof IResource)
			actionResource = (IResource) obj;
		if (obj != null && obj instanceof ICompilationUnit)
			actionResource = (IResource) ((ICompilationUnit) obj).getResource();
		if (obj != null)
			project = actionResource.getProject();
	}

	protected void open(IFile aFile) {
		IWorkbenchWindow workbenchWindow =
			WOLipsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
		if (workbenchWindow != null) {
			IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
			if (workbenchPage != null) {
				try {
					workbenchPage.openEditor(aFile);
				} catch (Exception anException) {
					WOLipsPlugin.log(anException);
				}
			}
		}
	}

	protected void findFilesInResourceByName(
		ArrayList anArrayList,
		IResource aResource,
		String aFileName) {
		if ((aResource != null)) {
			if ((aResource instanceof IContainer)
				|| (aResource instanceof IProject)) {
				IResource resource =
					((IContainer) aResource).findMember(aFileName);
				if ((resource != null) && (resource instanceof IFile))
					anArrayList.add(resource);
				IResource[] members = null;
				try {
					members = ((IContainer) aResource).members();
				} catch (Exception anException) {
					WOLipsPlugin.log(anException);
				}
				for (int i = 0; i < members.length; i++) {
					IResource memberResource = members[i];
					if ((memberResource != null)
						&& (memberResource instanceof IContainer)
						&& (!memberResource.toString().endsWith(".framework"))
						&& (!memberResource.toString().endsWith(".woa")))
						this.findFilesInResourceByName(
							anArrayList,
							memberResource,
							aFileName);
				}
			}
		}
	}
}
