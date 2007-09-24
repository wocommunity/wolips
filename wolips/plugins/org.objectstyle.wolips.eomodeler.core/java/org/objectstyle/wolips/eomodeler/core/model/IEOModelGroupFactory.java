package org.objectstyle.wolips.eomodeler.core.model;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.objectstyle.wolips.eomodeler.core.Activator;

/**
 * Implemented by an extension point that can load EOModels within an
 * EOModelGroup.
 * 
 * @author mschrag
 */
public interface IEOModelGroupFactory {
	/**
	 * Loads models into the specified EOModelGroup based on the
	 * modelGroupResource provided.
	 * 
	 * @param modelGroupResource
	 *            the IProject, Folder, etc to load the model group from
	 * @param modelGroup
	 *            the model group to load into
	 * @param failures
	 *            the set of failures during load
	 * @param skipOnDuplicates
	 *            whether or not to skip duplicate models
	 * @throws EOModelException
	 *             if there is a problem loading models
	 */
	public void loadModelGroup(Object modelGroupResource, EOModelGroup modelGroup, Set<EOModelVerificationFailure> failures, boolean skipOnDuplicates, IProgressMonitor progressMonitor) throws EOModelException;

	public class Utility {
		public static void loadModelGroup(Object modelGroupResource, EOModelGroup modelGroup, Set<EOModelVerificationFailure> failures, boolean skipOnDuplicates, URL editingModelURL, IProgressMonitor progressMonitor) throws EOModelException {
			modelGroup.setEditingModelURL(editingModelURL);
			List<IEOModelGroupFactory> modelGroupFactories = IEOModelGroupFactory.Utility.modelGroupFactories();
			for (IEOModelGroupFactory modelGroupFactory : modelGroupFactories) {
				modelGroupFactory.loadModelGroup(modelGroupResource, modelGroup, failures, skipOnDuplicates, progressMonitor);
			}
			progressMonitor.setTaskName("Resolving model dependencies ...");
			modelGroup.resolve(failures);
			progressMonitor.setTaskName("Verifying model ...");
			modelGroup.verify(failures);
		}

		public static List<IEOModelGroupFactory> modelGroupFactories() {
			IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint("org.objectstyle.wolips.eomodeler.eomodelGroupFactory");
			IExtension[] extensions = extensionPoint.getExtensions();
			List<IEOModelGroupFactory> modelGroupFactories = new LinkedList<IEOModelGroupFactory>();
			for (IExtension extension : extensions) {
				IConfigurationElement[] configurationElements = extension.getConfigurationElements();
				for (IConfigurationElement configurationElement : configurationElements) {
					try {
						IEOModelGroupFactory modelGroupFactory = (IEOModelGroupFactory) configurationElement.createExecutableExtension("class");
						modelGroupFactories.add(modelGroupFactory);
					} catch (CoreException e) {
						e.printStackTrace();
						Activator.getDefault().log("Could not create EOModelGroup factory from configuration element: " + configurationElement, e);
					}
				}
			}
			return modelGroupFactories;
		}
	}
}
