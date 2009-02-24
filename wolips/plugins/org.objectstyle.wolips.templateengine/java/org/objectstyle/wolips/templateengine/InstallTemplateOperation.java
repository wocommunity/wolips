package org.objectstyle.wolips.templateengine;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;

public class InstallTemplateOperation extends WorkspaceModifyOperation {
	private ProjectTemplate _template;

	private IProject _project;

	private IContainer _targetContainer;

	public InstallTemplateOperation(ProjectTemplate template, IProject project, IContainer targetContainer) {
		this(template, project, targetContainer, IDEWorkbenchPlugin.getPluginWorkspace().getRoot());
	}

	public InstallTemplateOperation(ProjectTemplate template, IProject project, IContainer targetContainer, ISchedulingRule rule) {
		super(rule);
		_template = template;
		_project = project;
		_targetContainer = targetContainer;
	}

	public ProjectTemplate getTemplate() {
		return _template;
	}

	public IProject getProject() {
		return _project;
	}

	public IContainer getTargetContainer() {
		return _targetContainer;
	}

	@Override
	protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException {
		try {
			monitor.beginTask("", 2000);//$NON-NLS-1$

			try {
				preInstallTemplate(monitor);

				if (monitor.isCanceled()) {
					throw new OperationCanceledException();
				}

				if (_template != null) {
					_template.createProjectContents(_project, _targetContainer, new SubProgressMonitor(monitor, 1000));
				}
				postInstallTemplate(new SubProgressMonitor(monitor, 1000));

				if (!_project.isOpen()) {
					_project.open(new SubProgressMonitor(monitor, 1000));
				}
				_targetContainer.refreshLocal(IResource.DEPTH_INFINITE, new SubProgressMonitor(monitor, 1000));
				
				finishInstallTemplate(monitor);
			} catch (OperationCanceledException e) {
				throw e;
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("Failed to create project.", e);
			}
		} finally {
			monitor.done();
		}
	}

	protected void preInstallTemplate(@SuppressWarnings("unused") IProgressMonitor monitor) throws Exception {
		// DO NOTHING
	}

	protected void postInstallTemplate(@SuppressWarnings("unused") IProgressMonitor monitor) throws Exception {
		// DO NOTHING
	}
	
	protected void finishInstallTemplate(@SuppressWarnings("unused") IProgressMonitor monitor) throws Exception {
		// DO NOTHING
	}
}
