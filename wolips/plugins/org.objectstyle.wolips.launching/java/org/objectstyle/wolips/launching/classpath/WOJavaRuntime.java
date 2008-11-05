package org.objectstyle.wolips.launching.classpath;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.launching.DefaultEntryResolver;
import org.eclipse.jdt.internal.launching.LaunchingMessages;
import org.eclipse.jdt.internal.launching.LaunchingPlugin;
import org.eclipse.jdt.internal.launching.RuntimeClasspathEntry;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry2;
import org.eclipse.jdt.launching.IRuntimeClasspathEntryResolver;
import org.eclipse.jdt.launching.JavaRuntime;
import org.objectstyle.wolips.core.resources.internal.types.project.ProjectAdapter;
import org.objectstyle.wolips.core.resources.types.project.IProjectAdapter;
import org.objectstyle.wolips.jdt.classpath.WOFrameworkClasspathContainer;

public class WOJavaRuntime {
	//private static ThreadLocal fgProjects = new ThreadLocal(); // Lists
	//private static ThreadLocal fgEntryCount = new ThreadLocal(); // Integers

	/**
	 * Returns resolved entries for the given entry in the context of the given
	 * launch configuration. If the entry is of kind
	 * <code>VARIABLE</code> or <code>CONTAINER</code>, variable and container
	 * resolvers are consulted. If the entry is of kind <code>PROJECT</code>,
	 * and the associated Java project specifies non-default output locations,
	 * the corresponding output locations are returned. Otherwise, the given
	 * entry is returned.
	 * <p>
	 * If the given entry is a variable entry, and a resolver is not registered,
	 * the entry itself is returned. If the given entry is a container, and a
	 * resolver is not registered, resolved runtime classpath entries are calculated
	 * from the associated container classpath entries, in the context of the project
	 * associated with the given launch configuration.
	 * </p>
	 * @param entry runtime classpath entry
	 * @param configuration launch configuration
	 * @return resolved runtime classpath entry
	 * @exception CoreException if unable to resolve
	 * @see IRuntimeClasspathEntryResolver
	 * @since 2.0
	 */
	public static IRuntimeClasspathEntry[] resolveRuntimeClasspathEntry(IRuntimeClasspathEntry entry, ILaunchConfiguration configuration) throws CoreException {
		switch (entry.getType()) {
		case IRuntimeClasspathEntry.PROJECT:
			// if the project has multiple output locations, they must be returned
			IResource resource = entry.getResource();
			if (resource instanceof IProject) {
				IProject p = (IProject) resource;
				IJavaProject project = JavaCore.create(p);
				if (project == null || !p.isOpen() || !project.exists()) {
					return new IRuntimeClasspathEntry[0];
				}
				IRuntimeClasspathEntry[] entries = resolveOutputLocations(project, entry.getClasspathProperty());
				if (entries != null) {
					return entries;
				}
			} else {
				// could not resolve project
				abort(MessageFormat.format(LaunchingMessages.JavaRuntime_Classpath_references_non_existant_project___0__3, new String[] { entry.getPath().lastSegment() }), null);
			}
			break;
		case IRuntimeClasspathEntry.VARIABLE:
			IRuntimeClasspathEntryResolver resolver = getVariableResolver(entry.getVariableName());
			if (resolver == null) {
				IRuntimeClasspathEntry[] resolved = resolveVariableEntry(entry, null, configuration);
				if (resolved != null) {
					return resolved;
				}
				break;
			}
			return resolver.resolveRuntimeClasspathEntry(entry, configuration);
		case IRuntimeClasspathEntry.CONTAINER:
			resolver = getContainerResolver(entry.getVariableName());
			if (resolver == null) {
				return computeDefaultContainerEntries(entry, configuration);
			}
			return resolver.resolveRuntimeClasspathEntry(entry, configuration);
		case IRuntimeClasspathEntry.ARCHIVE:
			// verify the archive exists
			String location = entry.getLocation();
			if (location == null) {
				abort(MessageFormat.format(LaunchingMessages.JavaRuntime_Classpath_references_non_existant_archive___0__4, new String[] { entry.getPath().toString() }), null);
			}
			File file = new File(location);
			if (!file.exists()) {
				abort(MessageFormat.format(LaunchingMessages.JavaRuntime_Classpath_references_non_existant_archive___0__4, new String[] { entry.getPath().toString() }), null);
			}
			break;
		case IRuntimeClasspathEntry.OTHER:
			resolver = getContributedResolver(((IRuntimeClasspathEntry2) entry).getTypeId());
			return resolver.resolveRuntimeClasspathEntry(entry, configuration);
		default:
			break;
		}
		return new IRuntimeClasspathEntry[] { entry };
	}

	/**
	 * Returns resolved entries for the given entry in the context of the given
	 * Java project. If the entry is of kind
	 * <code>VARIABLE</code> or <code>CONTAINER</code>, variable and container
	 * resolvers are consulted. If the entry is of kind <code>PROJECT</code>,
	 * and the associated Java project specifies non-default output locations,
	 * the corresponding output locations are returned. Otherwise, the given
	 * entry is returned.
	 * <p>
	 * If the given entry is a variable entry, and a resolver is not registered,
	 * the entry itself is returned. If the given entry is a container, and a
	 * resolver is not registered, resolved runtime classpath entries are calculated
	 * from the associated container classpath entries, in the context of the 
	 * given project.
	 * </p>
	 * @param entry runtime classpath entry
	 * @param project Java project context
	 * @return resolved runtime classpath entry
	 * @exception CoreException if unable to resolve
	 * @see IRuntimeClasspathEntryResolver
	 * @since 2.0
	 */
	public static IRuntimeClasspathEntry[] resolveRuntimeClasspathEntry(IRuntimeClasspathEntry entry, IJavaProject project) throws CoreException {
		ProjectAdapter projectAdapter = (ProjectAdapter)project.getProject().getAdapter(IProjectAdapter.class);
		if (projectAdapter != null && projectAdapter.isFramework()) {
			IClasspathEntry cp = entry.getClasspathEntry();
			if (cp != null && !cp.isExported() && cp.getPath() != null && cp.getPath().segmentCount() > 0 && WOFrameworkClasspathContainer.ID.equals(cp.getPath().segment(0))) {
				return new IRuntimeClasspathEntry[0];
			}
		}
		
		switch (entry.getType()) {
		case IRuntimeClasspathEntry.PROJECT:
			// if the project has multiple output locations, they must be returned
			IResource resource = entry.getResource();
			if (resource instanceof IProject) {
				IProject p = (IProject) resource;
				IJavaProject jp = JavaCore.create(p);
				if (jp != null && p.isOpen() && jp.exists()) {
					IRuntimeClasspathEntry[] entries = resolveOutputLocations(jp, entry.getClasspathProperty());
					if (entries != null) {
						return entries;
					}
				} else {
					return new IRuntimeClasspathEntry[0];
				}
			}
			break;
		case IRuntimeClasspathEntry.VARIABLE:
			IRuntimeClasspathEntryResolver resolver = getVariableResolver(entry.getVariableName());
			if (resolver == null) {
				IRuntimeClasspathEntry[] resolved = resolveVariableEntry(entry, project, null);
				if (resolved != null) {
					return resolved;
				}
				break;
			}
			return resolver.resolveRuntimeClasspathEntry(entry, project);
		case IRuntimeClasspathEntry.CONTAINER:
			resolver = getContainerResolver(entry.getVariableName());
			if (resolver == null) {
				return computeDefaultContainerEntries(entry, project);
			}
			return resolver.resolveRuntimeClasspathEntry(entry, project);
		case IRuntimeClasspathEntry.OTHER:
			resolver = getContributedResolver(((IRuntimeClasspathEntry2) entry).getTypeId());
			return resolver.resolveRuntimeClasspathEntry(entry, project);
		default:
			break;
		}
		return new IRuntimeClasspathEntry[] { entry };
	}

	private static IRuntimeClasspathEntry[] resolveOutputLocations(IJavaProject project, int classpathProperty) {
		try {
			Method resolveOutputLocations = JavaRuntime.class.getDeclaredMethod("resolveOutputLocations", IJavaProject.class, int.class);
			resolveOutputLocations.setAccessible(true);
			return (IRuntimeClasspathEntry[]) resolveOutputLocations.invoke(null, project, classpathProperty);
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	private static IRuntimeClasspathEntry[] resolveVariableEntry(IRuntimeClasspathEntry entry, Object object, ILaunchConfiguration configuration) {
		try {
			Method resolveVariableEntry = JavaRuntime.class.getDeclaredMethod("resolveVariableEntry", IRuntimeClasspathEntry.class, Object.class, ILaunchConfiguration.class);
			resolveVariableEntry.setAccessible(true);
			return (IRuntimeClasspathEntry[]) resolveVariableEntry.invoke(null, entry, object, configuration);
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	/**
	 * Performs default resolution for a container entry.
	 * Delegates to the Java model.
	 */
	private static IRuntimeClasspathEntry[] computeDefaultContainerEntries(IRuntimeClasspathEntry entry, ILaunchConfiguration config) throws CoreException {
		IJavaProject project = entry.getJavaProject();
		if (project == null) {
			project = JavaRuntime.getJavaProject(config);
		}
		return computeDefaultContainerEntries(entry, project);
	}
	
	/**
	 * Returns a runtime classpath entry that corresponds to the given
	 * classpath entry. The classpath entry may not be of type <code>CPE_SOURCE</code>
	 * or <code>CPE_CONTAINER</code>.
	 * 
	 * @param entry a classpath entry
	 * @return runtime classpath entry
	 * @since 2.0
	 */
	private static IRuntimeClasspathEntry newRuntimeClasspathEntry(IClasspathEntry entry) {
		return new RuntimeClasspathEntry(entry);
	}	

	/**
	 * Performs default resolution for a container entry.
	 * Delegates to the Java model.
	 */
	private static IRuntimeClasspathEntry[] computeDefaultContainerEntries(IRuntimeClasspathEntry entry, IJavaProject project) throws CoreException {
		ThreadLocal fgProjects;
		ThreadLocal fgEntryCount;
		
		try {
			Field fgProjectsField = JavaRuntime.class.getDeclaredField("fgProjects");
			fgProjectsField.setAccessible(true);
			fgProjects = (ThreadLocal)fgProjectsField.get(null);
	
			Field fgEntryCountField = JavaRuntime.class.getDeclaredField("fgEntryCount");
			fgEntryCountField.setAccessible(true);
			fgEntryCount = (ThreadLocal)fgEntryCountField.get(null);
		}
		catch (Throwable t) {
			throw new RuntimeException(t);
		}

		if (project == null || entry == null) {
			// cannot resolve without entry or project context
			return new IRuntimeClasspathEntry[0];
		}
		IClasspathContainer container = JavaCore.getClasspathContainer(entry.getPath(), project);
		if (container == null) {
			abort(MessageFormat.format(LaunchingMessages.JavaRuntime_Could_not_resolve_classpath_container___0__1, new String[] { entry.getPath().toString() }), null);
			// execution will not reach here - exception will be thrown
			return null;
		}
		IClasspathEntry[] cpes = container.getClasspathEntries();
		int property = -1;
		switch (container.getKind()) {
		case IClasspathContainer.K_APPLICATION:
			property = IRuntimeClasspathEntry.USER_CLASSES;
			break;
		case IClasspathContainer.K_DEFAULT_SYSTEM:
			property = IRuntimeClasspathEntry.STANDARD_CLASSES;
			break;
		case IClasspathContainer.K_SYSTEM:
			property = IRuntimeClasspathEntry.BOOTSTRAP_CLASSES;
			break;
		}
		List resolved = new ArrayList(cpes.length);
		List projects = (List) fgProjects.get();
		Integer count = (Integer) fgEntryCount.get();
		if (projects == null) {
			projects = new ArrayList();
			fgProjects.set(projects);
			count = new Integer(0);
		}
		int intCount = count.intValue();
		intCount++;
		fgEntryCount.set(new Integer(intCount));
		try {
			for (int i = 0; i < cpes.length; i++) {
				IClasspathEntry cpe = cpes[i];
				if (cpe.getEntryKind() == IClasspathEntry.CPE_PROJECT) {
					IProject p = ResourcesPlugin.getWorkspace().getRoot().getProject(cpe.getPath().segment(0));
					IJavaProject jp = JavaCore.create(p);
					if (!projects.contains(jp)) {
						projects.add(jp);
						IRuntimeClasspathEntry classpath = JavaRuntime.newDefaultProjectClasspathEntry(jp);
						IRuntimeClasspathEntry[] entries = resolveRuntimeClasspathEntry(classpath, jp);
						for (int j = 0; j < entries.length; j++) {
							IRuntimeClasspathEntry e = entries[j];
							if (!resolved.contains(e)) {
								resolved.add(entries[j]);
							}
						}
					}
				} else {
					IRuntimeClasspathEntry e = newRuntimeClasspathEntry(cpe);
					if (!resolved.contains(e)) {
						resolved.add(e);
					}
				}
			}
		} finally {
			intCount--;
			if (intCount == 0) {
				fgProjects.set(null);
				fgEntryCount.set(null);
			} else {
				fgEntryCount.set(new Integer(intCount));
			}
		}
		// set classpath property
		IRuntimeClasspathEntry[] result = new IRuntimeClasspathEntry[resolved.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = (IRuntimeClasspathEntry) resolved.get(i);
			result[i].setClasspathProperty(property);
		}
		return result;
	}

	private static IRuntimeClasspathEntryResolver checkResolver(IRuntimeClasspathEntryResolver resolver) {
		IRuntimeClasspathEntryResolver returnedResolver = resolver;
		if (resolver instanceof DefaultEntryResolver) {
			returnedResolver = new WODefaultEntryResolver();
		}
		return returnedResolver;
	}

	private static IRuntimeClasspathEntryResolver getContributedResolver(String typeId) {
		try {
			Method getContributedResolver = JavaRuntime.class.getDeclaredMethod("getContributedResolver", String.class);
			getContributedResolver.setAccessible(true);
			return checkResolver((IRuntimeClasspathEntryResolver) getContributedResolver.invoke(null, typeId));
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	private static IRuntimeClasspathEntryResolver getContainerResolver(String variableName) {
		try {
			Method getContainerResolver = JavaRuntime.class.getDeclaredMethod("getContainerResolver", String.class);
			getContainerResolver.setAccessible(true);
			return checkResolver((IRuntimeClasspathEntryResolver) getContainerResolver.invoke(null, variableName));
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	private static IRuntimeClasspathEntryResolver getVariableResolver(String variableName) {
		try {
			Method getVariableResolver = JavaRuntime.class.getDeclaredMethod("getVariableResolver", String.class);
			getVariableResolver.setAccessible(true);
			return checkResolver((IRuntimeClasspathEntryResolver) getVariableResolver.invoke(null, variableName));
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	/**
	 * Throws a core exception with an internal error status.
	 * 
	 * @param message the status message
	 * @param exception lower level exception associated with the
	 *  error, or <code>null</code> if none
	 */
	private static void abort(String message, Throwable exception) throws CoreException {
		abort(message, IJavaLaunchConfigurationConstants.ERR_INTERNAL_ERROR, exception);
	}

	/**
	 * Throws a core exception with an internal error status.
	 * 
	 * @param message the status message
	 * @param code status code
	 * @param exception lower level exception associated with the
	 * 
	 *  error, or <code>null</code> if none
	 */
	private static void abort(String message, int code, Throwable exception) throws CoreException {
		throw new CoreException(new Status(IStatus.ERROR, LaunchingPlugin.getUniqueIdentifier(), code, message, exception));
	}
}
