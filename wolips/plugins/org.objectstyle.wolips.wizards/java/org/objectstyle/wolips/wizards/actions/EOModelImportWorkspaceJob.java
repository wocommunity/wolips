package org.objectstyle.wolips.wizards.actions;

import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class EOModelImportWorkspaceJob extends WorkspaceJob {
	IProject _project;

	HashMap<String, String> _modelPaths;

	public EOModelImportWorkspaceJob(String name, HashMap<String, String> paths, IProject project) {
		super(name);
		_project = project;
		_modelPaths = paths;
	}

	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
		try {
			EOModelImportSupport.importEOModelsToProject(_modelPaths, _project);
			_project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		} catch (final Throwable t) {
			t.printStackTrace();
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					MessageDialog.openError(new Shell(), "EOModel import failed", t.getMessage());
				}
			});
		}
		return new Status(IStatus.OK, org.objectstyle.wolips.eogenerator.ui.Activator.PLUGIN_ID, IStatus.OK, "Done", null);
	}

}
