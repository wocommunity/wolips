package org.objectstyle.wolips.builder.internal;

import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.RefreshTab;
import org.eclipse.ui.externaltools.internal.model.IExternalToolConstants;
import org.objectstyle.wolips.builder.BuilderPlugin;
import org.objectstyle.wolips.core.resources.builder.AbstractDeltaCleanBuilder;
import org.objectstyle.wolips.datasets.adaptable.Project;
import org.objectstyle.wolips.preferences.Preferences;

public class EOGeneratorBuilder extends AbstractDeltaCleanBuilder {
	private boolean myRunEOGenerator;

	public EOGeneratorBuilder() {
	}

	public boolean buildStarted(int _kind, Map _args,
			IProgressMonitor _monitor, IProject _project, Map _buildCache) {
		myRunEOGenerator = false;
		return false;
	}

	public boolean buildPreparationDone(int _kind, Map _args,
			IProgressMonitor _monitor, IProject _project, Map _buildCache) {
		String eogeneratorPath = Preferences.getPREF_EOGENERATOR_PATH();
		if (myRunEOGenerator && eogeneratorPath != null
				&& eogeneratorPath.trim().length() > 0) {
			Project project = (Project) _project.getAdapter(Project.class);
			String eogeneratorArgs = project.getEOGeneratorArgs(true);
			try {
				// -model MyEOModel.eomodeld
				// -refmodel
				// /Library/Frameworks/ERPrototypes.framework/Resources/erprototypes.eomodeld
				// -destination src/
				// -subclassDestination src/
				// -templatedir
				// /Volumes/Storage/Developer/Applications/EOGenerator/CustomTemplates/
				// -java -packagedirs
				ILaunchManager manager = DebugPlugin.getDefault()
						.getLaunchManager();
				ILaunchConfigurationType configType = manager
						.getLaunchConfigurationType(IExternalToolConstants.ID_PROGRAM_BUILDER_LAUNCH_CONFIGURATION_TYPE);
				ILaunchConfigurationWorkingCopy config = configType
						.newInstance(null, "EOGenerator");
				config.setAttribute(IExternalToolConstants.ATTR_LOCATION,
						eogeneratorPath);
				config.setAttribute(
						IExternalToolConstants.ATTR_WORKING_DIRECTORY, _project
								.getLocation().toOSString());
				config.setAttribute(DebugPlugin.ATTR_CAPTURE_OUTPUT, false);
				config.setAttribute(IExternalToolConstants.ATTR_SHOW_CONSOLE,
						true);
				config.setAttribute(
						IDebugUIConstants.ATTR_LAUNCH_IN_BACKGROUND, false);
				config
						.setAttribute(
								IExternalToolConstants.ATTR_PROMPT_FOR_ARGUMENTS,
								false);
				config
						.setAttribute(RefreshTab.ATTR_REFRESH_SCOPE,
								"${project}");
				config.setAttribute(RefreshTab.ATTR_REFRESH_RECURSIVE, true);
				if (eogeneratorArgs != null) {
					config.setAttribute(
							IExternalToolConstants.ATTR_TOOL_ARGUMENTS,
							eogeneratorArgs);
				}
				ILaunch launch = config.launch(ILaunchManager.RUN_MODE,
						_monitor);
			} catch (Throwable e) {
				e.printStackTrace();
				BuilderPlugin.getDefault().log(e);
			}
		}
		myRunEOGenerator = false;
		return false;
	}

	public void handleSource(IResource _resource, IProgressMonitor monitor,
			Map _buildCache) {
	}

	public void handleClasses(IResource _resource, IProgressMonitor monitor,
			Map _buildCache) {
	}

	public void handleClasspath(IResource _resource, IProgressMonitor monitor,
			Map _buildCache) {
	}

	public void handleOther(IResource _resource, IProgressMonitor monitor,
			Map _buildCache) {
	}

	public void handleWebServerResources(IResource _resource,
			IProgressMonitor monitor, Map _buildCache) {
	}

	public void handleWoappResources(IResource _resource,
			IProgressMonitor monitor, Map _buildCache) {
		if (!myRunEOGenerator) {
			if (_resource instanceof IFile) {
				IContainer parent = _resource.getParent();
				if (parent != null) {
					String parentName = parent.getName();
					if (parentName != null && parentName.endsWith(".eomodeld")) {
						myRunEOGenerator = true;
					}
				}
			}
		}
	}
}
