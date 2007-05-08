package org.objectstyle.wolips.eomodeler.core.model;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.objectstyle.wolips.eomodeler.core.Activator;

/**
 * An EOClassLoaderFactory is responsible for constructing a ClassLoader that
 * can be used to execute code from EOF.
 * 
 * @author mschrag
 */
public interface IEOClassLoaderFactory {
	/**
	 * Returns a ClassLoader that contains all the EOF classes in it.
	 * 
	 * @param model
	 *            the EOModel to use as the basis for the the ClassLoader
	 * @return an EOF ClassLoader
	 */
	public ClassLoader createClassLoaderForModel(EOModel model) throws EOModelException;

	public class Utility {
		public static ClassLoader createClassLoader(EOModel model) throws EOModelException {
			List<IEOClassLoaderFactory> classLoaderFactories = new LinkedList<IEOClassLoaderFactory>();
			IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint("org.objectstyle.wolips.eomodeler.eoclassLoaderFactory");
			IExtension[] extensions = extensionPoint.getExtensions();
			for (IExtension extension : extensions) {
				IConfigurationElement[] configurationElements = extension.getConfigurationElements();
				for (IConfigurationElement configurationElement : configurationElements) {
					try {
						IEOClassLoaderFactory classLoaderFactory = (IEOClassLoaderFactory) configurationElement.createExecutableExtension("class");
						classLoaderFactories.add(classLoaderFactory);
					} catch (CoreException e) {
						Activator.getDefault().log("Could not create EOClassLoader factory from configuration element: " + configurationElement, e);
					}
				}
			}
			ClassLoader classLoader = null;
			if (classLoaderFactories.size() > 1) {
				throw new EOModelException("There was more than one EOF ClassLoader factory defined.");
			} else if (classLoaderFactories.size() == 0) {
				throw new EOModelException("There was no EOF ClassLoader factory defined.");
			} else {
				classLoader = classLoaderFactories.get(0).createClassLoaderForModel(model);
			}
			return classLoader;
		}
	}
}
