package org.objectstyle.wolips.baseforplugins.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.objectstyle.wolips.baseforplugins.Activator;

public class WOLipsNatureUtils {
	public final static String INCREMENTAL_FRAMEWORK_ID = "org.objectstyle.wolips.incrementalframeworknature";

	public final static String INCREMENTAL_APPLICATION_ID = "org.objectstyle.wolips.incrementalapplicationnature";

	public final static String ANT_FRAMEWORK_ID = "org.objectstyle.wolips.antframeworknature";

	public final static String ANT_APPLICATION_ID = "org.objectstyle.wolips.antapplicationnature";

	private final static String TARGET_BUILDER_ID = "org.objectstyle.wolips.targetbuilder.targetbuildernature";

	private final static String[] NATURES = new String[] { INCREMENTAL_FRAMEWORK_ID, INCREMENTAL_APPLICATION_ID, ANT_FRAMEWORK_ID, ANT_APPLICATION_ID };

	/**
	 * @param project
	 * @param monitor
	 * @return true when the nature successfully added
	 * @throws CoreException
	 */
	public static boolean addIncrementalFrameworkNatureToProject(IProject project, IProgressMonitor monitor) throws CoreException {
		return addNatureToProject(WOLipsNatureUtils.INCREMENTAL_FRAMEWORK_ID, project, monitor);
	}

	/**
	 * @param project
	 * @param monitor
	 * @return true when the nature successfully added
	 * @throws CoreException
	 */
	public static boolean addIncrementalApplicationNatureToProject(IProject project, IProgressMonitor monitor) throws CoreException {
		return addNatureToProject(WOLipsNatureUtils.INCREMENTAL_APPLICATION_ID, project, monitor);
	}

	/**
	 * @param project
	 * @param monitor
	 * @return true when the nature successfully added
	 * @throws CoreException
	 */
	public static boolean addAntFrameworkNatureToProject(IProject project, IProgressMonitor monitor) throws CoreException {
		return addNatureToProject(WOLipsNatureUtils.ANT_FRAMEWORK_ID, project, monitor);
	}

	/**
	 * @param project
	 * @param monitor
	 * @return true when the nature successfully added
	 * @throws CoreException
	 */
	public static boolean addAntApplicationNatureToProject(IProject project, IProgressMonitor monitor) throws CoreException {
		return addNatureToProject(WOLipsNatureUtils.ANT_APPLICATION_ID, project, monitor);
	}

	public static boolean addNatureToProject(String id, IProject project, IProgressMonitor monitor) throws CoreException {
		IProjectNature nature = project.getNature(id);
		if (nature == null) {
			IProjectDescription description = project.getDescription();
			List<String> naturesList = new ArrayList<String>(Arrays.asList(description.getNatureIds()));
			naturesList.add(id);
			description.setNatureIds(naturesList.toArray(new String[naturesList.size()]));
			project.setDescription(description, monitor);
		}
		return project.isNatureEnabled(id);
	}

	public static void removeNaturesFromProject(IProject project, IProgressMonitor monitor) throws CoreException {
		setNatureForProject(null, false, project, monitor);
	}

	public static void setNatureForProject(String id, boolean useTargetBuilder, IProject project, IProgressMonitor monitor) throws CoreException {
		IProjectDescription description = project.getDescription();
		List<String> naturesList = new ArrayList<String>(Arrays.asList(description.getNatureIds()));
		naturesList.remove(WOLipsNatureUtils.ANT_APPLICATION_ID);
		naturesList.remove(WOLipsNatureUtils.ANT_FRAMEWORK_ID);
		naturesList.remove(WOLipsNatureUtils.INCREMENTAL_APPLICATION_ID);
		naturesList.remove(WOLipsNatureUtils.INCREMENTAL_FRAMEWORK_ID);
		naturesList.remove(WOLipsNatureUtils.TARGET_BUILDER_ID);
		if (id != null) {
			naturesList.add(id);
		}
		if (useTargetBuilder) {
			naturesList.add(WOLipsNatureUtils.TARGET_BUILDER_ID);
		}
		description.setNatureIds(naturesList.toArray(new String[naturesList.size()]));
		project.setDescription(description, monitor);
	}

	public static void removeNatureFromProject(String id, IProject project, IProgressMonitor monitor) throws CoreException {
		IProjectNature nature = project.getNature(id);
		if (nature != null) {
			IProjectDescription description = project.getDescription();
			List<String> naturesList = new ArrayList<String>(Arrays.asList(description.getNatureIds()));
			naturesList.remove(id);
			description.setNatureIds(naturesList.toArray(new String[naturesList.size()]));
			project.setDescription(description, monitor);
		}
	}

	/**
	 * @return The wonature if one is installes otherwise null
	 * @throws CoreException
	 */
	public static IProjectNature getNature(IProject project) {
		if (!project.isAccessible() || !project.exists()) {
			return null;
		}
		IProjectNature nature = null;
		try {
			for (int i = 0; i < NATURES.length; i++) {
				nature = project.getNature(NATURES[i]);
				if (nature != null) {
					return nature;
				}
			}
		} catch (CoreException e) {
			Activator.getDefault().debug("Error while resolving nature for project: " + project.getName(), e);
		}
		return nature;
	}

	public static boolean isWOLipsNature(IProject project) {
		return WOLipsNatureUtils.getNature(project) != null;
	}
	
	/**
	 * Returns true if this project is a Maven project.
	 * 
	 * @param project the project to test
	 * @return true if the project is a Maven project
	 */
	public static boolean isMavenNature(IProject project) {
		try {
			// The old nature string
			boolean hasMavenNature = project.hasNature("org.maven.ide.eclipse.maven2Nature");

			if(!hasMavenNature ) {
				// The nature id used today
				hasMavenNature = project.hasNature("org.eclipse.m2e.core.maven2Nature");
			}

			return hasMavenNature;
			
		} catch (CoreException e) {
			Activator.getDefault().debug("Error while resolving nature for project: " + project.getName(), e);
			return false;
		}

	}
}
