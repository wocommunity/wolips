package org.objectstyle.wolips.eomodeler.core.model;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
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
	 * Returns whether or not this factory can load a model from the given
	 * resource.
	 * 
	 * @param modelResource
	 *            the resource to check
	 * @return true if this factory can load the given modelResource
	 */
	public boolean canLoadModelFrom(Object modelResource);

	/**
	 * Returns an EOModel inside of an EOModelGroup.
	 * 
	 * @param modelResource
	 *            the File, IFile, IFolder, etc that represents the model to
	 *            load
	 * @param failures
	 *            the set of failures during load
	 * @param skipOnDuplicates
	 *            whether or not to skip duplicate models
	 * @return the loaded EOModel
	 * @throws EOModelException
	 *             if there is a problem loading models
	 */
	public EOModel loadModel(Object modelResource, Set<EOModelVerificationFailure> failures, boolean skipOnDuplicates) throws EOModelException;

	/**
	 * Returns whether or not this factory can load a model group from the given
	 * resource.
	 * 
	 * @param modelGroupResource
	 *            the resource to check
	 * @return true if this factory can load the given modelResource
	 */
	public boolean canLoadModelGroupFrom(Object modelGroupResource);

	/**
	 * Returns an entire loaded EOModelGroup.
	 * 
	 * @param modelGroupResource
	 *            the IProject, Folder, etc to load the model group from
	 * @param failures
	 *            the set of failures during load
	 * @param skipOnDuplicates
	 *            whether or not to skip duplicate models
	 * @return the loaded EOModelGroup
	 * @throws EOModelException
	 *             if there is a problem loading models
	 */
	public EOModelGroup loadModelGroup(Object modelGroupResource, Set<EOModelVerificationFailure> failures, boolean skipOnDuplicates, URL editingModelURL) throws EOModelException;

	public class Utility {
		public static EOModel loadModel(Object modelResource, Set<EOModelVerificationFailure> failures, boolean skipOnDuplicates) throws EOModelException {
			EOModel model = null;
			List<IEOModelGroupFactory> modelGroupFactories = IEOModelGroupFactory.Utility.modelGroupFactories();
			for (IEOModelGroupFactory modelGroupFactory : modelGroupFactories) {
				if (modelGroupFactory.canLoadModelFrom(modelResource)) {
					model = modelGroupFactory.loadModel(modelResource, failures, skipOnDuplicates);
				}
			}
			return model;
		}

		public static EOModelGroup loadModelGroup(Object modelGroupResource, Set<EOModelVerificationFailure> failures, boolean skipOnDuplicates, URL editingModelURL) throws EOModelException {
			EOModelGroup modelGroup = null;
			List<IEOModelGroupFactory> modelGroupFactories = IEOModelGroupFactory.Utility.modelGroupFactories();
			for (IEOModelGroupFactory modelGroupFactory : modelGroupFactories) {
				if (modelGroupFactory.canLoadModelGroupFrom(modelGroupResource)) {
					modelGroup = modelGroupFactory.loadModelGroup(modelGroupResource, failures, skipOnDuplicates, editingModelURL);
				}
			}
			return modelGroup;
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
