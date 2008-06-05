package org.objectstyle.wolips.eomodeler.eclipse;

import java.net.URL;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.JavaCore;
import org.objectstyle.wolips.baseforplugins.util.URLUtils;
import org.objectstyle.wolips.eomodeler.core.model.AbstractEOClassLoader;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.utils.EclipseFileUtils;
import org.objectstyle.wolips.launching.actions.WOJavaApplicationLaunchShortcut;
import org.objectstyle.wolips.variables.VariablesPlugin;

public class EclipseEOClassLoaderFactory extends AbstractEOClassLoader {
	@Override
	protected void fillInDevelopmentClasspath(Set<URL> classpathUrls) throws Exception {
		// AK: we don't want to re-jar each time we make a change....
		String workSpacePath = VariablesPlugin.getDefault().getWOProjectDevelopmentPath();
		if (workSpacePath != null) {
			URL classUrl = new URL("file://" + workSpacePath + "wolips/core/plugins/org.objectstyle.wolips.eomodeler.core/bin/");
			classpathUrls.add(classUrl);
		}
	}

	@Override
	protected void fillInModelClasspath(EOModel model, Set<URL> classpathUrls) throws Exception {
		IProject project = null;
		IFile eclipseFile = EclipseFileUtils.getEclipseFile(model.getModelURL());
		if (eclipseFile != null) {
			project = eclipseFile.getProject();
		}
		if (project == null) {
			URL modelURL = model.getModelURL();
			if (modelURL != null) {
				IContainer[] modelContainers = ResourcesPlugin.getWorkspace().getRoot().findContainersForLocation(new Path(URLUtils.cheatAndTurnIntoFile(modelURL).getAbsolutePath()));
				// IContainer[] modelContainers =
				// ResourcesPlugin.getWorkspace().getRoot().findContainersForLocation(new
				// Path(modelURL.toString()));
				for (int modelContainerNum = 0; modelContainerNum < modelContainers.length; modelContainerNum++) {
					IContainer modelContainer = modelContainers[modelContainerNum];
					IProject modelProject = modelContainer.getProject();
					classpathUrls.addAll(WOJavaApplicationLaunchShortcut.createClasspathURLsForProject(JavaCore.create(modelProject)));
				}
			}
		} else {
			classpathUrls.addAll(WOJavaApplicationLaunchShortcut.createClasspathURLsForProject(JavaCore.create(project)));
		}
	}
}
