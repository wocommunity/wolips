package org.objectstyle.wolips.jdt;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.IStartup;
import org.objectstyle.wolips.jdt.classpath.WOFrameworkResourceListener;
import org.objectstyle.wolips.variables.VariablesPlugin;

public class JdtPluginStartup implements IStartup {

	public void earlyStartup() {
		// Trigger wolips.properties to write ...
		VariablesPlugin.getDefault().getGlobalVariables();

		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.addResourceChangeListener(new WOFrameworkResourceListener());
		IWorkspaceRoot workspaceRoot = workspace.getRoot();

		for (IProject project : workspaceRoot.getProjects()) {
			if (project.isAccessible()) {
				ProjectFrameworkAdapter projectFrameworkAdapter = (ProjectFrameworkAdapter) project.getAdapter(ProjectFrameworkAdapter.class);
				if (projectFrameworkAdapter != null) {
					projectFrameworkAdapter.initializeProject();
				}
			}
		}
	}

}
