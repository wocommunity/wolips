package org.objectstyle.wolips.eomodeler.editors;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IEditorInput;
import org.objectstyle.wolips.eomodeler.Activator;

public class LoadEOModelWorkspaceJob extends WorkspaceJob {
	private EOModelEditor _editor;

	private IEditorInput _editorInput;

	public LoadEOModelWorkspaceJob(EOModelEditor editor, IEditorInput editorInput) {
		super("Loading EOModel ...");
		_editor = editor;
		_editorInput = editorInput;
	}

	@Override
	public IStatus runInWorkspace(final IProgressMonitor monitor) throws CoreException {
		IProgressMonitor monitorWrapper = new IProgressMonitor() {
			public void beginTask(String name, int totalWork) {
				monitor.beginTask(name, totalWork);
			}
			
			public void done() {
				monitor.done();
			}
			
			public void internalWorked(double work) {
				monitor.internalWorked(work);
			}
			
			public boolean isCanceled() {
				return monitor.isCanceled();
			}
			
			public void setCanceled(boolean value) {
				monitor.setCanceled(value);
			}
			
			public void setTaskName(String name) {
				setName(name);
				monitor.setTaskName(name);
			}
			
			public void subTask(String name) {
				monitor.subTask(name);
			}

			public void worked(int work) {
				monitor.worked(work);
			}
		};
		_editor._loadInBackground(monitorWrapper);
		return new Status(IStatus.OK, Activator.PLUGIN_ID, IStatus.OK, "Done", null);
	}
}
